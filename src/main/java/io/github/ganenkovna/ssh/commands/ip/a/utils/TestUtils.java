package io.github.ganenkovna.ssh.commands.ip.a.utils;

import static io.github.ganenkovna.util.StringUtils.normalizeForDto;

import com.jcraft.jsch.Session;
import io.github.ganenkovna.ssh.commands.ip.a.IpA;
import io.github.ganenkovna.ssh.commands.ip.a.dto.InterfaceDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v4.InterfaceIpv4ConfigDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v6.InterfaceIpv6ConfigDto;
import io.github.ganenkovna.util.StringUtils;
import io.github.ganenkovna.util.ip.VlanValidation;
import java.util.List;
import java.util.Objects;

/**
 * Вспомогательные методы для smoke-тестов вывода {@code ip a}.
 *
 * <p>Класс предназначен исключительно для тестов; в продуктивном коде не используется.
 * Все методы статические, состояние отсутствует. Позволяет выполнять базовые проверки
 * данных, полученных через {@link IpA#showInterfaces(Session)}, без дублирования кода
 * в тестах.</p>
 *
 * <p>Принципы работы:</p>
 * <ul>
 *   <li>все строковые аргументы нормализуются через
 *   {@link StringUtils#normalizeForDto(String, String)};</li>
 *   <li>по имени интерфейса выполняется сравнение без учёта регистра
 *   (см. {@link #interfaceExists(Session, String)});</li>
 *   <li>коллекции из DTO считаются «никогда не {@code null}, могут быть пустыми».</li>
 * </ul>
 */
public final class TestUtils {
  /** Запрет инстанцирования. */
  private TestUtils() {
    throw new AssertionError("No instances");
  }

  /**
   * Проверяет существование интерфейса по имени.
   *
   * <p>Сравнение имени выполняется без учёта регистра.</p>
   *
   * @param session активная SSH-сессия; строго не {@code null}
   * @param interfaceName имя интерфейса; не {@code null}, не пустое/пробельное
   * @return {@code true}, если интерфейс найден; иначе {@code false}
   * @throws NullPointerException если {@code session} равна {@code null}
   * @throws IllegalArgumentException если {@code interfaceName} пустой/пробельный
   * @see IpA#showInterfaces(Session)
   */
  public static boolean interfaceExists(Session session, String interfaceName){
    Objects.requireNonNull(session);
    interfaceName = normalizeForDto(interfaceName, "Название интерфейса");

    List<InterfaceDto> interfaces = IpA.showInterfaces(session);
    for (InterfaceDto it : interfaces){
      if (interfaceName.equalsIgnoreCase(
          it.getInterfaceParams().getName())){
        return true;
      }
    }
    return false;
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
   *         пустой/пробельный (см. {@link StringUtils#normalizeForDto(String, String)})
   * @see IpA#showInterfaces(Session)
   */
  public static boolean hasInterfaceIpAddress(Session session, String interfaceName, String ipAddress) {
    Objects.requireNonNull(session);
    interfaceName = normalizeForDto(interfaceName, "Название интерфейса");
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

  /**
   * Проверяет существование VLAN-интерфейса вида {@code iface.vlan@iface}.
   *
   * @param session активная SSH-сессия; строго не {@code null}
   * @param interfaceName базовое имя интерфейса; не {@code null}, не пустое/пробельное
   * @param vlanId VLAN ID; допустимый диапазон {@code 1..4094}
   * @return {@code true}, если VLAN-интерфейс найден; иначе {@code false}
   * @throws NullPointerException если {@code session} равна {@code null}
   * @throws IllegalArgumentException если {@code interfaceName} пустой/пробельный
   *                                  или {@code vlanId} вне диапазона
   * @see #getVlanInterfaceName(String, int)
   * @see VlanValidation#validateVlanId(int)
   * @see IpA#showInterfaces(Session)
   */
  public static boolean vlanInterfaceExists(Session session, String interfaceName, int vlanId){
    Objects.requireNonNull(session);
    String vlanInterfaceName = getVlanInterfaceName(interfaceName, vlanId);

    return interfaceExists(session, vlanInterfaceName);
  }

  /**
   * Проверяет, имеет ли VLAN-интерфейс вида {@code iface.vlan@iface} указанный IP-адрес.
   *
   * @param session активная SSH-сессия; строго не {@code null}
   * @param interfaceName базовое имя интерфейса; не {@code null}, не пустое/пробельное
   * @param vlanId VLAN ID; допустимый диапазон {@code 1..4094}
   * @param ipAddress IP-адрес в формате {@code address/prefix}; не {@code null}, не пустой/пробельный
   * @return {@code true}, если VLAN-интерфейс найден и содержит IP-адрес; иначе {@code false}
   * @throws NullPointerException если любой из аргументов равен {@code null}
   * @throws IllegalArgumentException если строковые аргументы пустые/пробельные
   *                                  или {@code vlanId} вне диапазона
   * @see #getVlanInterfaceName(String, int)
   * @see IpA#showInterfaces(Session)
   */
  public static boolean hasVlanInterfaceIpAddress(Session session, String interfaceName, int vlanId, String ipAddress){
    Objects.requireNonNull(session);
    String vlanInterfaceName = getVlanInterfaceName(interfaceName, vlanId);

    return hasInterfaceIpAddress(session, vlanInterfaceName, ipAddress);
  }

  /**
   * Формирует имя VLAN-интерфейса в виде {@code iface.vlan@iface} и валидирует входные параметры.
   *
   * <p>Имя базового интерфейса нормализуется, VLAN ID валидируется по IEEE 802.1Q.</p>
   *
   * @param interfaceName базовое имя интерфейса; не {@code null}, не пустое/пробельное
   * @param vlanId VLAN ID; допустимый диапазон {@code 1..4094}
   * @return корректно сформированное имя VLAN-интерфейса; никогда не {@code null}
   * @throws IllegalArgumentException если {@code interfaceName} пустой/пробельный
   *                                  или {@code vlanId} вне диапазона
   * @see VlanValidation#validateVlanId(int)
   */
  public static String getVlanInterfaceName(String interfaceName, int vlanId){
     interfaceName = normalizeForDto(interfaceName, "Название интерфейса");
     VlanValidation.validateVlanId(vlanId);
     return interfaceName + "." + vlanId + "@" + interfaceName;
  }
}
