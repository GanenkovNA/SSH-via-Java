package io.github.ganenkovna.ssh.commands.ip.a;

import io.github.ganenkovna.ssh.client.SshChannel;
import io.github.ganenkovna.ssh.commands.ip.a.dto.InterfaceDto;
import io.github.ganenkovna.ssh.commands.ip.a.service.IpAParser;
import com.jcraft.jsch.Session;
import java.util.List;

/**
 * Выполняет на удалённой машине команду {@code ip a} и преобразует результат
 * в список DTO сетевых интерфейсов.
 *
 * <p>Команда вызывается по SSH через {@link SshChannel},
 * а текстовый вывод разбирается парсером {@link IpAParser}.</p>
 *
 * <p>Перед выполнением отключается цветовая разметка вывода
 * ({@code IPROUTE_COLOR=never}, {@code -c=never}) — это гарантирует, что парсер
 * получит чистый текст без управляющих ANSI-последовательностей.</p>
 *
 * <p>Класс не хранит состояния и предназначен исключительно как оболочка вокруг SSH-вызова
 * команды {@code ip a}.</p>
 *
 * <h4>Пример</h4>
 * <pre>{@code
 * Session session = ... // активная SSH-сессия
 * List<InterfaceDto> interfaces = IpA.showInterfaces(session);
 * }</pre>
 *
 * @implNote Класс не потокозависим и может безопасно использоваться из разных потоков,
 * так как не содержит изменяемых полей.
 *
 * @see SshChannel
 * @see IpAParser
 * @see InterfaceDto
 */
public final class IpA {
  private static final String CMD_IP_A = "IPROUTE_COLOR=never ip -c=never a";

  /** Запрет инстанцирования. */
  private IpA() {
    throw new AssertionError("No instances");
  }

  /**
   * Выполняет команду {@code ip a} на удалённом сервере через SSH и парсит её вывод.
   *
   * <p>Выполнение производится с отключённой цветовой разметкой
   * ({@code IPROUTE_COLOR=never}, {@code -c=never}) для упрощения парсинга.</p>
   *
   * @param session активная SSH-сессия; строго не {@code null}, должна быть установлена
   * @return список интерфейсов; никогда не {@code null}, может быть пустым
   * @throws NullPointerException если {@code session == null}
   * @throws IllegalStateException если команда {@code ip a} завершилась с ненулевым кодом
   *                               или произошла ошибка выполнения
   * @see SshChannel
   * @see IpAParser
   */
  public static List<InterfaceDto> showInterfaces(Session session) {
    SshChannel channel = new SshChannel(session);
    final String[] result = channel.execChannel(CMD_IP_A);

    final int exitCode;
    try {
      exitCode = Integer.parseInt(result[0]);
    } catch (NumberFormatException e) {
      throw new IllegalStateException("Некорректный код завершения команды `ip a`: " + result[0], e);
    }

    if (exitCode == 0) {
      return IpAParser.parseOutput(result[1]);
    } else {
      throw new IllegalStateException("Команда 'ip a' не была успешно выполнена"
          + "\nКод завершения: " + exitCode
          + "\nВывод stderr: " + result[2]);
    }
  }
}
