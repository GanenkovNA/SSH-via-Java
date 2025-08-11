package com.github.GanenkovNA.ssh.commands.ip.a.dto.base;

import com.github.GanenkovNA.service.StringUtils;

/**
 * Состояние сетевого интерфейса из вывода `ip link`.
 *
 * <p>Соответствует полю `state` в выводе команды `ip a`.
 * Отражают различные состояния интерфейса на канальном (L2) и сетевом (L3) уровнях.
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/ip-link.8.html">Документация ip-link(8)</a>
 * @see <a href="https://www.kernel.org/doc/html/latest/networking/operstates.html">Kernel Networking Operstates</a>
 */
public enum InterfaceState {

  /** Интерфейс включён и активен (флаг {@code IFF_UP}). */
  UP,

  /** Интерфейс выключен административно (флаг {@code !IFF_UP}). */
  DOWN,

  /**
   * Состояние не может быть определено.
   *
   * <p>Возникает при ошибках драйвера или аппаратных сбоях.
   */
  UNKNOWN,

  /** Физический уровень активен (кабель подключён, флаг {@code IFF_LOWER_UP}).
   *
   * <p>Соответствует статусу {@code NO-CARRIER} в Ethernet-интерфейсах.
   */
  LOWER_UP,

  /**
   * Нет соединения на физическом уровне (кабель отключён).
   *
   * <p>Соответствует статусу {@code NO-CARRIER} в Ethernet-интерфейсах.
   */
  NO_CARRIER,

  /**
   * Интерфейс в режиме ожидания (флаг {@code IFF_DORMANT}).
   *
   * <p>Характерно для PPPoE перед установкой соединения.
   */
  DORMANT;

  /**
   * Проверяет существование указанного состояния интерфейса в перечислении.
   *
   * <p><b>Нормализация имени:</b></p>
   * <ul>
   *   <li>Приведение к верхнему регистру</li>
   *   <li>Замена тире на подчёркивания</li>
   *   <li>Удаление пробелов по краям</li>
   * </ul>
   *
   * @param input название состояния (может быть null)
   * @return true если состояние существует, false если:
   *         <ul>
   *           <li>input == null</li>
   *           <li>строка пустая</li>
   *           <li>состояние не найдено</li>
   *         </ul>
   *
   * @implNote Примеры:
   * <ul>
   *   <li>isValid("up") → true</li>
   *   <li>isValid("admin_down") → true</li>
   *   <li>isValid(null) → false</li>
   * </ul>
   */
  public static boolean isValid(String input) {
    try {
      InterfaceState.valueOf(
          StringUtils.normalizeForEnum(input, "", ""));
      return true;
    } catch (NullPointerException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Возвращает состояние интерфейса по имени (без учёта регистра).
   *
   * <p><b>Требования к имени:</b></p>
   * <ol>
   *   <li>Не может быть null</li>
   *   <li>Не может быть пустой строкой</li>
   *   <li>Должно соответствовать одному из значений перечисления</li>
   * </ol>
   *
   * @param input название состояния
   * @return соответствующее состояние интерфейса
   * @throws NullPointerException если input == null
   * @throws IllegalArgumentException если:
   *         <ul>
   *           <li>строка пустая после trim()</li>
   *           <li>состояние не найдено</li>
   *         </ul>
   *
   * @implNote Примеры:
   * <ul>
   *   <li>getIgnoreCase("up") → InterfaceState.UP</li>
   *   <li>getIgnoreCase("LOWER-UP") → InterfaceState.LOWER_UP</li>
   * </ul>
   */
  public static InterfaceState getIgnoreCase(String input)
      throws IllegalArgumentException, NullPointerException {
    input = StringUtils.normalizeForEnum(input,
        "Значение состояния интерфейса не может быть null",
        "Значение состояния интерфейса не может быть пустым");

    try {
      return InterfaceState.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Значение состояния интерфейса не найдено: " + input);
    }
  }
}
