package io.github.ganenkovna.ssh.client;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.io.InputStream;

/**
 * Обёртка над {@link ChannelExec} для выполнения команд через активную SSH-сессию.
 *
 * <p>Канал автоматически закрывается после выполнения команды. Для каждой новой команды
 * необходимо создавать новый экземпляр {@code SshChannel}.</p>
 *
 * <p><b>Контракт:</b> сессия должна быть установлена ({@code session.isConnected() == true}).
 * Экземпляр класса не потокобезопасен.</p>
 *
 * @see Session
 * @see ChannelExec
 */

public class SshChannel {
  private final Session session;
  private static final int CHANNEL_CONNECT_TIMEOUT_MS = 10_000;

  /**
   * Создаёт SSH-канал для выполнения команд в рамках существующей сессии.
   *
   * <p>Канал использует переданную сессию, но не управляет её жизненным циклом —
   * закрывать сессию следует отдельно через {@link SshSession#closeSession()}.</p>
   *
   * @param session активная SSH-сессия; строго не {@code null}, должна быть установлена
   * @throws NullPointerException если {@code session == null}
   * @throws IllegalStateException если {@code session.isConnected() == false}
   */
  public SshChannel(final Session session) {
    if (session == null) {
      throw new NullPointerException("session == null");
    }
    if (!session.isConnected()) {
      throw new IllegalStateException("SSH-сессия не установлена (session.isConnected() == false)");
    }
    this.session = session;
  }

  /**
   * Читает данные из указанного потока (stdout или stderr) и добавляет их в буфер.
   *
   * <p>Используется внутренне в {@link #execChannel(String)} для асинхронного чтения вывода.</p>
   *
   * @param stream входной поток; не {@code null}
   * @param stdBuffer буфер для накопления текста
   */
  private static void printStream(InputStream stream, StringBuilder stdBuffer) {
    try {
      byte[] buffer = new byte[1024];
      int len;
      while ((len = stream.read(buffer)) > 0) {
        stdBuffer.append(new String(buffer, 0, len));
      }
    } catch (IOException e) {
      throw new RuntimeException("Ошибка печати потока", e);
    }
  }

  /**
   * Выполняет команду на удалённом сервере через SSH-канал типа "exec".
   *
   * <p>Возвращает массив строк:
   * <ol>
   *   <li>Код завершения команды (exit status).</li>
   *   <li>Вывод команды (stdout).</li>
   *   <li>Ошибки команды (stderr).</li>
   * </ol>
   *
   * @param command Команда для выполнения (например, "ls -la").
   * @return Массив строк {@code [exitStatus, stdout, stderr]}.
   * @throws RuntimeException если произошла ошибка ввода-вывода или прерывание потока.
   */
  public String[] execChannel(final String command) {
    if (command == null) {
      throw new NullPointerException("command == null");
    }
    if (command.isBlank()) {
      throw new IllegalArgumentException("command не может быть пустой/пробельной");
    }

    ChannelExec channel = null;
    InputStream in = null;
    InputStream err = null;
    int exitStatus;

    try {
      // Открываем канал для выполнения команд
      channel = (ChannelExec) session.openChannel("exec");

      // Указываем команду
      channel.setCommand(command);
      // Указываем, что не будем передавать данные на сервер
      channel.setInputStream(null);

      // Получаем потоки вывода и ошибок
      in = channel.getInputStream();
      err = channel.getExtInputStream();

      // Создаём буферы для хранения вывода
      StringBuilder stdoutBuffer = new StringBuilder();
      StringBuilder stderrBuffer = new StringBuilder();

      // Поток для чтения stdout
      final InputStream finalIn = in;
      Thread stdoutThread = new Thread(
          () -> printStream(finalIn, stdoutBuffer));
      // Поток для чтения stderr
      final InputStream finalErr = err;
      Thread stderrThread = new Thread(
          () -> printStream(finalErr, stderrBuffer));

      // Запускаем потоки и подключаем канал
      stdoutThread.start();
      stderrThread.start();
      channel.connect(CHANNEL_CONNECT_TIMEOUT_MS);

      // Ждём завершения потоков
      try {
        stdoutThread.join();
        stderrThread.join();
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException("Ожидание потоков чтения было прервано", ie);
      }

      // Проверяем код завершения
      exitStatus = channel.getExitStatus();

      return new String[]{
          Integer.toString(exitStatus),
          stdoutBuffer.toString(),
          stderrBuffer.toString()
      };
    } catch (JSchException | IOException e) {
      throw new IllegalStateException("Не удалось выполнить команду по SSH: " + command, e);
    } finally {
      try {
        // Завершаем все процессы
        if (in != null) {
          in.close();
        }
      } catch (IOException ignore) {}
      try {
        if (err != null) {
          err.close();
        }
      } catch (IOException ignore) {}
      if (channel != null && channel.isConnected()) {
        channel.disconnect();
      }
    }
  }
}
