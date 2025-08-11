package com.github.GanenkovNA.ssh.commands.ip.a.dto.base;

import com.github.GanenkovNA.service.StringUtils;

/** Хранит возможные флаги, установленные в интерфейсе.
 *
 *  <p>Соответствуют флагам ядра Linux (IFF_* в <linux/if.h>).
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
   */
  RUNNING,

  /**
   * Нет несущего сигнала (флаг !IFF_LOWER_UP).
   * Пример: отключённый Ethernet-кабель.
   */
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
   * <p><b>Нормализация имени:</b></p>
   * <ul>
   *   <li>Приведение к верхнему регистру</li>
   *   <li>Замена тире на подчёркивания</li>
   *   <li>Удаление пробелов по краям</li>
   * </ul>
   *
   * @param input название флага (может быть null)
   * @return true если флаг существует, false если:
   *         <ul>
   *           <li>input == null</li>
   *           <li>строка пустая</li>
   *           <li>флаг не найден</li>
   *         </ul>
   *
   * @implNote Примеры:
   * <ul>
   *   <li>isValid("up") → true</li>
   *   <li>isValid("loopback") → true</li>
   *   <li>isValid("no-carrier") → true (преобразуется в NO_CARRIER)</li>
   * </ul>
   *   <li>getIgnoreCase("POINTOPOINT") → InterfaceFlag.POINTOPOINT</li>
   */
  public static boolean isValid(String input) {
    try {
      InterfaceFlag.valueOf(
          StringUtils.normalizeForEnum(input, "", ""));
      return true;
    } catch (NullPointerException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Возвращает флаг интерфейса по имени (без учёта регистра).
   *
   * <p><b>Требования к имени:</b></p>
   * <ol>
   *   <li>Не может быть null</li>
   *   <li>Не может быть пустой строкой</li>
   *   <li>Должно соответствовать одному из значений перечисления</li>
   * </ol>
   *
   * @param input название флага
   * @return соответствующий флаг интерфейса
   * @throws NullPointerException если input == null
   * @throws IllegalArgumentException если:
   *         <ul>
   *           <li>строка пустая после trim()</li>
   *           <li>флаг не найден</li>
   *         </ul>
   *
   * @implNote Примеры:
   * <ul>
   *   <li>getIgnoreCase("up") → InterfaceFlag.UP</li>
   *   <li>getIgnoreCase("NO-CARRIER") → InterfaceFlag.NO_CARRIER</li>
   * </ul>
   */
  public static InterfaceFlag getIgnoreCase(String input)
      throws IllegalArgumentException, NullPointerException {
    input = StringUtils.normalizeForEnum(input,
        "Значение флага интерфейса не может быть null",
        "Значение флага интерфейса не может быть пустым");

    try {
      return InterfaceFlag.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Значение флага интерфейса не найдено: " + input);
    }
  }
}
