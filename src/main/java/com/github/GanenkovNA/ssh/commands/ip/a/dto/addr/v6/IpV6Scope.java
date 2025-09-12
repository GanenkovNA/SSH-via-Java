package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v6;

import com.github.GanenkovNA.service.StringUtils;

/**
 * Область видимости и флаги состояния IPv6-адреса (как в выводе {@code ip -6 addr}).
 *
 * <p>Содержит как значения "scope" по RFC 4007 (GLOBAL, LINK, HOST, SITE),
 * так и практические флаги адресов, которые выводит iproute2
 * ({@code dynamic}, {@code mngtmpaddr}, {@code deprecated}, {@code tentative}, и т.д.).</p>
 *
 * <p>Таким образом, перечисление совмещает стандартные области видимости и
 * дополнительные статусные метки адреса для удобства парсинга.</p>
 *
 * @implNote Флаги появляются сразу после блока {@code scope ...} в выводе iproute2.
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
   * Значение из старых версий iproute2, больше не используется для IPv6-адресов.
   *
   * @deprecated заменен на {@link #GLOBAL} в современных реализациях
   */
  @Deprecated(since = "legacy IPv6 scopes (pre-RFC 4007)")
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
   * Проверяет существование указанной области видимости IPv6.
   *
   * <p>Перед проверкой выполняется нормализация в {@link StringUtils#normalizeForEnum(String)}.</p>
   *
   * <p>Возвращает {@code true}, если после нормализации значение найдено;
   * возвращает {@code false}, если {@code input == null}, строка пустая после trim()
   * или такой области не существует.</p>
   *
   * @param input название области видимости (может быть {@code null})
   * @return {@code true}, если область существует; иначе {@code false}
   * @see StringUtils#normalizeForEnum(String)
   */
  public static boolean isValid(String input) {
    try {
        IpV6Scope.valueOf(
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
   * @param input название области видимости; не может быть {@code null} или пустым
   * @return соответствующий элемент перечисления
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после trim() или значение не найдено
   * @see StringUtils#normalizeForEnum(String, String, String)
   */
  public static IpV6Scope getIgnoreCase(String input) {
    input = StringUtils.normalizeForEnum(input,
            "Значение области видимости IPv6-адреса не может быть null",
            "Значение области видимости IPv6-адреса не может быть пустым");

    try {
        return IpV6Scope.valueOf(input);
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Значение области видимости IPv6-адреса не найдено: " + input);
    }
  }
}
