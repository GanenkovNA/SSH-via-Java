package io.github.ganenkovna.util.ip.dto;

import io.github.ganenkovna.util.StringUtils;

/**
 * Режим генерации IPv6-адреса интерфейса.
 *
 * <p>Соответствует параметру {@code inet6_addr_gen_mode}, используемому в выводе
 * утилиты {@code ip link}.</p>
 *
 * <p>Определяет алгоритм автоматической генерации IPv6-адресов
 * для сетевого интерфейса.</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/ip-link.8.html">
 *      ip-link(8)</a>
 */
public enum Inet6AddrGenMode {

  /** Генерация IPv6-адреса на основе EUI-64. */
  EUI64,

  /** Автоматическая генерация IPv6-адреса отключена. */
  NONE,

  /** Стабильный IPv6-адрес, вычисляемый на основе secret. */
  STABLE_SECRET,

  /** Случайная генерация IPv6-адреса. */
  RANDOM;

  /**
   * Проверяет существование указанного режима генерации IPv6-адреса.
   *
   * <p>Перед проверкой выполняется нормализация входной строки
   * в {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * <p>Метод не выбрасывает исключений.</p>
   *
   * @param input имя режима генерации IPv6-адреса; может быть {@code null}
   * @return {@code true}, если после нормализации значение соответствует элементу
   *         перечисления; иначе {@code false}
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static boolean isValid(String input) {
    try {
      Inet6AddrGenMode.valueOf(
          StringUtils.normalizeForEnum(input, "Режим генерации IPv6-адреса"));
      return true;
    } catch (NullPointerException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Возвращает режим генерации IPv6-адреса по имени,
   * игнорируя регистр символов и дефисы.
   *
   * <p>Нормализация идентична {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * @param input имя режима генерации IPv6-адреса; не может быть {@code null}
   * @return соответствующий элемент перечисления
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после {@code trim()}
   *                                  или режим не найден
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static Inet6AddrGenMode getIgnoreCase(String input) {
    input = StringUtils.normalizeForEnum(input, "Режим генерации IPv6-адреса");

    try {
      return Inet6AddrGenMode.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Режим генерации IPv6-адреса не найден: " + input);
    }
  }
}
