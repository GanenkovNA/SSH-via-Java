package io.github.ganenkovna.util.ip.dto;

import io.github.ganenkovna.util.StringUtils;

/**
 * Область видимости маршрута (route scope) из вывода команды {@code ip route}.
 *
 * <p>Определяет, на каком уровне действует маршрут и где он считается
 * доступным: на локальном хосте, в пределах канала, сайта или глобально.</p>
 *
 * <p>Перед проверкой и преобразованием строкового значения выполняется
 * нормализация в {@link StringUtils#normalizeForEnum(String, String)}.</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/ip-route.8.html">
 *     Документация ip-route(8)</a>
 */
public enum RouteScope {

  /**
   * Маршрут действует только в пределах локального хоста.
   *
   * <p>Используется, например, для loopback-адресов.</p>
   */
  HOST,

  /**
   * Маршрут действует в пределах канального сегмента (link).
   *
   * <p>Обычно применяется для напрямую подключённых сетей.</p>
   */
  LINK,

  /**
   * Маршрут действует в пределах сайта (site-local).
   *
   * <p>Используется редко и в основном в специфических сетевых конфигурациях.</p>
   */
  SITE,

  /**
   * Глобальный маршрут, доступный вне локального сегмента.
   *
   * <p>Наиболее распространённый тип для обычных маршрутов в таблице.</p>
   */
  GLOBAL;

  /**
   * Проверяет существование указанной области видимости маршрутизации.
   *
   * <p>Перед проверкой выполняется нормализация
   * в {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * <p>Возвращает {@code true}, если после нормализации значение найдено;
   * возвращает {@code false}, если {@code input == null}, строка пуста
   * после {@code trim()} или такой области видимости не существует.</p>
   *
   * @param input название области видимости маршрутизации (может быть {@code null})
   * @return {@code true}, если область видимости существует; иначе {@code false}
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static boolean isValid(String input) {
    try {
      RouteScope.valueOf(
          StringUtils.normalizeForEnum(input, "Область видимости маршрутизации"));
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
   * @param input название области видимости маршрутизации; не может быть {@code null} или пустым
   * @return соответствующий элемент перечисления
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после {@code trim()}
   *         или значение не соответствует ни одной области видимости
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static RouteScope getIgnoreCase(String input) {
    input = StringUtils.normalizeForEnum(input, "Область видимости маршрутизации");

    try {
      return RouteScope.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Неизвестная область видимости: " + input);
    }
  }
}
