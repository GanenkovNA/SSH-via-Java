package io.github.ganenkovna.util.ip.dto;

import static io.github.ganenkovna.util.StringUtils.normalizeForEnum;

import io.github.ganenkovna.util.StringUtils;

/**
 * Флаги состояния IPv4-адреса (из вывода `ip a`).
 */
public enum IpV4AddressFlag {

  /**
   * Адрес получен динамически (DHCP, SLAAC, etc).
   *
   * <p>Пример: {@code inet 192.168.1.100/24 ... dynamic}</p>
   */
  DYNAMIC,

  /**
   * Ядро не создало автоматический маршрут для подсети.
   *
   * <p>Пример: {@code inet 172.20.15.37/21 ... noprefixroute}</p>
   */
  NOPREFIXROUTE,

  /**
   * Дополнительный (вторичный) адрес на интерфейсе.
   *
   * <p>Пример: {@code inet 192.168.1.200/24 ... secondary}</p>
   */
  SECONDARY,

  /**
   * Адрес устарел (не рекомендуется для новых соединений).
   *
   * <p>Пример: {@code inet 192.168.1.100/24 ... deprecated}</p>
   */
  DEPRECATED,

  /**
   * Адрес закреплён статически (не удаляется автоматически).
   *
   * <p>Пример: {@code inet 192.168.1.100/24 ... permanent}</p>
   */
  PERMANENT;

  /**
   * Проверяет валидность строкового представления флага IPv4.
   *
   * <p>Перед проверкой выполняется нормализация
   * в {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * <p>Возвращает {@code true}, если после нормализации значение найдено;
   * возвращает {@code false}, если {@code input == null}, строка пустая после trim()
   * или такой области не существует.</p>
   *
   * @param input строка для проверки (может быть null)
   * @return {@code true}, если строка соответствует одному из значений перечисления;
   *         иначе {@code false}
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static boolean isValid(String input) {
    try {
      IpV4AddressFlag.valueOf(
          normalizeForEnum(input, "Флаг состояния IPv4"));
      return true;
    } catch (NullPointerException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Возвращает элемент перечисления по строковому представлению
   * (без учёта регистра и с заменой '-'→'_').
   *
   * <p>Нормализация идентична {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * @param input строка для поиска; не может быть {@code null} или пустой
   * @return соответствующий элемент перечисления
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пустая после trim() или значение не найдено
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static IpV4AddressFlag getIgnoreCase(String input) {
    input = normalizeForEnum(input, "Флаг состояния IPv4");

    try {
      return IpV4AddressFlag.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Значение флага состояния IPv4 не найдено: " + input);
    }
  }
}