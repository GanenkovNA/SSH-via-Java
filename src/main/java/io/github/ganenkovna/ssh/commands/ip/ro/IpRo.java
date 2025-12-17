package io.github.ganenkovna.ssh.commands.ip.ro;

import com.jcraft.jsch.Session;
import io.github.ganenkovna.ssh.client.SshChannel;
import io.github.ganenkovna.ssh.commands.ip.ro.dto.IpRoDTO;
import io.github.ganenkovna.ssh.commands.ip.ro.service.IpRoParser;
import java.util.List;
import java.util.Objects;

/**
 * Утилитарный класс для выполнения команды {@code ip -j ro} по SSH
 * и получения информации о маршрутах Linux.
 *
 * <p>Класс инкапсулирует выполнение команды {@code ip --color=never -j ro}
 * через {@link SshChannel} и преобразование JSON-вывода в список DTO
 * {@link IpRoDTO} с помощью {@link IpRoParser}.</p>
 *
 * <p>Методы класса не управляют жизненным циклом {@link Session}:
 * SSH-сессия должна быть уже установлена вызывающей стороной и не закрывается
 * внутри данного API.</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/ip-route.8.html">ip-route(8)</a>
 * @see IpRoParser
 * @see IpRoDTO
 */
public final class IpRo {
  private static final String CMD_IP_RO = "ip --color=never -j ro";
  private static final String COMMAND = "ip -j ro";

  /** Запрет инстанцирования. */
  private IpRo() {
    throw new AssertionError("No instances");
  }

  /**
   * Выполняет команду {@code ip -j ro} на удалённом хосте по SSH
   * и возвращает список маршрутов Linux.
   *
   * <p>Метод выполняет команду {@code ip --color=never -j ro},
   * ожидает JSON-вывод и передаёт его на разбор в
   * {@link IpRoParser#parseOutput(String)}.</p>
   *
   * <p>Возвращаемый список никогда не {@code null}, но может быть пустым,
   * если таблица маршрутизации пуста или вывод команды не содержит элементов.</p>
   *
   * @param session активная SSH-сессия; строго не {@code null}
   * @return список DTO с информацией о маршрутах; никогда не {@code null}
   * @throws NullPointerException если {@code session == null}
   * @throws IllegalStateException если код завершения команды не является числом,
   *                               либо команда завершилась с ненулевым кодом
   *                               (stderr включается в сообщение)
   * @see IpRoParser#parseOutput(String)
   * @see IpRoDTO
   */
  public static List<IpRoDTO> showRoutes(Session session) {
    Objects.requireNonNull(session, "SSH session не должна быть null");
    SshChannel channel = new SshChannel(session);
    final String[] result = channel.execChannel(CMD_IP_RO);

    final int exitCode;
    try {
      exitCode = Integer.parseInt(result[0]);
    } catch (NumberFormatException e) {
      throw new IllegalStateException("Некорректный код завершения команды `"
          + COMMAND + "`: " + result[0], e);
    }

    if (exitCode == 0) {
      return IpRoParser.parseOutput(result[1]);
    } else {
      throw new IllegalStateException("Команда '" + COMMAND + "' не была выполнена успешно"
          + "\nКод завершения: " + exitCode
          + "\nВывод stderr: " + result[2]);
    }
  }
}
