package io.github.ganenkovna.ssh.commands.ip.ro;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.ganenkovna.ssh.TestBase;
import io.github.ganenkovna.ssh.commands.ip.ro.dto.IpRoDTO;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IpRoSmokeTests extends TestBase {
  private final List<IpRoDTO> result = IpRo.showRoutes(currentSession);

  private static final ObjectMapper mapper = new ObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT);

  @BeforeEach
  public void checkListOfRoutes() {
    Assertions.assertNotNull(result, "Список маршрутов - null!");
    Assertions.assertFalse(result.isEmpty(),
        "Получен пустой список маршрутов!");
  }

  @Test
  public void shouldExecIpRoAndOutput() throws JsonProcessingException {
    System.out.println(mapper.writeValueAsString(result));
  }

  @Test
  public void shouldNotContainNullValues() {
    SoftAssertions softly = new SoftAssertions();

    for(IpRoDTO route : result) {
      final String routeId = identifyRoute(route);
      if(routeId == null) {
        softly.fail("Невозможно идентифицировать маршрут: `dst` пустой/null");
        continue;
      }

      if(!route.unparsedParams().isEmpty()) {
        softly.assertThat(route.unparsedParams())
            .as("Нераспознанные параметры в маршруте к `%s`", routeId)
            .isEmpty();
      }
    }

    softly.assertAll();
  }

  private static String identifyRoute(IpRoDTO route){
    String dst = route.dst();

    if (dst != null && !dst.isBlank()) {
      return dst;
    }
    return null;
  }
}
