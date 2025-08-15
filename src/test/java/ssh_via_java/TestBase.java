package ssh_via_java;

import com.github.GanenkovNA.ssh.client.SshSession;
import com.github.GanenkovNA.ssh.host.HostConfigReader;
import com.github.GanenkovNA.ssh.host.HostConnectionConfigDto;
import com.jcraft.jsch.Session;
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
 * @see HostConnectionConfigDto DTO с параметрами подключения
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
  protected static String hostConnectionConfigPath = "/host_connection_config.json";
  /** Загруженная конфигурация подключения. */
  protected static HostConnectionConfigDto config = HostConfigReader.getHostConnectionConfig(hostConnectionConfigPath);
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
    sessionManage = new SshSession(config);
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
