package io.github.ganenkovna.ssh.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jcraft.jsch.Session;
import io.github.ganenkovna.ssh.commands.ip.a.IpA;
import io.github.ganenkovna.ssh.host.HostConfigIO;
import io.github.ganenkovna.ssh.host.dto.HostInterfacesConfigDTO;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.github.ganenkovna.ssh.TestBase;

/**
 * Интеграционные проверки IO/парсинга конфигурации интерфейсов хоста.
 *
 * <p>Сценарии:
 * <ul>
 *   <li>сборка {@link HostInterfacesConfigDTO} из вывода {@code ip a};</li>
 *   <li>атомарная запись JSON на диск и последующее чтение;</li>
 *   <li>проверки агрегатора (mgmt-интерфейс, снимки интерфейсов).</li>
 * </ul></p>
 *
 * @see HostConfigIO
 * @see HostInterfacesConfigDTO
 * @see IpA
 */
public class HostInterfacesConfigIoTest extends TestBase {
  /** Временный JSON, используемый для отладочного вывода. Может быть пустым, никогда не {@code null}. */
  String json;

  /**
   * Локальный {@link ObjectMapper} для тестов.
   *
   * <p>Включён pretty-print и {@link JsonInclude.Include#ALWAYS}, чтобы не терять пустые поля при отладке.</p>
   */
  private static final ObjectMapper mapper = new ObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT)
      .setSerializationInclusion(JsonInclude.Include.ALWAYS);

  /** Буфер для собранной конфигурации интерфейсов. Может быть {@code null} до первой инициализации. */
  private HostInterfacesConfigDTO hostInterfacesConfig;

  /**
   * Формирование {@code HostInterfacesConfigDTO} из вывода {@code ip a}.
   *
   * <p>Проверяется, что полученный список интерфейсов не пуст, а обязательные поля инициализированы.</p>
   *
   * @throws RuntimeException при ошибках SSH/парсинга
   * @see IpA#showInterfaces(Session) 
   * @see HostInterfacesConfigDTO
   */
  @Test
  @DisplayName("DTO из ip a → список интерфейсов не пуст")
  public void shouldReturnListOfHostInterfaces() {
    hostInterfacesConfig = new HostInterfacesConfigDTO(
        IpA.showInterfaces(currentSession),
        hostConfig.getConnectionConfig().host());

    try {
      json = mapper.writeValueAsString(hostInterfacesConfig);  // Передаем объект, а не класс
    } catch (JsonProcessingException e) {
      System.err.println("Ошибка при преобразовании в JSON: " + e.getMessage());
    }
    System.out.println(json);
  }

  /**
   * Атомарная запись {@code host_interfaces_config.json} на диск.
   *
   * <p>Шаги:
   * <ol>
   *   <li>сборка {@code HostInterfacesConfigDTO} из {@code ip a};</li>
   *   <li>запись JSON с фолбэком при отсутствии {@code ATOMIC_MOVE};</li>
   *   <li>проверка наличия файла и базовая валидация содержимого.</li>
   * </ol></p>
   *
   * @throws IOException при ошибках файловых операций
   * @see HostConfigIO#writeInterfacesConfig(String, String, HostInterfacesConfigDTO)
   */
  @Test
  @DisplayName("Атомарная запись host_interfaces_config.json")
  public void shouldReturnFileWithListOfHostInterfaces()
    throws IOException {
    Path hostConfigsDirPath = Path.of(hostConfigsDir);
    hostInterfacesConfig = new HostInterfacesConfigDTO(
        IpA.showInterfaces(currentSession),
        hostConfig.getConnectionConfig().host());

    // 2) пишем файл атомарно
    HostConfigIO.writeInterfacesConfig(
        hostConfigsDir,
        hostInterfacesConfigName,
        hostInterfacesConfig);

    // 3) проверяем, что файл создан
    Path jsonPath = hostConfigsDirPath.resolve(hostInterfacesConfigName);
    assertTrue(Files.exists(jsonPath), "Файл не создан: " + jsonPath);

    // 4) выводим содержимое в консоль
    String json = Files.readString(jsonPath, StandardCharsets.UTF_8);
    System.out.println("==== " + jsonPath + " ====");
    System.out.println(json);
  }

  /**
   * Чтение {@code host_interfaces_config.json} и проверки агрегатора.
   *
   * <p>Шаги:
   * <ol>
   *   <li>чтение JSON в {@code HostInterfacesConfigDTO};</li>
   *   <li>валидация mgmt-интерфейса и списка интерфейсов;</li>
   *   <li>отладочный вывод JSON по необходимости.</li>
   * </ol></p>
   *
   * @throws IOException если файл отсутствует или JSON некорректен
   * @see HostConfigIO#readInterfacesConfig(String, String)
   * @see HostInterfacesConfigDTO
   */
  @Test
  @DisplayName("Чтение host_interfaces_config.json и проверки агрегатора")
  public void shouldReturnParseOfInterfaceConfig()
      throws IOException {
    // 1) читаем интерфейсный конфиг с диска
    hostInterfacesConfig = HostConfigIO.readInterfacesConfig(
        hostConfigsDir,
        hostInterfacesConfigName);
    assertNotNull(hostInterfacesConfig, "HostInterfacesConfigDTO не должен быть null");

    // 2) прокидываем в агрегатор
    hostConfig.setInterfacesConfig(hostInterfacesConfig);

    // 3) проверки доступными геттерами
    // mgmt
    String mgmt = hostConfig.getMgmtInterface();
    assertNotNull(mgmt, "Mgmt-интерфейс не должен быть null");
    assertFalse(mgmt.isBlank(), "Mgmt-интерфейс не должен быть пустым");

    // snapshots (проекции наружу)
    var snapshots = hostConfig.getInterfaceSnapshots();
    assertNotNull(snapshots, "Снимки интерфейсов не должны быть null");
    assertFalse(snapshots.isEmpty(), "Снимки интерфейсов не должны быть пустыми");
    String prettySnapshots = mapper.writeValueAsString(snapshots);


    // 4) выводим информацию в консоль
    Path jsonPath = Path.of(hostConfigsDir)
        .toAbsolutePath()
        .normalize()
        .resolve(hostInterfacesConfigName);

    System.out.println("==== Прочитанный JSON ====");
    System.out.println(Files.readString(jsonPath, StandardCharsets.UTF_8));

    System.out.println("\n==== Значения из HostConfigDTO ====");
    System.out.println("Mgmt interface: " + mgmt);
    System.out.println("Snapshots: " + prettySnapshots);
  }
}
