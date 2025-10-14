package io.github.ganenkovna.ssh.commands.ip.a.dto.base;

import io.github.ganenkovna.util.StringUtils;

/**
 * Состояние сетевого интерфейса из вывода {@code ip link}.
 *
 * <p>Соответствует полю {@code state} в выводе команды {@code ip a}.
 * Отражает различные состояния интерфейса на канальном (L2) и сетевом (L3) уровнях.</p>
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
   */
  LOWER_UP,

  /**
   * Нет соединения на физическом уровне (кабель отключён).
   */
  NO_CARRIER,

  /**
   * Интерфейс в режиме ожидания (флаг {@code IFF_DORMANT}).
   *
   * <p>Характерно для PPPoE перед установкой соединения.
   */
  DORMANT;

  /**
   * Проверяет существование указанного состояния интерфейса.
   *
   * <p>Перед проверкой выполняется нормализация в {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * <p>Возвращает {@code true}, если после нормализации значение найдено;
   * возвращает {@code false}, если {@code input == null}, строка пустая после trim()
   * или такого состояния не существует.</p>
   *
   * @param input название состояния (может быть {@code null})
   * @return {@code true}, если состояние существует; иначе {@code false}
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static boolean isValid(String input) {
    try {
      InterfaceState.valueOf(
          StringUtils.normalizeForEnum(input, "Состояние интерфейса"));
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
   * @param input название состояния интерфейса; не может быть {@code null} или пустым
   * @return соответствующий элемент перечисления
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после trim() или значение не найдено
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static InterfaceState getIgnoreCase(String input) {
    input = StringUtils.normalizeForEnum(input,"Состояние интерфейса");

    try {
      return InterfaceState.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Значение состояния интерфейса не найдено: " + input);
    }
  }
}
