package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v4;

import com.github.GanenkovNA.service.StringUtils;

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
   * <p><b>Нормализация имени:</b></p>
   * <ul>
   *   <li>Приведение к верхнему регистру</li>
   *   <li>Замена тире на подчёркивания</li>
   *   <li>Удаление пробелов по краям</li>
   * </ul>
   *
   * @param input название области видимости (может быть null)
   * @return true если область существует, false если:
   *         <ul>
   *           <li>input == null</li>
   *           <li>строка пустая</li>
   *           <li>область не найдена</li>
   *         </ul>
   *
   * @implNote Примеры:
   * <ul>
   *   <li>isValid("global") → true</li>
   *   <li>isValid("link-local") → true (преобразуется в LINK)</li>
   *   <li>isValid("invalid") → false</li>
   * </ul>
   */
  public static boolean isValid(String input) {
    try {
      IpV4Scope.valueOf(
          StringUtils.normalizeForEnum(input));
      return true;
    } catch (NullPointerException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Проверяет существование указанной области видимости IPv4.
   *
   * <p><b>Нормализация имени:</b></p>
   * <ul>
   *   <li>Приведение к верхнему регистру</li>
   *   <li>Замена тире на подчёркивания</li>
   *   <li>Удаление пробелов по краям</li>
   * </ul>
   *
   * @param input название области видимости (может быть null)
   * @return true если область существует, false если:
   *         <ul>
   *           <li>input == null</li>
   *           <li>строка пустая</li>
   *           <li>область не найдена</li>
   *         </ul>
   *
   * @implNote Примеры:
   * <ul>
   *   <li>isValid("global") → true</li>
   *   <li>isValid("link-local") → true (преобразуется в LINK)</li>
   *   <li>isValid("invalid") → false</li>
   * </ul>
   */
  public static IpV4Scope getIgnoreCase(String input)
      throws IllegalArgumentException, NullPointerException {
    input = StringUtils.normalizeForEnum(input,
        "Значение области видимости IPv4 не может быть null",
        "Значение области видимости IPv4 не может быть пустым");

    try {
      return IpV4Scope.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Значение области видимости IPv4 не найдено: " + input);
    }
  }
}
