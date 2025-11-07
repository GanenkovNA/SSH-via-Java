package io.github.ganenkovna.ssh.commands.cat.vlan;

import io.github.ganenkovna.ssh.TestBase;
import io.github.ganenkovna.ssh.commands.cat.vlan.dto.VlanInterfaceDto;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VlanInterfacesSmokeTest extends TestBase {
  @Test
  public void shouldReturnListOfVlanInterfaces(){
    List<VlanInterfaceDto> testVlanInterfaces = CatVlanConfig.showVlanConfig(currentSession);
    Assertions.assertFalse(testVlanInterfaces.isEmpty());
    for(VlanInterfaceDto it : testVlanInterfaces){
      System.out.println(it);
    }
  }
}
