package io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v4;

import static io.github.ganenkovna.util.StringUtils.normalizeForEnum;
import io.github.ganenkovna.util.StringUtils;

/**
 * Область видимости IPv4-адреса.
 *
 * <p>Определяет зону действия IPv4-адреса в соответствии с архитектурой scoped addressing. </p>
 */
public enum IpV4Scope {

  /** Глобальная маршрутизация (интернет).
   *
   * <p>Адреса с такой областью видимости маршрутизируются глобально.
   */
  GLOBAL,

  /**
   * Локальная область видимости организации (site-local).
   *
   * <p>Адреса действуют в пределах одной организации.
   */
  SITE,

  /**
   * Локальная область видимости уровня канала передачи данных (link-local).
   *
   * <p>Адреса действуют только в пределах одного сегмента сети.
   */
  LINK,

  /**
   * Область видимости только в пределах хоста (host-local).
   *
   * <p>Адреса не выходят за пределы хоста.
   */
  HOST;

  /**
   * Проверяет существование указанной области видимости IPv4.
   *
   * <p>Перед проверкой выполняется нормализация в {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * <p>Возвращает {@code true}, если после нормализации значение найдено;
   * возвращает {@code false}, если {@code input == null}, строка пустая после trim()
   * или такой области не существует.</p>
   *
   * @param input название области видимости (может быть {@code null})
   * @return {@code true}, если область существует; иначе {@code false}
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static boolean isValid(String input) {
    try {
      IpV4Scope.valueOf(
          normalizeForEnum(input, "Область видимости IPv4"));
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
   * @param input название области видимости; не может быть {@code null} или пустым
   * @return соответствующий элемент перечисления
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после trim() или значение не найдено
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static IpV4Scope getIgnoreCase(String input) {
    input = normalizeForEnum(input,"Область видимости IPv4");

    try {
      return IpV4Scope.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Значение области видимости IPv4 не найдено: " + input);
    }
  }
}
