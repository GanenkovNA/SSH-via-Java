package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v6;

import com.fasterxml.jackson.annotation.JsonInclude;
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
 * Конфигурация IPv6-адреса сетевого интерфейса.
 *
 * <p>Содержит параметры адресации, флаги генерации, маршрутизации и времени жизни IPv6-адреса.
 * Все строковые представления адресов должны соответствовать RFC 5952.
 *
 * @see IpV6Scope Область видимости IPv6-адреса
 * @see GenerationFlags Флаги генерации IPv6-адресов
 * @see RouteFlags Флаги маршрутизации IPv6
 * @see LifeTimeParamsDto Параметры времени жизни адреса
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InterfaceIpv6ConfigDto {

  /** IPv6-адрес интерфейса в формате RFC 5952. */
  private String address;

  /** Длина префикса адреса (от 0 до 128). */
  @Setter(AccessLevel.NONE)
  private Integer prefix;

  /**
   * Область видимости IPv6-адреса.
   *
   * @see IpV6Scope
   */
  @Setter(AccessLevel.NONE)
  private List<IpV6Scope> scopes = new ArrayList<>();

  /**
   * Список флагов генерации адреса.
   *
   * @see GenerationFlags
   */
  @Setter(AccessLevel.NONE)
  private List<GenerationFlags> generationFlags = new ArrayList<>();

  /**
   * Список флагов маршрутизации.
   *
   * @see RouteFlags
   */
  @Setter(AccessLevel.NONE)
  private List<RouteFlags> routeFlags = new ArrayList<>();

  /** Параметры времени жизни адреса.
   *
   * @see LifeTimeParamsDto
   */
  private LifeTimeParamsDto lifeTimeParams;

  /** Список нераспознанных параметров. */
  @Setter(AccessLevel.NONE)
  private List<String> unknownParams = new ArrayList<>();

  /**
   * Устанавливает IPv6-адрес интерфейса с валидацией формата.
   *
   * @param address IPv6-адрес в формате RFC 5952
   * @throws NullPointerException если address равен null
   * @throws IllegalArgumentException если:
   *         - address пустая строка
   *         - не соответствует формату IPv6
   * @see IpValidation#validateIpv6
   */
  public void setAddress(String address)
      throws NullPointerException, IllegalArgumentException {
    address = StringUtils.normalizeForDto(address,
        "Значение IPv6-адреса не может быть null",
        "Значение IPv6-адреса не может быть пустым");

    try {
      IpValidation.validateIpv6(address);
      this.address = address;
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Невалидное значение IPv6-адреса: " + address);
    }
  }

  /**
   * Устанавливает длину префикса IPv6-адреса.
   *
   * @param prefix длина префикса (0-128)
   * @throws IllegalArgumentException если prefix вне допустимого диапазона
   */
  public void setPrefix(int prefix)
      throws IllegalArgumentException {
    if (prefix >= 0 && prefix <= 128) {
      this.prefix = prefix;
    } else {
      throw new IllegalArgumentException("Невалидное значение префикса IPv6-адреса: " + address
          + "\nДопустимые значения от 0 до 128");
    }
  }

  /**
   * Добавляет область видимости IPv6-адреса.
   *
   * @param scope область видимости адреса
   * @throws NullPointerException если scope равен null
   * @see IpV6Scope
   */
  public void addScope(IpV6Scope scope)
      throws NullPointerException {
    Objects.requireNonNull(scope, "Значение области видимости IPv6-адреса не может быть null");
    scopes.add(scope);
  }

  /**
   * Добавляет флаг генерации IPv6-адреса.
   *
   * @param generationFlag флаг генерации адреса
   * @throws NullPointerException если generationFlag равен null
   * @see GenerationFlags
   */
  public void addGenerationFlag(GenerationFlags generationFlag)
      throws NullPointerException {
    Objects.requireNonNull(generationFlag,
        "Значение флага генерации IPv6-адреса не может быть null");
    generationFlags.add(generationFlag);
  }

  /**
   * Добавляет флаг маршрутизации IPv6.
   *
   * @param routeFlag флаг маршрутизации
   * @throws NullPointerException если routeFlag равен null
   * @see RouteFlags
   */
  public void addRouteFlag(RouteFlags routeFlag)
      throws NullPointerException {
    Objects.requireNonNull(routeFlag,
        "Значение флага маршрутизации IPv6-адреса не может быть null");
    routeFlags.add(routeFlag);
  }

  /**
   * Устанавливает параметры времени жизни IPv6-адреса.
   *
   * @param lifeTimeParams параметры времени жизни
   * @throws NullPointerException если lifeTimeParams равен null
   * @see LifeTimeParamsDto
   */
  public void setLifeTimeParams(LifeTimeParamsDto lifeTimeParams)
      throws NullPointerException {
    Objects.requireNonNull(lifeTimeParams,
        "Значение флага маршрутизации IPv6-адреса не может быть null");
    this.lifeTimeParams = lifeTimeParams;
  }

  /**
   * Добавляет нераспознанный параметр в список.
   *
   * @param unknownParam параметр для добавления
   * @throws NullPointerException если unknownParam равен null
   * @throws IllegalArgumentException если unknownParam пуст
   */
  public void addUnknownParam(String unknownParam)
      throws NullPointerException, IllegalArgumentException {
    unknownParams.add(
        StringUtils.normalizeForDto(
            unknownParam,
            "Параметр IPv6-адреса не может быть null",
            "Параметр IPv6-адреса не может быть пустым"));
  }
}