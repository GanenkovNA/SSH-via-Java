package io.github.ganenkovna.ssh.commands.bridge.link.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.ganenkovna.util.ip.dto.InterfaceFlag;
import io.github.ganenkovna.util.ip.dto.StpPortState;
import io.github.ganenkovna.util.ip.dto.service.CaptureUnknown;
import io.github.ganenkovna.util.ip.dto.service.InterfaceFlagDeserializer;
import io.github.ganenkovna.util.ip.dto.service.StpPortStateDeserializer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Вспомогательный POJO для десериализации вывода команды {@code bridge -j link}.
 *
 * <p>Класс используется исключительно как промежуточная модель для Jackson
 * при парсинге JSON-вывода утилиты {@code bridge}. После десериализации
 * экземпляры, как правило, преобразуются в более строгие и иммутабельные DTO.</p>
 *
 * <p>Поддерживает «мягкий» режим парсинга:
 * известные поля отображаются в соответствующие свойства,
 * неизвестные JSON-поля и нераспознанные значения enum-полей
 * сохраняются в карту {@link #unparsedParams} без выбрасывания исключений.</p>
 *
 * <p>Для отдельных полей используются кастомные десериализаторы:
 * {@link InterfaceFlagDeserializer} и {@link StpPortStateDeserializer},
 * которые выполняют нормализацию значений и перехват неизвестных enum-значений.</p>
 *
 * <p>Все поля являются nullable и отражают фактический JSON,
 * полученный от {@code bridge -j link}; отсутствие значения в JSON
 * приводит к {@code null} в соответствующем поле.</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/bridge.8.html">bridge(8)</a>
 * @see InterfaceFlagDeserializer
 * @see StpPortStateDeserializer
 */
@Data
public final class BridgeLinkPortPOJO implements CaptureUnknown {

  /** Индекс сетевого интерфейса (поле {@code ifindex}). */
  private Integer ifindex;

  /** Имя сетевого интерфейса (поле {@code ifname}). */
  private String ifname;

  /**
   * Набор флагов интерфейса.
   *
   * <p>Десериализуется с использованием {@link InterfaceFlagDeserializer}.
   * Может быть {@code null}, если поле отсутствует в JSON
   * или если все значения флагов оказались неизвестными.</p>
   */
  @JsonDeserialize(using = InterfaceFlagDeserializer.class)
  private Set<InterfaceFlag> flags;

  /** Значение MTU интерфейса. */
  private Integer mtu;

  /** Имя master-интерфейса (моста), если интерфейс является его портом. */
  private String master;

  /**
   * STP-состояние порта моста.
   *
   * <p>Десериализуется с использованием {@link StpPortStateDeserializer}.
   * В случае неизвестного значения поле будет {@code null},
   * а исходное значение сохранено в {@link #unparsedParams}.</p>
   */
  @JsonDeserialize(using = StpPortStateDeserializer.class)
  private StpPortState state;

  /** Приоритет порта в контексте STP. */
  private Integer priority;

  /** Стоимость пути (path cost) порта в STP. */
  private Integer cost;

  /**
   * Карта неизвестных полей и значений.
   *
   * <p>Содержит:
   * <ul>
   *   <li>JSON-поля, отсутствующие в модели класса;</li>
   *   <li>значения enum-полей, которые не удалось распарсить.</li>
   * </ul>
   *
   * <p>Карта никогда не {@code null}, может быть пустой.
   * Заполняется автоматически через {@link #captureUnknown(String, Object)}.</p>
   */
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Setter(AccessLevel.NONE)
  private final Map<String, Object> unparsedParams = new HashMap<>();

  /**
   * Перехватывает неизвестные поля и значения при десериализации JSON.
   *
   * <p>Метод вызывается Jackson:
   * <ul>
   *   <li>для JSON-полей, отсутствующих в модели класса
   *       (через {@link JsonAnySetter});</li>
   *   <li>из кастомных десериализаторов при обнаружении
   *       неизвестных значений enum-полей.</li>
   * </ul>
   *
   * <p>Ключом выступает имя JSON-поля либо логический ключ, заданный десериализатором.</p>
   *
   * @param key имя JSON-поля либо логический ключ,
   *            заданный десериализатором для нераспознанного значения
   * @param value исходное значение, полученное из JSON
   */
  @JsonAnySetter
  @Override
  public void captureUnknown(String key, Object value) {
    unparsedParams.put(key, value);
  }
}
