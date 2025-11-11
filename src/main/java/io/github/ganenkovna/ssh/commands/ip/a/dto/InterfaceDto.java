package io.github.ganenkovna.ssh.commands.ip.a.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v4.InterfaceIpv4ConfigDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v6.InterfaceIpv6ConfigDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.base.InterfaceBaseConfigDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.physical.InterfacePhysicalConfigDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Содержит полную информацию о сетевом интерфейсе.
 *
 * <p>Объединяет базовые параметры, физические характеристики и IP-конфигурации.</p>
 *
 * <p>Инварианты: коллекции никогда не {@code null}, могут быть пустыми; мутация извне
 * невозможна — только через методы {@code add...()}.</p>
 *
 * @see InterfaceBaseConfigDto
 * @see InterfacePhysicalConfigDto
 * @see InterfaceIpv4ConfigDto
 * @see InterfaceIpv6ConfigDto
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class InterfaceDto {

  /** Базовые параметры интерфейса. */
  private InterfaceBaseConfigDto interfaceParams;

  /** Физические параметры (MAC, тип канала). */
  private InterfacePhysicalConfigDto interfacePhysicalParams;

  /** Список IPv4-конфигураций. */
  @Setter(AccessLevel.NONE)
  private final List<InterfaceIpv4ConfigDto> ipv4 = new ArrayList<>();

  /** Список IPv6-конфигураций. */
  @Setter(AccessLevel.NONE)
  private final List<InterfaceIpv6ConfigDto> ipv6 = new ArrayList<>();

  /** Список необработанных строк вывода. */
  @Setter(AccessLevel.NONE)
  private final List<String> unknownLines = new ArrayList<>();

  /**
   * Устанавливает базовые параметры сетевого интерфейса.
   *
   * @param interfaceParams DTO с базовыми параметрами интерфейса
   * @throws NullPointerException если interfaceParams равен null
   * @see InterfaceBaseConfigDto
   */
  public void setInterfaceParams(InterfaceBaseConfigDto interfaceParams) {
    Objects.requireNonNull(interfaceParams,
        "interfaceParams (базовые параметры интерфейса) не может быть null");
    this.interfaceParams = interfaceParams;
  }

  /**
   * Устанавливает физические параметры сетевого интерфейса.
   *
   * @param interfacePhysicalParams DTO с физическими параметрами интерфейса
   * @throws NullPointerException если interfacePhysicalParams равен null
   * @see InterfacePhysicalConfigDto
   */
  public void setInterfacePhysicalParams(InterfacePhysicalConfigDto interfacePhysicalParams) {
    Objects.requireNonNull(interfacePhysicalParams,
        "interfacePhysicalParams (физические параметры интерфейса) не может быть null");
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
    Objects.requireNonNull(config, "IPv4-конфигурация не может быть null");
    ipv4.add(config);
  }

  /** Возвращает неизменяемый снимок IPv4-конфигураций; никогда не {@code null}. */
  public List<InterfaceIpv4ConfigDto> getIpv4() {
    return List.copyOf(ipv4);
  }

  /**
   * Добавляет IPv6-конфигурацию интерфейса.
   *
   * @param config DTO с IPv6-настройками интерфейса
   * @throws NullPointerException если config равен null
   * @see InterfaceIpv6ConfigDto
   */
  public void addIpv6(InterfaceIpv6ConfigDto config) {
    Objects.requireNonNull(config, "IPv6-конфигурация не может быть null");
    ipv6.add(config);
  }

  /** Возвращает неизменяемый снимок IPv6-конфигураций; никогда не {@code null}. */
  public List<InterfaceIpv6ConfigDto> getIpv6() {
    return List.copyOf(ipv6);
  }

  /**
   * Добавляет необработанную строку вывода команды.
   *
   * <p>Используется для сохранения строк, которые не удалось распознать,
   * но которые могут потребоваться для полного восстановления состояния интерфейса.</p>
   *
   * @param unknownLine необработанная строка вывода
   * @throws NullPointerException если {@code unknownLine == null}
   */
  public void addUnknownLine(String unknownLine) {
    Objects.requireNonNull(unknownLine, "Строка вывода не может быть null");
    unknownLines.add(unknownLine);
  }

  /** Возвращает неизменяемый снимок неизвестных строк; никогда не {@code null}. */
  public List<String> getUnknownLines() {
    return List.copyOf(unknownLines);
  }
}