package io.github.ganenkovna.ssh.commands.ip.a.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.ganenkovna.util.ip.dto.InterfaceFlag;
import io.github.ganenkovna.util.ip.dto.InterfaceState;
import io.github.ganenkovna.util.ip.dto.QdiscType;
import io.github.ganenkovna.util.ip.dto.service.CaptureUnknown;
import io.github.ganenkovna.util.ip.dto.service.InterfaceFlagDeserializer;
import io.github.ganenkovna.util.ip.dto.service.InterfaceStateDeserializer;
import io.github.ganenkovna.util.ip.dto.service.QdiscTypeDeserializer;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * DTO, представляющий одну запись интерфейса из JSON-вывода команды {@code ip -j a}.
 *
 * <p>Соответствует объекту верхнего уровня в массиве, возвращаемом
 * командой {@code ip --color=never -j address} (или {@code ip -j a}).</p>
 *
 * <p>Содержит:</p>
 * <ul>
 *   <li>общие параметры сетевого интерфейса (имя, индекс, MTU, состояние);</li>
 *   <li>флаги и типы, нормализуемые в enum-представления;</li>
 *   <li>список IPv4/IPv6-адресов интерфейса ({@link AddrInfoPojo});</li>
 *   <li>неизвестные или нераспознанные параметры, сохранённые без потери данных.</li>
 * </ul>
 *
 * <p>Класс предназначен исключительно для хранения данных и
 * <strong>не выполняет валидацию или нормализацию значений</strong>,
 * за исключением преобразования enum-полей при десериализации.</p>
 *
 * <p>Экземпляры класса <strong>не потокобезопасны</strong>.</p>
 *
 * @see AddrInfoPojo
 * @see InterfaceFlag
 * @see InterfaceState
 * @see QdiscType
 * @see <a href="https://man7.org/linux/man-pages/man8/ip-address.8.html">ip-address(8)</a>
 */
@Data
public final class IpAddrPojo implements CaptureUnknown {

  /**
   * Уникальный индекс интерфейса (ifindex) в ядре Linux.
   *
   * <p>Используется ядром для однозначной идентификации интерфейса
   * в системных вызовах и netlink-сообщениях.</p>
   */
  private Integer ifindex;

  /**
   * Имя связанного (родительского) интерфейса.
   *
   * <p>Присутствует, например, для VLAN, bridge-портов и других
   * логических интерфейсов.</p>
   *
   * <p>Может быть {@code null}, если интерфейс не имеет родителя.</p>
   */
  private String link;

  /**
   * Имя сетевого интерфейса (например, {@code eth0}, {@code lo}, {@code br0}).
   */
  private String ifname;

  /**
   * Набор флагов состояния интерфейса.
   *
   * <p>Соответствуют битовой маске {@code IFF_*}, определённой
   * в {@code <linux/if.h>}.</p>
   *
   * <p>Коллекция никогда не {@code null}, может быть пустой.</p>
   *
   * @see InterfaceFlag
   */
  @JsonDeserialize(using = InterfaceFlagDeserializer.class)
  private EnumSet<InterfaceFlag> flags;

  /**
   * Maximum Transmission Unit (MTU) интерфейса в байтах.
   *
   * <p>Типичные значения:</p>
   * <ul>
   *   <li>Ethernet — {@code 1500};</li>
   *   <li>Jumbo Frames — до {@code 9000};</li>
   *   <li>минимум: {@code 68} для IPv4 и {@code 1280} для IPv6.</li>
   * </ul>
   */
  private Integer mtu;

  /**
   * Тип дисциплины очереди (qdisc), применяемой к интерфейсу.
   *
   * <p>Определяет алгоритм управления очередью передачи пакетов
   * (например, {@code noqueue}, {@code pfifo_fast}, {@code fq_codel}).</p>
   *
   * @see QdiscType
   */
  @JsonDeserialize(using = QdiscTypeDeserializer.class)
  private QdiscType qdisc;

  /**
   * Операционное состояние интерфейса.
   *
   * <p>Отражает текущее состояние с точки зрения ядра Linux
   * (например, {@code UP}, {@code DOWN}, {@code UNKNOWN}).</p>
   *
   * @see InterfaceState
   */
  @JsonDeserialize(using = InterfaceStateDeserializer.class)
  private InterfaceState operstate;

  /**
   * Группа интерфейсов (ifgroup).
   *
   * <p>Используется для логической группировки интерфейсов.
   * По умолчанию обычно имеет значение {@code default}.</p>
   */
  private String group;

  /**
   * Длина очереди передачи (transmit queue length).
   *
   * <p>Если {@code null}, используется значение по умолчанию,
   * определяемое ядром Linux.</p>
   */
  private Integer txqlen;

  /**
   * Тип канального уровня интерфейса.
   *
   * <p>Примеры значений: {@code ether}, {@code loopback},
   * {@code vlan}, {@code bridge}.</p>
   */
  @JsonProperty("link_type")
  private String linkType;

  /**
   * MAC-адрес интерфейса.
   *
   * <p>Формат: {@code xx:xx:xx:xx:xx:xx}.</p>
   *
   * <p>Может быть {@code null} для виртуальных или
   * псевдо-интерфейсов.</p>
   */
  private String address;

  /**
   * Широковещательный MAC-адрес интерфейса.
   *
   * <p>Обычно имеет значение {@code ff:ff:ff:ff:ff:ff},
   * но может отличаться в зависимости от типа интерфейса.</p>
   */
  private String broadcast;

  /**
   * Широковещательный MAC-адрес интерфейса.
   *
   * <p>Обычно имеет значение {@code ff:ff:ff:ff:ff:ff},
   * но может отличаться в зависимости от типа интерфейса.</p>
   */
  @JsonProperty("addr_info")
  private List<AddrInfoPojo> addrInfo;

  /**
   * Карта неизвестных или нераспознанных параметров.
   *
   * <p>Содержит:</p>
   * <ul>
   *   <li>JSON-поля, отсутствующие в модели класса;</li>
   *   <li>значения enum-полей, которые не удалось сопоставить
   *       с известными значениями.</li>
   * </ul>
   *
   * <p>Карта никогда не {@code null}, может быть пустой.</p>
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
