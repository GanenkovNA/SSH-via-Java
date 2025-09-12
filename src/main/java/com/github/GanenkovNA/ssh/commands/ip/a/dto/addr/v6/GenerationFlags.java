package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v6;

import com.github.GanenkovNA.service.StringUtils;

/**
 * Флаги, указывающие способ генерации IPv6-адреса.
 *
 * <p></p>Флаги, указывающие способ генерации IPv6-адреса (как в выводе {@code ip -6 addr}).</p>
 *
 * <p>Определяют, каким методом был создан адрес. Соответствуют:
 * <ul>
 *   <li>RFC 4291 — EUI-64</li>
 *   <li>RFC 4941 — временные адреса</li>
 *   <li>RFC 7217 — стабильные приватные адреса</li>
 * </ul>
 */
public enum GenerationFlags {

  /** Без специальных флагов генерации. */
  NONE,

  /** Адрес сгенерирован из MAC-адреса (формат Modified EUI-64, RFC 4291). */
  EUI64,

  /**
   * Временный адрес (RFC 4941).
   *
   * <p>Используется для защиты приватности. Меняется со временем.
   */
  TEMPORARY,

  /**
   * Стабильный приватный адрес (RFC 7217).
   *
   * <p>Генерируется на основе хеша (устойчив, но не раскрывает MAC-адрес).
   */
  STABLE_PRIVACY,

  /** Адрес, полученный автоматически (через SLAAC или DHCPv6). */
  DYNAMIC,

  /** Адрес, добавленный вручную администратором. */
  MANUAL;

  /**
   * Проверяет валидность строкового представления флага генерации IPv6.
   *
   * <p>Перед проверкой выполняется нормализация в {@link StringUtils#normalizeForEnum(String)}.</p>
   *
   * <p>Возвращает {@code true}, если после нормализации значение найдено;
   * возвращает {@code false}, если {@code input == null}, строка пустая после trim()
   * или такой области не существует.</p>
   *
   * @param input название флага генерации (может быть {@code null})
   * @return {@code true}, если значение найдено; иначе {@code false}
   * @see StringUtils#normalizeForEnum(String)
   */
  public static boolean isValid(String input) {
    try {
      GenerationFlags.valueOf(
          StringUtils.normalizeForEnum(input));
      return true;
    } catch (NullPointerException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Возвращает элемент перечисления по имени, игнорируя регистр и дефисы.
   *
   * <p>Нормализация идентична {@link StringUtils#normalizeForEnum(String)}.</p>
   *
   * @param input название флага генерации (без учета регистра)
   * @return соответствующий элемент перечисления
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после trim() или значение не найдено
   * @see StringUtils#normalizeForEnum(String, String, String)
   */
  public static GenerationFlags getIgnoreCase(String input) {
    input = StringUtils.normalizeForEnum(input,
        "Значение флага генерации IPv6 не может быть null",
        "Значение флага генерации IPv6 не может быть пустым");

    try {
      return GenerationFlags.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Значение флага генерации IPv6 не найдено: " + input);
    }
  }
}
