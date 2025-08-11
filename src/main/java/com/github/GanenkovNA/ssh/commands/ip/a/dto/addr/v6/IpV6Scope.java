package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v6;

import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v4.IpV4Scope;
import java.util.Arrays;

/**
 * Область видимости IPv6-адреса.
 *
 * <p>Определяет, где действителен адрес (локально, глобально и т.д.). Аналогичен {@link IpV4Scope},
 * но с дополнительными IPv6-специфичными значениями (RFC 4007).
 */
public enum IpV6Scope {
  /** Глобальная маршрутизация (интернет). */
  GLOBAL,

  /** Локальная сеть (L2). */
  LINK,

  /** Только текущий хост (аналог loopback). */
  HOST,

  /**
   * Локальная организация (site-local, устаревшее, RFC 3879).
   *
   * @deprecated Site-local адреса (fec0::/10) удалены из стандарта.
   */
  @Deprecated(since = "RFC 3879 (2004)")
  SITE,

  /**
   * Устаревшее значение (полная маршрутизация).
   *
   * @deprecated заменен на {@link #GLOBAL} в современных реализациях
   */
  @Deprecated
  UNIVERSE,

  /** Временный адрес для автоматической конфигурации. */
  MNGTMPADDR,

  /**
   * Режим совместимости с IPv4 (устаревшее, RFC 4291).
   *
   * <p>Использовался для адресов:
   * <ul>
   *   <li>IPv4-совместимый IPv6 (<code>::/96</code>) — deprecated</li>
   *   <li>IPv4-отображённый IPv6 (<code>::ffff:0:0/96</code>) — всё ещё используется</li>
   * </ul>
   *
   * @deprecated Для совместимости с IPv4 используйте {@link #GLOBAL} или явные преобразования.
   *             IPv4-совместимые адреса (<code>::/96</code>) удалены из стандарта.
   */
  @Deprecated(since = "RFC 4291 (2006)")
  COMPAT,

  /** Динамический адрес (SLAAC). */
  DYNAMIC;

  /**
   * Проверяет наличие значения в перечислении по имени (без учета регистра).
   *
   * @param input значение для проверки (может быть null)
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
  public static IpV6Scope getIgnoreCase(String input) {
    return IpV6Scope.valueOf(input.toUpperCase());
  }
}
