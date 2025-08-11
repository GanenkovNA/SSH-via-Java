package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v6;

import com.github.GanenkovNA.service.StringUtils;

/**
 * Флаги, указывающие способ генерации IPv6-адреса.
 *
 * <p>Определяют, каким методом был создан адрес/
 * Соответствуют RFC 4291 (EUI-64), 4941 (временные адреса) и 7217 (стабильные приватные адреса).
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
   * Проверяет, является ли строка допустимым значением флага генерации IPv6.
   *
   * <p>Метод выполняет нормализацию входной строки:
   * <ol>
   *   <li>Приводит к верхнему регистру</li>
   *   <li>Удаляет лишние пробелы</li>
   *   <li>Заменяет специальные символы</li>
   * </ol>
   *
   * @param input строка для проверки (может быть null)
   * @return true если строка соответствует одному из значений enum после нормализации,
   *         false если:
   *         - input == null
   *         - input пустая строка
   *         - значение не найдено
   * @see StringUtils#normalizeForEnum
   */
  public static boolean isValid(String input) {
    try {
      GenerationFlags.valueOf(
          StringUtils.normalizeForEnum(input, "", ""));
      return true;
    } catch (NullPointerException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Возвращает элемент enum по строковому представлению (без учета регистра).
   *
   * <p>Перед поиском выполняет нормализацию строки:
   * <ol>
   *   <li>Проверяет на null и пустую строку</li>
   *   <li>Приводит к верхнему регистру</li>
   *   <li>Удаляет лишние пробелы и специальные символы</li>
   * </ol>
   *
   * @param input название флага генерации (без учета регистра)
   * @return соответствующий элемент enum
   * @throws NullPointerException если input == null
   * @throws IllegalArgumentException если input пустая строка
   * @throws RuntimeException если значение не найдено после нормализации
   * @see StringUtils#normalizeForEnum
   */
  public static GenerationFlags getIgnoreCase(String input)
      throws IllegalArgumentException, NullPointerException {
    input = StringUtils.normalizeForEnum(input,
        "Значение флага генерации IPv6 не может быть null",
        "Значение флага генерации IPv6 не может быть пустым");

    try {
      return GenerationFlags.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Значение флага генерации IPv6 не найдено: " + input);
    }
  }
}
