package io.github.ganenkovna.ssh.commands.cat.vlan;

import com.jcraft.jsch.Session;
import io.github.ganenkovna.ssh.client.SshChannel;
import io.github.ganenkovna.ssh.commands.cat.vlan.dto.VlanInterfaceDto;
import io.github.ganenkovna.ssh.commands.cat.vlan.service.VlanConfigParser;
import java.util.List;
import java.util.Objects;

/**
 * Обёртка над вызовом {@code cat /proc/net/vlan/config} по SSH и парсингом результата.
 *
 * <p>Выполняет системную команду на удалённом хосте через {@link SshChannel},
 * получает текстовый вывод и преобразует его в список {@link VlanInterfaceDto}
 * с помощью {@link VlanConfigParser}.</p>
 *
 * <p>Контракт {@link SshChannel#execChannel(String)}: метод возвращает строковый массив
 * как минимум из трёх элементов: {@code [0]=exitCode}, {@code [1]=stdout}, {@code [2]=stderr}.
 * Код завершения {@code 0} трактуется как успешный запуск команды.</p>
 *
 * @see VlanConfigParser
 * @see VlanInterfaceDto
 */
public final class CatVlanConfig {
  private static final String COMMAND = "cat /proc/net/vlan/config";

  /** Запрет инстанцирования. */
  private CatVlanConfig() {
    throw new AssertionError("No instances");
  }

  /**
   * Выполняет {@code cat /proc/net/vlan/config} на удалённом хосте
   * и возвращает список VLAN-интерфейсов.
   *
   * @param session активная SSH-сессия; не {@code null}
   * @return неизменяемый список интерфейсов; никогда не {@code null}, может быть пустым
   * @throws NullPointerException если {@code session == null}
   * @throws IllegalStateException если:
   *     <ul>
   *         <li>ответ от {@link SshChannel#execChannel(String)} некорректен
   *             (меньше 3 элементов);</li>
   *         <li>код завершения команды не равен нулю;</li>
   *         <li>код завершения не удалось распарсить как число.</li>
   *     </ul>
   */
  public static List<VlanInterfaceDto> showVlanConfig(Session session) {
    Objects.requireNonNull(session);
    SshChannel channel = new SshChannel(session);
    final String[] result = channel.execChannel(COMMAND);

    if (result == null || result.length < 3) {
      throw new IllegalStateException(
          "Некорректный ответ от execChannel для `" + COMMAND
              + "`: ожидается [exit, stdout, stderr]");
    }

    final int exitCode;
    try {
      exitCode = Integer.parseInt(result[0]);
    } catch (NumberFormatException e) {
      throw new IllegalStateException("Некорректный код завершения команды `" + COMMAND
          + "`: " + result[0], e);
    }

    if (exitCode == 0) {
      return VlanConfigParser.parseOutput(result[1]);
    } else {
      throw new IllegalStateException("Команда 'ip a' не была успешно выполнена"
          + "\nКод завершения: " + exitCode
          + "\nВывод stderr: " + result[2]);
    }
  }
}
