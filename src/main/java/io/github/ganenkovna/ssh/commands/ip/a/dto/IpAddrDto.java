package io.github.ganenkovna.ssh.commands.ip.a.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.ganenkovna.util.ip.dto.InterfaceFlag;
import io.github.ganenkovna.util.ip.dto.InterfaceState;
import io.github.ganenkovna.util.ip.dto.QdiscType;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * DTO, представляющий сетевой интерфейс из JSON-вывода команды {@code ip -j address}
 * (сокращённо {@code ip -j a}).
 *
 * <p>Запись содержит как базовые параметры интерфейса (имя, индекс, MTU, состояние),
 * так и список IP-адресов, назначенных интерфейсу. DTO является неизменяемым
 * и предназначен для передачи данных после парсинга вывода {@code ip}.</p>
 *
 * <p>Неизвестные или неподдерживаемые поля JSON сохраняются в
 * {@code unparsedParams} для отладки и обратной совместимости
 * с новыми версиями утилиты {@code ip}.</p>
 *
 * <p>DTO не выполняет валидацию значений и предполагает,
 * что проверка форматов и диапазонов производится на этапе парсинга
 * или в вызывающем коде.</p>
 *
 * @param ifindex
 *     уникальный индекс интерфейса (ifindex) в ядре Linux;
 *     может быть {@code null}, если поле отсутствует в выводе
 * @param link
 *     имя родительского интерфейса (например, для VLAN или macvlan);
 *     может быть {@code null}
 * @param ifname
 *     имя сетевого интерфейса (например, {@code eth0}, {@code lo});
 *     может быть {@code null}
 * @param flags
 *     набор флагов состояния интерфейса;
 *     может быть {@code null}, если флаги не распознаны или отсутствуют
 * @param mtu
 *     значение MTU (Maximum Transmission Unit) в байтах;
 *     может быть {@code null}
 * @param qdisc
 *     тип очереди (qdisc), назначенной интерфейсу;
 *     может быть {@code null}, если тип не распознан
 * @param operstate
 *     текущее операционное состояние интерфейса;
 *     может быть {@code null}, если состояние неизвестно
 * @param group
 *     группа интерфейса (например, {@code default});
 *     может быть {@code null}
 * @param txqlen
 *     длина очереди передачи (txqueuelen);
 *     может быть {@code null}
 * @param linkType
 *     тип канального уровня (например, {@code ether}, {@code loopback});
 *     может быть {@code null}
 * @param address
 *     MAC-адрес интерфейса в строковом представлении;
 *     может быть {@code null}
 * @param broadcast
 *     широковещательный MAC-адрес интерфейса;
 *     может быть {@code null}
 * @param addrInfo
 *     список IP-адресов, назначенных интерфейсу;
 *     никогда не {@code null}, может быть пустым
 * @param unparsedParams
 *     карта неизвестных или неподдерживаемых параметров JSON;
 *     никогда не {@code null}, может быть пустой
 *
 * @see AddrInfoDto
 * @see InterfaceFlag
 * @see InterfaceState
 * @see QdiscType
 */
public record IpAddrDto(
    Integer ifindex,
    String link,
    String ifname,
    EnumSet<InterfaceFlag> flags,
    Integer mtu,
    QdiscType qdisc,
    InterfaceState operstate,
    String group,
    Integer txqlen,
    @JsonProperty("link_type")
    String linkType,
    String address,
    String broadcast,
    @JsonProperty("addr_info")
    List<AddrInfoDto> addrInfo,
    Map<String, Object> unparsedParams
) {}
