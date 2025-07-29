package com.github.GanenkovNA.ssh.commands.ip.a.dto;

import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.InterfaceIpv4ConfigDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.InterfaceIpv6ConfigDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.base.InterfaceBaseConfigDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.physical.InterfacePhysicalConfigDto;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

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
  private List<InterfaceIpv4ConfigDto> ipv4 = new ArrayList<>();

  /** Список IPv6-конфигураций. */
  private List<InterfaceIpv6ConfigDto> ipv6 = new ArrayList<>();

  /** Список необработанных строк вывода. */
  private List<String> unknownLines = new ArrayList<>();

  /** Добавление IPv4-конфигурации в список. */
  public void addIpv4(InterfaceIpv4ConfigDto config) {
    ipv4.add(config);
  }

  /** Добавление IPv6-конфигурации в список. */
  public void addIpv6(InterfaceIpv6ConfigDto config) {
    ipv6.add(config);
  }

  /** Добавление необработанных строк вывода в список. */
  public void addUnknownLine(String unknownLine) {
    unknownLines.add(unknownLine);
  }
}