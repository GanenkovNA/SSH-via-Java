package io.github.ganenkovna.ssh.commands.bridge.link;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.ganenkovna.ssh.TestBase;
import io.github.ganenkovna.ssh.commands.bridge.link.dto.BridgeLinkPortDTO;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Интеграционные смоук-тесты для выполнения и парсинга вывода {@code bridge -j link}.
 *
 * <p>Проверяется целостность данных, полученных через {@link BridgeLink#showBridgePorts(com.jcraft.jsch.Session)}:
 * <ul>
 *   <li>команда успешно выполняется по SSH и возвращает непустой список портов;</li>
 *   <li>результат сериализуется в JSON без ошибок (для удобства отладки выводится в консоль);</li>
 *   <li>в DTO отсутствуют нераспознанные параметры ({@code unparsedParams});</li>
 *   <li>все компоненты {@code record} не содержат {@code null}-значений.</li>
 * </ul></p>
 *
 * <p>Тесты используют активную SSH-сессию из {@link TestBase} (поле {@code currentSession}).</p>
 *
 * @see BridgeLink
 * @see BridgeLinkPortDTO
 * @see TestBase
 */
public class BridgeLinkSmokeTests extends TestBase {

  /**
   * Выполняет {@code bridge -j link} по SSH и печатает результат в консоль в виде форматированного JSON.
   *
   * <p>Тест дополнительно проверяет, что результат парсинга не {@code null} и не пустой.
   * Форматирование JSON включено только для читаемого диагностического вывода.</p>
   *
   * @throws JsonProcessingException если сериализация результата в JSON завершилась ошибкой
   * @see BridgeLink#showBridgePorts(com.jcraft.jsch.Session)
   * @see ObjectMapper
   */
  @Test
  public void shouldExecBridgeLinkAndOutput() throws JsonProcessingException {
    final List<BridgeLinkPortDTO> result = BridgeLink.showBridgePorts(currentSession);
    Assertions.assertNotNull(result, "Список bridge портов - null!");
    Assertions.assertFalse(result.isEmpty(),
        "Получен пустой список bridge портов!");

    ObjectMapper mapper = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);
    System.out.println(mapper.writeValueAsString(result));
  }

  /**
   * Проверяет, что при парсинге {@code bridge -j link} не остаётся неизвестных/неподдерживаемых полей.
   *
   * <p>Если утилита {@code bridge} вернула поля, которые не покрыты DTO/десериализацией,
   * они попадают в {@link BridgeLinkPortDTO#unparsedParams()}.
   * Тест падает, если {@code unparsedParams} не пустой хотя бы у одного порта.</p>
   *
   * @see BridgeLinkPortDTO#unparsedParams()
   * @see BridgeLink#showBridgePorts(com.jcraft.jsch.Session)
   */
  @Test
  public void shouldNotContainUnparsedParams() {
    final List<BridgeLinkPortDTO> result = BridgeLink.showBridgePorts(currentSession);
    Assertions.assertNotNull(result, "Список bridge портов - null!");
    Assertions.assertFalse(result.isEmpty(),
        "Получен пустой список bridge портов!");

    SoftAssertions softly = new SoftAssertions();

    for(BridgeLinkPortDTO port : result) {
      final String portId = identifyPort(port);
      if(portId == null) {
        softly.fail("Невозможно идентифицировать порт: `ifname` и `ifindex` пустые/null");
        continue;
      }

      if (!port.unparsedParams().isEmpty()) {
        softly.assertThat(port.unparsedParams())
            .as("Нераспознанные параметры у порта `%s`", portId)
            .isEmpty();
      }
    }

    softly.assertAll();
  }

  /**
   * Проверяет, что ни один компонент {@link BridgeLinkPortDTO} не равен {@code null}.
   *
   * <p>Проверка выполняется отражением по всем {@code record}-компонентам.
   * Если компонент прочитать не удалось, тест фиксирует ошибку через мягкие проверки
   * (без немедленного прерывания на первом же сбое).</p>
   *
   * @see BridgeLink#showBridgePorts(com.jcraft.jsch.Session)
   * @see BridgeLinkPortDTO
   */
  @Test
  public void shouldNotContainNullValues() {
    final List<BridgeLinkPortDTO> result = BridgeLink.showBridgePorts(currentSession);
    Assertions.assertNotNull(result, "Список bridge портов - null!");
    Assertions.assertFalse(result.isEmpty(),
        "Получен пустой список bridge портов!");

    SoftAssertions softly = new SoftAssertions();

    for(BridgeLinkPortDTO port : result) {
      final String portId = identifyPort(port);
      if(portId == null) {
        softly.fail("Невозможно идентифицировать порт: `ifname` и `ifindex` пустые/null");
        continue;
      }

      for (var c : port.getClass().getRecordComponents()) {
        try{
          softly.assertThat(c.getAccessor().invoke(port))
              .as("Порт `%s`, ключ `%s`", portId, c.getName())
              .isNotNull();
        } catch (ReflectiveOperationException e) {
          softly.fail("Порт `%s`: не удалось прочитать поле `%s`: %s",
              portId, c.getName(), e.toString());
        }
      }
    }
    softly.assertAll();
  }

  /**
   * Строит человекочитаемый идентификатор порта для сообщений об ошибках.
   *
   * <p>Приоритет:
   * <ol>
   *   <li>{@link BridgeLinkPortDTO#ifname()} — если не {@code null} и не пробельная;</li>
   *   <li>{@link BridgeLinkPortDTO#ifindex()} — если не {@code null}.</li>
   * </ol></p>
   *
   * @param port порт; строго не {@code null}
   * @return идентификатор порта для логов/ассертов или {@code null}, если идентифицировать порт не удалось
   * @throws NullPointerException если {@code port == null}
   * @see BridgeLinkPortDTO#ifname()
   * @see BridgeLinkPortDTO#ifindex()
   */
  private static String identifyPort(BridgeLinkPortDTO port) {
    String name = port.ifname();
    Integer idx = port.ifindex();

    if (name != null && !name.isBlank()) {
      return name;
    }
    if (idx != null) {
      return "ifindex=" + idx;
    }
    return null;
  }
}
