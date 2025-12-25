package io.github.ganenkovna.ssh.commands.ip.a.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.ganenkovna.util.ip.dto.IpV4AddressFlag;
import io.github.ganenkovna.util.ip.dto.IpV4Scope;
import java.util.EnumSet;
import java.util.Map;

/**
 * DTO с информацией об IPv4-адресе сетевого интерфейса
 * из JSON-вывода команды {@code ip -j address}.
 *
 * <p>Соответствует элементу массива {@code addr_info} для адресов
 * семейства {@code inet}. Содержит параметры IP-адреса, префикса,
 * области видимости, времени жизни и дополнительных флагов.</p>
 *
 * <p>Экземпляры данного record являются неизменяемыми и предназначены
 * исключительно для чтения и анализа состояния сетевых интерфейсов.</p>
 *
 * <p>Неизвестные или неподдерживаемые поля JSON-вывода сохраняются
 * в {@code unparsedParams} для последующей диагностики и отладки.</p>
 *
 * @param family семейство адреса; для IPv4 всегда {@code "inet"}
 *               (может быть {@code null}, если поле отсутствует в JSON)
 *
 * @param local IPv4-адрес интерфейса в формате {@code x.x.x.x};
 *              может быть {@code null}, если адрес не указан
 *
 * @param prefixlen длина сетевого префикса IPv4-адреса
 *                  (например, {@code 24} для {@code /24});
 *                  может быть {@code null}
 *
 * @param broadcast широковещательный IPv4-адрес (broadcast)
 *                  в формате {@code x.x.x.x};
 *                  может быть {@code null}, если не задан
 *
 * @param scope область действия IPv4-адреса
 *              (например, {@code global}, {@code host}, {@code link});
 *              может быть {@code null}, если значение не распознано
 *
 * @param label метка интерфейса (например, {@code eth0}, {@code lo});
 *              может быть {@code null}
 *
 * @param flags набор флагов состояния IPv4-адреса
 *              (например, {@code dynamic}, {@code noprefixroute});
 *              может быть {@code null} или пустым;
 *              при наличии всегда представлен как {@link EnumSet}
 *
 * @param validLifeTime время (в секундах), в течение которого адрес
 *                      остаётся валидным;
 *                      {@code 0} означает немедленную недействительность,
 *                      {@code 4294967295} — неограниченное время жизни;
 *                      может быть {@code null}
 *
 * @param preferredLifeTime время (в секундах), в течение которого адрес
 *                          считается предпочтительным для исходящих соединений;
 *                          может быть {@code null}
 *
 * @param unparsedParams карта необработанных параметров JSON,
 *                       не поддержанных текущей моделью;
 *                       никогда не {@code null}, может быть пустой
 *
 * @see AddrInfoDto
 * @see IpAddrInfo
 * @see IpV4Scope
 * @see IpV4AddressFlag
 */
public record InetAddrInfoDto(
    String family,
    String local,
    Integer prefixlen,
    String broadcast,
    IpV4Scope scope,
    String label,
    EnumSet<IpV4AddressFlag> flags,
    @JsonProperty("valid_life_time")
    Long validLifeTime,
    @JsonProperty("preferred_life_time")
    Long preferredLifeTime,
    Map<String, Object> unparsedParams
) implements AddrInfoDto, IpAddrInfo {}
