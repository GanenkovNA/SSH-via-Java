package com.github.GanenkovNA.ssh.commands.ip.a;

import com.github.GanenkovNA.ssh.client.SshChannel;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.InterfaceDto;
import com.github.GanenkovNA.ssh.commands.ip.a.service.IpAParser;
import com.jcraft.jsch.Session;
import java.util.List;

/**
 * Основной класс для выполнения команды `ip a` через SSH и парсинга её вывода.
 *
 * <p>Пример использования:
 * <pre>{@code
 * Session session = ... // активная SSH-сессия
 * List<InterfaceDto> interfaces = IpA.showInterfaces(session);
 * }
 * </pre>
 */
public class IpA {

  /**
   * Выполняет команду `ip a` на удаленном сервере и возвращает структурированные данные.
   *
   * @param session активная SSH-сессия
   * @return список сетевых интерфейсов
   * @throws RuntimeException если команда завершилась с ошибкой
   */
  public static List<InterfaceDto> showInterfaces(Session session) {
    SshChannel channel = new SshChannel(session);

    String[] result = channel.execChannel("ip a");

    if (result[0].equals(Integer.toString(0))) {
      return IpAParser.parse(result[1]);
    } else {
      throw new RuntimeException("Команда 'ip a' не была успешно выполнена"
          + "\nКод завершения: " + result[0]
          + "\nВывод stderr: " + result[2]);
    }
  }
}
