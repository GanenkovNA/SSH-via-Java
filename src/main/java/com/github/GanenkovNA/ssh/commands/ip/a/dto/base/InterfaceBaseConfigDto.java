package com.github.GanenkovNA.ssh.commands.ip.a.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.GanenkovNA.service.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Базовые параметры сетевого интерфейса Linux.
 *
 * <p>Содержит информацию, извлеченную из первой строки вывода `ip a` для каждого интерфейса.
 * Соответствует данным из ядра Linux, доступным через netlink-интерфейс.
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/ip-link.8.html">Документация ip-link(8)</a>
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InterfaceBaseConfigDto {

  /**
   * Уникальный индекс интерфейса (ifindex) в ядре Linux.
   *
   * <p>Используется для идентификации интерфейса в системных вызовах.
   */
  @Setter(AccessLevel.NONE)
  private Integer index;

  /**Имя сетевого интерфейса. */
  private String name;

  /**
   * Флаги состояния интерфейса.
   *
   * <p>Соответствуют битовой маске {@code IFF_*} из {@code <linux/if.h>}.
   *
   * @see InterfaceFlag
   */
  @Setter(AccessLevel.NONE)
  private List<InterfaceFlag> flags = new ArrayList<>();

  /**
   * Maximum Transmission Unit (MTU) в байтах.
   *
   * <p>Допустимые значения:
   * <ul>
   *   <li>Ethernet: обычно 1500</li>
   *   <li>Jumbo frames: до 9000</li>
   *   <li>Минимальное значение: 68 для IPv4, 1280 для IPv6</li>
   * </ul>
   */
  @Setter(AccessLevel.NONE)
  private Integer mtu;

  /**
   * Дисциплина очереди (qdisc), применяемая к интерфейсу.
   *
   * @see QdiscType
   */
  private QdiscType qdiscType;

  /** Родительский интерфейс (для VLAN, bridge и т.д.). */
  private String master;

  /**
   * Текущее состояние интерфейса.
   *
   * @see InterfaceState
   */
  private InterfaceState state;

  /** Группа интерфейсов (ifgroup). */
  private String group;

  /**
   * Длина очереди передачи (transmit queue length).
   *
   * <p>Если {@code null}, значение по умолчанию определяется ядром.
   */
  @Setter(AccessLevel.NONE)
  private Integer qlen;

  /** Список нераспознанных параметров. */
  @Setter(AccessLevel.NONE)
  private List<String> unknownParams = new ArrayList<>();

  /**
   * Устанавливает индекс интерфейса.
   *
   * @param index положительное целое число
   * @throws IllegalArgumentException если index ≤ 0
   */
  public void setIndex(int index)
      throws IllegalArgumentException {
    if (index <= 0) {
      throw new IllegalArgumentException("Недопустимое значение индекса интерфейса: " + index
          + ". Индекс должен быть положительным числом.");
    }
    this.index = index;
  }

  /**
   * Устанавливает имя сетевого интерфейса.
   *
   * <p>Допустимые имена должны соответствовать:
   * <ul>
   *   <li>Длина: 1-16 символов (ограничение IFNAMSIZ в ядре Linux)</li>
   *   <li>Допустимые символы: буквы (A-Z, a-z), цифры (0-9), дефис (-), подчёркивание (_)</li>
   *   <li>Не может начинаться с цифры или дефиса</li>
   *   <li>Не может быть зарезервированными именами: "all", "default"</li>
   * </ul>
   *
   * @param name имя интерфейса
   * @throws NullPointerException если name == null
   * @throws IllegalArgumentException если name пустое
   */
  public void setName(String name)
      throws NullPointerException, IllegalArgumentException {
    this.name = StringUtils.normalizeForDto(name,
        "Имя интерфейса не может быть null",
        "Имя интерфейса не может быть пустым");
  }

  /**
   * Добавляет флаг состояния интерфейса.
   *
   * @param flag флаг для добавления
   * @throws NullPointerException если flag равен {@code null}
   */
  public void addFlag(InterfaceFlag flag)
      throws NullPointerException {
    Objects.requireNonNull(flag, "Флаг не может быть null");
    flags.add(flag);
  }

  /**
   * Устанавливает MTU (Maximum Transmission Unit).
   *
   * @param mtu Значение MTU. Допустимые значения:
   *            - Для обычных интерфейсов (Ethernet, Wi-Fi): 68-9000
   *            - Для loopback (lo): дополнительно разрешены 65535 и 65536
   * @throws IllegalArgumentException если значение вне допустимого диапазона
   */
  public void setMtu(int mtu)
      throws IllegalArgumentException {
    // Проверяем стандартный диапазон для обычных интерфейсов
    if (mtu >= 68 && mtu <= 9000) {
      this.mtu = mtu;
      return;
    }

    // Дополнительная проверка для loopback
    if (mtu == 65535 || mtu == 65536) {
      this.mtu = mtu;
      return;
    }

    throw new IllegalArgumentException("Недопустимое значение MTU: " + mtu
        + ". Допустимые значения:\n"
        + "- Для обычных интерфейсов: 68-9000\n"
        + "- Для loopback: дополнительно 65535 и 65536");
  }

  /**
   * Устанавливает дисциплину очереди (qdisc).
   *
   * @param qdiscType тип дисциплины (не null)
   * @throws NullPointerException если передан null
   */
  public void setQdiscType(QdiscType qdiscType)
      throws NullPointerException {
    Objects.requireNonNull(qdiscType, "Дисциплина очереди (qdisc) не может быть null");
    this.qdiscType = qdiscType;
  }

  /**
   * Устанавливает родительский интерфейс.
   *
   * @param master имя родительского интерфейса (например, "br0" для bridge)
   * @throws IllegalArgumentException если имя пустое
   * @throws NullPointerException если передан null
   */
  public void setMaster(String master)
      throws NullPointerException, IllegalArgumentException {
    this.master = StringUtils.normalizeForDto(master,
        "Родительский интерфейс (master) не может быть null",
        "Родительский интерфейс (master) не может быть пустым");
  }

  /**
   * Устанавливает текущее состояние интерфейса.
   *
   * @param state состояние интерфейса (не null)
   * @throws NullPointerException если передан null
   */
  public void setState(InterfaceState state)
      throws NullPointerException {
    Objects.requireNonNull(state, "Состояние интерфейса не может быть null");
    this.state = state;
  }

  /**
   * Устанавливает группу интерфейса.
   *
   * @param group имя группы (может быть null)
   * @throws IllegalArgumentException если группа пустая
   * @throws NullPointerException если передан null
   */
  public void setGroup(String group)
      throws NullPointerException, IllegalArgumentException {
    this.group = StringUtils.normalizeForDto(group,
        "Группа интерфейсов (group) не может быть null",
        "Группа интерфейсов (group) не может быть пустой");
  }

  /**
   * Устанавливает длину очереди передачи (qlen).
   *
   * @param qlen длина очереди (≥ 0)
   * @throws IllegalArgumentException если значение отрицательное
   */
  public void setQlen(int qlen)
      throws IllegalArgumentException {
    if (qlen < 0) {
      throw new IllegalArgumentException("Недопустимое значение Qlen: " + qlen
          + ". Qlen должна быть положительным числом.");
    }
    this.qlen = qlen;
  }

  /**
   * Добавляет нераспознанный параметр.
   *
   * @param unknownParam параметр для добавления
   * @throws NullPointerException если параметр равен {@code null}
   * @throws IllegalArgumentException если параметр пуст
   */
  public void addUnknownParam(String unknownParam)
      throws NullPointerException, IllegalArgumentException {
    unknownParams.add(
        StringUtils.normalizeForDto(
            unknownParam,
            "Базовый параметр сетевого интерфейса не может быть null",
            "Базовый параметры сетевого интерфейса не может быть пустым"));
  }
}
