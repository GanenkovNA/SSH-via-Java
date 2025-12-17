package io.github.ganenkovna.util.ip.dto;

import io.github.ganenkovna.util.StringUtils;

/**
 * Тип маршрута из вывода команды {@code ip route}.
 *
 * <p>Соответствует полю {@code type} в JSON-выводе команды {@code ip -j route}.
 * Определяет семантику обработки пакетов для данного маршрута
 * (доставка, запрет, перенаправление, отбрасывание и т.д.).</p>
 *
 * <p>Перед проверкой и преобразованием строкового значения выполняется
 * нормализация в {@link StringUtils#normalizeForEnum(String, String)}.</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/ip-route.8.html">ip-route(8)</a>
 */
public enum RouteType {

  /** Обычный маршрут доставки пакетов. */
  UNICAST,

  /** Локальный маршрут (адрес принадлежит текущему хосту). */
  LOCAL,

  /** Широковещательный маршрут. */
  BROADCAST,

  /** Многоадресный маршрут. */
  MULTICAST,

  /** Anycast-маршрут. */
  ANYCAST,

  /** Маршрут с немедленным отбрасыванием пакетов. */
  BLACKHOLE,

  /** Маршрут, возвращающий ошибку «узел недостижим». */
  UNREACHABLE,

  /** Маршрут, запрещающий пересылку пакетов. */
  PROHIBIT,

  /** Маршрут, принудительно передающий управление следующему правилу. */
  THROW,

  /** Маршрут с трансляцией сетевых адресов (NAT). */
  NAT;

  /**
   * Проверяет существование указанного типа маршрута.
   *
   * <p>Перед проверкой выполняется нормализация
   * в {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * <p>Возвращает {@code true}, если после нормализации значение найдено;
   * возвращает {@code false}, если {@code input == null}, строка пуста
   * после {@code trim()} или такой тип маршрута не существует.</p>
   *
   * @param input название типа маршрута (может быть {@code null})
   * @return {@code true}, если тип маршрута существует; иначе {@code false}
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static boolean isValid(String input) {
    try {
      RouteType.valueOf(
          StringUtils.normalizeForEnum(input, "Тип маршрута"));
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
   * @param input название типа маршрута; не может быть {@code null} или пустым
   * @return соответствующий элемент перечисления
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после {@code trim()}
   *         или значение не соответствует ни одному типу маршрута
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static RouteType getIgnoreCase(String input) {
    input = StringUtils.normalizeForEnum(input, "Тип маршрута");

    try {
      return RouteType.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Неизвестный тип маршрута: " + input);
    }
  }
}
