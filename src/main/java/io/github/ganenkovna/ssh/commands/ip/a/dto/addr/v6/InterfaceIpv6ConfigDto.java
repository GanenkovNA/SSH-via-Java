package io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v6;

import static io.github.ganenkovna.util.StringUtils.normalizeForDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.ganenkovna.util.StringUtils;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.LifeTimeParamsDto;
import io.github.ganenkovna.util.IpValidation;
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
 * Конфигурация IPv6-адреса сетевого интерфейса.
 *
 * <p>Содержит параметры адресации, множества флагов генерации и маршрутизации, области видимости
 * и параметры времени жизни IPv6-адреса. Все строковые представления адресов должны
 * соответствовать RFC 5952.</p>
 *
 * @see IpV6Scope Область видимости IPv6-адреса
 * @see GenerationFlag Флаги генерации IPv6-адресов
 * @see RouteFlag Флаги маршрутизации IPv6
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
   * Множество областей видимости IPv6-адреса.
   *
   * <p>Внутренне хранится как {@code EnumSet}; наружу возвращается
   * неизменяемое представление.</p>
   *
   * @see IpV6Scope
   */
  @Setter(AccessLevel.NONE)
  private EnumSet<IpV6Scope> scopes = EnumSet.noneOf(IpV6Scope.class);

  /**
   * Множество флагов генерации IPv6-адреса.
   *
   * <p>Внутренне хранится как {@code EnumSet}; наружу возвращается
   * неизменяемое представление.</p>
   *
   * @see GenerationFlag
   */
  @Setter(AccessLevel.NONE)
  private EnumSet<GenerationFlag> generationFlags = EnumSet.noneOf(GenerationFlag.class);

  /**
   * Множество флагов маршрутизации IPv6-адреса.
   *
   * <p>Внутренне хранится как {@code EnumSet}; наружу возвращается
   * неизменяемое представление.</p>
   *
   * @see RouteFlag
   */

  @Setter(AccessLevel.NONE)
  private EnumSet<RouteFlag> routeFlags = EnumSet.noneOf(RouteFlag.class);

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
   * <p>Нормализация выполняется в {@link StringUtils#normalizeForDto(String, String)}:
   * {@code trim()} и проверка на пустую строку.</p>
   *
   * @param address IPv6-адрес в формате RFC&nbsp;5952; не {@code null}
   * @throws NullPointerException если {@code address == null}
   * @throws IllegalArgumentException если строка пуста после {@code trim()} или адрес не соответствует формату IPv6
   * @see IpValidation#validateIpv6(String)
   * @see StringUtils#normalizeForDto(String, String)
   * @see <a href="https://www.rfc-editor.org/rfc/rfc5952">RFC 5952</a>
   * @see <a href="https://www.rfc-editor.org/rfc/rfc4291">RFC 4291</a>
   * @see <a href="https://www.rfc-editor.org/rfc/rfc4007">RFC 4007</a>
   */
  public void setAddress(String address)
      throws NullPointerException, IllegalArgumentException {
    address = normalizeForDto(address,"IPv6-адрес");

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
   * @param prefix длина префикса (диапазон {@code 0..128})
   * @throws IllegalArgumentException если {@code prefix} вне диапазона {@code 0..128}
   */
  public void setPrefix(int prefix)
      throws IllegalArgumentException {
    if (prefix >= 0 && prefix <= 128) {
      this.prefix = prefix;
    } else {
      throw new IllegalArgumentException("Невалидное значение префикса IPv6-адреса: " + prefix
          + "\nДопустимые значения от 0 до 128");
    }
  }

  /**
   * Добавляет область видимости IPv6-адреса.
   *
   * @param scope область видимости; не {@code null}
   * @throws NullPointerException если {@code scope == null}
   * @see IpV6Scope
   */
  public void addScope(IpV6Scope scope)
      throws NullPointerException {
    Objects.requireNonNull(scope, "Значение области видимости IPv6-адреса не может быть null");
    scopes.add(scope);
  }

  /**
   * Возвращает области видимости IPv6-адреса.
   *
   * @return неизменяемое множество областей; никогда не {@code null}, может быть пустым
   */
  public Set<IpV6Scope> getScopes() {
    return Collections.unmodifiableSet(scopes);
  }

  /**
   * Добавляет флаг генерации IPv6-адреса.
   *
   * @param generationFlag флаг генерации; не {@code null}
   * @throws NullPointerException если {@code generationFlag == null}
   * @see GenerationFlag
   */
  public void addGenerationFlag(GenerationFlag generationFlag)
      throws NullPointerException {
    Objects.requireNonNull(generationFlag,
        "Значение флага генерации IPv6-адреса не может быть null");
    generationFlags.add(generationFlag);
  }

  /**
   * Возвращает флаги генерации IPv6-адреса.
   *
   * @return неизменяемое множество флагов; никогда не {@code null}, может быть пустым
   */
  public Set<GenerationFlag> getGenerationFlags() {
    return Collections.unmodifiableSet(generationFlags);
  }

  /**
   * Добавляет флаг маршрутизации IPv6-адреса.
   *
   * @param routeFlag флаг маршрутизации; не {@code null}
   * @throws NullPointerException если {@code routeFlag == null}
   * @see RouteFlag
   */
  public void addRouteFlag(RouteFlag routeFlag)
      throws NullPointerException {
    Objects.requireNonNull(routeFlag,
        "Значение флага маршрутизации IPv6-адреса не может быть null");
    routeFlags.add(routeFlag);
  }

  /**
   * Возвращает флаги маршрутизации IPv6-адреса.
   *
   * @return неизменяемое множество флагов; никогда не {@code null}, может быть пустым
   */
  public Set<RouteFlag> getRouteFlags() {
    return Collections.unmodifiableSet(routeFlags);
  }

  /**
   * Устанавливает параметры времени жизни IPv6-адреса.
   *
   * @param lifeTimeParams параметры времени жизни; не {@code null}
   * @throws NullPointerException если {@code lifeTimeParams == null}
   * @see LifeTimeParamsDto
   */
  public void setLifeTimeParams(LifeTimeParamsDto lifeTimeParams)
      throws NullPointerException {
    Objects.requireNonNull(lifeTimeParams,
        "Значение параметров времени жизни IPv6-адреса не может быть null");
    this.lifeTimeParams = lifeTimeParams;
  }

  /**
   * Добавляет нераспознанный параметр.
   *
   * <p>Нормализация выполняется в {@link StringUtils#normalizeForDto(String, String)}.</p>
   *
   * @param unknownParam параметр для добавления; не {@code null}
   * @throws NullPointerException если {@code unknownParam == null}
   * @throws IllegalArgumentException если параметр пуст после {@code trim()}
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void addUnknownParam(String unknownParam)
      throws NullPointerException, IllegalArgumentException {
    unknownParams.add(
        normalizeForDto(unknownParam,"Параметр IPv6-адреса"));
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