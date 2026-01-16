package io.github.ganenkovna.util.ip.dto;

import io.github.ganenkovna.util.StringUtils;

/**
 * Режим работы канального уровня (L2) сетевого интерфейса.
 *
 * <p>Соответствует параметру {@code mode}, выводимому утилитой {@code ip link}
 * (например: {@code mode DEFAULT}, {@code mode DORMANT}).</p>
 *
 * <p>Определяет состояние линка с точки зрения канального уровня и
 * используется ядром Linux для управления поведением интерфейса.</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/ip-link.8.html">
 *      ip-link(8)</a>
 */
public enum LinkMode {

  /**
   * Обычный режим работы сетевого интерфейса.
   *
   * <p>Интерфейс функционирует в стандартном режиме без дополнительных
   * ограничений или специальных состояний.</p>
   */
  DEFAULT,

  /**
   * Интерфейс находится в «спящем» (dormant) состоянии.
   *
   * <p>Как правило означает, что интерфейс логически поднят,
   * но не может передавать трафик до выполнения внешних условий
   * (например, установления carrier, активации нижележащего устройства
   * или завершения negotiation).</p>
   */
  DORMANT;

  /**
   * Проверяет существование указанного режима работы L2-интерфейса.
   *
   * <p>Перед проверкой выполняется нормализация входной строки
   * в {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * <p>Метод не выбрасывает исключений.</p>
   *
   * @param input имя режима работы L2-интерфейса; может быть {@code null}
   * @return {@code true}, если после нормализации значение соответствует элементу
   *         перечисления; иначе {@code false}
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static boolean isValid(String input) {
    try {
      LinkMode.valueOf(
          StringUtils.normalizeForEnum(input,
              "Режим работы канального уровня сетевого интерфейса"));
      return true;
    } catch (NullPointerException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Возвращает режим работы L2-интерфейса по имени,
   * игнорируя регистр символов и дефисы.
   *
   * <p>Нормализация идентична {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * @param input имя режима работы L2-интерфейса; не может быть {@code null}
   * @return соответствующий элемент перечисления
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после {@code trim()}
   *                                  или режим не найден
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static LinkMode getIgnoreCase(String input) {
    input = StringUtils.normalizeForEnum(input,
        "Режим работы канального уровня сетевого интерфейса");

    try {
      return LinkMode.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Режим работы канального уровня сетевого интерфейса не найден: " + input);
    }
  }
}
