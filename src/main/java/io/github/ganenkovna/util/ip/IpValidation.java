package io.github.ganenkovna.util.ip;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Валидатор IP-адресов.
 *
 * <p>Поддерживаемые форматы:</p>
 * <ul>
 *   <li>IPv4 (RFC 791): {@code 192.168.0.1}</li>
 *   <li>IPv6 полный и сокращённый (RFC 4291 / RFC 5952): {@code 2001:db8::1}, {@code ::1}</li>
 *   <li>IPv4-mapped IPv6: {@code ::ffff:192.168.0.1}</li>
 *   <li>Необязательный zone-suffix (RFC 4007): {@code fe80::1%eth0}</li>
 * </ul>
 *
 * <p>Все методы бросают исключения при невалидных данных (fail-fast).</p>
 *
 * @implNote Для ускорения проверки используются предкомпилированные регулярные выражения.
 */
public final class IpValidation {
  /** Запрет инстанцирования. */
  private IpValidation() {
    throw new AssertionError("No instances");
  }

  private static final Pattern IP4_PATTERN = Pattern.compile(
          "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
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
}
