package io.github.ganenkovna.util.ip;

import static io.github.ganenkovna.util.StringUtils.normalizeForDto;
import static io.github.ganenkovna.util.StringUtils.requireNonBlank;
import io.github.ganenkovna.util.StringUtils;
import java.util.Objects;
import java.util.regex.Pattern;
import org.apache.commons.net.util.SubnetUtils;

/**
 * Утилиты для проверки и анализа IP-адресов.
 *
 * <p>Поддерживает базовые операции над IPv4 и IPv6:
 * <ul>
 *   <li>валидацию форматов (RFC 791, 4291, 5952, 4007);</li>
 *   <li>проверку принадлежности адреса подсети в CIDR-нотации для IPv4;</li>
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
  private static final Pattern IP4_PREFIX_LEN = Pattern.compile("^(?:3[0-2]|[12]?\\d)$");
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
   * Проверяет корректность IPv4-CIDR строки вида {@code A.B.C.D/len}.
   *
   * <p>Выполняются следующие шаги:</p>
   * <ol>
   *   <li>нормализация входной строки через {@link StringUtils#normalizeForDto(String, String)};</li>
   *   <li>проверка структуры {@code ip/prefix};</li>
   *   <li>валидация IPv4-адреса через {@link #validateIpv4(String)};</li>
   *   <li>валидация длины префикса через {@link #validateIpv4Prefix(String)}.</li>
   * </ol>
   *
   * @param cidr CIDR-строка; не может быть {@code null} или пустой
   * @return {@code true}, если строка корректна
   * @throws NullPointerException если {@code cidr == null}
   * @throws IllegalArgumentException если строка не соответствует формату {@code A.B.C.D/len}
   * @see #validateIpv4(String)
   * @see #validateIpv4Prefix(String)
   */
  public static boolean validateIpv4Cidr(String cidr){
    cidr = normalizeForDto(cidr, "CIDR-строка");

    final int slash = cidr.indexOf('/');
    if (slash <= 0 || slash == cidr.length()-1){
      throw new IllegalArgumentException("Ожидается формат A.B.C.D/len: " + cidr);
    }

    final String ip = cidr.substring(0, slash).trim();
    validateIpv4(ip);

    final String len = cidr.substring(slash + 1).trim();
    validateIpv4Prefix(len);

    return true;
  }

  /**
   * Проверяет корректность IPv4-адреса.
   *
   * @param ip IPv4-адрес; не может быть {@code null}
   * @return {@code true}, если строка соответствует формату IPv4
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
   * Проверяет корректность длины IPv4-префикса.
   *
   * <p>Допустимые значения: {@code 0..32}.</p>
   *
   * @param prefix длина префикса; не может быть {@code null}
   * @return {@code true}, если префикс корректен
   * @throws NullPointerException если {@code prefix == null}
   * @throws IllegalArgumentException если значение вне диапазона {@code 0..32}
   */
  public static boolean validateIpv4Prefix(String prefix){
    Objects.requireNonNull(prefix, "IPv4-префикс не может быть null");
    if (!IP4_PREFIX_LEN.matcher(prefix).matches()) {
      throw new IllegalArgumentException("Длина префикса вне диапазона 0..32: " + prefix);
    }
    return true;
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
   * Проверяет, принадлежит ли IPv4-адрес указанной подсети (CIDR v4).
   *
   * <p>Ожидается строка формата {@code A.B.C.D/len}, где {@code len} в диапазоне {@code 0..32}.
   * Метод валидирует как адрес, так и маску префикса и использует {@link SubnetUtils}
   * для вычисления принадлежности. По умолчанию адрес сети и broadcast-адрес
   * считаются <em>не</em>хостами и в диапазон не входят.</p>
   *
   * <p>Пример использования:</p>
   * <pre>{@code
   * boolean result = IpUtils.isIpInSubnet("192.168.10.10", "192.168.10.0/24");
   * // result == true
   * }</pre>
   *
   * @param ip проверяемый IP-адрес; не {@code null}
   * @param cidr подсеть в CIDR-нотации (например, {@code 192.168.10.0/24}); не {@code null}
   * @return {@code true}, если {@code ip} входит в указанную подсеть (без учёта адреса сети и broadcast)
   * @throws NullPointerException если любой аргумент равен {@code null}
   * @throws IllegalArgumentException если {@code ip} или {@code cidr} не соответствуют IPv4-формату
   * @see SubnetUtils
   * @see #validateIpv4(String)
   * @see #validateIpv4Cidr(String)
   */
  public static boolean isIpInSubnet(String ip, String cidr){
    ip = requireNonBlank(ip, "IP-адрес");
    validateIpv4(ip);
    cidr = requireNonBlank(cidr, "Подсеть в CIDR-нотации");
    validateIpv4Cidr(cidr);

    SubnetUtils utils = new SubnetUtils(cidr);
    return utils.getInfo().isInRange(ip);
  }

  /**
   * Проверяет пересечение двух IPv4-подсетей (CIDR v4).
   *
   * <p>Определение пересечения выполняется через сравнение числовых интервалов:
   * вычисляются минимальный/максимальный адреса каждой подсети, после чего
   * интервалы проверяются на пересечение по правилу {@code aLow <= bHigh && bLow <= aHigh}.</p>
   *
   * <p><b>Важно:</b> для корректности сравнения адрес сети и broadcast-адрес
   * считаются как обычные хост-адреса ({@link SubnetUtils#setInclusiveHostCount(boolean)} = {@code true}).</p>
   *
   * <p>Пример:</p>
   * <pre>{@code
   * // true: 192.168.1.0/24 и 192.168.1.128/25 пересекаются
   * boolean r1 = IpUtils.cidrOverlap("192.168.1.0/24", "192.168.1.128/25");
   *
   * // false: непересекающиеся подсети
   * boolean r2 = IpUtils.cidrOverlap("10.0.0.0/24", "10.0.1.0/24");
   * }</pre>
   *
   * @param cidr1 подсеть 1 в виде {@code A.B.C.D/len}; не {@code null}
   * @param cidr2 подсеть 2 в виде {@code A.B.C.D/len}; не {@code null}
   * @return {@code true}, если диапазоны адресов пересекаются; иначе {@code false}
   * @throws NullPointerException если любой аргумент равен {@code null}
   * @throws IllegalArgumentException если любая из строк не соответствует формату IPv4-CIDR
   * @see SubnetUtils
   * @see SubnetUtils#setInclusiveHostCount(boolean)
   * @see #validateIpv4Cidr(String)
   */
  public static boolean cidrOverlap(String cidr1, String cidr2){
    cidr1 = requireNonBlank(cidr1, "Подсеть 1 в CIDR-нотации");
    validateIpv4Cidr(cidr1);
    cidr2 = requireNonBlank(cidr2, "Подсеть 2 в CIDR-нотации");
    validateIpv4Cidr(cidr2);

    SubnetUtils s1 = new SubnetUtils(cidr1);
    SubnetUtils s2 = new SubnetUtils(cidr2);

    //Считаем network address и broadcast address как обычные хосты
    s1.setInclusiveHostCount(true);
    s2.setInclusiveHostCount(true);

    SubnetUtils.SubnetInfo i1 = s1.getInfo();
    SubnetUtils.SubnetInfo i2 = s2.getInfo();

    long aLow = ipToLong(i1.getLowAddress());
    long aHigh = ipToLong(i1.getHighAddress());
    long bLow = ipToLong(i2.getLowAddress());
    long bHigh = ipToLong(i2.getHighAddress());

    return aLow <= bHigh && bLow <= aHigh;
  }

  /**
   * Преобразует IPv4-адрес в беззнаковое 32-битное представление в виде {@code long}.
   *
   * <p>Ожидается нормализованный адрес в точечной записи {@code A.B.C.D}, где каждый октет
   * в диапазоне {@code 0..255}. Старший октет попадает в старшие 8 бит результата.</p>
   *
   * <p>Примеры:</p>
   * <pre>{@code
   * ipToLong("0.0.0.0")       == 0L
   * ipToLong("255.255.255.255") == 4294967295L
   * ipToLong("192.168.1.1")     == 3232235777L
   * }</pre>
   *
   * @param ip IPv4-адрес в виде {@code A.B.C.D}; не {@code null}, корректность формата ожидается «сверху»
   * @return числовое представление адреса как {@code long} в диапазоне {@code 0..4294967295}
   * @throws NumberFormatException если любой из октетов не является десятичным числом
   * @implNote Метод предполагает, что валидность строки уже проверена (например, через {@link #validateIpv4(String)}),
   * и используется внутренне для расчётов диапазонов.
   */
  private static long ipToLong(String ip) {
    long result = 0;
    for (String part : ip.split("\\.")) {
      result = result << 8 | Integer.parseInt(part);
    }
    return result;
  }
}
