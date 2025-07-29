package com.github.GanenkovNA.ssh.commands.ip.service;

import java.util.regex.Pattern;

/**
 * Валидатор MAC-адресов согласно IEEE 802.
 *
 * <p>Поддерживает форматы:
 * <ul>
 *   <li>Стандартный: "00:1a:2b:3c:4d:5e"
 *   <li>Cisco-стиль: "001a.2b3c.4d5e"
 *   <li>Без разделителей: "001a2b3c4d5e"
 * </ul>
 */
public final class MacValidation {
  // Регулярное выражение для стандартных форматов MAC
  private static final Pattern MAC_PATTERN = Pattern.compile(
      "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$"  // С разделителями `:` или `-`
          + "|^([0-9A-Fa-f]{4}\\.[0-9A-Fa-f]{4}\\.[0-9A-Fa-f]{4})$"  // С точками (Cisco)
          + "|^([0-9A-Fa-f]{12})$"  // Без разделителей
  );

  /**
   * Проверяет корректность MAC-адреса.
   *
   * @param mac адрес для проверки
   * @throws IllegalArgumentException если mac не соответствует формату
   */
  public static boolean validateMac(String mac) {
    if (mac == null || mac.isBlank()) {
      throw new IllegalArgumentException("Передан пустой MAC");
    } else {
      mac = mac.trim();
      if (MAC_PATTERN.matcher(mac).matches()) {
        return true;
      } else {
        throw new IllegalArgumentException("Неверный формат MAC: " + mac);
      }
    }
  }
}
