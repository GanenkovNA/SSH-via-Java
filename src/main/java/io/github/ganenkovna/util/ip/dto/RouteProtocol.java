package io.github.ganenkovna.util.ip.dto;

import io.github.ganenkovna.util.StringUtils;

/**
 * Протокол источника маршрута, указанный в выводе команды {@code ip -j route}.
 *
 * <p>Соответствует полю {@code protocol} (или {@code proto}) в JSON-выводе
 * утилиты {@code ip route}. Определяет источник добавления маршрута
 * в таблицу маршрутизации Linux.</p>
 *
 * <p>Перед проверкой и преобразованием входных строк выполняется нормализация
 * в {@link StringUtils#normalizeForEnum(String, String)}:
 * приведение к верхнему регистру, удаление пробелов и замена дефисов
 * на подчёркивания.</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/ip-route.8.html">ip-route(8)</a>
 */
public enum RouteProtocol {

  /** Маршрут, добавленный ядром автоматически. */
  KERNEL,

  /** Маршрут, добавленный при загрузке системы. */
  BOOT,

  /** Статически сконфигурированный маршрут. */
  STATIC,

  /** Маршрут, полученный через DHCP. */
  DHCP,

  /** Маршрут, полученный через IPv6 Router Advertisement (RA). */
  RA,

  /** Маршрут, созданный как результат перенаправления (redirect). */
  REDIRECT,

  /** Маршрут, полученный по протоколу BGP. */
  BGP,

  /** Маршрут, полученный по протоколу OSPF. */
  OSPF,

  /** Маршрут, полученный по протоколу RIP. */
  RIP;

  /**
   * Проверяет существование указанного протокола источника маршрута.
   *
   * <p>Перед проверкой выполняется нормализация
   * в {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * <p>Возвращает {@code true}, если после нормализации значение найдено;
   * возвращает {@code false}, если {@code input == null}, строка пуста
   * после {@code trim()} или такой протокол не существует.</p>
   *
   * @param input название протокола источника маршрута (может быть {@code null})
   * @return {@code true}, если протокол существует; иначе {@code false}
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static boolean isValid(String input) {
    try {
      RouteProtocol.valueOf(
          StringUtils.normalizeForEnum(input, "Протокол источника маршрута"));
      return true;
    } catch (NullPointerException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Возвращает элемент перечисления по имени, игнорируя регистр и дефисы.
   *
   * <p>Нормализация идентична {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * @param input название протокола источника маршрута; не может быть {@code null} или пустым
   * @return соответствующий элемент перечисления
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после trim() или значение не найдено
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static RouteProtocol getIgnoreCase(String input) {
    input = StringUtils.normalizeForEnum(input, "Протокол источника маршрута");

    try {
      return RouteProtocol.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Неизвестный протокол источника маршрута: " + input);
    }
  }
}
