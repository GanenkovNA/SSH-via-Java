package io.github.ganenkovna.ssh.client;

import static io.github.ganenkovna.util.StringUtils.requireNonBlank;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import io.github.ganenkovna.ssh.host.dto.HostConfigDTO;
import io.github.ganenkovna.ssh.host.dto.HostConnectionConfigDTO;
import java.util.Objects;
import lombok.Getter;

/**
 * Представляет управляемую SSH-сессию для подключения к удалённому серверу.
 *
 * <p>Отвечает за установку/разрыв соединения и предоставляет ссылку на активную
 * {@link Session} для последующего создания каналов (exec, sftp и др.).</p>
 *
 * <p><strong>Ограничения:</strong> текущая реализация поддерживает только
 * парольную аутентификацию; проверка ключа хоста отключена
 * ({@code StrictHostKeyChecking=no}) — это небезопасно для production.</p>
 *
 * @see HostConfigDTO
 * @see HostConnectionConfigDTO
 * @see Session
 */
public final class SshSession {
  /** Имя пользователя. Строго не {@code null}. */
  private final String username;
  /** Адрес SSH-хоста. Строго не {@code null}. */
  private final String host;
  /** Порт SSH. В диапазоне {@code 1..65535}. */
  private final int port;
  /** Пароль пользователя. Строго не {@code null}, не пустой/пробельный. */
  private final String password;

  /**
   * Текущая сессия JSch.
   *
   * <p>Возвращает {@code null}, если подключение ещё не установлено либо уже закрыто.
   * После {@link #closeSession()} ссылка очищается (становится {@code null}).</p>
   */
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
   * Создаёт менеджер SSH-сессии из агрегированной конфигурации хоста.
   *
   * <p>Параметры подключения ({@code host}, {@code port}, {@code username}, {@code password})
   * берутся из {@link HostConfigDTO#getConnectionConfig()}
   * и считаются уже провалидированными на уровне DTO.</p>
   *
   * @param config агрегированная конфигурация хоста; строго не {@code null}
   * @throws NullPointerException если {@code config} равен {@code null}
   * @see HostConfigDTO
   * @see HostConnectionConfigDTO
   */
  public SshSession(HostConfigDTO config) {
    Objects.requireNonNull(config, "config не может быть null");

    this.username = config.getConnectionConfig().username();
    this.host = config.getConnectionConfig().host();
    this.port = config.getConnectionConfig().port();
    this.password = config.getConnectionConfig().password();
  }

  /**
   * Создаёт менеджер SSH-сессии из явных параметров.
   *
   * @param username имя пользователя; строго не {@code null}, не пустой/пробельный
   * @param host адрес SSH-сервера; строго не {@code null}, не пустой/пробельный
   * @param port порт SSH в диапазоне {@code 1..65535}
   * @param password пароль; строго не {@code null}, не пустой/пробельный
   * @throws NullPointerException если любой строковый параметр равен {@code null}
   * @throws IllegalArgumentException если {@code username}/{@code host}/{@code password}
   *                                  пусты/пробельны или
   *                                  {@code port} вне диапазона {@code 1..65535}
   */
  public SshSession(String username, String host, int port, String password) {
    this.username = requireNonBlank(username, "Имя пользователя");
    this.host = requireNonBlank(host, "Адрес хоста (SSH-сервера)");
    this.port = port;
    this.password = requireNonBlank(password, "Пароль");
  }

  /**
   * Устанавливает SSH-соединение с сервером на основе параметров экземпляра.
   *
   * <p>Выполняются следующие действия:</p>
   * <ul>
   *   <li>Создаётся {@link JSch} и инициализируется {@link Session};</li>
   *   <li>Отключается проверка ключа хоста ({@code StrictHostKeyChecking=no});</li>
   *   <li>Явно задаётся тип аутентификации ({@code PreferredAuthentications=password});</li>
   *   <li>Выполняется подключение с таймаутом {@value #CONNECT_TIMEOUT_MS} мс;</li>
   *   <li>Настраиваются keep-alive: интервал {@value #KEEPALIVE_INTERVAL_MS} мс,
   *       максимум попыток {@value #KEEPALIVE_COUNT_MAX}.</li>
   * </ul>
   *
   * @return активная SSH-сессия; никогда не {@code null}
   * @throws IllegalStateException если не удалось установить соединение
   * @implNote Поддерживается только парольная аутентификация. Проверка ключа хоста отключена и
   *           не должна использоваться в боевой среде.
   * @see Session
   * @see JSch
   */
  public Session createSession() {
    try {
      JSch jsch = new JSch();
      session = jsch.getSession(
          username,
          host,
          port);

      if (password.isEmpty()) {
        throw new IllegalStateException(
            "Key-based аутентификация ещё не реализована: "
                + "password пустой; требуется непустой пароль");
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
    System.out.printf("Закрытие подключения к %s:%d как %s...%n",
        host, port, username);
    if (session != null) {
      try {
        if (session.isConnected()) {
          session.disconnect();
        }
      } finally {
        session = null;
      }
    }
  }
}
