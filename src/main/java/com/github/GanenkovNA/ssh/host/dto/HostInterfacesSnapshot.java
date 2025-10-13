package com.github.GanenkovNA.ssh.host.dto;

import static com.github.GanenkovNA.service.StringUtils.normalizeForDto;
import com.github.GanenkovNA.service.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Неизменяемый снимок интерфейса для безопасного чтения.
 *
 * <p>Содержит имя интерфейса и список тестов, изменения от которых не удалось откатить.
 * {@code interfaceName} нормализуется в
 * {@link StringUtils#normalizeForDto(String, String)} и хранится
 * в каноническом виде. Коллекция {@code failedTests} — никогда не {@code null}, может быть пустой;
 * наружу возвращается неизменяемая копия. Порядок элементов соответствует порядку добавления.</p>
 *
 * <p>Это «снимок» состояния: дальнейшие изменения исходного {@link HostInterfaceDTO} не влияют
 * на уже созданный {@code HostInterfacesSnapshot}.</p>
 *
 * @see HostInterfaceDTO
 * @see HostInterfacesConfigDTO
 * @see StringUtils#normalizeForDto(String, String)
 */
public record HostInterfacesSnapshot(String interfaceName, List<String> failedTests) {
  /**
   * Создаёт снимок с нормализацией имени и фиксацией неизменяемой копии списка тестов.
   *
   * <p>Строка {@code interfaceName} нормализуется (обрезка пробелов, проверка на пустоту) и сохраняется
   * в каноническом виде. Коллекция {@code failedTests} копируется в неизменяемый список
   * (см. {@link java.util.List#copyOf(java.util.Collection)}); порядок элементов сохраняется.</p>
   *
   * @param interfaceName имя интерфейса; строго не {@code null} и не пустое после {@code trim()}
   * @param failedTests список названий тестов; никогда не {@code null}, может быть пустым
   * @throws NullPointerException если {@code interfaceName == null} или {@code failedTests == null}
   * @throws IllegalArgumentException если {@code interfaceName} пустая/пробельная после {@code trim()}
   * @see StringUtils#normalizeForDto(String, String)
   */
  public HostInterfacesSnapshot {
    interfaceName = normalizeForDto(interfaceName, "Имя интерфейса");
    Objects.requireNonNull(failedTests, "failedTests не может быть null");
    failedTests = List.copyOf(failedTests); // делает список read-only и фиксирует порядок
  }

  /**
   * Строит неизменяемый снимок из «живого» DTO.
   *
   * <p>Имя интерфейса берётся из {@code dto} и нормализуется повторно (идемпотентно).
   * Множество тестов копируется в список для сохранения порядка, затем фиксируется как
   * неизменяемый.</p>
   *
   * @param dto исходный DTO; строго не {@code null}
   * @return неизменяемый снимок интерфейса
   * @throws NullPointerException если {@code dto == null}
   * @see HostInterfaceDTO
   */
  public static HostInterfacesSnapshot from(HostInterfaceDTO dto) {
    Objects.requireNonNull(dto, "HostInterfaceDTO не может быть null");
    // dto.getFailedTests() вернёт Set<String> (unmodifiable), сохраним порядок в List
    return new HostInterfacesSnapshot(
        dto.getInterfaceName(),
        new ArrayList<>(dto.getFailedTests())
    );
  }
}
