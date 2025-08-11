package com.github.GanenkovNA.ssh.commands.ip.a.dto;

import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v4.InterfaceIpv4ConfigDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v6.InterfaceIpv6ConfigDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.base.InterfaceBaseConfigDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.physical.InterfacePhysicalConfigDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Содержит полную информацию о сетевом интерфейсе.
 *
 * <p>Объединяет базовые параметры, физические характеристики и IP-конфигурации.
 *
 * @see InterfaceBaseConfigDto
 * @see InterfacePhysicalConfigDto
 * @see InterfaceIpv4ConfigDto
 * @see InterfaceIpv6ConfigDto
 */
@Data
public class InterfaceDto {

  /** Базовые параметры интерфейса. */
  private InterfaceBaseConfigDto interfaceParams;

  /** Физические параметры (MAC, тип канала). */
  private InterfacePhysicalConfigDto interfacePhysicalParams;

  /** Список IPv4-конфигураций. */
  @Setter(AccessLevel.NONE)
  private List<InterfaceIpv4ConfigDto> ipv4 = new ArrayList<>();

  /** Список IPv6-конфигураций. */
  @Setter(AccessLevel.NONE)
  private List<InterfaceIpv6ConfigDto> ipv6 = new ArrayList<>();

  /** Список необработанных строк вывода. */
  @Setter(AccessLevel.NONE)
  private List<String> unknownLines = new ArrayList<>();

  /**
   * Устанавливает базовые параметры сетевого интерфейса.
   *
   * @param interfaceParams DTO с базовыми параметрами интерфейса
   * @throws NullPointerException если interfaceParams равен null
   * @see InterfaceBaseConfigDto
   */
  public void setInterfaceParams(InterfaceBaseConfigDto interfaceParams)
      throws NullPointerException {
    Objects.requireNonNull(interfaceParams,
        "Строка базовых параметров интерфейса не может быть null");
    this.interfaceParams = interfaceParams;
  }

  /**
   * Устанавливает физические параметры сетевого интерфейса.
   *
   * @param interfacePhysicalParams DTO с физическими параметрами интерфейса
   * @throws NullPointerException если interfacePhysicalParams равен null
   * @see InterfacePhysicalConfigDto
   */
  public void setInterfacePhysicalParams(InterfacePhysicalConfigDto interfacePhysicalParams)
      throws NullPointerException {
    Objects.requireNonNull(interfacePhysicalParams,
        "Строка физических параметров интерфейса не может быть null");
    this.interfacePhysicalParams = interfacePhysicalParams;
  }

  /**
   * Добавляет IPv4-конфигурацию интерфейса.
   *
   * @param config DTO с IPv4-настройками интерфейса
   * @throws NullPointerException если config равен null
   * @see InterfaceIpv4ConfigDto
   */
  public void addIpv4(InterfaceIpv4ConfigDto config) {
    Objects.requireNonNull(config, "Строка IPv4-конфигурации не может быть null");
    ipv4.add(config);
  }

  /**
   * Добавляет IPv6-конфигурацию интерфейса.
   *
   * @param config DTO с IPv6-настройками интерфейса
   * @throws NullPointerException если config равен null
   * @see InterfaceIpv6ConfigDto
   */
  public void addIpv6(InterfaceIpv6ConfigDto config) {
    Objects.requireNonNull(config, "Строка IPv6-конфигурации не может быть null");
    ipv6.add(config);
  }

  /**
   * Добавляет необработанную строку вывода команды.
   *
   * <p>Используется для сохранения строк, которые не удалось распознать,
   * но которые могут потребоваться для полного восстановления состояния интерфейса.
   *
   * @param unknownLine необработанная строка вывода
   * @throws NullPointerException если unknownLine равен null
   */
  public void addUnknownLine(String unknownLine) {
    Objects.requireNonNull(unknownLine, "Строка вывода не может быть null");
    unknownLines.add(unknownLine);
  }
}