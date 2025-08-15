package ssh_via_java.ip.a;

import static org.junit.jupiter.api.Assertions.assertTrue;
import com.github.GanenkovNA.ssh.commands.ip.a.IpA;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.InterfaceDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v4.InterfaceIpv4ConfigDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v6.InterfaceIpv6ConfigDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import ssh_via_java.TestBase;

public class UnknownParamsTest extends TestBase {
  String listOfUnknownParams = "";

  @Test
  public void shouldNotHaveUnknownParams(){
    List<InterfaceDto> interfaces = IpA.showInterfaces(currentSession);

    for (InterfaceDto interfaceDTO : interfaces) {
      // Нераспознанные строки
      if (!interfaceDTO.getUnknownLines().isEmpty()){
        for (String unknownParam : interfaceDTO.getUnknownLines()) {
          listOfUnknownParams += interfaceDTO.getInterfaceParams().getName() + ": " + unknownParam + "\n";
        }
      }
      // Базовые параметры
      if (!interfaceDTO.getInterfaceParams().getUnknownParams().isEmpty()){
        for (String unknownParam : interfaceDTO.getInterfaceParams().getUnknownParams()){
          listOfUnknownParams += interfaceDTO.getInterfaceParams().getName() + ".InterfaceParam: " + unknownParam + "\n";
        }
      }
      // Физические параметры
      if (!interfaceDTO.getInterfacePhysicalParams().getUnknownParams().isEmpty()){
        for (String unknownParam : interfaceDTO.getInterfacePhysicalParams().getUnknownParams()){
          listOfUnknownParams += interfaceDTO.getInterfaceParams().getName() + ".PhysicalParam: " + unknownParam + "\n";
        }
      }
      // Параметры IPv4
      for (InterfaceIpv4ConfigDto ipv4ConfigDto : interfaceDTO.getIpv4()){
        if (!ipv4ConfigDto.getUnknownParams().isEmpty()){
          for (String unknownParam : ipv4ConfigDto.getUnknownParams()){
            listOfUnknownParams += interfaceDTO.getInterfaceParams().getName() + ".IPv4: " + unknownParam + "\n";
          }
        }
        // Параметры LifeTime
        if (!ipv4ConfigDto.getLifeTimeParams().getUnknownParams().isEmpty()){
          for (String unknownParam : ipv4ConfigDto.getLifeTimeParams().getUnknownParams()){
            listOfUnknownParams += interfaceDTO.getInterfaceParams().getName() + ".IPv4.LifeTime: " + unknownParam + "\n";
          }
        }
      }
      // Параметры IPv6
      for (InterfaceIpv6ConfigDto ipv6ConfigDto : interfaceDTO.getIpv6()){
        if (!ipv6ConfigDto.getUnknownParams().isEmpty()){
          for (String unknownParam : ipv6ConfigDto.getUnknownParams()){
            listOfUnknownParams += interfaceDTO.getInterfaceParams().getName() + ".IPv6: " + unknownParam + "\n";
          }
        }
        // Параметры LifeTime
        if (!ipv6ConfigDto.getLifeTimeParams().getUnknownParams().isEmpty()){
          for (String unknownParam : ipv6ConfigDto.getLifeTimeParams().getUnknownParams()){
            listOfUnknownParams += interfaceDTO.getInterfaceParams().getName() + ".IPv6.LifeTime: " + unknownParam + "\n";
          }
        }
      }
    }

    assertTrue(listOfUnknownParams.isBlank(), listOfUnknownParams);
  }
}
