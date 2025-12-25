package io.github.ganenkovna.ssh.commands.ip.a.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.ganenkovna.util.ip.dto.IpV4AddressFlag;
import io.github.ganenkovna.util.ip.dto.IpV4Scope;
import io.github.ganenkovna.util.ip.dto.service.CaptureUnknown;
import io.github.ganenkovna.util.ip.dto.service.IpV4AddressFlagDeserializer;
import io.github.ganenkovna.util.ip.dto.service.IpV4ScopeDeserializer;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Информация об IPv4-адресе сетевого интерфейса из вывода команды {@code ip -j a}.
 *
 * <p>Класс представляет один элемент массива {@code addr_info} для адресов
 * семейства {@code inet} (IPv4). Содержит параметры адреса, его область действия,
 * флаги состояния и времена жизни.</p>
 *
 * <p>Экземпляры данного класса создаются Jackson при разборе JSON-вывода команды
 * {@code ip -j a} и не предназначены для ручного конструирования.</p>
 *
 * <p>Класс <strong>не потокобезопасен</strong> и используется как DTO.</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/ip-address.8.html">ip-address(8)</a>
 * @see AddrInfoPojo
 */
@Data
public final class InetAddrInfoPojo implements AddrInfoPojo, CaptureUnknown {

  /**
   * Семейство адреса.
   *
   * <p>Для данного DTO ожидаемое значение — {@code inet} (IPv4).</p>
   */
  private String family;

  /**
   * IPv4-адрес интерфейса.
   *
   * <p>Задаётся в стандартной точечной нотации {@code x.x.x.x}
   * без указания префикса.</p>
   */
  private String local;

  /**
   * Длина сетевого префикса.
   *
   * <p>Соответствует CIDR-префиксу IPv4-адреса.
   * Допустимый диапазон значений: от {@code 0} до {@code 32} включительно.</p>
   */
  private Integer prefixlen;

  /**
   * Широковещательный IPv4-адрес (broadcast).
   *
   * <p>Присутствует не для всех адресов и может быть {@code null},
   * например, для адресов с {@code scope host}.</p>
   */
  private String broadcast;

  /**
   * Область действия IPv4-адреса (scope).
   *
   * <p>Определяет, в каком контексте адрес считается достижимым
   * (например, {@code global}, {@code link}, {@code host}).</p>
   *
   * @see IpV4Scope
   */
  @JsonDeserialize(using = IpV4ScopeDeserializer.class)
  private IpV4Scope scope;

  /**
   * Метка интерфейса (label), ассоциированная с адресом.
   *
   * <p>Как правило, совпадает с именем интерфейса
   * (например, {@code eth0}) либо содержит суффикс
   * для алиасов.</p>
   */
  private String label;

  /**
   * Флаги состояния IPv4-адреса.
   *
   * <p>Отражают дополнительные характеристики адреса, такие как
   * {@code dynamic}, {@code noprefixroute} и другие.</p>
   *
   * <p>Коллекция никогда не {@code null}, может быть пустой.</p>
   *
   * @see IpV4AddressFlag
   */
  @JsonDeserialize(using = IpV4AddressFlagDeserializer.class)
  private EnumSet<IpV4AddressFlag> flags;

  /**
   * Время жизни адреса до перехода в недействительное состояние.
   *
   * <p>Задаётся в секундах. Значение {@code 4294967295}
   * используется ядром Linux для обозначения «бесконечного»
   * времени жизни.</p>
   */
  @JsonProperty("valid_life_time")
  private Long validLifeTime;

  /**
   * Предпочтительное время жизни адреса.
   *
   * <p>По истечении этого времени адрес считается устаревшим,
   * но может продолжать использоваться до окончания
   * {@link #validLifeTime}.</p>
   */
  @JsonProperty("preferred_life_time")
  private Long preferredLifeTime;

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
