package io.github.ganenkovna.ssh.commands.ip.a.dto.base;

import io.github.ganenkovna.util.StringUtils;

/** Хранит возможные флаги, установленные в интерфейсе.
 *
 * <p>Соответствуют флагам ядра Linux ({@code IFF_*} в {@code <linux/if.h>}).</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man7/netdevice.7.html">netdevice(7)</a>
 * @see <a href="https://www.kernel.org/doc/html/latest/networking/operstates.html">Состояния интерфейсов</a>
 */
public enum InterfaceFlag {

  /**
   * Интерфейс включён административно (флаг IFF_UP).
   * Не гарантирует работоспособность физического уровня.
   */
  UP,

  /**
   * Физический уровень активен (флаг IFF_LOWER_UP).
   * Пример: Ethernet с подключённым кабелем.
   */
  LOWER_UP,

  /**
   * Поддержка широковещания (флаг IFF_BROADCAST).
   * Характерно для Ethernet-интерфейсов.
   */
  BROADCAST,

  /**
   * Поддержка multicast-трафика (флаг IFF_MULTICAST).
   * Обязательно для IPv6-интерфейсов.
   */
  MULTICAST,

  /**
   * Promiscuous mode (флаг IFF_PROMISC).
   * Используется для сниффинга трафика.
   */
  PROMISC,

  /**
   * Приём всех multicast-пакетов (флаг IFF_ALLMULTI).
   * Используется в multicast-роутерах.
   */
  ALLMULTI,

  /**
   * Отключён ARP (флаг IFF_NOARP).
   * Характерно для PPP и VPN-туннелей.
   */
  NOARP,

  /**
   * Точечное соединение (флаг IFF_POINTOPOINT).
   * Пример: PPPoE, GRE-туннели.
   */
  POINTOPOINT,

  /**
   * Loopback-интерфейс (флаг IFF_LOOPBACK).
   * Пример: интерфейс lo.
   */
  LOOPBACK,

  /**
   * Динамический интерфейс (флаг IFF_DYNAMIC).
   * Может появляться/исчезать во время работы (например, PPP).
   */
  DYNAMIC,

  /**
   * Драйвер активен (флаг IFF_RUNNING).
   * Отличается от UP (может быть RUNNING без UP).
   * В современных выводах iproute2 чаще ориентируются на {@link #LOWER_UP}.
   */
  RUNNING,

  /**
   * Нет несущего сигнала.
   *
   * <p><b>Не является</b> флагом {@code IFF_*}; это операционное состояние, которое
   * отображается как {@code NO-CARRIER} в выводе {@code ip link} при отсутствии carrier.</p>
   *
   * @deprecated Не относится к {@code IFF_*}. Используйте {@link #LOWER_UP} для признака линка.
   */
  @Deprecated
  NO_CARRIER,

  /**
   * Режим ожидания (флаг IFF_DORMANT).
   * Пример: PPPoE перед установкой соединения.
   */
  DORMANT,

  /**
   * Эхо-ответчик (флаг IFF_ECHO).
   * Редко используется в специальных устройствах.
   */
  ECHO;

  /**
   * Проверяет существование указанного флага интерфейса.
   *
   * <p>Перед проверкой выполняется нормализация
   * в {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * <p>Возвращает {@code true}, если после нормализации значение найдено;
   * возвращает {@code false}, если {@code input == null}, строка пустая после trim()
   * или такого флага не существует.</p>
   *
   * @param input название флага интерфейса (может быть {@code null})
   * @return {@code true}, если флаг интерфейса существует; иначе {@code false}
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static boolean isValid(String input) {
    try {
      InterfaceFlag.valueOf(
          StringUtils.normalizeForEnum(input, "Флаг интерфейса"));
      return true;
    } catch (NullPointerException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Возвращает флаг интерфейса по имени, игнорируя регистр и дефисы.
   *
   * <p>Нормализация идентична {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * @param input название флага интерфейса; не может быть {@code null} или пустым
   * @return соответствующий элемент перечисления
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после trim() или значение не найдено
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static InterfaceFlag getIgnoreCase(String input) {
    input = StringUtils.normalizeForEnum(input, "Флаг интерфейса");

    try {
      return InterfaceFlag.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Значение флага интерфейса не найдено: " + input);
    }
  }
}
