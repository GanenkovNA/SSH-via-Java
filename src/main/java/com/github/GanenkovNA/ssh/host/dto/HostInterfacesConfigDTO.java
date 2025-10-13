package com.github.GanenkovNA.ssh.host.dto;

import static com.github.GanenkovNA.service.StringUtils.normalizeForDto;
import com.github.GanenkovNA.service.StringUtils;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.InterfaceDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v4.InterfaceIpv4ConfigDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v6.InterfaceIpv6ConfigDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.Getter;

/**
 * Конфигурация интерфейсов хоста.
 *
 * <p>Хранит имя management-интерфейса и набор «рабочих» интерфейсов в виде {@link HostInterfaceDTO}.
 * {@code mgmtInterface} — строго не {@code null} и не пустая/пробельная строка
 * (нормализация выполняется в {@link StringUtils#normalizeForDto(String, String)}).
 * Коллекция {@code hostInterfaces} — никогда не {@code null}, может быть пустой; наружу не
 * экспонируется напрямую. Для безопасного чтения доступен снимок через
 * {@link #getInterfaceSnapshots()}.</p>
 *
 * <p>Контракты:</p>
 * <ul>
 *   <li>{@code mgmtInterface} не обязан присутствовать среди имён в {@code hostInterfaces};</li>
 *   <li>имена интерфейсов уникальны (дубликаты запрещены);</li>
 *   <li>все строки нормализуются и хранятся в каноническом виде.</li>
 * </ul>
 *
 * <p>JSON-де/сериализация:</p>
 * <ul>
 *   <li>используется конструктор с {@code @JsonCreator}; поле {@code mgmtInterface} — обязательное;</li>
 *   <li>лишние поля в JSON запрещены ({@code ignoreUnknown = false});</li>
 *   <li>{@code hostInterfaces} читается/пишется как массив объектов {@link HostInterfaceDTO};</li>
 *   <li>для стабильного вывода задан порядок полей {@code mgmtInterface → hostInterfaces}.</li>
 * </ul>
 *
 * @see HostInterfaceDTO
 * @see HostInterfacesSnapshot
 * @see StringUtils#normalizeForDto(String, String)
 */
@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY)
@com.fasterxml.jackson.annotation.JsonPropertyOrder({ "mgmtInterface", "hostInterfaces" })
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = false)
public class HostInterfacesConfigDTO {
  // (?i) — без учёта регистра; ^...$ — строгое совпадение
  private static final Pattern LOOPBACK_PATTERN = Pattern.compile("(?i)^(lo|loopback)$");
  @Getter
  private String mgmtInterface;
  @com.fasterxml.jackson.annotation.JsonProperty("hostInterfaces")
  private final List<HostInterfaceDTO> hostInterfaces = new ArrayList<>();

  public HostInterfacesConfigDTO(List<InterfaceDto> parsedInterfaces, String hostIp) {
    Objects.requireNonNull(hostIp, "hostIp == null");
    Objects.requireNonNull(parsedInterfaces, "parsedInterfaces == null");
    boolean mgmtAssigned = false;

    for (InterfaceDto parsedInterface : parsedInterfaces){
      if (parsedInterface == null){
        continue;
      }
      final String interfaceName = parsedInterface
          .getInterfaceParams().getName();
      boolean isMgmtHere = false;

      if (LOOPBACK_PATTERN.matcher(interfaceName).matches()){
        continue;
      }

      if (!mgmtAssigned && interfaceHasRequiredIp(parsedInterface, hostIp)){
        setMgmtInterface(interfaceName);
        mgmtAssigned = true;
        isMgmtHere = true;
      }

      if (!isMgmtHere){
        addInterface(interfaceName);
      }
    }

    if (!mgmtAssigned) {
      throw new IllegalArgumentException("Не найден интерфейс с IP: " + hostIp);
    }
  }


  /**
   * JSON-конструктор конфигурации.
   *
   * <p>{@code mgmtInterface} обязателен и нормализуется; {@code hostInterfaces} может быть {@code null}
   * или пустым. Элементы списка проверяются на {@code null} и дубликаты имён; при наличии дубликата
   * создаётся исключение.</p>
   *
   * @param mgmtInterface имя management-интерфейса; строго не {@code null} и не пустое после {@code trim()}
   * @param hostInterfaces список интерфейсов; может быть {@code null} или пустым
   * @throws NullPointerException если {@code mgmtInterface == null} или встречен {@code null}-элемент в {@code hostInterfaces}
   * @throws IllegalArgumentException если {@code mgmtInterface} пустая/пробельная строка либо обнаружен дубликат имени интерфейса
   * @see com.fasterxml.jackson.annotation.JsonCreator
   * @see HostInterfaceDTO
   * @see StringUtils#normalizeForDto(String, String)
   */
  @com.fasterxml.jackson.annotation.JsonCreator
  public HostInterfacesConfigDTO(
      @com.fasterxml.jackson.annotation.JsonProperty(value = "mgmtInterface", required = true) String mgmtInterface,
      @com.fasterxml.jackson.annotation.JsonProperty("hostInterfaces") List<HostInterfaceDTO> hostInterfaces
  ) {
    setMgmtInterface(mgmtInterface); // нормализация + NPE/IAE
    if (hostInterfaces != null) {
      for (HostInterfaceDTO dto : hostInterfaces) {
        Objects.requireNonNull(dto, "hostInterfaces содержит null-элемент");
        // запрет дублей по имени
        if (indexOfInterfaceByName(dto.getInterfaceName()) >= 0) {
          throw new IllegalArgumentException("Дубликат интерфейса: \"" + dto.getInterfaceName() + '"');
        }
        this.hostInterfaces.add(dto);
      }
    }
  }

