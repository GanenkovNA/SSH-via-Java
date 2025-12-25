package io.github.ganenkovna.ssh.commands.ip.a.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Базовый интерфейс для описания IP-адресов сетевого интерфейса,
 * получаемых из вывода команды {@code ip -j a}.
 *
 * <p>Интерфейс используется Jackson для полиморфной десериализации
 * элементов массива {@code addr_info} в зависимости от значения
 * поля {@code family}.</p>
 *
 * <p>Поддерживаемые семейства адресов:
 * <ul>
 *   <li>{@code inet} — IPv4-адрес ({@link InetAddrInfoPojo});</li>
 *   <li>{@code inet6} — IPv6-адрес ({@link Inet6AddrInfoPojo}).</li>
 * </ul>
 *
 * <p>Поле {@code family} является обязательным и используется как
 * дискриминатор типа при десериализации. Значение свойства
 * сохраняется в объекте ( {@code visible = true} ), что позволяет
 * использовать его в логике после разбора JSON.</p>
 *
 * <p>Реализации интерфейса, как правило, являются POJO и
 * <strong>не потокобезопасны</strong>.</p>
 *
 * @see InetAddrInfoPojo
 * @see Inet6AddrInfoPojo
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "family",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = InetAddrInfoPojo.class, name = "inet"),
    @JsonSubTypes.Type(value = Inet6AddrInfoPojo.class, name = "inet6")
})
public interface AddrInfoPojo {

  /**
   * Возвращает семейство IP-адреса.
   *
   * <p>Используется Jackson как дискриминатор типа при полиморфной
   * десериализации.</p>
   *
   * <p>Типичные значения:
   * <ul>
   *   <li>{@code inet}</li>
   *   <li>{@code inet6}</li>
   * </ul>
   *
   * @return семейство IP-адреса; может быть {@code null} до инициализации
   */
  String getFamily();

  /**
   * Устанавливает семейство IP-адреса.
   *
   * <p>Метод вызывается Jackson в процессе десериализации JSON.</p>
   *
   * @param family семейство IP-адреса
   */
  void setFamily(String family);
}
