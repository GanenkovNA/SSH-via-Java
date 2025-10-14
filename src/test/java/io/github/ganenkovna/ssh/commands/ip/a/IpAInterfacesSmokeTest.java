package io.github.ganenkovna.ssh.commands.ip.a;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.ganenkovna.ssh.TestBase;
import io.github.ganenkovna.ssh.commands.ip.a.dto.InterfaceDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v4.InterfaceIpv4ConfigDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v6.InterfaceIpv6ConfigDto;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Интеграционные смоук-тесты для парсера вывода {@code ip a}.
 *
 * <p>Проверяется целостность и корректность данных, полученных через {@link IpA#showInterfaces(com.jcraft.jsch.Session)}:
 * <ul>
 *   <li>успешная сериализация каждого интерфейса в валидный JSON без исключений;</li>
 *   <li>отсутствие нераспознанных строк и параметров на всех уровнях DTO (интерфейс, IPv4/IPv6, lifetime и пр.).</li>
 * </ul></p>
 *
 * <p>Тест использует активную SSH-сессию из {@link io.github.ganenkovna.ssh.TestBase}
 * и выводит отладочную информацию в консоль при обнаружении расхождений
 * (например, при наличии неизвестных параметров или ошибках сериализации).</p>
 *
 * <p>Класс не предназначен для модульного тестирования отдельных методов —
 * это сквозная проверка корректности связки парсера, DTO и сериализации.</p>
 *
 * @see IpA
 * @see io.github.ganenkovna.ssh.TestBase
 * @see io.github.ganenkovna.ssh.commands.ip.a.dto.InterfaceDto
 * @see io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v4.InterfaceIpv4ConfigDto
 * @see io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v6.InterfaceIpv6ConfigDto
 */
public class IpAInterfacesSmokeTest extends TestBase {
  /**
   * Локальный {@link ObjectMapper} для тестовой сериализации.
   *
   * <p>Включён {@link SerializationFeature#INDENT_OUTPUT} для удобной ручной инспекции JSON.
   * Используется только в тестах.</p>
   */
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT);

  /** Временный буфер для отладочного JSON. Никогда не {@code null}, может быть пустым. */
  private String json = "";

  /**
   * Список интерфейсов, полученных из вывода {@code ip a} для текущей SSH-сессии.
   *
   * <p>Инициализируется один раз при запуске тестов методом
   * {@link IpA#showInterfaces(com.jcraft.jsch.Session)} на основе {@link #currentSession}.</p>
   *
   * <p>Никогда не {@code null}, может быть пустым, если парсер не распознал ни одного интерфейса.</p>
   *
   * @see IpA#showInterfaces(com.jcraft.jsch.Session)
   * @see io.github.ganenkovna.ssh.commands.ip.a.dto.InterfaceDto
   */
  private static final List<InterfaceDto> interfaces = IpA.showInterfaces(currentSession);

  /**
   * Базовая проверка корректности списка интерфейсов перед каждым тестом.
   *
   * <p>Убеждается, что коллекция {@link #interfaces} успешно инициализирована и содержит хотя бы один элемент.
   * Вызывается автоматически перед каждым тестовым методом.</p>
   *
   * @throws AssertionError если {@link #interfaces} равен {@code null} или пуст
   * @see org.junit.jupiter.api.BeforeEach
   * @see IpA#showInterfaces(com.jcraft.jsch.Session)
   */
  @BeforeEach
  public void checkListOfInterfaces() {
    assertNotNull(interfaces, "Список интерфейсов равен null");
    assertFalse(interfaces.isEmpty(), "Список интерфейсов пуст");
  }

  /**
   * Сериализация каждого интерфейса в валидный JSON без исключений.
   *
   * <p>Для каждого распознанного интерфейса выполняется сериализация в строку JSON (pretty-print);
   * дополнительно проверяется, что базовые поля не {@code null}.</p>
   *
   * @throws RuntimeException при ошибках SSH, парсинга или сериализации в JSON
   * @see IpA#showInterfaces(com.jcraft.jsch.Session)
   * @see ObjectMapper
   */
  @Test
  @DisplayName("ip a → печать JSON (отладочно)")
  public void shouldOutputJson() {
    SoftAssertions softly = new SoftAssertions();

    for (InterfaceDto it : interfaces) {
      softly.assertThat(it.getInterfaceParams().getIndex())
          .as("index for %s", it)
          .isNotNull();
      softly.assertThat(it.getInterfaceParams().getName())
          .as("name for %s", it)
          .isNotNull();

      softly.assertThatCode(() -> {
        json = MAPPER.writeValueAsString(it);
        softly.assertThat(json)
            .as("JSON content for %s", it)
            .isNotNull()
            .isNotBlank();
        System.out.println("=== " + it.getInterfaceParams().getName() + " ===\n" + json);
      }).as("JSON serialization for %s", it)
          .doesNotThrowAnyException();
    }

    softly.assertAll();
  }

  /**
   * Проверка отсутствия «неизвестных параметров» после парсинга вывода {@code ip a}.
   *
   * <p>Для каждого интерфейса из списка {@link #interfaces} проверяется, что все коллекции
   * нераспознанных значений (unknown-параметров) пусты:
   * <ul>
   *   <li>{@link InterfaceDto#getUnknownLines()} — нераспознанные строки всего интерфейса;</li>
   *   <li>{@code InterfaceDto#getInterfaceParams().getUnknownParams()} — базовые параметры интерфейса;</li>
   *   <li>{@code InterfaceDto#getInterfacePhysicalParams().getUnknownParams()} — физические параметры;</li>
   *   <li>{@code InterfaceIpv4ConfigDto#getUnknownParams()} и {@code getLifeTimeParams().getUnknownParams()} — параметры IPv4;</li>
   *   <li>{@code InterfaceIpv6ConfigDto#getUnknownParams()} и {@code getLifeTimeParams().getUnknownParams()} — параметры IPv6.</li>
   * </ul></p>
   *
   * <p>Если хотя бы один неизвестный параметр найден, его имя и контекст выводятся в консоль,
   * а тест завершается с ошибкой, показывая полный список таких параметров.</p>
   *
   * @throws AssertionError если найдены неизвестные параметры
   * @see IpA
   * @see InterfaceDto
   * @see InterfaceIpv4ConfigDto
   * @see InterfaceIpv6ConfigDto
   */
  @Test
  @DisplayName("ip a → unknown params: пусто")
  public void shouldNotHaveUnknownParams() {
    final StringBuilder unknownParams = new StringBuilder();

    for (InterfaceDto it : interfaces) {
      final String name = it.getInterfaceParams().getName();

      // Нераспознанные строки всего интерфейса
      if (!it.getUnknownLines().isEmpty()) {
        for (String p : it.getUnknownLines()) {
          unknownParams.append(name).append(": ").append(p).append('\n');
        }
      }

      // Базовые параметры
      if (!it.getInterfaceParams().getUnknownParams().isEmpty()) {
        for (String p : it.getInterfaceParams().getUnknownParams()) {
          unknownParams.append(name).append(".InterfaceParam: ").append(p).append('\n');
        }
      }

      // Физические параметры
      if (!it.getInterfacePhysicalParams().getUnknownParams().isEmpty()) {
        for (String p : it.getInterfacePhysicalParams().getUnknownParams()) {
          unknownParams.append(name).append(".PhysicalParam: ").append(p).append('\n');
        }
      }

      // IPv4
      for (InterfaceIpv4ConfigDto v4 : it.getIpv4()) {
        if (!v4.getUnknownParams().isEmpty()) {
          for (String p : v4.getUnknownParams()) {
            unknownParams.append(name).append(".IPv4: ").append(p).append('\n');
          }
        }
        if (!v4.getLifeTimeParams().getUnknownParams().isEmpty()) {
          for (String p : v4.getLifeTimeParams().getUnknownParams()) {
            unknownParams.append(name).append(".IPv4.LifeTime: ").append(p).append('\n');
          }
        }
      }

      // IPv6
      for (InterfaceIpv6ConfigDto v6 : it.getIpv6()) {
        if (!v6.getUnknownParams().isEmpty()) {
          for (String p : v6.getUnknownParams()) {
            unknownParams.append(name).append(".IPv6: ").append(p).append('\n');
          }
        }
        if (!v6.getLifeTimeParams().getUnknownParams().isEmpty()) {
          for (String p : v6.getLifeTimeParams().getUnknownParams()) {
            unknownParams.append(name).append(".IPv6.LifeTime: ").append(p).append('\n');
          }
        }
      }
    }

    boolean hasUnknown = unknownParams.length() > 0;
    if (hasUnknown) {
      System.out.println("Найдены неизвестные параметры:\n" + unknownParams);
    }
    assertFalse(hasUnknown, unknownParams.toString());
  }
}
