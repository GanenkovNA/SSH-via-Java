package io.github.ganenkovna.ssh.commands.ip.a.dto.base;

import static io.github.ganenkovna.util.StringUtils.normalizeForDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.ganenkovna.util.StringUtils;
import io.github.ganenkovna.util.ip.MtuValidation;
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
 * Базовые параметры сетевого интерфейса Linux.
 *
 * <p>Содержит информацию, извлечённую из первой строки вывода {@code ip a} для каждого интерфейса.
 * Соответствует данным из ядра Linux, доступным через netlink-интерфейс.</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/ip-link.8.html">Документация ip-link(8)</a>
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class InterfaceBaseConfigDto {

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
  private EnumSet<InterfaceFlag> flags = EnumSet.noneOf(InterfaceFlag.class);

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
  public void setIndex(int index) {
    if (index <= 0) {
      throw new IllegalArgumentException("Недопустимое значение индекса интерфейса: " + index
          + ". Индекс должен быть положительным числом.");
    }
    this.index = index;
  }

  /**
   * Устанавливает имя интерфейса со строгой проверкой формата.
   *
   * <p>Дополнительно к нормализации проверяется:</p>
   * <ul>
   *   <li> длина 1–16 символов;</li>
   *   <li> имя не начинается с дефиса или цифры;</li>
   *   <li> разрешены только латиница, цифры, дефис и подчёркивание;</li>
   *   <li> зарезервированные имена {@code all}, {@code default} запрещены.</li>
   * </ul>
   *
   * @param name имя интерфейса
   * @throws NullPointerException если {@code name == null}
   * @throws IllegalArgumentException если строка пустая после trim() или не соответствует требованиям
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void setName(String name) {
    name = normalizeForDto(name,"Имя интерфейса");

    if (!name.matches("^(?![-\\d])[A-Za-z0-9_-]{1,16}$")
        || name.equalsIgnoreCase("all")
        || name.equalsIgnoreCase("default")) {
      throw new IllegalArgumentException("Недопустимое имя интерфейса: " + name);
    }

    this.name = name;
  }

  /**
   * Устанавливает имя интерфейса без строгой проверки формата.
   *
   * <p>Выполняется только нормализация.</p>
   *
   * <p>Используется для значений, поступающих из доверенного источника
   * (например, тестовый стенд).</p>
   *
   * @param name имя интерфейса
   * @throws NullPointerException если {@code name == null}
   * @throws IllegalArgumentException если пустая строка после trim()
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void setNameWithoutValidation(String name) {
    this.name = normalizeForDto(name,"Имя интерфейса");
  }

  /**
   * Добавляет флаг состояния интерфейса.
   *
   * @param flag флаг для добавления
   * @throws NullPointerException если flag равен {@code null}
   */
  public void addFlag(InterfaceFlag flag) {
    Objects.requireNonNull(flag, "Флаг не может быть null");
    flags.add(flag);
  }

  /**
   * Возвращает множество флагов интерфейса.
   *
   * <p>Коллекция представляет собой уникальный набор значений
   * {@link InterfaceFlag}, соответствующих состоянию интерфейса.
   * Внутренне хранится в {@link EnumSet}, наружу возвращается
   * неизменяемое представление.</p>
   *
   * <p>Попытка изменить возвращённый {@code Set} приведёт к
   * {@link UnsupportedOperationException}.</p>
   *
   * @return неизменяемое множество установленных флагов интерфейса;
   *         никогда не {@code null} (может быть пустым)
   */
  public Set<InterfaceFlag> getFlags() {
    return Collections.unmodifiableSet(flags);
  }

  /**
   * Задаёт значение MTU для данного интерфейса.
   *
   * <p>Перед присвоением выполняется валидация значения
   * (приближена к поведению {@code iproute2}):</p>
   * <ul>
   *   <li>для обычных интерфейсов допустим диапазон {@code 68..9000};</li>
   *   <li>для loopback-интерфейса дополнительно допускаются {@code 65535} и {@code 65536}.</li>
   * </ul>
   *
   * <p>Примечание: минимальные значения по протоколам (например, IPv6 ≥ {@code 1280})
   * не проверяются на уровне DTO, так как зависят от конкретной конфигурации
   * и контролируются ядром или сетевыми службами.</p>
   *
   * @param mtu значение MTU
   * @throws IllegalArgumentException если значение выходит за пределы допустимых
   */
  public void setMtu(int mtu) {
    MtuValidation.validateMtu(mtu);
    this.mtu = mtu;
  }

  /**
   * Устанавливает дисциплину очереди (qdisc).
   *
   * @param qdiscType тип дисциплины (не null)
   * @throws NullPointerException если передан null
   */
  public void setQdiscType(QdiscType qdiscType) {
    Objects.requireNonNull(qdiscType, "Дисциплина очереди (qdisc) не может быть null");
    this.qdiscType = qdiscType;
  }

  /**
   * Устанавливает родительский интерфейс.
   *
   * @param master имя родительского интерфейса (например, "br0" для bridge)
   * @throws IllegalArgumentException если имя пустое
   * @throws NullPointerException если передан null
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void setMaster(String master) {
    this.master = normalizeForDto(master,"Родительский интерфейс (master)");
  }

  /**
   * Устанавливает текущее состояние интерфейса.
   *
   * @param state состояние интерфейса (не null)
   * @throws NullPointerException если передан null
   */
  public void setState(InterfaceState state) {
    Objects.requireNonNull(state, "Состояние интерфейса не может быть null");
    this.state = state;
  }

  /**
   * Устанавливает группу интерфейса.
   *
   * @param group имя группы
   * @throws IllegalArgumentException если группа пустая
   * @throws NullPointerException если передан null
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void setGroup(String group) {
    this.group = normalizeForDto(group,"Группа интерфейсов (group)");
  }

  /**
   * Устанавливает длину очереди передачи (qlen).
   *
   * @param qlen длина очереди (≥ 0)
   * @throws IllegalArgumentException если значение отрицательное
   */
  public void setQlen(int qlen) {
    if (qlen < 0) {
      throw new IllegalArgumentException("Недопустимое значение Qlen: " + qlen
          + ". Qlen должна быть неотрицательным числом.");
    }
    this.qlen = qlen;
  }

  /**
   * Добавляет нераспознанный параметр.
   *
   * @param unknownParam параметр для добавления
   * @throws NullPointerException если параметр равен {@code null}
   * @throws IllegalArgumentException если параметр пуст
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void addUnknownParam(String unknownParam) {
    unknownParams.add(
        normalizeForDto(unknownParam,"Базовый параметр сетевого интерфейса"));
  }

  /**
   * Возвращает список неизвестных базовых параметров интерфейса.
   *
   * <p>Каждый вызов метода возвращает неизменяемую копию
   * внутреннего списка {@code unknownParams}. Попытка модификации
   * приведёт к {@link UnsupportedOperationException}.</p>
   *
   * <p>Гарантируется, что метод никогда не возвращает {@code null};
   * при отсутствии элементов возвращается пустой список.</p>
   *
   * @return неизменяемая копия списка неизвестных параметров интерфейса;
   *         никогда не {@code null}
   */
  public List<String> getUnknownParams() {
    return List.copyOf(unknownParams);
  }
}
