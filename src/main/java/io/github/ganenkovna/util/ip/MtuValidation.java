package io.github.ganenkovna.util.ip;

import java.util.Set;

/**
 * Валидатор значений MTU (Maximum Transmission Unit) для сетевых интерфейсов Linux.
 *
 * <p>Поддерживаются следующие диапазоны:
 * <ul>
 *   <li>обычные интерфейсы — от {@code 68} до {@code 9000} включительно;</li>
 *   <li>loopback-интерфейсы — дополнительные значения {@code 65535} и {@code 65536}.</li>
 * </ul>
 *
 * <p>Метод {@link #validateMtu(int)} выполняет строгую проверку и выбрасывает
 * {@link IllegalArgumentException} при выходе за диапазон; метод
 * {@link #isMtuValid(int)} возвращает булев результат без исключений.</p>
 *
 * <p>Пример использования:</p>
 * <pre>{@code
 * MtuValidation.validateMtu(1500);   // OK
 * MtuValidation.validateMtu(65536);  // OK для loopback
 * MtuValidation.validateMtu(42);     // IllegalArgumentException
 * }</pre>
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc791">RFC 791 — Internet Protocol</a>
 * @see <a href="https://man7.org/linux/man-pages/man7/netdevice.7.html">man 7 netdevice</a>
 */
public final class MtuValidation {
  private static final int MTU_MIN_VALUE = 68;
  private static final int MTU_MAX_VALUE = 9000;
  private static final Set<Integer> MTU_LOOPBACK_VALUES = Set.of(65535, 65536);

  /** Запрет инстанцирования. */
  private MtuValidation() {
    throw new AssertionError("No instances");
  }

  /**
   * Проверяет корректность значения MTU.
   *
   * @param mtu проверяемое значение (в байтах)
   * @throws IllegalArgumentException если {@code mtu} меньше {@code 68},
   *                                  больше {@code 9000} и не входит
   *                                  в список допустимых loopback-значений;
   *                                  сообщение содержит фактическое значение
   */
  public static void validateMtu(int mtu){
    if (!isMtuValid(mtu)){
      throw new IllegalArgumentException(
          "Недопустимое значение MTU: " + mtu
              + ". Допустимые диапазоны: 68–9000 (обычные интерфейсы) "
              + "или 65535/65536 (loopback).");
    }
  }

  /**
   * Проверяет, входит ли значение MTU в допустимый диапазон.
   *
   * @param mtu проверяемое значение (в байтах)
   * @return {@code true}, если значение допустимо; иначе {@code false}
   */
  public static boolean isMtuValid(int mtu) {
    return (mtu >= MTU_MIN_VALUE && mtu <= MTU_MAX_VALUE)
        || MTU_LOOPBACK_VALUES.contains(mtu);
  }
}
