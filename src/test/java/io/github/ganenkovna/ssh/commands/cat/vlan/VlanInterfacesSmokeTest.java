package io.github.ganenkovna.ssh.commands.cat.vlan;

import io.github.ganenkovna.ssh.TestBase;
import io.github.ganenkovna.ssh.commands.cat.vlan.dto.VlanInterfaceDto;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Smoke-тест команды {@code cat /proc/net/vlan/config}.
 *
 * <p>Выполняет команду на текущем SSH-хосте и проверяет,
 * что список VLAN-интерфейсов успешно получен и не пуст.</p>
 *
 * <p>Тест не проверяет корректность отдельных полей {@link VlanInterfaceDto},
 * только факт успешного выполнения команды и наличие хотя бы одной записи.</p>
 *
 * @see CatVlanConfig#showVlanConfig(com.jcraft.jsch.Session)
 * @see VlanInterfaceDto
 */
public class VlanInterfacesSmokeTest extends TestBase {

  /** Проверяет, что {@code cat /proc/net/vlan/config} возвращает хотя бы один VLAN-интерфейс. */
  @Test
  public void shouldReturnListOfVlanInterfaces(){
    List<VlanInterfaceDto> testVlanInterfaces = CatVlanConfig.showVlanConfig(currentSession);

    Assertions.assertNotNull(testVlanInterfaces, "Список не должен быть null");
    Assertions.assertFalse(testVlanInterfaces.isEmpty(), "Ожидается непустой список VLAN-интерфейсов");

    System.out.println("Найдено VLAN-интерфейсов: " + testVlanInterfaces.size());
    for (VlanInterfaceDto it : testVlanInterfaces) {
      System.out.println(it);
    }
  }
}
