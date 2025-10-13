package com.github.GanenkovNA.ssh.host.dto;

import static com.github.GanenkovNA.service.StringUtils.normalizeForDto;
import com.github.GanenkovNA.service.StringUtils;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * DTO сетевого интерфейса хоста.
 *
 * <p>Хранит имя интерфейса и набор тестов, изменения от которых не удалось откатить.
 * {@code interfaceName} — строго не {@code null} и не пустая/пробельная строка (нормализация выполняется
 * в {@link StringUtils#normalizeForDto(String, String)}).
 * Коллекция {@code failedTests} — никогда не {@code null}, может быть пустой; снаружи возвращается
 * неизменяемая копия, порядок элементов соответствует порядку добавления.</p>
 *
 * <p>При JSON-де/сериализации используется конструктор с аннотацией {@code @JsonCreator}:
 * поле {@code interfaceName} обязательно; элементы {@code failedTests} нормализуются, дубликаты
 * удаляются (идемпотентность добавления тестов).</p>
 *
 * @see HostInterfacesConfigDTO
 * @see StringUtils#normalizeForDto(String, String)
 */
//@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"interfaceName", "failedTests"})
@ToString
@EqualsAndHashCode(of = "interfaceName")
public final class HostInterfaceDTO {
  /** Имя интерфейса. Строго не {@code null} и не пустое. */
  @Getter
  private final String interfaceName;

  /** Список тестов, изменения от которых не получилось откатить. */
  // @JsonInclude(JsonInclude.Include.ALWAYS)
  private final Set<String> failedTests = new LinkedHashSet<>();

  /**
   * Создаёт DTO интерфейса с пустым набором «проваленных» тестов.
   *
   * <p>Имя нормализуется (обрезка пробелов, проверка на пустоту) и сохраняется в каноническом виде.</p>
   *
   * @param interfaceName имя интерфейса; строго не {@code null} и не пустое после {@code trim()}
   * @throws NullPointerException если {@code interfaceName == null}
   * @throws IllegalArgumentException если {@code interfaceName} пустая/пробельная после {@code trim()}
   * @see StringUtils#normalizeForDto(String, String)
   */
  public HostInterfaceDTO(String interfaceName){
    this.interfaceName = normalizeForDto(interfaceName, "Имя интерфейса");
  }

  /**
   * JSON-конструктор для десериализации.
   *
   * <p>{@code interfaceName} обязателен и нормализуется; {@code failedTests} может быть {@code null}.
   * Каждый тест из {@code failedTests} нормализуется; дубликаты удаляются, порядок сохраняется.</p>
   *
   * @param interfaceName имя интерфейса; строго не {@code null} и не пустое после {@code trim()}
   * @param failedTests список названий тестов; может быть {@code null} или пустым
   * @throws NullPointerException если {@code interfaceName == null} или встречен {@code null} в {@code failedTests}
   * @throws IllegalArgumentException если {@code interfaceName} пустая/пробельная либо элемент {@code failedTests} пустой/пробельный
   * @see com.fasterxml.jackson.annotation.JsonCreator
   * @see StringUtils#normalizeForDto(String, String)
   */
  @com.fasterxml.jackson.annotation.JsonCreator
  public HostInterfaceDTO(
      @com.fasterxml.jackson.annotation.JsonProperty(value = "interfaceName", required = true) String interfaceName,
      @com.fasterxml.jackson.annotation.JsonProperty("failedTests") List<String> failedTests
  ) {
    this.interfaceName = normalizeForDto(interfaceName, "Имя интерфейса");
    if (failedTests != null) {
      for (String t : failedTests) {
        this.failedTests.add(normalizeForDto(t, "test"));
      }
    }
  }

  /**
   * Возвращает неизменяемую копию множества «проваленных» тестов.
   *
   * <p>Коллекция — никогда не {@code null}, может быть пустой. Порядок соответствует порядку добавления.</p>
   *
   * @return копия множества тестов; никогда не {@code null}, может быть пустой
   */
  public Set<String> getFailedTests() {
    return Set.copyOf(failedTests);
  }

  /**
   * Добавляет тест в список тех, изменения от которых не получилось откатить.
   *
   * <p>Название теста нормализуется; повторное добавление того же значения не изменяет состояние
   * (дубликаты игнорируются).</p>
   *
   * @param test название теста; строго не {@code null} и не пустое после {@code trim()}
   * @throws NullPointerException если {@code test == null}
   * @throws IllegalArgumentException если {@code test} пустая/пробельная после {@code trim()}
   * @see #isTestFailed(String)
   * @see com.github.GanenkovNA.service.StringUtils#normalizeForDto(String, String)
   */
  public void addFailedTest(String test) {
    failedTests.add(normalizeForDto(test, "test"));
  }

  /**
   * Проверяет, присутствует ли указанный тест в списке «проваленных».
   *
   * <p>Выполняется та же нормализация, что и при добавлении через {@link #addFailedTest(String)}.</p>
   *
   * @param test название теста; строго не {@code null} и не пустое после {@code trim()}
   * @return {@code true}, если тест уже присутствует; иначе {@code false}
   * @throws NullPointerException если {@code test == null}
   * @throws IllegalArgumentException если {@code test} пустая/пробельная после {@code trim()}
   * @see #addFailedTest(String)
   * @see com.github.GanenkovNA.service.StringUtils#normalizeForDto(String, String)
   */
  public boolean isTestFailed(String test) {
    return failedTests.contains(normalizeForDto(test, "test"));
  }
}
