package io.github.ganenkovna.util.ip.IpUtilsTests;

import static io.github.ganenkovna.util.ip.IpUtils.validateIpv4Cidr;
import io.github.ganenkovna.util.ip.IpUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Smoke-тесты для метода {@link IpUtils#validateIpv4Cidr(String)}.
 *
 * <p>Покрываются базовые сценарии проверки IPv4-CIDR строк:</p>
 * <ul>
 *   <li>корректный формат {@code A.B.C.D/len};</li>
 *   <li>ошибочный формат без слэша;</li>
 *   <li>некорректная длина префикса (вне диапазона {@code 0..32});</li>
 *   <li>некорректный IPv4-адрес.</li>
 * </ul>
 *
 * <p>Тесты проверяют, что метод выбрасывает ожидаемые исключения
 * {@link IllegalArgumentException} с информативным сообщением и не
 * поглощает ошибки формата.</p>
 *
 * @see IpUtils#validateIpv4Cidr(String)
 * @see IpUtils#validateIpv4(String)
 * @see IpUtils#validateIpv4Prefix(String)
 */
public class Ipv4CidrValidationSmokeTests {
  private static final String validIpv4Cidr = "192.168.0.10/24";
  private static final String invalidCidrFormat = "192.168.0.1024";
  private static final String invalidPrefix = "192.168.0.10/52";
  private static final String invalidIpv4 = "192.168.0.257/24";

  /**
   * Проверяет, что корректная CIDR-строка успешно проходит валидацию.
   *
   * <p>Сценарий:</p>
   * <ul>
   *   <li>вход: {@link #validIpv4Cidr} — корректная IPv4-CIDR строка;</li>
   *   <li>ожидается: метод возвращает {@code true} без исключений.</li>
   * </ul>
   *
   * @see IpUtils#validateIpv4Cidr(String)
   */
  @Test
  public void shouldValidate(){
    Assertions.assertTrue(validateIpv4Cidr(validIpv4Cidr));
  }

  /**
   * Проверяет, что строка без разделителя {@code '/'} вызывает
   * {@link IllegalArgumentException}.
   *
   * <p>Сценарий:</p>
   * <ul>
   *   <li>вход: {@link #invalidCidrFormat} — отсутствует разделитель CIDR-формата;</li>
   *   <li>ожидается: сообщение содержит фразу
   *       {@code "Ожидается формат A.B.C.D/len:"}.</li>
   * </ul>
   *
   * @throws IllegalArgumentException ожидаемое исключение
   * @see IpUtils#validateIpv4Cidr(String)
   */
  @Test
  public void shouldReturnIaCidrFormat(){
    IllegalArgumentException ex = Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> validateIpv4Cidr(invalidCidrFormat));

    Assertions.assertTrue(ex.getMessage()
        .contains("Ожидается формат A.B.C.D/len:"));
  }

  /**
   * Проверяет, что длина префикса вне диапазона {@code 0..32} вызывает
   * {@link IllegalArgumentException}.
   *
   * <p>Сценарий:</p>
   * <ul>
   *   <li>вход: {@link #invalidPrefix} — префикс вне допустимого диапазона;</li>
   *   <li>ожидается: сообщение содержит фразу
   *       {@code "Длина префикса вне диапазона 0..32:"}.</li>
   * </ul>
   *
   * @throws IllegalArgumentException ожидаемое исключение
   * @see IpUtils#validateIpv4Prefix(String)
   * @see IpUtils#validateIpv4Cidr(String)
   */
  @Test
  public void shouldReturnIaPrefix(){
    IllegalArgumentException ex = Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> validateIpv4Cidr(invalidPrefix));

    Assertions.assertTrue(ex.getMessage()
        .contains("Длина префикса вне диапазона 0..32:"));
  }

  /**
   * Проверяет, что некорректный IPv4-адрес вызывает
   * {@link IllegalArgumentException}.
   *
   * <p>Сценарий:</p>
   * <ul>
   *   <li>вход: {@link #invalidIpv4} — значение содержит октет вне диапазона 0..255;</li>
   *   <li>ожидается: сообщение содержит фразу
   *       {@code "Неверный формат IPv4-адреса:"}.</li>
   * </ul>
   *
   * @throws IllegalArgumentException ожидаемое исключение
   * @see IpUtils#validateIpv4(String)
   * @see IpUtils#validateIpv4Cidr(String)
   */
  @Test
  public void shouldReturnIaIpv4(){
    IllegalArgumentException ex = Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> validateIpv4Cidr(invalidIpv4));

    Assertions.assertTrue(ex.getMessage()
        .contains("Неверный формат IPv4-адреса:"));
  }
}
