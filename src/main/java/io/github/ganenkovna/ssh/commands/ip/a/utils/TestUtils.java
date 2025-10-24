package io.github.ganenkovna.ssh.commands.ip.a.utils;

import static io.github.ganenkovna.util.StringUtils.normalizeForDto;

import com.jcraft.jsch.Session;
import io.github.ganenkovna.ssh.commands.ip.a.IpA;
import io.github.ganenkovna.ssh.commands.ip.a.dto.InterfaceDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v4.InterfaceIpv4ConfigDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v6.InterfaceIpv6ConfigDto;
import java.util.List;
import java.util.Objects;

/**
 * Вспомогательные методы для smoke-тестов командного вывода {@code ip a}.
 *
 * <p>Предназначен исключительно для тестовых сценариев — не используется в продуктивном коде.
 * Все методы статические, состояние отсутствует. С помощью этих утилит можно выполнять
 * базовые проверки корректности данных, полученных через {@link IpA#showInterfaces(Session)},
 * без написания дублирующего кода в тестах.</p>
 *
 * <p>Типичные сценарии использования:</p>
 * <ul>
 *   <li>проверить, что интерфейс имеет определённый IPv4/IPv6-адрес;</li>
 *   <li>в дальнейшем — убедиться, что интерфейс присутствует/отсутствует в списке;</li>
 *   <li>проверить MTU, MAC-адрес или флаги интерфейса (при расширении API).</li>
 * </ul>
 *
 * <p>Все методы требуют активной SSH-сессии и используют нормализацию аргументов
 * через {@link io.github.ganenkovna.util.StringUtils#normalizeForDto(String, String)}.</p>
 *
 * @see InterfaceDto
 * @see InterfaceIpv4ConfigDto
 * @see InterfaceIpv6ConfigDto
 */
public final class TestUtils {
  /** Запрет инстанцирования. */
  private TestUtils() {
    throw new AssertionError("No instances");
  }

  /**
   * Проверяет, имеет ли указанный интерфейс заданный IP-адрес
   * (включая IPv4 и IPv6) в рамках активной SSH-сессии.
   *
   * <p>Выполняется поиск по нормализованному имени интерфейса
   * и точное сравнение строкового представления адреса {@code address/prefix}.</p>
   *
   * @param session активная SSH-сессия; строго не {@code null}
   * @param interfaceName имя интерфейса; не {@code null}, не пустое/пробельное
   * @param ipAddress IP-адрес в формате {@code address/prefix}; не {@code null}, не пустой/пробельный
   * @return {@code true}, если интерфейс найден и содержит указанный IP-адрес;
   *         {@code false}, если интерфейс отсутствует или адрес не найден
   * @throws NullPointerException если любой из аргументов равен {@code null}
   * @throws IllegalArgumentException если {@code interfaceName} или {@code ipAddress}
   *         пустой/пробельный (см. {@link io.github.ganenkovna.util.StringUtils#normalizeForDto(String, String)})
   * @see IpA#showInterfaces(Session)
   */
  public static boolean hasInterfaceIpAddress(Session session, String interfaceName, String ipAddress) {
    Objects.requireNonNull(session);
    interfaceName = normalizeForDto(interfaceName, "Имя интерфейса");
    ipAddress = normalizeForDto(ipAddress, "IP-адрес");

    List<InterfaceDto> interfaces = IpA.showInterfaces(session);

    for (InterfaceDto interfaceDto : interfaces) {
      // находим нужный интерфейс
      if (interfaceName.equalsIgnoreCase(
          interfaceDto.getInterfaceParams().getName())) {
        // проходимся по IPv4
        for (InterfaceIpv4ConfigDto ipv4ConfigDto : interfaceDto.getIpv4()) {
          String interfaceIpv4Address = ipv4ConfigDto.getAddress()
              + "/" + ipv4ConfigDto.getPrefix();
          if (ipAddress.equals(interfaceIpv4Address)) {
            return true;
          }
        }
        // проходимся по IPv6
        for (InterfaceIpv6ConfigDto ipv6ConfigDto : interfaceDto.getIpv6()) {
          String interfaceIpv6Address = ipv6ConfigDto.getAddress()
              + "/" + ipv6ConfigDto.getPrefix();
          if (ipAddress.equals(interfaceIpv6Address)) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
