package com.github.GanenkovNA.ssh.commands.ip.a.dto.physical;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.GanenkovNA.service.StringUtils;
import com.github.GanenkovNA.ssh.commands.ip.service.MacValidation;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Физические параметры сетевого интерфейса.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InterfacePhysicalConfigDto {

  /** Тип канального уровня (например, "ether", "loopback"). */
  private String linkType;

  /** MAC-адрес в формате "xx:xx:xx:xx:xx:xx". */
  private String mac;

  /** Широковещательный MAC-адрес (обычно "ff:ff:ff:ff:ff:ff"). */
  private String broadcastMac;

  /** Список нераспознанных параметров. */
  @Setter(AccessLevel.NONE)
  private List<String> unknownParams = new ArrayList<>();

  /**
   * Устанавливает тип канального уровня интерфейса.
   *
   * @param linkType тип канального уровня (например, "ether", "loopback", "ppp")
   * @throws NullPointerException если linkType равен null
   * @throws IllegalArgumentException если linkType пустая строка
   * @see <a href="https://www.kernel.org/doc/html/latest/networking/operstates.html">Link Types</a>
   */
  public void setLinkType(String linkType)
      throws NullPointerException, IllegalArgumentException {
    this.linkType = StringUtils.normalizeForDto(linkType,
        "Значение типа канального уровня не может быть null",
        "Значение типа канального уровня не может быть пустым");
  }

  /**
   * Устанавливает MAC-адрес интерфейса.
   *
   * @param mac MAC-адрес в формате "xx:xx:xx:xx:xx:xx" или "xx-xx-xx-xx-xx-xx"
   * @throws NullPointerException если mac равен null
   * @throws IllegalArgumentException если:
   *         - mac пустая строка
   *         - не соответствует формату MAC-адреса
   * @see MacValidation#validateMac(String)
   */
  public void setMac(String mac)
      throws NullPointerException, IllegalArgumentException {
    mac = StringUtils.normalizeForDto(mac,
        "Значение MAC-адреса не может быть null",
        "Значение MAC-адреса не может быть пустым");

    MacValidation.validateMac(mac);
    this.mac = mac;
  }

  /**
   * Устанавливает broadcast MAC-адрес.
   *
   * @param broadcastMac MAC-адрес в формате "xx:xx:xx:xx:xx:xx"
   * @throws NullPointerException если broadcastMac равен null
   * @throws IllegalArgumentException если:
   *         - broadcastMac пустая строка
   *         - не соответствует формату MAC-адреса
   * @see MacValidation#validateMac(String)
   */
  public void setBroadcastMac(String broadcastMac)
      throws NullPointerException, IllegalArgumentException {
    broadcastMac = StringUtils.normalizeForDto(broadcastMac,
        "Значение broadcast MAC-адреса не может быть null",
        "Значение broadcast MAC-адреса не может быть пустым");

    try {
      MacValidation.validateMac(broadcastMac);
      this.broadcastMac = broadcastMac;
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Невалидное значение broadcast MAC-адреса: "
          + broadcastMac);
    }

  }

  /**
   * Добавляет нераспознанный физический параметр интерфейса.
   *
   * <p>Используется для сохранения параметров, которые не могут быть обработаны текущей версией,
   * но должны быть сохранены для обратной совместимости.
   *
   * @param unknownParam параметр для добавления
   * @throws NullPointerException если unknownParam равен null
   * @throws IllegalArgumentException если unknownParam пустая строка
   */
  public void addUnknownParam(String unknownParam) {
    unknownParams.add(
        StringUtils.normalizeForDto(
            unknownParam,
            "Физический параметр IPv4-адреса не может быть null",
            "Физический параметр IPv4-адреса не может быть пустым"));
  }
}
