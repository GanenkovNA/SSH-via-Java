package io.github.ganenkovna.util.ip.PortValidationTests;

import static io.github.ganenkovna.util.ip.PortValidation.validatePort;
import io.github.ganenkovna.util.ip.PortValidation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Smoke-тесты для методов валидации портов из {@link PortValidation}.
 *
 * <p>Покрываются базовые сценарии корректности значений TCP/UDP портов:</p>
 * <ul>
 *   <li>валидное числовое значение порта ({@code 0..65535});</li>
 *   <li>отрицательные значения;</li>
 *   <li>валидное строковое представление порта;</li>
 *   <li>строки с нецифровыми символами;</li>
 *   <li>строки, содержащие значение вне диапазона 16-битного unsigned целого.</li>
 * </ul>
 *
 * <p>Тесты проверяют, что методы {@link PortValidation#validatePort(int)} и
 * {@link PortValidation#validatePort(String)} выбрасывают ожидаемые исключения
 * {@link IllegalArgumentException} при ошибках формата или диапазона, а также
 * корректно обрабатывают валидные входные данные.</p>
 *
 * @see PortValidation#validatePort(int)
 * @see PortValidation#validatePort(String)
 */
public class PortValidationSmokeTests {
  private static final int validPortInt = 443;
  private static final int invalidPortInt = -10;
  private static final String validPortStr = "22";
  private static final String invalidPortStrChars = "22a";
  private static final String invalidPortStrRange = "99999";

  /**
   * Проверяет, что корректное целое значение порта проходит валидацию.
   *
   * <p>Вход:</p>
   * <ul>
   *   <li>{@link #validPortInt} — допустимое значение порта;</li>
   * </ul>
   */
  @Test
  public void shouldValidateIntPort() {
    Assertions.assertTrue(validatePort(validPortInt));
  }

  /**
   * Проверяет, что отрицательное значение порта вызывает исключение.
   *
   * <p>Вход:</p>
   * <ul>
   *   <li>{@link #invalidPortInt} — меньше 0;</li>
   * </ul>
   */
  @Test
  public void shouldFailOnNegativePort() {
    IllegalArgumentException ex = Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> validatePort(invalidPortInt)
    );

    Assertions.assertTrue(ex.getMessage().contains("Недопустимое значение порта"));
  }

  /**
   * Проверяет, что корректная строка порта проходит валидацию.
   *
   * <p>Вход:</p>
   * <ul>
   *   <li>{@link #validPortStr} — строка с допустимым значением порта;</li>
   * </ul>
   */
  @Test
  public void shouldValidateStringPort() {
    Assertions.assertTrue(validatePort(validPortStr));
  }

  /**
   * Проверяет, что строка с недопустимыми символами вызывает исключение.
   *
   * <p>Вход:</p>
   * <ul>
   *   <li>{@link #invalidPortStrChars} — содержит нецифровые символы;</li>
   * </ul>
   */
  @Test
  public void shouldFailOnNonDigitString() {
    IllegalArgumentException ex = Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> validatePort(invalidPortStrChars)
    );

    Assertions.assertTrue(ex.getMessage().contains("Порт должен содержать только цифры"));
  }

  /**
   * Проверяет, что строковое значение вне диапазона вызывает исключение.
   *
   * <p>Вход:</p>
   * <ul>
   *   <li>{@link #invalidPortStrRange} — превышает 65535;</li>
   * </ul>
   */
  @Test
  public void shouldFailOnStringOutOfRange() {
    IllegalArgumentException ex = Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> validatePort(invalidPortStrRange)
    );

    Assertions.assertTrue(ex.getMessage().contains("Недопустимое значение порта"));
  }
}
