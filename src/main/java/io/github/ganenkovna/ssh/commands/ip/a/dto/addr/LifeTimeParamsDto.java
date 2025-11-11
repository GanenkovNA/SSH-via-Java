package io.github.ganenkovna.ssh.commands.ip.a.dto.addr;

import static io.github.ganenkovna.util.StringUtils.normalizeForDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.ganenkovna.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Параметры времени жизни IP-адреса (IPv4/IPv6).
 *
 * <p>Определяет срок действия адреса до его устаревания (preferred) и полной недействительности (valid).</p>
 *
 * <p>Поддерживаются человекочитаемые строковые значения, например:
 * <ul>
 *   <li>{@code forever} — бессрочное действие;</li>
 *   <li>{@code 3600sec}, {@code 60min}, {@code 2hour} — интервалы в секундах/минутах/часах;</li>
 *   <li>{@code 0sec} — недействителен сразу.</li>
 * </ul></p>
 *
 * <p><b>Важно:</b> данный DTO выполняет только базовую нормализацию строк
 * (через {@link StringUtils#normalizeForDto(String, String)}):
 * {@code trim()} и проверки на {@code null}/{@code empty}. Семантическая проверка форматов и соотношений
 * значений (например, сравнение {@code preferred} и {@code valid}) вне области ответственности этого класса.</p>
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class LifeTimeParamsDto {

  /** Время жизни адреса до недействительности. */
  private String validLft;

  /** Время жизни адреса до "устаревания". */
  private String preferredLft;

  /** Список нераспознанных параметров времени жизни (служебные данные парсера). */
  @Setter(AccessLevel.NONE)
  private List<String> unknownParams = new ArrayList<>();


  /**
   * Устанавливает время жизни адреса до недействительности (valid lifetime).
   *
   * <p>Примеры значений:
   * <ul>
   *   <li>{@code 3600sec}, {@code 60min}, {@code 2hour};</li>
   *   <li>{@code 0sec};</li>
   *   <li>{@code forever}.</li>
   * </ul></p>
   *
   * @param validLft строка с временем жизни
   * @throws NullPointerException если {@code validLft == null}
   * @throws IllegalArgumentException если строка пустая после {@code trim()}
   * @see StringUtils#normalizeForDto(String, String)
   * @see <a href="https://man7.org/linux/man-pages/man8/ip-address.8.html">ip-address(8)</a>
   * @see <a href="https://www.rfc-editor.org/rfc/rfc4862#section-5.5.4">RFC 4862 §5.5.4</a>
   */
  public void setValidLft(String validLft) {
    this.validLft = normalizeForDto(validLft,"validLft")
        .toLowerCase();
  }

  /**
   * Устанавливает время жизни адреса до устаревания (preferred lifetime).
   *
   * <p>Примеры значений:
   * <ul>
   *   <li>{@code 1800sec}, {@code 30min};</li>
   *   <li>{@code forever}.</li>
   * </ul></p>
   *
   * @param preferredLft строка с временем жизни
   * @throws NullPointerException если {@code preferredLft == null}
   * @throws IllegalArgumentException если строка пустая после {@code trim()}
   * @see #setValidLft(String)
   * @see StringUtils#normalizeForDto(String, String)
   * @see <a href="https://www.kernel.org/doc/html/latest/networking/ip-sysctl.html">IP sysctl</a>
   */
  public void setPreferredLft(String preferredLft) {
    this.preferredLft = normalizeForDto(preferredLft,"preferredLft")
        .toLowerCase();
  }

  /**
   * Добавляет нераспознанный параметр времени жизни.
   *
   * <p>Используется для сохранения параметров, которые не могут быть обработаны текущей версией,
   * но должны быть сохранены для обратной совместимости.</p>
   *
   * @param unknownParam параметр для добавления
   * @throws NullPointerException если {@code unknownParam == null}
   * @throws IllegalArgumentException если строка пустая после {@code trim()}
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void addUnknownParam(String unknownParam) {
    unknownParams.add(
        normalizeForDto(unknownParam,"Параметр времени жизни IP-адреса"));
  }

  /**
   * Возвращает нераспознанные параметры конфигурации.
   *
   * @return неизменяемый список; никогда не {@code null}, может быть пустым
   */
  public List<String> getUnknownParams() {
    return List.copyOf(unknownParams);
  }
}
