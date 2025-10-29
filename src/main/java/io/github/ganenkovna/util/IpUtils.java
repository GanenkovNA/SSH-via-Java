package io.github.ganenkovna.util;

import java.util.Objects;
import java.util.regex.Pattern;
import org.apache.commons.net.util.SubnetUtils;

/**
 * Утилиты для проверки и анализа IP-адресов.
 *
 * <p>Поддерживает базовые операции над IPv4 и IPv6:
 * <ul>
 *   <li>валидацию форматов (RFC 791, 4291, 5952, 4007);</li>
 *   <li>проверку принадлежности адреса подсети в CIDR-нотации;</li>
 *   <li>распознавание zone-suffix для link-local IPv6-адресов.</li>
 * </ul>
 *
 * <p>Все методы выполняют проверку аргументов (fail-fast) и бросают исключения при ошибках
 * формата или {@code null}-значениях.</p>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc791">RFC 791 — Internet Protocol (IPv4)</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc4291">RFC 4291 — IPv6 Addressing Architecture</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc5952">RFC 5952 — IPv6 Text Representation</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc4007">RFC 4007 — IPv6 Scoped Address Architecture</a>
 */
public final class IpUtils {
  /** Запрет инстанцирования. */
  private IpUtils() {
    throw new AssertionError("No instances");
  }

  // === IPv4 ===
  private static final Pattern IP4_PATTERN = Pattern.compile(
          "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
  // === IPv6 ===
  private static final Pattern IP6_FULL_PATTERN = Pattern.compile(
      "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
  private static final Pattern IP6_SHORT_PATTERN = Pattern.compile(
      "^(([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4})?::(([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4})?$");
  private static final Pattern IP6_EMBEDDED_PATTERN = Pattern.compile(
      "^::[fF]{4}:(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
  // Допустимый zone-suffix (RFC 4007): латиница/цифры/._:-  — пример: %eth0, %enp0s3
  private static final Pattern IPV6_ZONE_SUFFIX = Pattern.compile("%[\\w.:-]+$");

  /**
   * Проверяет корректность IPv4-адреса.
   *
   * @param ip адрес для проверки; не может быть {@code null}
   * @return {@code true}, если адрес соответствует формату IPv4
   * @throws NullPointerException если {@code ip == null}
   * @throws IllegalArgumentException если адрес не соответствует формату IPv4
   * @see <a href="https://www.rfc-editor.org/rfc/rfc791">RFC 791</a>
   */
  public static boolean validateIpv4(String ip) {
    Objects.requireNonNull(ip, "IPv4-адрес не может быть null");
    if (IP4_PATTERN.matcher(ip).matches()) {
        return true;
    }
    throw new IllegalArgumentException("Неверный формат IPv4-адреса: " + ip);
  }

  /**
   * Проверяет корректность IPv6-адреса.
   *
   * <p>Поддерживаются:</p>
   * <ul>
   *   <li>полный и сокращённый IPv6 (RFC 4291 / RFC 5952)</li>
   *   <li>IPv4-mapped IPv6</li>
   *   <li>необязательный zone-suffix (RFC 4007)</li>
   * </ul>
   *
   * @param ip адрес для проверки; не может быть {@code null}
   * @return {@code true}, если адрес соответствует одному из поддерживаемых форматов
   * @throws NullPointerException если {@code ip == null}
   * @throws IllegalArgumentException если адрес не соответствует формату IPv6
   * @see <a href="https://www.rfc-editor.org/rfc/rfc4291">RFC 4291</a>
   * @see <a href="https://www.rfc-editor.org/rfc/rfc5952">RFC 5952</a>
   * @see <a href="https://www.rfc-editor.org/rfc/rfc4007">RFC 4007</a>
   */
  public static boolean validateIpv6(String ip) {
    Objects.requireNonNull(ip, "IPv6-адрес не может быть null");

    int zoneIdx = ip.indexOf('%');
    if (zoneIdx >= 0){
      if (!IPV6_ZONE_SUFFIX.matcher(ip.substring(zoneIdx)).find()) {
          throw new IllegalArgumentException("Неверный zone-suffix у IPv6-адреса: " + ip);
        }
      ip = ip.substring(0, zoneIdx);
    }

    if (IP6_FULL_PATTERN.matcher(ip).matches()
        || IP6_SHORT_PATTERN.matcher(ip).matches()
        || IP6_EMBEDDED_PATTERN.matcher(ip).matches()) {
      return true;
    }
    throw new IllegalArgumentException("Неверный формат IPv6-адреса: " + ip);
  }

  /**
   * Проверяет, принадлежит ли IP-адрес указанной подсети в CIDR-нотации.
   *
   * <p>Пример использования:</p>
   * <pre>{@code
   * boolean result = IpUtils.isIpInSubnet("192.168.10.10", "192.168.10.0/24");
   * // result == true
   * }</pre>
   *
   * @param ip проверяемый IP-адрес; не {@code null}
   * @param cidr подсеть в CIDR-нотации (например {@code 192.168.10.0/24}); не {@code null}
   * @return {@code true}, если {@code ip} входит в указанную подсеть
   * @throws NullPointerException если любой аргумент равен {@code null}
   * @throws IllegalArgumentException если {@code cidr} не соответствует формату CIDR
   * @see <a href="https://www.rfc-editor.org/rfc/rfc4632">RFC 4632 — CIDR for IPv4</a>
   * @see <a href="https://www.rfc-editor.org/rfc/rfc4291">RFC 4291 — IPv6 Addressing</a>
   */
  public static boolean isIpInSubnet(String ip, String cidr){
    Objects.requireNonNull(ip, "IP-адрес не может быть null");
    Objects.requireNonNull(cidr, "CIDR-подсеть не может быть null");
    SubnetUtils utils = new SubnetUtils(cidr);
    return utils.getInfo().isInRange(ip);
  }
}
