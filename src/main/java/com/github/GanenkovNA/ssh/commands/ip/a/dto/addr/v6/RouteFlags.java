package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v6;

import com.github.GanenkovNA.service.StringUtils;

/**
 * Флаги конфигурации/маршрутизации из вывода {@code ip -6 addr} для IPv6-адресов.
 *
 * <p>Используются iproute2 для указания особенностей обработки адресов и связанных с ними маршрутов
 * (например, {@code noprefixroute}, {@code nodad}, {@code optimistic}, {@code autoconf}).</p>
 */
public enum RouteFlags {

  /**
   * Без специальных флагов. Обычное поведение маршрутизации.
   */
  NONE,

  /**
   * Не создавать маршрут для префикса при добавлении адреса.
   */
  NOPREFIXROUTE,

  /**
   * Отключить проверку дубликатов адресов (Duplicate Address Detection - DAD).
   */
  NODAD,

  /**
   * Оптимистичный DAD (RFC 4429). Разрешает использование адреса до завершения проверки.
   */
  OPTIMISTIC,

  /**
   * Автоматически настроенный адрес (Stateless Address Autoconfiguration - SLAAC).
   */
  AUTOCONF;

  /**
   * Проверяет существование флага маршрутизации.
   *
   * <p>Перед проверкой выполняется нормализация в {@link StringUtils#normalizeForEnum(String)}.</p>
   *
   * <p>Возвращает {@code true}, если после нормализации значение найдено;
   * возвращает {@code false}, если {@code input == null}, строка пустая после trim()
   * или такого флага не существует.</p>
   *
   * @param input название флага маршрутизации (может быть {@code null})
   * @return {@code true}, если флаг существует; иначе {@code false}
   * @see StringUtils#normalizeForEnum(String)
   */
  public static boolean isValid(String input) {
    try {
      RouteFlags.valueOf(
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
   * @param input название флага маршрутизации; не может быть {@code null} или пустым
   * @return соответствующий элемент перечисления
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после trim() или значение не найдено
   * @see StringUtils#normalizeForEnum(String, String, String)
   */
  public static RouteFlags getIgnoreCase(String input) {
    input = StringUtils.normalizeForEnum(input,
        "Значение флага маршрутизации IPv6 не может быть null",
        "Значение флага маршрутизации IPv6 не может быть пустым");

    try{
      return RouteFlags.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Значение флага маршрутизации IPv6 не найдено: " + input);
    }
  }
}
