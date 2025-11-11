package io.github.ganenkovna.ssh.commands.ip.a.utils;

import io.github.ganenkovna.ssh.TestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Smoke-тест метода {@link IpATestSupport#getVlanInterfaceName(String, int)}.
 *
 * <p>Проверяет корректность формирования имени VLAN-интерфейса для вывода {@code ip a}.
 * Формат имени: {@code <iface>.<vid>@<iface>} (например, {@code enp0s8.5@enp0s8}).</p>
 *
 * @see IpATestSupport#getVlanInterfaceName(String, int)
 */
public class GetVlanInterfaceNameSmokeTest extends TestBase {
  /** Имя тестового интерфейса; строго не {@code null}. */
  private static final String testInterface = "enp0s8";
  /** Корректный VLAN ID. */
  private static final int validVlanId = 5;
  /** Ожидаемое имя VLAN-интерфейса. */
  private static final String expectedName = testInterface + "." + validVlanId + "@" + testInterface;

  /**
   * Проверяет, что {@link IpATestSupport#getVlanInterfaceName(String, int)}
   * возвращает имя VLAN-интерфейса в корректном формате.
   */
  @Test
  @DisplayName("Проверка генерации имени VLAN интерфейса для `ip a`")
  public void shouldReturnTrue(){
    String result = IpATestSupport.getVlanInterfaceName(testInterface, validVlanId);
    Assertions.assertEquals(expectedName, result,
        "Ожидалось имя \"" + expectedName + "\", получено \"" + result + "\"");
  }
}
