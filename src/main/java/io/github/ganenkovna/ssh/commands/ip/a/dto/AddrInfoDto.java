package io.github.ganenkovna.ssh.commands.ip.a.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Базовый DTO для описания IP-адреса сетевого интерфейса,
 * возвращаемого командой {@code ip -j a}.
 *
 * <p>Интерфейс используется как маркер и точка полиморфной
 * десериализации Jackson для различных семейств IP-адресов
 * (IPv4 и IPv6).</p>
 *
 * <p>Конкретная реализация определяется значением поля
 * {@code family}, которое присутствует в JSON и используется
 * Jackson для выбора соответствующего подтипа:</p>
 * <ul>
 *   <li>{@code inet} — {@link InetAddrInfoDto} (IPv4);</li>
 *   <li>{@code inet6} — {@link Inet6AddrInfoDto} (IPv6).</li>
 * </ul>
 *
 * <p>Интерфейс предназначен для использования в агрегирующих DTO
 * (например, {@code IpAddrDto}) и не содержит логики валидации.</p>
 *
 * @see InetAddrInfoDto
 * @see Inet6AddrInfoDto
 * @see JsonTypeInfo
 * @see JsonSubTypes
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "family",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = InetAddrInfoDto.class, name = "inet"),
    @JsonSubTypes.Type(value = Inet6AddrInfoDto.class, name = "inet6")
})
public interface AddrInfoDto {

  /**
   * Возвращает семейство IP-адреса.
   *
   * <p>Значение используется для полиморфной десериализации
   * и должно соответствовать одному из поддерживаемых семейств
   * (например, {@code inet} или {@code inet6}).</p>
   *
   * @return семейство IP-адреса, никогда не {@code null}
   */
  String family();
}
