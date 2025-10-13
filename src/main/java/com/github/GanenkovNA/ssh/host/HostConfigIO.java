package com.github.GanenkovNA.ssh.host;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.GanenkovNA.ssh.host.dto.HostConnectionConfigDTO;
import com.github.GanenkovNA.ssh.host.dto.HostInterfacesConfigDTO;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * Утилитарный класс для чтения и записи конфигурационных файлов хоста.
 *
 * <p>Основные задачи:</p>
 * <ul>
 *   <li>Чтение {@link HostConnectionConfigDTO} (параметры SSH-подключения);</li>
 *   <li>Чтение и запись {@link HostInterfacesConfigDTO} (конфигурация интерфейсов);</li>
 *   <li>Строгая сериализация/десериализация JSON (Jackson с жёсткими флагами);</li>
 *   <li>Атомарная запись через временный файл с фолбэком для FS без {@link StandardCopyOption#ATOMIC_MOVE}.</li>
 * </ul>
 *
 * <p><b>Потокобезопасность:</b> класс не потокобезопасен.</p>
 *
 * <p>Пример использования:</p>
 * <pre>{@code
 * Path baseDir = Path.of("./src/test/resources");
 * HostConnectionConfigDTO connection = HostConfigIO.readConnectionConfig(
 *     baseDir.toString(), "host_connection_config.json");
 *
 * HostInterfacesConfigDTO interfaces = HostConfigIO.readInterfacesConfig(
 *     baseDir.toString(), "host_interfaces_config.json");
 *
 * // правим interfaces и сохраняем
 * HostConfigIO.writeInterfacesConfig(baseDir.toString(), "host_interfaces_config.json", interfaces);
 * }</pre>
 *
 * @see HostConnectionConfigDTO
 * @see HostInterfacesConfigDTO
 */
public final class HostConfigIO {
  /**
   * Кодировка для чтения и записи конфигурационных файлов.
   *
   * <p>Используется во всех файловых операциях данного класса; никогда не {@code null}.</p>
   */
  private static final Charset UTF8 = StandardCharsets.UTF_8;

  /**
   * Общий экземпляр {@link ObjectMapper} с «строгими» флагами для JSON:
   * <ul>
   *   <li>{@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} = {@code true};</li>
   *   <li>{@link DeserializationFeature#FAIL_ON_MISSING_CREATOR_PROPERTIES} = {@code true};</li>
   *   <li>{@link SerializationFeature#INDENT_OUTPUT} = {@code true} (красивый вывод при записи).</li>
   * </ul>
   *
   * <p>Экземпляр настраивается один раз и переиспользуется методами класса.</p>
   */
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
      .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true)
      .configure(SerializationFeature.INDENT_OUTPUT, true);

  /**
   * Запрет инстанцирования утилитарного класса.
   *
   * @throws AssertionError всегда, при попытке вызвать конструктор
   */
  private HostConfigIO() {
    throw new AssertionError("No instances");
  }

  /**
   * Читает JSON-файл с параметрами SSH-подключения и десериализует его
   * в объект {@link HostConnectionConfigDTO}.
   *
   * <p>Ожидается наличие указанной директории и файла внутри неё. Десериализация строгая:
   * неизвестные поля или отсутствие обязательных свойств приводят к ошибке.</p>
   *
   * @param hostConfigsDir путь к директории с конфигурациями; строго не {@code null}, должна существовать
   * @param hostConnectionConfigName имя файла конфигурации подключения; строго не {@code null}
   * @return десериализованный {@link HostConnectionConfigDTO}; никогда не {@code null}
   * @throws IOException если директория не найдена, файл отсутствует либо JSON некорректен
   * @see HostConnectionConfigDTO
   */
  public static HostConnectionConfigDTO readConnectionConfig(String hostConfigsDir,
                                                             String hostConnectionConfigName)
      throws IOException {
    Path hostConfigsDirPath = toExistingDirectory(hostConfigsDir);
    Path hostConnectionConfigPath = requireFileInDirectory(hostConfigsDirPath, hostConnectionConfigName);

    try (Reader r = Files.newBufferedReader(hostConnectionConfigPath, UTF8)) {
      return MAPPER.readValue(r, HostConnectionConfigDTO.class);
    } catch (IOException e) {
      throw new IOException("Ошибка при чтении HostConnectionConfig: " + hostConnectionConfigPath, e);
    }
  }

  /**
   * Записывает объект {@link HostInterfacesConfigDTO} в JSON-файл конфигурации интерфейсов.
   *
   * <p>Запись выполняется атомарно: данные сначала пишутся во временный файл
   * рядом с целевым ({@code *.tmp}), после чего выполняется перемещение с
   * {@link StandardCopyOption#ATOMIC_MOVE} и {@link StandardCopyOption#REPLACE_EXISTING}.
   * Если файловая система не поддерживает атомарный перенос, выполняется фолбэк без атомарности
   * (Windows/FS limitation).</p>
   *
   * @param hostConfigsDir путь к директории для записи; строго не {@code null}, должна существовать
   * @param hostInterfacesConfigName имя файла конфигурации интерфейсов; строго не {@code null}
   * @param dto объект конфигурации интерфейсов; строго не {@code null}
   * @throws IOException если директория не найдена, файл невозможно создать или произошла ошибка записи
   * @see HostInterfacesConfigDTO
   * @see Files#move(Path, Path, java.nio.file.CopyOption...)
   */
  public static void writeInterfacesConfig(String hostConfigsDir,
                                           String hostInterfacesConfigName,
                                           HostInterfacesConfigDTO dto)
      throws IOException {

    Path hostConfigsDirPath = toExistingDirectory(hostConfigsDir);
    Path hostInterfacesConfigPath = hostConfigsDirPath
        .resolve(hostInterfacesConfigName)
        .normalize();

    // tmp рядом с целевым файлом
    Path tmp = hostConfigsDirPath.resolve(hostInterfacesConfigPath.getFileName().toString() + ".tmp");
    try (Writer w = Files.newBufferedWriter(tmp, UTF8)) {   // 3. пишем в tmp (UTF-8)
      MAPPER.writeValue(w, dto);
    }
    // 4. атомарный move, при необходимости — фолбэк
    try {
      Files.move(tmp, hostInterfacesConfigPath,
          StandardCopyOption.ATOMIC_MOVE,
          StandardCopyOption.REPLACE_EXISTING);
    } catch (AtomicMoveNotSupportedException e) {
      // Windows/FS limitation — без атомарности
      Files.move(tmp, hostInterfacesConfigPath, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  /**
   * Читает JSON-файл конфигурации интерфейсов и десериализует его
   * в объект {@link HostInterfacesConfigDTO}.
   *
   * <p>Ожидается наличие указанной директории и файла внутри неё. Десериализация строгая:
   * неизвестные поля или отсутствие обязательных свойств приводят к ошибке.</p>
   *
   * @param hostConfigsDir путь к директории с конфигурациями; строго не {@code null}, должна существовать
   * @param hostInterfacesConfigName имя файла конфигурации интерфейсов; строго не {@code null}
   * @return десериализованный {@link HostInterfacesConfigDTO}; никогда не {@code null}
   * @throws IOException если директория не найдена, файл отсутствует либо JSON некорректен
   * @see HostInterfacesConfigDTO
   */
  public static HostInterfacesConfigDTO readInterfacesConfig(String hostConfigsDir,
                                                             String hostInterfacesConfigName)
      throws IOException {
    Path hostConfigsDirPath = toExistingDirectory(hostConfigsDir);
    Path hostInterfacesConfigPath = requireFileInDirectory(hostConfigsDirPath, hostInterfacesConfigName);

    try (Reader r = Files.newBufferedReader(hostInterfacesConfigPath, UTF8)) {
      return MAPPER.readValue(r, HostInterfacesConfigDTO.class);
    } catch (IOException e) {
      throw new IOException("Ошибка при чтении HostInterfacesConfig: " + hostInterfacesConfigPath, e);
    }
  }

  /**
   * Преобразует строковый путь к директории в {@link Path} и проверяет, что директория существует.
   *
   * <p>Путь нормализуется и приводится к абсолютному. Если путь существует, но указывает на файл,
   * будет выброшено исключение.</p>
   *
   * @param hostConfigsDir путь к директории; строго не {@code null}
   * @return нормализованный абсолютный {@link Path} к существующей директории; никогда не {@code null}
   * @throws IOException если директория отсутствует либо по пути обнаружен файл
   */
  private static Path toExistingDirectory(String hostConfigsDir)
      throws IOException {
    Objects.requireNonNull(hostConfigsDir, "hostConfigsDir == null");
    Path path = Path.of(hostConfigsDir)
        .toAbsolutePath()
        .normalize();

    if (Files.exists(path)) {
      if (!Files.isDirectory(path)) {
        throw new IOException("Ожидалась директория, но найден файл: " + path);
      }
    } else {
      throw new IOException("Директория не найдена: " + path);
    }

    return path;
  }

  /**
   * Проверяет наличие указанного файла внутри заданной директории и возвращает путь к нему.
   *
   * <p>Путь формируется как {@code hostConfigsDirPath.resolve(hostConfigName).normalize()}.
   * Если файл отсутствует, выбрасывается исключение.</p>
   *
   * @param hostConfigsDirPath путь к существующей директории; строго не {@code null}
   * @param hostConfigName имя файла внутри директории; строго не {@code null}
   * @return нормализованный {@link Path} к существующему файлу; никогда не {@code null}
   * @throws IOException если файл отсутствует по вычисленному пути
   * @see Path#resolve(String)
   */
  private static Path requireFileInDirectory(Path hostConfigsDirPath,
                                             String hostConfigName)
      throws IOException {
    Objects.requireNonNull(hostConfigsDirPath, "hostConfigsDirPath == null");
    Objects.requireNonNull(hostConfigName, "hostConfigName == null");

    Path file = hostConfigsDirPath
        .resolve(hostConfigName)
        .normalize();
    if (!Files.exists(file)) {
      throw new IOException("Файл не найден: " + file);
    }

    return file;
  }
}
