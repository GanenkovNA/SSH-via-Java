package io.github.ganenkovna.ssh.commands.bridge.link;

import com.jcraft.jsch.Session;
import io.github.ganenkovna.ssh.client.SshChannel;
import io.github.ganenkovna.ssh.commands.bridge.link.dto.BridgeLinkPortDTO;
import io.github.ganenkovna.ssh.commands.bridge.link.service.BridgeLinkParser;
import java.util.List;
import java.util.Objects;

/**
 * Утилитарный класс для выполнения команды {@code bridge -j link} по SSH
 * и получения информации о портах Linux bridge.
 *
 * <p>Класс инкапсулирует выполнение команды {@code bridge --color=never -j link}
 * через {@link SshChannel} и преобразование JSON-вывода в список DTO
 * {@link BridgeLinkPortDTO} с помощью {@link BridgeLinkParser}.</p>
 *
 * <p>Команда возвращает сведения обо всех интерфейсах, зарегистрированных
 * в подсистеме Linux bridge, включая slave-интерфейсы мостов, их STP-состояние,
 * приоритет и стоимость пути.</p>
 *
 * <p>Класс является утилитарным, не хранит состояния и не предназначен
 * для инстанцирования.</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/bridge.8.html">bridge(8)</a>
 * @see BridgeLinkParser
 * @see BridgeLinkPortDTO
 */
public final class BridgeLink {
  private static final String CMD_BRIDGE_LINK = "bridge --color=never -j link";
  private static final String COMMAND = "bridge -j link";

  /** Запрет инстанцирования. */
  private BridgeLink() {
    throw new AssertionError("No instances");
  }

  /**
   * Выполняет команду {@code bridge -j link} на удалённом хосте по SSH
   * и возвращает список портов Linux bridge.
   *
   * <p>Метод выполняет команду {@code bridge --color=never -j link},
   * ожидает JSON-вывод и передаёт его на разбор в
   * {@link BridgeLinkParser#parseOutput(String)}.</p>
   *
   * <p>Возвращаемый список никогда не {@code null}, но может быть пустым,
   * если в системе отсутствуют bridge-порты или вывод команды не содержит
   * элементов.</p>
   *
   * @param session активная SSH-сессия; строго не {@code null}
   * @return список DTO с информацией о bridge-портах; никогда не {@code null}
   * @throws NullPointerException если {@code session == null}
   * @throws IllegalStateException если команда завершилась с ненулевым кодом,
   *                               если формат кода завершения некорректен
   *                               или если выполнение команды завершилось ошибкой
   * @see BridgeLinkParser#parseOutput(String)
   * @see BridgeLinkPortDTO
   */
  public static List<BridgeLinkPortDTO> showBridgePorts(Session session) {
    Objects.requireNonNull(session, "SSH session не должна быть null");
    SshChannel channel = new SshChannel(session);
    final String[] result = channel.execChannel(CMD_BRIDGE_LINK);

    final int exitCode;
    try {
      exitCode = Integer.parseInt(result[0]);
    } catch (NumberFormatException e) {
      throw new IllegalStateException("Некорректный код завершения команды `"
          + COMMAND + "`: " + result[0], e);
    }

    if (exitCode == 0) {
      return BridgeLinkParser.parseOutput(result[1]);
    } else {
      throw new IllegalStateException("Команда '" + COMMAND + "' не была выполнена успешно"
          + "\nКод завершения: " + exitCode
          + "\nВывод stderr: " + result[2]);
    }
  }
}
