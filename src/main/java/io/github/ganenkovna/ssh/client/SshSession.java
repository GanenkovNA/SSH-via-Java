package io.github.ganenkovna.ssh.client;

import io.github.ganenkovna.ssh.host.dto.HostConfigDTO;
import io.github.ganenkovna.ssh.host.dto.HostConnectionConfigDTO;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.util.Objects;
import lombok.Getter;

/**
 * Представляет SSH-сессию для подключения к удалённому серверу.
 * Обеспечивает установку и разрыв соединения,
 * а также служит основой для создания каналов (exec, sftp и др.).
 *
 * <p>Пример использования:
 * <pre>
 * {@code
 * HostConnectionConfigDTO config = new HostConnectionConfigDTO("user", "host", 22, "password");
 * SshSession sshSession = new SshSession(config);
 * sshSession.createSession();
 * // ... работа с каналами ...
 * sshSession.closeSession();
 * }
 * </pre>
 *
 * @see HostConnectionConfigDTO
 * @see Session (JSch)
 */
public final class SshSession {
  private final HostConfigDTO config;
  @Getter
  private Session session;

  // Ключи JSch config
  private static final String CFG_STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
  private static final String CFG_PREFERRED_AUTH = "PreferredAuthentications";

  // Значения JSch config
  private static final String VAL_NO = "no";
  private static final String VAL_AUTH_PASSWORD = "password";

  // Таймауты и keep-alive (мс)
  private static final int CONNECT_TIMEOUT_MS = 10_000;
  private static final int KEEPALIVE_INTERVAL_MS = 15_000;
  private static final int KEEPALIVE_COUNT_MAX = 3;

  /**
   * Создаёт менеджер SSH-сессии с указанной конфигурацией.
   *
   * <p>Параметры подключения (хост, порт, пользователь, пароль) берутся из
   * {@link HostConnectionConfigDTO}. Значения валидируются при создании DTO.</p>
   *
   * @param config параметры подключения; строго не {@code null}
   * @throws NullPointerException если {@code config} равен {@code null}
   * @see HostConnectionConfigDTO
   */
  public SshSession(HostConfigDTO config) {
    this.config = Objects.requireNonNull(config, "config не может быть null");
  }

  /**
   * Устанавливает SSH-соединение с сервером на основе параметров {@link HostConnectionConfigDTO}.
   *
   * <p>Поддерживается только парольная аутентификация. Если поле {@code password} пустое,
   * генерируется исключение, поскольку key-based аутентификация ещё не реализована.</p>
   *
   * <p>В процессе подключения выполняются следующие действия:</p>
   * <ul>
   *   <li>Отключается проверка ключа хоста ({@code StrictHostKeyChecking=no})</li>
   *   <li>Явно задаётся тип аутентификации ({@code PreferredAuthentications=password})</li>
   *   <li>Выполняется подключение с таймаутом {@value #CONNECT_TIMEOUT_MS} мс</li>
   *   <li>Устанавливаются параметры keep-alive:
   *       {@value #KEEPALIVE_INTERVAL_MS} мс интервал и {@value #KEEPALIVE_COUNT_MAX} попыток</li>
   *   <li>Для удобства отладки выводится сообщение о попытке подключения</li>
   * </ul>
   *
   * @return активная SSH-сессия; никогда не {@code null}
   * @throws IllegalStateException если {@code password} пустой
   *                               (key-based аутентификация ещё не реализована)
   *                               или не удалось установить соединение
   * @see HostConnectionConfigDTO
   * @see Session
   */
  public Session createSession() {
    String username = config.getConnectionConfig().username();
    String host = config.getConnectionConfig().host();
    int port = config.getConnectionConfig().port();

    try {
      JSch jsch = new JSch();
      session = jsch.getSession(
          username,
          host,
          port);

      String password = config.getConnectionConfig().password();
      if (password.isEmpty()){
        throw new IllegalStateException(
            "Key-based аутентификация ещё не реализована: password пустой; требуется непустой пароль");
      } else {
        session.setPassword(password);
      }

      // Отключаем проверку ключа хоста (небезопасно, но для теста подойдет)
      session.setConfig(CFG_STRICT_HOST_KEY_CHECKING, VAL_NO);
      // Явно задаём тип аутентификации
      session.setConfig(CFG_PREFERRED_AUTH, VAL_AUTH_PASSWORD);

      // Начинаем сессию, устанавливаем таймаут и keep-alive
      System.out.printf("Подключение к %s:%d как %s...%n",
          host, port, username);
      session.connect(CONNECT_TIMEOUT_MS);
      session.setServerAliveInterval(KEEPALIVE_INTERVAL_MS);
      session.setServerAliveCountMax(KEEPALIVE_COUNT_MAX);

      return session;
    } catch (JSchException e) {
      throw new IllegalStateException(
          "Не удалось установить SSH-сессию к " + host + ":" + port
              + " как " + username, e);
    }
  }

  /**
   * Закрывает SSH-сессию, если она была открыта.
   *
   * <p>Метод идемпотентен: при повторных вызовах на уже закрытой или отсутствующей сессии
   * никаких исключений не выбрасывается. Внутреннее поле ссылки на сессию обнуляется.</p>
   */
  public void closeSession() {
    if (session != null){
      try {
        if (session.isConnected()){
          session.disconnect();
        }
      } finally {
        session = null;
      }
    }
  }
}
