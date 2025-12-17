package io.github.ganenkovna.ssh.commands.bridge.link.dto;

import io.github.ganenkovna.util.ip.dto.StpPortState;
import io.github.ganenkovna.util.ip.dto.InterfaceFlag;
import java.util.Map;
import java.util.Set;

/**
 * DTO, представляющий информацию о порте Linux bridge (slave-интерфейсе)
 * из вывода команды {@code bridge -j link}.
 *
 * <p>Используется как результат парсинга JSON-вывода утилиты {@code bridge},
 * отражая состояние портов Linux bridge (slave-интерфейсов), включая STP-состояние,
 * приоритет и стоимость пути.</p>
 *
 * <p>Экземпляры этого {@code record} являются иммутабельными и предназначены
 * исключительно для передачи данных (DTO). Валидация значений выполняется
 * на этапе парсинга и десериализации.</p>
 *
 * <p>Неизвестные или неподдерживаемые поля JSON-вывода сохраняются в {@code unparsedParams}
 * без интерпретации, чтобы не терять данные при изменениях формата команды
 * {@code bridge}.</p>
 *
 * @param ifindex
 *     Числовой индекс сетевого интерфейса в системе.
 *     <p>Соответствует полю {@code ifindex} из вывода {@code bridge -j link}.
 *     Может быть {@code null}, если поле отсутствует в JSON.</p>
 *
 * @param ifname
 *     Имя сетевого интерфейса (например, {@code eth1}).
 *     <p>Соответствует полю {@code ifname}. Может быть {@code null}.</p>
 *
 * @param flags
 *     Набор флагов интерфейса.
 *     <p>Соответствует полю {@code flags}. Никогда не {@code null},
 *     но может быть пустым.</p>
 *
 * @param mtu
 *     Значение MTU (Maximum Transmission Unit) интерфейса.
 *     <p>Соответствует полю {@code mtu}. Может быть {@code null}.</p>
 *
 * @param master
 *     Имя bridge-интерфейса, к которому подключён данный порт.
 *     <p>Соответствует полю {@code master}. Может быть {@code null},
 *     если интерфейс не входит в bridge.</p>
 *
 * @param state
 *     STP-состояние порта bridge.
 *     <p>Соответствует полю {@code state} из {@code bridge link}
 *     (например, {@code forwarding}, {@code blocking}). Может быть {@code null},
 *     если значение неизвестно или не распознано.</p>
 *
 * @param priority
 *     Приоритет порта в алгоритме STP.
 *     <p>Соответствует полю {@code priority}. Может быть {@code null}.</p>
 *
 * @param cost
 *     Стоимость пути (path cost) порта в STP.
 *     <p>Соответствует полю {@code cost}. Может быть {@code null}.</p>
 *
 * @param unparsedParams
 *     Карта дополнительных полей JSON, не сопоставленных с явными компонентами DTO.
 *     <p>Никогда не {@code null}, может быть пустой.
 *     Ключ — имя поля JSON, значение — его десериализованное представление
 *     в виде {@link Object} без дополнительной интерпретации.</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/bridge.8.html">
 *     bridge(8) — Linux manual page</a>
 * @see StpPortState
 * @see InterfaceFlag
 */
public record BridgeLinkPortDTO(
    Integer ifindex,
    String ifname,
    Set<InterfaceFlag> flags,
    Integer mtu,
    String master,
    StpPortState state,
    Integer priority,
    Integer cost,
    Map<String, Object> unparsedParams
) {}
