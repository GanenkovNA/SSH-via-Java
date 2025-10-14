package io.github.ganenkovna.ssh;

import io.github.ganenkovna.ssh.client.SshSession;
import io.github.ganenkovna.ssh.host.HostConfigIO;
import io.github.ganenkovna.ssh.host.dto.HostConfigDTO;
import io.github.ganenkovna.ssh.host.dto.HostConnectionConfigDTO;
import com.jcraft.jsch.Session;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * Базовый класс для тестов, работающих с SSH-соединением.
 * <p>
 * Предоставляет общую функциональность для всех тестов:
 * <ul>
 *   <li>Загрузку конфигурации подключения из JSON-файла
 *   <li>Установку и закрытие SSH-сессии
 *   <li>Общие ресурсы для тестовых классов-потомков
 * </ul>
 *
 * <p>Использование:
 * <pre>{@code
 * public class MyTest extends TestBase {
 *     @Test
 *     public void testSomething() {
 *         // Используем currentSession для тестов
 *     }
 * }
 * }</pre>
 *
 * @see HostConnectionConfigDTO DTO с параметрами подключения
 * @see SshSession Менеджер SSH-сессий
 */
public class TestBase {
  /**
   * Путь к файлу конфигурации SSH-подключения в classpath.
   * <p>
   * Файл должен содержать параметры в формате JSON:
   * <pre>{@code
   * {
   *   "host": "example.com",
   *   "port": 22,
   *   "username": "user",
   *   "password": "secret"
   * }
   * }</pre>
   */
  protected static final String hostConfigsDir = "./src/test/resources";
  protected static final String hostConnectionConfigName = "host_connection_config.json";
  protected static final String hostInterfacesConfigName = "host_interfaces_config.json";

  protected static final HostConfigDTO hostConfig;
  static {
    try {
      hostConfig = new HostConfigDTO(HostConfigIO
          .readConnectionConfig(hostConfigsDir, hostConnectionConfigName));
    } catch (IOException e) {
      throw new RuntimeException("Ошибка при загрузке host_connection_config.json", e);
    }
  }

  /** Менеджер SSH-сессии. */
  protected static SshSession sessionManage;
  /**
   * Текущая активная SSH-сессия.
   *
   * <p>Доступна для использования в тестах-потомках.
   */
  protected static Session currentSession;

  /**
   * Инициализирует тестовое окружение перед всеми тестами.
   * <p>Выполняет:
   *   <ol>
   *     <li>Загрузку конфигурации из JSON-файла
   *     <li>Создание SSH-сессии
   *   </ol>
   *
   * @throws RuntimeException если не удалось установить соединение
   */
  @BeforeAll
  public static void startUp(){
    sessionManage = new SshSession(hostConfig);
    currentSession = sessionManage.createSession();
  }

  /**
   * Очищает тестовое окружение после всех тестов.
   *
   * <p>Корректно закрывает SSH-сессию и освобождает ресурсы.
   */
  @AfterAll
  public static void tearDown(){
    sessionManage.closeSession();
  }
}
