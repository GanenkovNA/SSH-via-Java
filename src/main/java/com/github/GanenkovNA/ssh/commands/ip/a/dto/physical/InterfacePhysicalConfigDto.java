package com.github.GanenkovNA.ssh.commands.ip.a.dto.physical;

import com.github.GanenkovNA.ssh.commands.ip.service.MacValidation;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Физические параметры сетевого интерфейса.
 */
@Data
public class InterfacePhysicalConfigDto {

  /** Тип канального уровня (например, "ether", "loopback"). */
  private String linkType;

  /** MAC-адрес в формате "xx:xx:xx:xx:xx:xx". */
  private String mac;

  /** Широковещательный MAC-адрес (обычно "ff:ff:ff:ff:ff:ff"). */
  private String broadcastMac;

  /** Список нераспознанных параметров. */
  private List<String> unknownParams = new ArrayList<>();

  /** Задание значения MAC-адреса.
   *
   * <p>Проверяется корректность устанавливаемого значения</p>
   *
   * @see MacValidation#validateMac(String)
   */
  public void setMac(String mac) throws IllegalArgumentException {
    MacValidation.validateMac(mac);
    this.mac = mac;
  }

  /** Задание значения широковещательного MAC-адреса.
   *
   * <p>Проверяется корректность устанавливаемого значения</p>
   *
   * @see MacValidation#validateMac(String)
   */
  public void setBroadcastMac(String broadcastMac) throws IllegalArgumentException {
    MacValidation.validateMac(broadcastMac);
    this.broadcastMac = broadcastMac;
  }

  /** Добавление нераспознанного параметра в список. */
  public void addUnknownParam(String unknownParam) {
    unknownParams.add(unknownParam);
  }
}
