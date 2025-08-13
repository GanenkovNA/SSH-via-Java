package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.GanenkovNA.service.StringUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Параметры времени жизни IP-адреса (IPv4/IPv6).
 *
 * <p>Определяет срок действия адреса до его устаревания и полной недействительности.
 * Поддерживает как абсолютные значения времени, так и ключевое слово "forever".
 *
 * <p>Формат значений:
 * <ul>
 *   <li>{@code "forever"} - бессрочное действие
 *   <li>{@code "<number>sec"} - время в секундах (например, {@code "3600sec"})
 *   <li>{@code "<number>min"} - время в минутах
 *   <li>{@code "<number>hour"} - время в часах
 * </ul>
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LifeTimeParamsDto {

  /** Время жизни адреса до недействительности. */
  private String validLft;

  /** Время жизни адреса до "устаревания". */
  private String preferredLft;

  /** Список нераспознанных параметров. */
  @Setter(AccessLevel.NONE)
  private List<String> unknownParams = new ArrayList<>();


  /**
   * Устанавливает время жизни адреса до недействительности (valid lifetime).
   *
   * <p>Поддерживаемые форматы значений:
   * <ul>
   *   <li>Абсолютное время: "3600sec", "60min", "2hour"</li>
   *   <li>Относительное время: "0sec" (недействителен сразу)</li>
   *   <li>Специальные значения: "forever", "infinity"</li>
   * </ul>
   *
   * @param validLft строка с временем жизни
   * @throws NullPointerException если validLft равен null
   * @throws IllegalArgumentException если validLft:
   *         - пустая строка
   *         - содержит только пробелы
   *         - имеет недопустимый формат
   *
   * @see <a href="https://man7.org/linux/man-pages/man8/ip-address.8.html">ip-address(8)</a>
   * @see <a href="https://tools.ietf.org/html/rfc4862#section-5.5.4">RFC 4862 Section 5.5.4</a>
   */
  public void setValidLft(String validLft)
      throws NullPointerException, IllegalArgumentException {
    this.validLft = StringUtils.normalizeForDto(validLft,
        "Значение validLft не может быть null",
        "Значение validLft не может быть пустым");
  }

  /**
   * Устанавливает время жизни адреса до устаревания (preferred lifetime).
   *
   * <p>Значение должно быть ≤ validLft. Поддерживаемые форматы:
   * <ul>
   *   <li>Число + единица: "1800sec", "30min"</li>
   *   <li>Специальные значения: "forever"</li>
   * </ul>
   *
   * @param preferredLft строка с временем жизни
   * @throws NullPointerException если preferredLft равен null
   * @throws IllegalArgumentException если preferredLft:
   *         - пустая строка
   *         - имеет недопустимый формат
   *         - превышает validLft
   *
   * @see #setValidLft(String)
   * @see <a href="https://www.kernel.org/doc/html/latest/networking/ip-sysctl.html">IP sysctl</a>
   */
  public void setPreferredLft(String preferredLft)
      throws NullPointerException, IllegalArgumentException {
    this.preferredLft = StringUtils.normalizeForDto(preferredLft,
        "Значение preferredLft не может быть null",
        "Значение preferredLft не может быть пустым");
  }

  /**
   * Добавляет нераспознанный параметр времени жизни.
   *
   * <p>Используется для сохранения параметров, которые не могут быть обработаны текущей версией,
   * но должны быть сохранены для обратной совместимости.
   *
   * @param unknownParam параметр для добавления
   * @throws NullPointerException если unknownParam равен null
   * @throws IllegalArgumentException если unknownParam:
   *         - пустая строка
   *         - содержит только пробелы
   */
  public void addUnknownParam(String unknownParam)
      throws NullPointerException, IllegalArgumentException {
    unknownParams.add(
        StringUtils.normalizeForDto(
            unknownParam,
            "Параметр времени жизни IP-адреса не может быть null",
            "Параметр времени жизни IP-адреса не может быть пустым"));
  }
}
