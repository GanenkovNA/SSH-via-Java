package io.github.ganenkovna.ssh.commands.ip.a.utils;

import static io.github.ganenkovna.ssh.commands.ip.a.utils.IpAddrTestSupport.hasInterfaceIpAddress;
import io.github.ganenkovna.ssh.TestBase;
import io.github.ganenkovna.ssh.commands.ip.a.IpAddr;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Smoke-тесты для метода {@link IpAddrTestSupport#hasInterfaceIpAddress}.
 *
 * <p>Проверяют базовую корректность работы утилиты при анализе вывода {@code ip a}
 * в рамках активной SSH-сессии, полученной от {@link TestBase}.
 * Тесты не эмулируют вывод и не подменяют парсер — используется реальное подключение
 * к тестовому стенду с известной конфигурацией интерфейсов.</p>
 *
 * <p>Покрываемые сценарии:</p>
 * <ul>
 *   <li>интерфейс содержит указанный IP-адрес (ожидается {@code true});</li>
 *   <li>интерфейс не содержит указанный IP-адрес (ожидается {@code false}).</li>
 * </ul>
 *
 * @see IpAddrTestSupport#hasInterfaceIpAddress
 * @see IpAddr#showInterfaces(com.jcraft.jsch.Session)
 */
public class HasInterfaceIpAddressSmokeTests extends TestBase {
  /** Имя тестового интерфейса; строго не {@code null}. */
  private static final String testInterface = "eth1";
  /** Адрес, который гарантированно существует на стенде. */
  private static final String validTestAddress = "192.168.10.10/24";
  /** Адрес, который гарантированно отсутствует на стенде. */
  private static final String invalidTestAddress = "192.168.10.9/24";

  /**
   * Проверяет, что {@link IpAddrTestSupport#hasInterfaceIpAddress} возвращает {@code true},
   * если интерфейс действительно содержит указанный IP-адрес.
   */
  @Test
  @DisplayName("Проверка существующего IP-адреса")
  public void shouldReturnTrue() {
    boolean result = hasInterfaceIpAddress(currentSession, testInterface, validTestAddress);
    Assertions.assertTrue(result);
  }

  /**
   * Проверяет, что {@link IpAddrTestSupport#hasInterfaceIpAddress} возвращает {@code false},
   * если интерфейс не содержит указанный IP-адрес.
   */
  @Test
  @DisplayName("Проверка несуществующего IP-адреса")
  public void shouldReturnFalse() {
    boolean result = hasInterfaceIpAddress(currentSession, testInterface, invalidTestAddress);
    Assertions.assertFalse(result);
  }
}