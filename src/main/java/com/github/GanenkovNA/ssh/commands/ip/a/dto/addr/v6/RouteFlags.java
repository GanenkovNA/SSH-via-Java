package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v6;

import java.util.Arrays;

/**
 * Флаги маршрутизации, используемые для управления поведением сетевых интерфейсов
 * при работе с IPv6 адресами и маршрутами.
 *
 * <p>Определяет специальные параметры обработки маршрутов и адресов.
 */
public enum RouteFlags {

  /** Без специальных флагов. Обычное поведение маршрутизации. */
  NONE,

  /** * Не создавать маршрут для префикса при добавлении адреса. */
  NOPREFIXROUTE,

  /** Отключить проверку дубликатов адресов (Duplicate Address Detection - DAD). */
  NODAD,

  /** Оптимистичный DAD (RFC 4429). Разрешает использование адреса до завершения проверки. */
  OPTIMISTIC,

  /** Автоматически настроенный адрес (Stateless Address Autoconfiguration - SLAAC). */
  AUTOCONF;

  /**
   * Проверяет наличие значения в перечислении по имени (без учета регистра).
   *
   * @param input имя значения для проверки (может быть null)
   * @return {@code true} если перечисление содержит значение с указанным именем,
   *         {@code false} если input равен null или значение не найдено
   */
  public static boolean contains(String input) {
    if (input == null) {
      return false;
    }
    return Arrays.stream(values())
        .anyMatch(e -> e.name().equalsIgnoreCase(input));
  }

  /**
   * Возвращает элемент перечисления по имени без учета регистра.
   *
   * @param input имя значения (без учета регистра)
   * @return соответствующий элемент перечисления
   * @throws IllegalArgumentException если элемент с указанным именем не существует
   * @throws NullPointerException если input равен null
   */
  public static RouteFlags getIgnoreCase(String input) {
    return RouteFlags.valueOf(input.toUpperCase());
  }
}
