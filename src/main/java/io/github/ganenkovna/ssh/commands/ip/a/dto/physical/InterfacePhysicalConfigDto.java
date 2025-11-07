package io.github.ganenkovna.ssh.commands.ip.a.dto.physical;

import static io.github.ganenkovna.util.StringUtils.normalizeForDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.ganenkovna.util.StringUtils;
import io.github.ganenkovna.util.ip.MacValidation;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/** Физические параметры сетевого интерфейса. */
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
   * @param linkType тип канального уровня (например, {@code ether}, {@code loopback}, {@code ppp})
   * @throws NullPointerException если linkType равен null
   * @throws IllegalArgumentException если linkType пустая строка
   * @see StringUtils#normalizeForDto(String, String)
   * @see <a href="https://man7.org/linux/man-pages/man8/ip-link.8.html">ip-link(8)</a>
   * @implNote Типы соответствуют семействам каналов ({@code link/*}) и значениям {@code ARPHRD_*} из {@code <linux/if_arp.h>}.
   */
  public void setLinkType(String linkType) {
    this.linkType = normalizeForDto(linkType,"Тип канального уровня");
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
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void setMac(String mac) {
    mac = normalizeForDto(mac,"MAC-адрес");

    MacValidation.validateMac(mac);
    this.mac = mac;
  }

  /**
   * Устанавливает broadcast MAC-адрес.
   *
   * @param broadcastMac MAC-адрес в формате "xx:xx:xx:xx:xx:xx" или "xx-xx-xx-xx-xx-xx"
   * @throws NullPointerException если broadcastMac равен null
   * @throws IllegalArgumentException если:
   *         - broadcastMac пустая строка
   *         - не соответствует формату MAC-адреса
   * @see MacValidation#validateMac(String)
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void setBroadcastMac(String broadcastMac) {
    broadcastMac = normalizeForDto(broadcastMac,"broadcast MAC-адреса");

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
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void addUnknownParam(String unknownParam) {
    unknownParams.add(
        normalizeForDto(unknownParam,"Физический параметр интерфейса"));
  }

  /**
   * Возвращает список неизвестных физических параметров интерфейса.
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
