package io.github.ganenkovna.ssh.commands.cat.vlan.utils;

import static io.github.ganenkovna.ssh.commands.cat.vlan.utils.CatVlanConfigTestSupport.vlanInterfaceExists;
import com.jcraft.jsch.Session;
import io.github.ganenkovna.ssh.TestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Smoke-тесты для метода {@link CatVlanConfigTestSupport#vlanInterfaceExists}.
 *
 * <p>Проверяют корректность определения наличия VLAN-интерфейса
 * на удалённом хосте через {@code cat /proc/net/vlan/config}.</p>
 *
 * <p>Используются реальные данные стенда:
 * <ul>
 *   <li>{@link #testInterface} — имя базового интерфейса;</li>
 *   <li>{@link #validVlanId} — VLAN, который гарантированно существует;</li>
 *   <li>{@link #invalidVlanId} — VLAN, который гарантированно отсутствует.</li>
 * </ul></p>
 *
 * @see CatVlanConfigTestSupport#vlanInterfaceExists(Session, String, int)
 */
public class VlanInterfaceExistsSmokeTests extends TestBase {
  /** Имя тестового интерфейса; строго не {@code null}. */
  private static final String testInterface = "enp0s8";
  /** VLAN ID, который гарантированно существует на стенде. */
  private static final int validVlanId = 5;
  /** VLAN ID, который гарантированно отсутствует на стенде. */
  private static final int invalidVlanId = 6;

  /**
   * Проверяет, что метод корректно возвращает {@code true}
   * для существующего VLAN-интерфейса.
   */
  @Test
  @DisplayName("Проверка существующего VLAN интерфейса")
  public void shouldReturnTrue() {
    boolean result = vlanInterfaceExists(currentSession, testInterface, validVlanId);
    Assertions.assertTrue(result);
  }

  /**
   * Проверяет, что метод корректно возвращает {@code false}
   * для несуществующего VLAN-интерфейса.
   */
  @Test
  @DisplayName("Проверка несуществующего VLAN интерфейса")
  public void shouldReturnFalse() {
    boolean result = vlanInterfaceExists(currentSession, testInterface, invalidVlanId);
    Assertions.assertFalse(result);
  }
}