  /**
   * Устанавливает имя management-интерфейса.
   *
   * <p>Строка нормализуется (обрезка пробелов, проверка на пустоту) и сохраняется в каноническом виде.</p>
   *
   * @param mgmtInterface имя интерфейса; строго не {@code null} и не пустое после {@code trim()}
   * @throws NullPointerException если {@code mgmtInterface == null}
   * @throws IllegalArgumentException если {@code mgmtInterface} пустая/пробельная после {@code trim()}
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void setMgmtInterface(String mgmtInterface){
    this.mgmtInterface = normalizeForDto(mgmtInterface, "Management-интерфейс");
  }

  /**
   * Добавляет интерфейс в конфигурацию.
   *
   * <p>Имя нормализуется; при попытке добавить интерфейс с уже существующим именем выбрасывается исключение.</p>
   *
   * @param interfaceName имя интерфейса; строго не {@code null} и не пустое после {@code trim()}
   * @throws NullPointerException если {@code interfaceName == null}
   * @throws IllegalArgumentException если {@code interfaceName} пустая/пробельная либо интерфейс с таким именем уже существует
   * @see HostInterfaceDTO
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void addInterface (String interfaceName) {
    interfaceName = normalizeForDto(interfaceName, "Имя интерфейса");

    if (indexOfInterfaceByName(interfaceName) >= 0) {
      throw new IllegalArgumentException("Интерфейс \"" + interfaceName + "\" уже существует");
    }
    hostInterfaces.add(new HostInterfaceDTO(interfaceName));
  }

  /**
   * Добавляет тест в список «не откатилось» для указанного интерфейса.
   *
   * <p>Метод строгий: интерфейс должен существовать. И имя интерфейса, и название теста нормализуются;
   * повторное добавление того же теста не изменяет состояние (идемпотентность на уровне {@link HostInterfaceDTO}).</p>
   *
   * @param interfaceName имя интерфейса; строго не {@code null} и не пустое после {@code trim()}
   * @param test название теста; строго не {@code null} и не пустое после {@code trim()}
   * @throws NullPointerException если любой из параметров {@code null}
   * @throws IllegalArgumentException если любой из параметров пустой/пробельный либо интерфейс не существует
   * @see HostInterfaceDTO#addFailedTest(String)
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void addFailedTestForInterface(String interfaceName, String test) {
    interfaceName = normalizeForDto(interfaceName, "Имя интерфейса");
    test = normalizeForDto(test, "test");

    int index = indexOfInterfaceByName(interfaceName);
    if (index >= 0) {
      hostInterfaces.get(index).addFailedTest(test);
    } else {
      throw new IllegalArgumentException("Интерфейс \"" + interfaceName + "\" не существует");
    }
  }

  /**
   * Удаляет интерфейс по имени.
   *
   * <p>Имя нормализуется. Если интерфейс не найден — выбрасывается исключение. Значение
   * {@code mgmtInterface} умышленно не изменяется, даже если совпадает по имени с удалённым интерфейсом.</p>
   *
   * @param interfaceName имя интерфейса; строго не {@code null} и не пустое после {@code trim()}
   * @throws NullPointerException если {@code interfaceName == null}
   * @throws IllegalArgumentException если {@code interfaceName} пустая/пробельная или интерфейс отсутствует в конфигурации
   * @see StringUtils#normalizeForDto(String, String)
   */
  public void removeInterfaceByName(String interfaceName) {
    interfaceName = normalizeForDto(interfaceName, "Имя интерфейса");

    int index = indexOfInterfaceByName(interfaceName);
    if (index >= 0) {
      hostInterfaces.remove(index);
    } else {
      throw new IllegalArgumentException(
          "Интерфейс \"" + interfaceName + "\" отсутствует в конфигурации (hostInterfaces)");
    }
  }

  /**
   * Возвращает неизменяемый список снимков интерфейсов.
   *
   * <p>Каждый элемент — {@link HostInterfacesSnapshot}, отражающий состояние на момент вызова.
   * Список — никогда не {@code null}, может быть пустым; внешняя модификация невозможна.</p>
   *
   * @return неизменяемый список снимков; никогда не {@code null}, может быть пустым
   * @see HostInterfacesSnapshot
   */
  @com.fasterxml.jackson.annotation.JsonIgnore
  public List<HostInterfacesSnapshot> getInterfaceSnapshots() {
    List<HostInterfacesSnapshot> out = new ArrayList<>(hostInterfaces.size());
    for (HostInterfaceDTO it : hostInterfaces) {
      out.add(HostInterfacesSnapshot.from(it));
    }
    return List.copyOf(out);
  }


  private int indexOfInterfaceByName(String normalizedName){
    Objects.requireNonNull(normalizedName, "normalizedName не может быть null");

    for (int i = 0; i < hostInterfaces.size(); i++) {
      HostInterfaceDTO it = hostInterfaces.get(i);
      if (it != null && normalizedName.equals(it.getInterfaceName())){
        return i;
      }
    }
    return -1;
  }

  private static boolean interfaceHasRequiredIp(InterfaceDto itf, String requiredIp) {
    // IPv4
    if (itf.getIpv4() != null){
      for (InterfaceIpv4ConfigDto ip4 : itf.getIpv4()){
        if (ip4 == null){
          continue;
        }
        final String address = ip4.getAddress();
        if (requiredIp.equals(address)){
          return true;
        }
      }
    }
    // IPv6
    if (itf.getIpv6() != null){
      for (InterfaceIpv6ConfigDto ip6 : itf.getIpv6()){
        if (ip6 == null){
          continue;
        }
        final String address = ip6.getAddress();
        if (requiredIp.equals(address)){
          return true;
        }
      }
    }
    return false;
  }
}
