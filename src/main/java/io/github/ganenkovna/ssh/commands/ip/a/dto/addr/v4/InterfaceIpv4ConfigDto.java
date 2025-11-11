package io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v4;

import static io.github.ganenkovna.util.StringUtils.normalizeForDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.ganenkovna.util.ip.IpUtils;
import io.github.ganenkovna.util.StringUtils;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.LifeTimeParamsDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class InterfaceIpv4ConfigDto {

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
  private EnumSet<IpV4AddressFlag> ipV4AddressFlags = EnumSet.noneOf(IpV4AddressFlag.class);

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
   * @param address IPv4-адрес в формате {@code x.x.x.x}; не {@code null}
   * @throws NullPointerException если {@code address == null}
   * @throws IllegalArgumentException если строка пуста после trim() или адрес не соответствует формату IPv4
   * @see IpUtils#validateIpv4(String)
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void setAddress(String address) {
    address = normalizeForDto(address,"IPv4-адрес");

    try {
      IpUtils.validateIpv4(address);
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
  public void setPrefix(int prefix) {
    if (prefix >= 0 && prefix <= 32) {
      this.prefix = prefix;
    } else {
      throw new IllegalArgumentException("Невалидное значение префикса IPv4-адреса: " + prefix
          + "\nДопустимые значения от 0 до 32");
    }
  }

  /**
   * Устанавливает широковещательный IPv4-адрес (broadcast).
   *
   * @param broadcast IPv4-адрес в формате {@code x.x.x.x}; не {@code null}
   * @throws NullPointerException если {@code broadcast == null}
   * @throws IllegalArgumentException если строка пуста после trim() или адрес не соответствует формату IPv4
   * @see IpUtils#validateIpv4(String)
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void setBroadcast(String broadcast) {
    broadcast = normalizeForDto(broadcast,"IPv4 broadcast");

    try {
      IpUtils.validateIpv4(broadcast);
      this.broadcast = broadcast;
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Невалидное значение IPv4 broadcast: " + broadcast);
    }
  }

  /**
   * Устанавливает область видимости IPv4-адреса.
   *
   * @param scope область видимости адреса
   * @throws NullPointerException если scope равен null
   * @see IpV4Scope
   */
  public void setScope(IpV4Scope scope) {
    Objects.requireNonNull(scope, "Значение области действия IPv4-адреса не может быть null");
    this.scope = scope;
  }

  /**
   * Устанавливает параметр ядра NET_DEVICE.
   *
   * @param netDevice название сетевого устройства
   * @throws NullPointerException если netDevice равен null
   * @throws IllegalArgumentException если netDevice пустая строка
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void setNetDevice(String netDevice) {
    this.netDevice = normalizeForDto(netDevice,"NET_DEVICE");
  }

  /**
   * Добавляет флаг состояния IPv4-адреса.
   *
   * @param ipV4AddressFlag флаг состояния
   * @throws NullPointerException если ipV4AddressFlag равен null
   * @see IpV4AddressFlag
   */
  public void addIpV4AddressFlag(IpV4AddressFlag ipV4AddressFlag) {
    Objects.requireNonNull(ipV4AddressFlag, "Значение флага состояния IPv4 не может быть null");
    ipV4AddressFlags.add(ipV4AddressFlag);
  }

  /**
   * Возвращает множество флагов состояния IPv4-адреса.
   *
   * @return неизменяемое множество флагов; никогда не {@code null}, может быть пустым
   * @implNote Внутренне коллекция хранится в {@link java.util.EnumSet}, наружу возвращается
   *           неизменяемое представление.
   */
  public Set<IpV4AddressFlag> getIpV4AddressFlags() {
    return Collections.unmodifiableSet(ipV4AddressFlags);
  }

  /**
   * Добавляет нераспознанный параметр в список.
   *
   * @param unknownParam параметр для добавления
   * @throws NullPointerException если unknownParam равен null
   * @throws IllegalArgumentException если unknownParam пуст
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void addUnknownParam(String unknownParam) {
    unknownParams.add(
        normalizeForDto(unknownParam,"Параметр IPv4-адреса"));
  }

  /**
   * Возвращает неизменяемый список неизвестных параметров IPv4-конфигурации.
   *
   * @return неизменяемый список; никогда не {@code null}, может быть пустым
   */
  public List<String> getUnknownParams() {
    return List.copyOf(unknownParams);
  }
}
