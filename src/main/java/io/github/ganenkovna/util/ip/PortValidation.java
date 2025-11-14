package io.github.ganenkovna.util.ip;

import static io.github.ganenkovna.util.StringUtils.normalizeForDto;
import io.github.ganenkovna.util.StringUtils;

/**
 * Утилиты для проверки значений TCP/UDP портов.
 *
 * <p>Порт определяется как 16-битное беззнаковое число
 * (RFC 9293, раздел 3.1), допустимый диапазон: {@code 0..65535}.</p>
 *
 * <p>Класс предоставляет две перегрузки метода валидации:</p>
 * <ul>
 *   <li>{@link #validatePort(int)} — строгая проверка числового значения;</li>
 *   <li>{@link #validatePort(String)} — нормализация строки и преобразование в число.</li>
 * </ul>
 *
 * <p>Обе версии выполняют fail-fast проверки и выбрасывают
 * {@link IllegalArgumentException} при выходе за диапазон или
 * некорректном формате.</p>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9293">RFC 9293 — Transmission Control Protocol</a>
 */
public final class PortValidation {
  private static final int PORT_MIN_VALUE = 0;
  private static final int PORT_MAX_VALUE = 65535;

  /** Запрет инстанцирования. */
  private PortValidation() {
    throw new AssertionError("No instances");
  }

  /**
   * Проверяет корректность значения порта.
   *
   * <p>Допустимый диапазон: {@code 0..65535} (unsigned 16-bit).</p>
   *
   * @param port значение порта
   * @return {@code true}, если порт находится в диапазоне
   * @throws IllegalArgumentException если порт вне диапазона {@code 0..65535}
   */
  public static boolean validatePort(int port){
    if (port < PORT_MIN_VALUE || port > PORT_MAX_VALUE){
      throw new IllegalArgumentException("Недопустимое значение порта: " + port
          + " (ожидается 0..65535)");
    }
    return true;
  }

  /**
   * Проверяет корректность строкового представления порта.
   *
   * <p>Выполняются следующие шаги:</p>
   * <ol>
   *   <li>нормализация строки через {@link StringUtils#normalizeForDto(String, String)};</li>
   *   <li>проверка, что строка содержит только цифры;</li>
   *   <li>преобразование в {@code int} с перехватом возможного переполнения;</li>
   *   <li>валидация значения через {@link #validatePort(int)}.</li>
   * </ol>
   *
   * @param port строковое представление порта; не может быть {@code null} или пустым
   * @return {@code true}, если порт корректен
   *
   * @throws NullPointerException если {@code port == null}
   * @throws IllegalArgumentException если строка содержит недопустимые символы,
   * не является корректным числовым значением или содержит значение вне диапазона
   */
  public static boolean validatePort(String port){
    port = normalizeForDto(port, "Порт");
    if(!port.matches("\\d+")){
      throw new IllegalArgumentException("Порт должен содержать только цифры: " + port);
    }

    int portInt;
    try {
      portInt = Integer.parseInt(port);
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("Порт не является корректным числом: " + port, ex);
    }

    return validatePort(portInt);
  }
}