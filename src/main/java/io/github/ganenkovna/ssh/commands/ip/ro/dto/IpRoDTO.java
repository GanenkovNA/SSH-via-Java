package io.github.ganenkovna.ssh.commands.ip.ro.dto;

import io.github.ganenkovna.util.ip.dto.RouteFlag;
import io.github.ganenkovna.util.ip.dto.RouteProtocol;
import io.github.ganenkovna.util.ip.dto.RouteScope;
import io.github.ganenkovna.util.ip.dto.RouteType;
import java.util.Map;
import java.util.Set;

/**
 * DTO, представляющий информацию о маршруте из вывода команды {@code ip -j route}.
 *
 * <p>Используется как результат парсинга JSON-вывода утилиты {@code ip},
 * отражая основные параметры маршрута: назначение, шлюз, устройство,
 * протокол/область видимости/тип, а также дополнительные признаки
 * (флаги, метрика и т.п.).</p>
 *
 * <p>Неизвестные или неподдерживаемые поля JSON-вывода сохраняются в
 * {@code unparsedParams} без дополнительной интерпретации.</p>
 *
 * @param dst
 *     Назначение маршрута.
 *     <p>Соответствует полю {@code dst} (например, {@code default}, {@code 192.168.1.0/24},
 *     {@code 2001:db8::/64}). Может быть {@code null}.</p>
 *
 * @param gateway
 *     Шлюз (next-hop) для маршрута.
 *     <p>Соответствует полю {@code gateway}.
 *     Может быть {@code null} (например, для on-link маршрутов).</p>
 *
 * @param dev
 *     Имя сетевого устройства, через которое применяется маршрут.
 *     <p>Соответствует полю {@code dev}. Может быть {@code null}.</p>
 *
 * @param protocol
 *     Протокол источника маршрута.
 *     <p>Соответствует полю {@code protocol}. Может быть {@code null},
 *     если значение неизвестно или не распознано.</p>
 *
 * @param scope
 *     Область видимости маршрута.
 *     <p>Соответствует полю {@code scope}. Может быть {@code null},
 *     если значение неизвестно или не распознано.</p>
 *
 * @param type
 *     Тип маршрута.
 *     <p>Соответствует полю {@code type}. Может быть {@code null},
 *     если значение неизвестно или не распознано.</p>
 *
 * @param prefsrc
 *     Предпочитаемый исходный адрес.
 *     <p>Соответствует полю {@code prefsrc}. Может быть {@code null}.</p>
 *
 * @param flags
 *     Набор флагов маршрута.
 *     <p>Соответствует полю {@code flags}. Никогда не {@code null}, но может быть пустым.</p>
 *
 * @param metric
 *     Метрика маршрута.
 *     <p>Соответствует полю {@code metric}. Может быть {@code null}.</p>
 *
 * @param unparsedParams
 *     Карта дополнительных полей JSON, не сопоставленных с явными компонентами DTO.
 *     <p>Никогда не {@code null}, может быть пустой.
 *     Ключ — имя поля JSON, значение — его десериализованное представление
 *     в виде {@link Object} без дополнительной интерпретации.</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/ip-route.8.html">ip-route(8)</a>
 * @see RouteProtocol
 * @see RouteScope
 * @see RouteType
 * @see RouteFlag
 */
public record IpRoDTO(
    String dst,
    String gateway,
    String dev,
    RouteProtocol protocol,
    RouteScope scope,
    RouteType type,
    String prefsrc,
    Set<RouteFlag> flags,
    Integer metric,
    Map<String, Object> unparsedParams
) {}
