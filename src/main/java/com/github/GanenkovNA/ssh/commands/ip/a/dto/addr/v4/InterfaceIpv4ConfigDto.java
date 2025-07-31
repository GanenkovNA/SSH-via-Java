package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v4;

import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.LifeTimeParamsDto;
import com.github.GanenkovNA.ssh.commands.ip.service.IpValidation;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Конфигурация IPv4-адреса интерфейса.
 *
 * @see IpV4Scope
 * @see LifeTimeParamsDto
 */
@Data
public class InterfaceIpv4ConfigDto {

  /** IPv4-адрес интерфейса в формате "x.x.x.x". */
  private String address;

  /** Префикс IP-адреса. */
  private Integer prefix;

  /** Широковещательный IPv4-адрес (broadcast). */
  private String broadcast;

  /**
   * Область действия адреса (scope).
   *
   * @see IpV4Scope
   */
  private IpV4Scope scope;

  /** Параметр ядра NET_DEVICE.  */
  private String netDevice;

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
    IpValidation.validateIpv4(address);
    this.address = address;
  }

  /** Задание значения широковещательного адреса.
   *
   * <p>Проверяется корректность устанавливаемого значения</p>
   *
   * @see IpValidation#validateIpv4(String)
   */
  public void setBroadcast(String broadcast) throws IllegalArgumentException {
    IpValidation.validateIpv4(broadcast);
    this.broadcast = broadcast;
  }

  /** Добавление нераспознанного параметра в список. */
  public void addUnknownParam(String unknownParam) {
    unknownParams.add(unknownParam);
  }
}
