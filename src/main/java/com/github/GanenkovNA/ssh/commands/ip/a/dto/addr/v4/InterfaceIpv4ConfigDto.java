package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v4;

import com.github.GanenkovNA.service.StringUtils;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.LifeTimeParamsDto;
import com.github.GanenkovNA.ssh.commands.ip.service.IpValidation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Конфигурация IPv4-адреса сетевого интерфейса.
 *
 * <p>Содержит параметры адресации, область видимости и дополнительные параметры IPv4-адреса.
 * Все строковые представления адресов должны быть в формате "x.x.x.x".
 *
 * @see IpV4Scope Область видимости IPv4-адреса
 * @see LifeTimeParamsDto Параметры времени жизни адреса
 */
@Data
public class InterfaceIpv4ConfigDto {

  /** IPv4-адрес интерфейса в формате "x.x.x.x". */
  private String address;

  /** Префикс IP-адреса. */
  @Setter(AccessLevel.NONE)
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

  /** Флаги состояния адреса (dynamic, noprefixroute и др.).
   *
   * @see IpV4AddressFlag
   */
  @Setter(AccessLevel.NONE)
  private List<IpV4AddressFlag> ipV4AddressFlags = new ArrayList<>();

  /** Параметры времени жизни адреса.
   *
   * @see LifeTimeParamsDto
   */
  private LifeTimeParamsDto lifeTimeParams;

  /** Список нераспознанных параметров конфигурации. */
  @Setter(AccessLevel.NONE)
  private List<String> unknownParams = new ArrayList<>();


  /**
   * Устанавливает IPv4-адрес интерфейса с валидацией формата.
   *
   * @param address IPv4-адрес в формате "x.x.x.x"
   * @throws NullPointerException если address равен null
   * @throws IllegalArgumentException если:
   *         - address пустая строка
   *         - не соответствует формату IPv4
   * @see IpValidation#validateIpv4(String)
   * @see StringUtils#normalizeForDto(String, String, String)
   */
  public void setAddress(String address)
      throws NullPointerException, IllegalArgumentException {
    address = StringUtils.normalizeForDto(address,
        "Значение IPv4-адреса не может быть null",
        "Значение IPv4-адреса не может быть пустым");

    try {
      IpValidation.validateIpv4(address);
      this.address = address;
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Невалидное значение IPv4-адреса: " + address);
    }
  }

  /**
   * Устанавливает длину префикса IPv4-адреса.
   *
   * @param prefix длина префикса (0-32)
   * @throws IllegalArgumentException если prefix вне допустимого диапазона
   */
  public void setPrefix(int prefix)
      throws IllegalArgumentException {
    if (prefix >= 0 && prefix <= 32) {
      this.prefix = prefix;
    } else {
      throw new IllegalArgumentException("Невалидное значение префикса IPv4-адреса: " + address
          + "\nДопустимые значения от 0 до 32");
    }
  }

  /**
   * Устанавливает широковещательный IPv4-адрес (broadcast).
   *
   * @param broadcast IPv4-адрес в формате "x.x.x.x"
   * @throws NullPointerException если broadcast равен null
   * @throws IllegalArgumentException если:
   *         - broadcast пустая строка
   *         - не соответствует формату IPv4
   * @see IpValidation#validateIpv4(String)
   */
  public void setBroadcast(String broadcast)
      throws NullPointerException, IllegalArgumentException {
    broadcast = StringUtils.normalizeForDto(broadcast,
        "Значение IPv4 broadcast не может быть null",
        "Значение IPv4 broadcast не может быть пустым");

    try {
      IpValidation.validateIpv4(broadcast);
      this.broadcast = broadcast;
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Невалидное значение IPv4 broadcast: " + address);
    }
  }

  /**
   * Устанавливает область видимости IPv4-адреса.
   *
   * @param scope область видимости адреса
   * @throws NullPointerException если scope равен null
   * @see IpV4Scope
   */
  public void setScope(IpV4Scope scope)
      throws NullPointerException {
    Objects.requireNonNull(scope, "Значение области действия IPv4-адреса не может быть null");
    this.scope = scope;
  }

  /**
   * Устанавливает параметр ядра NET_DEVICE.
   *
   * @param netDevice название сетевого устройства
   * @throws NullPointerException если netDevice равен null
   * @throws IllegalArgumentException если netDevice пустая строка
   */
  public void setNetDevice(String netDevice)
      throws NullPointerException, IllegalArgumentException {
    this.netDevice = StringUtils.normalizeForDto(netDevice,
        "Значение NET_DEVICE не может быть null",
        "Значение NET_DEVICE не может быть пустым");
  }

  /**
   * Добавляет флаг состояния IPv4-адреса.
   *
   * @param ipV4AddressFlag флаг состояния
   * @throws NullPointerException если ipV4AddressFlag равен null
   * @see IpV4AddressFlag
   */
  private void addIpV4AddressFlag(IpV4AddressFlag ipV4AddressFlag)
      throws NullPointerException {
    Objects.requireNonNull(ipV4AddressFlag, "Значение флага состояния IPv4 не может быть null");
    ipV4AddressFlags.add(ipV4AddressFlag);
  }

  /**
   * Добавляет нераспознанный параметр в список.
   *
   * @param unknownParam параметр для добавления
   * @throws NullPointerException если unknownParam равен null
   * @throws IllegalArgumentException если unknownParam пуст
   */
  public void addUnknownParam(String unknownParam)
      throws NullPointerException {
    unknownParams.add(
        StringUtils.normalizeForDto(
            unknownParam,
            "Параметр IPv4-адреса не может быть null",
            "Параметр IPv4-адреса не может быть пустым"));
  }
}
