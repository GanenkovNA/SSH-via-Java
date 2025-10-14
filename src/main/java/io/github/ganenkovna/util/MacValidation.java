package io.github.ganenkovna.util;

import java.util.Objects;
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
  /** Запрет инстанцирования. */
  private MacValidation() {
    throw new AssertionError("No instances");
  }

  // Регулярное выражение для популярных форматов MAC.
  // 1) Один тип разделителя на весь адрес (':' ИЛИ '-'), через back-reference.
  // 2) Cisco-стиль с точками.
  // 3) Без разделителей (12 hex-символов).
  private static final Pattern MAC_PATTERN = Pattern.compile(
      "^(?:[0-9A-Fa-f]{2}([:-])[0-9A-Fa-f]{2}(?:\\1[0-9A-Fa-f]{2}){4})$"
          + "|^(?:[0-9A-Fa-f]{4}\\.[0-9A-Fa-f]{4}\\.[0-9A-Fa-f]{4})$"
          + "|^(?:[0-9A-Fa-f]{12})$");

  /**
   * Проверяет корректность MAC-адреса.
   *
   * @param mac адрес для проверки; не может быть {@code null}
   * @return {@code true}, если адрес корректен
   * @throws NullPointerException если {@code mac == null}
   * @throws IllegalArgumentException если адрес не соответствует поддерживаемым форматам
   */
  public static boolean validateMac(String mac) {
    Objects.requireNonNull(mac, "MAC-адрес не может быть null");
    if (MAC_PATTERN.matcher(mac).matches()) {
      return true;
    }
    throw new IllegalArgumentException("Неверный формат MAC-адреса: " + mac);
  }
}
