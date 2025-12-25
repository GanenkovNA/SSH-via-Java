package io.github.ganenkovna.ssh.commands.ip.a.utils;

import static io.github.ganenkovna.ssh.commands.ip.a.utils.IpAddrTestSupport.vlanInterfaceExists;
import io.github.ganenkovna.ssh.TestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Smoke-тесты для {@link IpAddrTestSupport#vlanInterfaceExists(com.jcraft.jsch.Session, String, int)}.
 *
 * <p>Проверяют наличие VLAN-интерфейса в выводе {@code ip a} по паре
 * «базовый интерфейс + VLAN ID». Внутри test-support метод сопоставляет
 * формат имени VLAN-интерфейса (например, {@code enp0s8.5@enp0s8}) с данными,
 * полученными из {@code ip a} (см. также проверку формата имени в соседнем тесте). </p>
 *
 * @see IpAddrTestSupport#vlanInterfaceExists(com.jcraft.jsch.Session, String, int)
 * @see IpAddrTestSupport#getVlanInterfaceName(String, int)
 */
public class VlanInterfaceExistsSmokeTests extends TestBase {
  /** Имя тестового интерфейса; строго не {@code null}. */
  private static final String testInterface = "eth1";
  /** VLAN ID, который гарантированно существует на стенде. */
  private static final int validVlanId = 5;
  /** VLAN ID, который гарантированно отсутствует на стенде. */
  private static final int invalidVlanId = 6;

  /** Ожидаем {@code true} для реально существующего VLAN-интерфейса. */
  @Test
  @DisplayName("Проверка существующего VLAN интерфейса")
  public void shouldReturnTrue() {
    boolean result = vlanInterfaceExists(currentSession, testInterface, validVlanId);
    Assertions.assertTrue(result);
  }

  /** Ожидаем {@code false} для отсутствующего VLAN-интерфейса. */
  @Test
  @DisplayName("Проверка несуществующего VLAN интерфейса")
  public void shouldReturnFalse() {
    boolean result = vlanInterfaceExists(currentSession, testInterface, invalidVlanId);
    Assertions.assertFalse(result);
  }
}