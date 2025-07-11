package com.github.GanenkovNA.ssh.client;

import com.github.GanenkovNA.ssh.host.HostConnectionConfigDto;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Getter;

/**
 * Представляет SSH-сессию для подключения к удалённому серверу.
 * Обеспечивает установку и разрыв соединения, а также служит основой для создания каналов (exec, sftp и др.).
 * <p>
 * Пример использования:
 * <pre>
 * {@code
 * HostConnectionConfigDto config = new HostConnectionConfigDto("user", "host", 22, "password");
 * SshSession sshSession = new SshSession(config);
 * sshSession.createSession();
 * // ... работа с каналами ...
 * sshSession.closeSession();
 * }
 * </pre>
 *
 * @see HostConnectionConfigDto
 * @see Session (JSch)
 */
public class SshSession {
    private final HostConnectionConfigDto config;
    @Getter
    private Session session;


    /**
     * Создает новый экземпляр SSH-сессии с указанными параметрами подключения.
     * <p>
     * Для установки соединения необходимо вызвать {@link #createSession()}.
     *
     * @param config DTO с параметрами подключения. Должно быть валидным.
     * @throws IllegalArgumentException если {@code config} не прошёл проверку.
     */
    public SshSession(HostConnectionConfigDto config) {
        if (config == null) {
            throw new IllegalArgumentException("Конфигурация не может быть пустой");
        }
        this.config = config;
    }

    /**
     * Создаёт и устанавливает SSH-сессию с сервером на основе переданных параметров.
     * <p>
     * Настройки включают:
     * <ul>
     *   <li>Аутентификацию по логину/паролю.</li>
     *   <li>Отключение проверки ключа хоста (StrictHostKeyChecking=no).</li>
     * </ul>
     *
     * @return Представление сессии (необходимо для создания канала в {@link SshChannel})
     * @throws RuntimeException если подключение не удалось (например, неверные параметры или сетевые проблемы).
     */
    public Session createSession() {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(
                    config.getUsername(),
                    config.getHost(),
                    config.getPort());
            session.setPassword(config.getPassword());

            // Отключаем проверку ключа хоста (небезопасно, но для теста подойдет)
            session.setConfig("StrictHostKeyChecking", "no");

            // Начинаем сессию
            System.out.printf("Подключение к %s:%d как %s...%n",
                    config.getHost(), config.getPort(), config.getUsername());
            session.connect();

            return session;
        } catch (JSchException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Закрывает SSH-сессию и освобождает ресурсы.
     * Если сессия уже закрыта или не была создана, метод завершается без ошибок.
     */
    public void closeSession(){
        session.disconnect();
    }
}
