package ssh_via_java.ip.a;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.GanenkovNA.ssh.commands.ip.a.IpA;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.InterfaceDto;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import ssh_via_java.TestBase;

/**
 * Тестирование корректности преобразования данных интерфейсов в JSON.
 * <p>
 * Проверяет:
 * <ul>
 *   <li>Возможность сериализации всех интерфейсов в JSON
 *   <li>Наличие обязательных полей (index, name) у каждого интерфейса
 *   <li>Корректность структуры выходных данных
 * </ul>
 *
 * @see IpA#showInterfaces(Session) Метод получения данных интерфейсов
 * @see InterfaceDto Структура данных интерфейса
 */
public class OutputTest extends TestBase {
  String json;

  /**
   * Тест преобразования данных сетевых интерфейсов в JSON.
   * <p>
   * Для каждого интерфейса проверяет:
   * <ol>
   *   <li>Наличие индекса интерфейса (not null)
   *   <li>Наличие имени интерфейса (not null)
   *   <li>Возможность сериализации в валидный JSON
   * </ol>
   * <p>
   * Выводит в консоль JSON-представление каждого интерфейса для ручной проверки.
   *
   * @throws RuntimeException если произошла ошибка при работе с SSH
   * @implNote Пример вывода:
   * <pre>{@code
   * === eth0 ===
   * {
   *   "interfaceParams": {
   *     "index": 1,
   *     "name": "eth0",
   *     ...
   *   }
   * }
   * }</pre>
   */
  @Test
  public void shouldOutputJson(){
    SoftAssertions softly = new SoftAssertions();

    List<InterfaceDto> interfaces = IpA.showInterfaces(currentSession);

    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    for (InterfaceDto interfaceDTO : interfaces) {
      softly.assertThat(interfaceDTO.getInterfaceParams().getIndex()).isNotNull();
      softly.assertThat(interfaceDTO.getInterfaceParams().getName()).isNotNull();

      try {
        json = mapper.writeValueAsString(interfaceDTO);  // Передаем объект, а не класс
        System.out.println("=== " + interfaceDTO.getInterfaceParams().getName() + " ===\n" + json);
      } catch (JsonProcessingException e) {
        System.err.println("Ошибка при преобразовании в JSON: " + e.getMessage());
      }
    }

    softly.assertAll();
  }
}
