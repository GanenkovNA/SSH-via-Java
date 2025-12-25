package io.github.ganenkovna.ssh.commands.ip.a.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.ganenkovna.util.ip.dto.IpV6Scope;
import java.util.Map;

/**
 * DTO, представляющий информацию об IPv6-адресе сетевого интерфейса
 * из вывода команды {@code ip -j address}.
 *
 * <p>Экземпляр описывает один IPv6-адрес, назначенный интерфейсу,
 * включая его префикс, область видимости и параметры времени жизни.</p>
 *
 * <p>Класс является неизменяемым (record) и предназначен для передачи
 * данных между слоями без дополнительной валидации.</p>
 *
 * <p>Неизвестные или неподдерживаемые поля JSON сохраняются
 * в {@code unparsedParams} для последующей диагностики и обратной
 * совместимости.</p>
 *
 * @param family семейство адресов; для IPv6 обычно {@code "inet6"},
 *               может быть {@code null}
 * @param local IPv6-адрес интерфейса в каноническом текстовом представлении
 *              (RFC 5952); может быть {@code null}
 * @param prefixlen длина префикса IPv6-адреса в диапазоне от {@code 0} до {@code 128};
 *                  может быть {@code null}
 * @param scope область видимости IPv6-адреса;
 *              может быть {@code null}, если значение не распознано
 * @param validLifeTime время жизни адреса до перехода в состояние {@code invalid},
 *                      в секундах; может быть {@code null}
 * @param preferredLifeTime время жизни адреса до перехода в состояние
 *                          {@code deprecated}, в секундах; может быть {@code null}
 * @param unparsedParams карта неизвестных параметров:
 *                       никогда не {@code null}, может быть пустой
 *
 * @see AddrInfoDto
 * @see IpAddrInfo
 * @see IpV6Scope
 */
public record Inet6AddrInfoDto(
    String family,
    String local,
    Integer prefixlen,
    IpV6Scope scope,
    @JsonProperty("valid_life_time")
    Long validLifeTime,
    @JsonProperty("preferred_life_time")
    Long preferredLifeTime,
    Map<String, Object> unparsedParams
) implements AddrInfoDto, IpAddrInfo {}
