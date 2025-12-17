package io.github.ganenkovna.ssh.commands.ip.ro.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.ganenkovna.util.ip.dto.RouteFlag;
import io.github.ganenkovna.util.ip.dto.RouteProtocol;
import io.github.ganenkovna.util.ip.dto.RouteScope;
import io.github.ganenkovna.util.ip.dto.RouteType;
import io.github.ganenkovna.util.ip.dto.service.CaptureUnknown;
import io.github.ganenkovna.util.ip.dto.service.RouteFlagDeserializer;
import io.github.ganenkovna.util.ip.dto.service.RouteProtocolDeserializer;
import io.github.ganenkovna.util.ip.dto.service.RouteScopeDeserializer;
import io.github.ganenkovna.util.ip.dto.service.RouteTypeDeserializer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Вспомогательный POJO для десериализации JSON-вывода команды {@code ip -j ro}.
 *
 * <p>Класс используется исключительно как промежуточная модель для Jackson
 * при парсинге JSON-вывода утилиты {@code ip}. После десериализации экземпляры,
 * как правило, преобразуются в более строгие и иммутабельные DTO.</p>
 *
 * <p>Поддерживает «мягкий» режим парсинга:
 * известные поля отображаются в соответствующие свойства,
 * неизвестные JSON-поля и нераспознанные значения enum-полей сохраняются
 * в карту {@link #unparsedParams} без выбрасывания исключений.</p>
 *
 * <p>Для отдельных полей используются кастомные десериализаторы:
 * {@link RouteProtocolDeserializer}, {@link RouteScopeDeserializer},
 * {@link RouteTypeDeserializer}, {@link RouteFlagDeserializer},
 * которые выполняют нормализацию значений и перехват неизвестных enum-значений.</p>
 *
 * <p>Все поля являются nullable и отражают фактический JSON,
 * полученный от {@code ip -j ro}; отсутствие значения в JSON
 * приводит к {@code null} в соответствующем поле.</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/ip-route.8.html">ip-route(8)</a>
 * @see RouteProtocolDeserializer
 * @see RouteScopeDeserializer
 * @see RouteTypeDeserializer
 * @see RouteFlagDeserializer
 */
@Data
public final class IpRoPOJO implements CaptureUnknown {

  /**
   * Назначение маршрута.
   *
   * <p>Соответствует полю {@code dst} (например, {@code default},
   * {@code 192.168.1.0/24}, {@code 2001:db8::/64}). Может быть {@code null}.</p>
   */
  private String dst;

  /**
   * Адрес следующего узла (gateway).
   *
   * <p>Соответствует полю {@code gateway}. Может быть {@code null},
   * если маршрут не использует шлюз.</p>
   */
  private String gateway;

  /**
   * Выходной интерфейс маршрута.
   *
   * <p>Соответствует полю {@code dev} (например, {@code eth0}). Может быть {@code null}.</p>
   */
  private String dev;

  /**
   * Протокол источника маршрута.
   *
   * <p>Десериализуется с использованием {@link RouteProtocolDeserializer}.
   * В случае неизвестного значения поле будет {@code null},
   * а исходное значение сохранено в {@link #unparsedParams}.</p>
   */
  @JsonDeserialize(using = RouteProtocolDeserializer.class)
  private RouteProtocol protocol;

  /**
   * Область видимости маршрута.
   *
   * <p>Десериализуется с использованием {@link RouteScopeDeserializer}.
   * В случае неизвестного значения поле будет {@code null},
   * а исходное значение сохранено в {@link #unparsedParams}.</p>
   */
  @JsonDeserialize(using = RouteScopeDeserializer.class)
  private RouteScope scope;

  /**
   * Тип маршрута.
   *
   * <p>Десериализуется с использованием {@link RouteTypeDeserializer}.
   * В случае неизвестного значения поле будет {@code null},
   * а исходное значение сохранено в {@link #unparsedParams}.</p>
   */
  @JsonDeserialize(using = RouteTypeDeserializer.class)
  private RouteType type;

  /**
   * Предпочтительный исходный адрес для маршрута.
   *
   * <p>Соответствует полю {@code prefsrc}. Может быть {@code null}.</p>
   */
  private String prefsrc;

  /**
   * Набор флагов маршрута.
   *
   * <p>Десериализуется с использованием {@link RouteFlagDeserializer}.
   * Может быть {@code null}, если поле отсутствует в JSON
   * или если все значения флагов оказались неизвестными.</p>
   */
  @JsonDeserialize(using = RouteFlagDeserializer.class)
  private Set<RouteFlag> flags;

  /**
   * Метрика маршрута.
   *
   * <p>Соответствует полю {@code metric}. Может быть {@code null}.</p>
   */
  private Integer metric;

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
