package io.github.ganenkovna.ssh.commands.ip.a.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.ganenkovna.util.ip.dto.IpV6Scope;
import io.github.ganenkovna.util.ip.dto.service.CaptureUnknown;
import io.github.ganenkovna.util.ip.dto.service.IpV6ScopeDeserializer;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * DTO, представляющий информацию об IPv6-адресе сетевого интерфейса
 * из JSON-вывода команды {@code ip -j address}.
 *
 * <p>Экземпляры данного класса соответствуют элементам массива
 * {@code addr_info} с {@code family = inet6} и содержат параметры
 * IPv6-адреса, назначенного интерфейсу: сам адрес, длину префикса,
 * область видимости и времена жизни.</p>
 *
 * <p>Класс используется исключительно как DTO для десериализации JSON
 * и не выполняет строгую валидацию значений. Проверки форматов и диапазонов
 * выполняются на более высоком уровне.</p>
 *
 * <p>Неизвестные поля и нераспознанные значения enum-полей сохраняются
 * в {@link #unparsedParams} через механизм {@link CaptureUnknown}.</p>
 *
 * <p><strong>Потокобезопасность:</strong> класс не является потокобезопасным.</p>
 *
 * @see AddrInfoPojo
 * @see IpV6Scope
 * @see <a href="https://man7.org/linux/man-pages/man8/ip-address.8.html">ip-address(8)</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc5952">RFC&nbsp;5952</a>
 */
@Data
public final class Inet6AddrInfoPojo implements AddrInfoPojo, CaptureUnknown {

  /**
   * Семейство адреса.
   *
   * <p>Для IPv6-адресов ожидаемое значение — {@code inet6}.</p>
   *
   * <p>Может быть {@code null}, если поле отсутствует в JSON.</p>
   */
  private String family;

  /**
   * IPv6-адрес интерфейса.
   *
   * <p>Представлен в канонической текстовой форме согласно
   * {@link <a href="https://datatracker.ietf.org/doc/html/rfc5952">RFC&nbsp;5952</a>}.</p>
   *
   * <p>Может быть {@code null}, если адрес отсутствует в выводе.</p>
   */
  private String local;

  /**
   * Длина префикса IPv6-адреса.
   *
   * <p>Ожидаемый диапазон значений — от {@code 0} до {@code 128} включительно.</p>
   *
   * <p>Может быть {@code null}, если поле отсутствует в JSON.</p>
   */
  private Integer prefixlen;

  /**
   * Область видимости IPv6-адреса.
   *
   * <p>Определяет применимость адреса (host, link, global и т.д.).</p>
   *
   * <p>Если значение не удалось распознать, оно сохраняется
   * в {@link #unparsedParams}, а поле принимает значение {@code null}.</p>
   *
   * @see IpV6Scope
   */
  @JsonDeserialize(using = IpV6ScopeDeserializer.class)
  private IpV6Scope scope;

  /**
   * Время жизни адреса до полной недействительности.
   *
   * <p>Значение задаётся в секундах. Специальные значения
   * (например, {@code 4294967295}) могут использоваться ядром
   * Linux для обозначения «бессрочного» адреса.</p>
   *
   * <p>Может быть {@code null}, если поле отсутствует в JSON.</p>
   */
  @JsonProperty("valid_life_time")
  private Long validLifeTime;

  /**
   * Предпочтительное время жизни адреса.
   *
   * <p>По истечении этого времени адрес остаётся валидным,
   * но перестаёт использоваться для исходящих соединений.</p>
   *
   * <p>Может быть {@code null}, если поле отсутствует в JSON.</p>
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
