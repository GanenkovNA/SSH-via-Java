package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr;

import com.github.GanenkovNA.ssh.commands.ip.service.IpValidation;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Конфигурация IPv6-адреса интерфейса.
 *
 * @see IpV6Scope
 * @see LifeTimeParamsDto
 */
@Data
public class InterfaceIpv6ConfigDto {

  /** IPv6-адрес интерфейса в формате RFC 5952. */
  private String address;

  /** Префикс IP-адреса. */
  private Integer prefix;

  /**
   * Область действия адреса (scope).
   *
   * @see IpV6Scope
   */
  private List<IpV6Scope> scopes = new ArrayList<>();

  /** Наличие флага noprefixroute. */
  private Boolean isNoPrefixRoute = false;

  /** Параметры времени жизни адреса. */
  private LifeTimeParamsDto lifeTimeParams;

  /** Список нераспознанных параметров. */
  private List<String> unknownParams = new ArrayList<>();

  /** Задание значения IP-адреса.
   *
   * <p>Проверяется корректность устанавливаемого значения</p>
   *
   * @see IpValidation#validateIpv4(String)
   */
  public void setAddress(String address) throws IllegalArgumentException {
    IpValidation.validateIpv6(address);
    this.address = address;
  }

  /** Добавление параметра области действия адреса в список. */
  public void addScope(IpV6Scope scope) {
    scopes.add(scope);
  }

  /** Добавление нераспознанного параметра в список. */
  public void addUnknownParam(String unknownParam) {
    unknownParams.add(unknownParam);
  }
}