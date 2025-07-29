package com.github.GanenkovNA.ssh.client;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.io.InputStream;

/**
 * Выполняет команды через SSH-канал в рамках существующей сессии.
 *
 * <p>Канал автоматически закрывается после выполнения команды. Для повторного выполнения команды
 * нужно создать новый экземпляр.
 *
 * @see Session (JSch)
 * @see ChannelExec
 */
public class SshChannel {
  private final Session session;
  /**
   * Код завершения последней выполненной SSH-команды.
   *
   * <p>Содержит:
   * <ul>
   *   <li>{@code null} — если команда ещё не выполнялась</li>
   *   <li>{@code 0} — успешное выполнение</li>
   *   <li>{@code >0} — код ошибки (зависит от команды)</li>
   * </ul>
   *
   * @see #execChannel(String)
   */
  private Integer exitStatus;

  /**
   * Создает SSH-канал для выполнения команд в рамках существующей сессии.
   *
   * <p>Канал будет использовать переданную сессию, но не управляет её жизненным циклом
   * (сессию нужно закрывать отдельно через {@link SshSession#closeSession()}).
   * </p>
   *
   * @param session Готовая SSH-сессия. Не может быть {@code null} или закрытой.
   * @throws IllegalStateException если сессия неактивна (отсутствует или разорвана).
   */
  public SshChannel(final Session session) {
    if (session == null || !session.isConnected()) {
      throw new IllegalArgumentException("Сессия должна быть запущена");
    }
    this.session = session;
  }

  /**
   * Считывает данные из потока (stdout/stderr) и сохраняет их в буфер.
   * Используется внутренне в {@link #execChannel(String)}.
   *
   * @param stream    Входной поток (InputStream).
   * @param stdBuffer Буфер для сохранения данных.
   * @throws RuntimeException если поток не может быть прочитан.
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
    try {
      // Открываем канал для выполнения команд
      ChannelExec channel = (ChannelExec) session.openChannel("exec");

      // Указываем комманду
      channel.setCommand(command);
      // Указываем, что не будем передавать данные на сервер
      channel.setInputStream(null);

      // Получаем потоки  вывода и ошибок
      InputStream in = channel.getInputStream();
      InputStream err = channel.getErrStream();
      // Создаём буферы для хранения вывода
      StringBuilder stdoutBuffer = new StringBuilder();
      StringBuilder stderrBuffer = new StringBuilder();
      // Поток для чтения stdout
      Thread stdoutThread = new Thread(
          () -> printStream(in, stdoutBuffer));
      // Поток для чтения stderr
      Thread stderrThread = new Thread(() -> printStream(err, stderrBuffer));

      // Запускаем потоки и подключаем канал
      stdoutThread.start();
      stderrThread.start();
      channel.connect();

      // Ждём завершения потоков
      stdoutThread.join();
      stderrThread.join();

      // Завершаем все процессы
      in.close();
      err.close();
      channel.disconnect();

      // Проверяем код завершения
      exitStatus = channel.getExitStatus();

      return new String[]{
          exitStatus.toString(),
          stdoutBuffer.toString(),
          stderrBuffer.toString()
      };
    } catch (JSchException | IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
