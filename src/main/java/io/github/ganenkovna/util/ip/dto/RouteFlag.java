package io.github.ganenkovna.util.ip.dto;

import io.github.ganenkovna.util.StringUtils;

/**
 * Флаг маршрута из вывода команды {@code ip route}.
 *
 * <p>Соответствует массиву {@code flags} в JSON-выводе команды
 * {@code ip -j route}. Флаги уточняют дополнительные свойства маршрута
 * (например, привязку к каналу, offload, ловушку и т.д.).</p>
 *
 * <p>Перед проверкой и преобразованием строкового значения выполняется
 * нормализация в {@link StringUtils#normalizeForEnum(String, String)}.</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/ip-route.8.html">ip-route(8)</a>
 */
public enum RouteFlag {

  /** Маршрут считается достижимым напрямую, даже если адрес не принадлежит подсети интерфейса. */
  ONLINK,

  /** Маршрут помечен как недействительный и не используется для пересылки пакетов. */
  DEAD,

  /** Маршрут применяется ко всем адресам без ограничения по подсети. */
  PERVASIVE,

  /** Маршрут обрабатывается аппаратно (hardware offload). */
  OFFLOAD,

  /** Маршрут используется как ловушка (пакеты передаются в userspace или отбрасываются). */
  TRAP;

  /**
   * Проверяет существование указанного флага маршрута.
   *
   * <p>Перед проверкой выполняется нормализация
   * в {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * <p>Возвращает {@code true}, если после нормализации значение найдено;
   * возвращает {@code false}, если {@code input == null}, строка пуста
   * после {@code trim()} или такой флаг не существует.</p>
   *
   * @param input название флага маршрута (может быть {@code null})
   * @return {@code true}, если флаг маршрута существует; иначе {@code false}
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static boolean isValid(String input) {
    try {
      RouteFlag.valueOf(
          StringUtils.normalizeForEnum(input, "Флаг маршрута"));
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
   * @param input название флага маршрута; не может быть {@code null} или пустым
   * @return соответствующий элемент перечисления
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после {@code trim()}
   *         или значение не соответствует ни одному флагу маршрута
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static RouteFlag getIgnoreCase(String input) {
    input = StringUtils.normalizeForEnum(input, "Флаг маршрута");

    try {
      return RouteFlag.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Неизвестный флаг маршрута: " + input);
    }
  }
}
