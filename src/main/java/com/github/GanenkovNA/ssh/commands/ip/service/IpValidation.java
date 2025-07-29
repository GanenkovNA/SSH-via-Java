package com.github.GanenkovNA.ssh.commands.ip.service;

import java.util.regex.Pattern;

/**
 * Валидатор IP-адресов согласно RFC 791 (IPv4) и RFC 4291 (IPv6).
 *
 * <p>Поддерживает все стандартные форматы записи, включая:
 * <ul>
 *   <li>IPv4: "192.168.1.1", "10.0.0.0/8"
 *   <li>IPv6: "2001:db8::1", "fe80::1%eth0"
 *   <li>Смешанные форматы: "::ffff:192.168.1.1"
 * </ul>
 */
public final class IpValidation {
  private static final Pattern IP4_PATTERN = Pattern.compile(
          "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
  private static final Pattern IP6_FULL_PATTERN = Pattern.compile(
      "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
  private static final Pattern IP6_SHORT_PATTERN = Pattern.compile(
      "^(([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4})?::(([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4})?$");
  private static final Pattern IP6_EMBEDDED_PATTERN = Pattern.compile(
      "^::[fF]{4}:(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

  /**
   * Проверяет корректность IPv4-адреса.
   *
   * @param ip адрес для проверки
   * @return true если адрес соответствует формату
   * @throws IllegalArgumentException если ip равен null, пустой строке или имеет неверный формат
   */
  public static boolean validateIpv4(String ip) {
    if (ip == null || ip.isBlank()) {
      throw new IllegalArgumentException("Передан пустой IP");
    } else {
      ip = ip.trim();
      if (IP4_PATTERN.matcher(ip).matches()) {
        return true;
      } else {
        throw new IllegalArgumentException("Неверный формат IP: " + ip);
      }
    }
  }

  /**
   * Проверяет корректность IPv6-адреса.
   *
   * @param ip адрес для проверки
   * @return true если адрес соответствует формату
   * @throws IllegalArgumentException если ip равен null, пустой строке или имеет неверный формат
   */
  public static boolean validateIpv6(String ip) {
    if (ip == null || ip.isBlank()) {
      throw new IllegalArgumentException("Передан пустой IP");
    } else {
      ip = ip.trim();
      if (IP6_FULL_PATTERN.matcher(ip).matches()
          || IP6_SHORT_PATTERN.matcher(ip).matches()
          || IP6_EMBEDDED_PATTERN.matcher(ip).matches()) {
        return true;
      } else {
        throw new IllegalArgumentException("Неверный формат IPv6: " + ip);
      }
    }
  }
}
