package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v4;

import com.github.GanenkovNA.service.StringUtils;

/**
 * Флаги состояния IPv4-адреса (из вывода `ip a`).
 */
public enum IpV4AddressFlag {

  /**
   * Адрес получен динамически (DHCP, SLAAC, etc).
   *
   * <p>Пример: `inet 192.168.1.100/24 ... dynamic`</p>
   */
  DYNAMIC,

  /**
   * Ядро не создало автоматический маршрут для подсети.
   *
   * <p>Пример: `inet 172.20.15.37/21 ... noprefixroute`</p>
   */
  NOPREFIXROUTE,

  /**
   * Дополнительный (вторичный) адрес на интерфейсе.
   *
   * <p>Пример: `inet 192.168.1.200/24 ... secondary`</p>
   */
  SECONDARY,

  /**
   * Адрес в процессе проверки на дублирование (DAD).
   *
   * <p>Пример: `inet 192.168.1.100/24 ... tentative`</p>
   */
  TENTATIVE,

  /**
   * Адрес устарел (не рекомендуется для новых соединений).
   *
   * <p>Пример: `inet 192.168.1.100/24 ... deprecated`</p>
   */
  DEPRECATED,

  /**
   * Обнаружен конфликт адресов (DAD failed).
   *
   * <p>Пример: `inet 192.168.1.100/24 ... dadfailed`</p>
   */
  DADFAILED,

  /**
   * Адрес закреплён статически (не удаляется автоматически).
   *
   * <p>Пример: `inet 192.168.1.100/24 ... permanent`</p>
   */
  PERMANENT,

  /**
   * Временный адрес (например, для приватности).
   *
   * <p>Пример: `inet 192.168.1.100/24 ... mngtmpaddr`</p>
   */
  MNGTMPADDR;

  /**
   * Проверяет валидность строкового представления флага IPv4.
   *
   * <p>Метод выполняет нормализацию строки перед проверкой:
   * <ul>
   *   <li>Удаляет лишние пробелы</li>
   *   <li>Приводит к верхнему регистру</li>
   *   <li>Заменяет спецсимволы</li>
   * </ul>
   *
   * @param input строка для проверки (может быть null)
   * @return true если строка соответствует одному из значений enum,
   *         false если:
   *         - передан null
   *         - строка пустая
   *         - значение не найдено
   * @see StringUtils#normalizeForEnum
   */
  public static boolean isValid(String input) {
    try {
      IpV4AddressFlag.valueOf(
          StringUtils.normalizeForEnum(input));
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
   *   <li>Удаляет лишние пробелы и спецсимволы</li>
   * </ol>
   *
   * @param input строка для поиска (должна соответствовать имени элемента enum)
   * @return соответствующий элемент enum
   * @throws NullPointerException если input равен null
   * @throws IllegalArgumentException если:
   *         - input пустая строка
   *         - элемент не найден после нормализации
   * @throws RuntimeException если нормализованная строка не соответствует ни одному элементу enum
   * @see StringUtils#normalizeForEnum(String, String, String)
   */
  public static IpV4AddressFlag getIgnoreCase(String input)
      throws IllegalArgumentException, NullPointerException {
    input = StringUtils.normalizeForEnum(input,
        "Значение флага состояния IPv4 не может быть null",
        "Значение флага состояния IPv4 не может быть пустым");

    try {
      return IpV4AddressFlag.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Значение флага состояния IPv4 не найдено: " + input);
    }
  }
}