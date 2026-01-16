package io.github.ganenkovna.util.ip.dto;

import io.github.ganenkovna.util.StringUtils;

/**
 * Тип канального уровня сетевого интерфейса ({@code link_type}) в модели Linux netdevice.
 *
 * <p>Соответствует значению JSON-поля {@code link_type} и префиксу {@code link/*},
 * выводимому утилитой {@code ip link}
 * (например: {@code link/ether}, {@code link/loopback}).</p>
 *
 * <p>Определяет физическую или логическую природу интерфейса
 * на канальном уровне модели OSI (Ethernet, loopback, bridge, VLAN и т.п.)
 * и используется ядром Linux и пользовательскими утилитами
 * для выбора соответствующей модели обработки кадров.</p>
 *
 * <p>Не следует путать с параметром {@code mode} из вывода {@code ip link},
 * который описывает режим работы интерфейса, а не его тип.</p>
 *
 * <p>Перед сопоставлением входного строкового значения выполняется нормализация
 * в {@link StringUtils#normalizeForEnum(String, String)}.</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/ip-link.8.html">
 *      ip-link(8)</a>
 */
public enum LinkType {

  /** Ethernet-интерфейс ({@code link/ether}). */
  ETHER,

  /** Loopback-интерфейс ({@code link/loopback}). */
  LOOPBACK,

  /** Bridge-интерфейс ({@code link/bridge}). */
  BRIDGE,

  /** VLAN-интерфейс ({@code link/vlan}). */
  VLAN,

  /**
   * Virtual Ethernet (veth) интерфейс.
   *
   * <p>В выводе {@code ip link} имеет тип {@code link/ether},
   * а конкретный подтип определяется через {@code info_kind=veth}.</p>
   */
  VETH,

  /**
   * TUN/TAP виртуальный интерфейс.
   *
   * <p>Используется для пользовательских сетевых туннелей.</p>
   */
  TUN,

  /**
   * Dummy-интерфейс.
   *
   * <p>Имеет тип {@code link/ether}, подтип {@code info_kind=dummy}.</p>
   */
  DUMMY,

  /** Неизвестный или неподдерживаемый тип интерфейса. */
  UNKNOWN;

  /**
   * Проверяет существование указанного типа канального уровня интерфейса.
   *
   * <p>Перед проверкой выполняется нормализация входной строки
   * в {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * <p>Метод не выбрасывает исключений.</p>
   *
   * @param input имя типа канального интерфейса; может быть {@code null}
   * @return {@code true}, если после нормализации значение соответствует элементу
   *         перечисления; иначе {@code false}
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static boolean isValid(String input) {
    try {
      LinkType.valueOf(
          StringUtils.normalizeForEnum(input,
              "Тип канального интерфейса"));
      return true;
    } catch (NullPointerException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Возвращает тип канального уровня интерфейса по имени,
   * игнорируя регистр символов и дефисы.
   *
   * <p>Нормализация идентична {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * @param input имя типа канального интерфейса; не может быть {@code null}
   * @return соответствующий элемент перечисления
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после {@code trim()}
   *                                  или значение не соответствует ни одному
   *                                  элементу перечисления
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static LinkType getIgnoreCase(String input) {
    input = StringUtils.normalizeForEnum(input,
        "Тип канального интерфейса");

    try {
      return LinkType.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Тип канального интерфейса не найден: " + input);
    }
  }
}
