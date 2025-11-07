package io.github.ganenkovna.util.ip;

/**
 * Валидатор идентификаторов VLAN (IEEE 802.1Q).
 *
 * <p>Допустимый диапазон VLAN ID — от {@code 1} до {@code 4094} включительно.
 * Значения {@code 0} и {@code 4095} зарезервированы стандартом и не могут использоваться
 * для пользовательских VLAN.</p>
 *
 * <p>Класс предоставляет два метода:
 * <ul>
 *   <li>{@link #isVlanIdValid(int)} — безопасная проверка без исключений;</li>
 *   <li>{@link #validateVlanId(int)} — строгая проверка с выбросом
 *       {@link IllegalArgumentException} при нарушении диапазона.</li>
 * </ul>
 *
 * <p>Пример использования:</p>
 * <pre>{@code
 * int id = 10;
 * VlanValidation.validateVlanId(id); // OK
 * boolean valid = VlanValidation.isVlanIdValid(4095); // false
 * }</pre>
 *
 * @see <a href="https://standards.ieee.org/standard/802_1Q-2018.html">IEEE 802.1Q-2018</a>
 */
public final class VlanValidation {
  private static final int MIN_VLAN_ID = 1;
  private static final int MAX_VLAN_ID = 4094;

  /** Запрет инстанцирования. */
  private VlanValidation() {
    throw new AssertionError("No instances");
  }

  /**
   * Проверяет корректность идентификатора VLAN.
   *
   * @param vlanId проверяемое значение
   * @throws IllegalArgumentException если {@code vlanId} меньше {@code 1}
   *                                  или больше {@code 4094};
   *                                  сообщение включает фактическое значение
   */
  public static void validateVlanId(int vlanId){
    if (!isVlanIdValid(vlanId)){
      throw new IllegalArgumentException("VLAN Id должен быть в диапазоне 1-4094, текущее значение: " + vlanId);
    }
  }

  /**
   * Проверяет, входит ли значение в допустимый диапазон VLAN ID.
   *
   * @param vlanId проверяемое значение
   * @return {@code true}, если {@code 1 ≤ vlanId ≤ 4094}; иначе {@code false}
   */
  public static boolean isVlanIdValid (int vlanId){
    return vlanId >= MIN_VLAN_ID && vlanId <= MAX_VLAN_ID;
  }
}
