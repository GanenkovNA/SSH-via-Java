package io.github.ganenkovna.ssh.host.dto;

import io.github.ganenkovna.ssh.host.HostConfigIO;
import io.github.ganenkovna.util.StringUtils;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

/**
 * Агрегатор конфигурации хоста:
 * <ul>
 *   <li>{@link HostConnectionConfigDTO} — параметры подключения к SSH-серверу;</li>
 *   <li>{@link HostInterfacesConfigDTO} — конфигурация интерфейсов (mgmt + набор «рабочих»).</li>
 * </ul>
 *
 * <p>Класс собирается из двух независимых источников
 * ({@code host_connection_config.json} и {@code host_interfaces_config.json}) и предоставляет
 * удобные методы чтения без прямой мутации внутренних коллекций.</p>
 */
public final class HostConfigDTO {
  /** Параметры подключения. Строго не {@code null}. */
  @Getter
  private final HostConnectionConfigDTO connectionConfig;

  /**
   * Конфигурация интерфейсов (mgmt + список). Может быть {@code null} до загрузки из JSON.
   * Мутации интерфейсов выполняются только через сам {@link HostInterfacesConfigDTO}.
   */
  private HostInterfacesConfigDTO interfacesConfig;

  /**
   * Создаёт конфигурацию хоста с заданными параметрами подключения.
   *
   * @param connectionConfig Параметры подключения. Строго не {@code null}.
   * @throws NullPointerException если {@code connectionConfig == null}
   */
  public HostConfigDTO(HostConnectionConfigDTO connectionConfig) {
    this.connectionConfig = Objects
        .requireNonNull(connectionConfig, "connectionConfig не может быть null");
  }

  /**
   * Устанавливает новый конфиг интерфейсов.
   *
   * <p>Метод предназначен исключительно для инфраструктурного кода
   * (например, {@link HostConfigIO})
   * при обновлении конфигурации интерфейсов после чтения или генерации
   * из системных данных ({@code ip a}).</p>
   *
   * <p>В прикладной логике и тестах изменения интерфейсов выполняются
   * через форвардеры самого агрегатора ({@code HostConfigDTO}), а не заменой
   * всего объекта интерфейсного конфига.</p>
   *
   * @param interfacesConfig новый конфиг интерфейсов; строго не {@code null}
   * @throws NullPointerException если {@code interfacesConfig == null}
   * @see #setMgmtInterface(String)
   * @see #addInterface(String)
   * @see #removeInterfaceByName(String)
   * @see #addFailedTestForInterface(String, String)
   * @see HostConfigIO
   */
  public void setInterfacesConfig(HostInterfacesConfigDTO interfacesConfig) {
    this.interfacesConfig = Objects.requireNonNull(interfacesConfig,
        "interfacesConfig не может быть null");
  }

  /**
   * Возвращает снимки интерфейсов для безопасного чтения.
   *
   * @return неизменяемый список снимков; никогда не {@code null}, может быть пустым
   */
  public List<HostInterfacesSnapshot> getInterfaceSnapshots() {
    return (interfacesConfig == null) ? List.of() : interfacesConfig.getInterfaceSnapshots();
  }


  // ---- Форвардеры (публичный API через агрегатор) ----

  /**
   * Устанавливает имя management-интерфейса.
   *
   * <p>Строка нормализуется и сохраняется в каноническом виде
   * (см. {@link StringUtils#normalizeForDto(String, String)}).
   * Внутренняя валидация и сообщения об ошибках делегируются в
   * {@link HostInterfacesConfigDTO#setMgmtInterface(String)}.</p>
   *
   * @param mgmtInterface имя интерфейса; строго не {@code null} и не пустое после {@code trim()}
   * @throws IllegalStateException если конфигурация интерфейсов не установлена
   * @throws NullPointerException если {@code mgmtInterface == null}
   * @throws IllegalArgumentException если имя пустое/пробельное
   */
  public void setMgmtInterface(String mgmtInterface) {
    requireInterfacesConfig().setMgmtInterface(mgmtInterface);
  }

  /**
   * Возвращает имя {@code management}-интерфейса (mgmt).
   *
   * <p>Значение определяется на этапе сборки {@link HostInterfacesConfigDTO} и соответствует
   * интерфейсу, IP которого совпадает с IP хоста из {@link HostConnectionConfigDTO}.</p>
   *
   * <p>Метод делегирует вызов в {@link HostInterfacesConfigDTO#getMgmtInterface()}.
   * Если конфигурация интерфейсов отсутствует, будет выброшено исключение.</p>
   *
   * @return имя {@code management}-интерфейса;
   *     никогда не {@code null} и не пустое после {@code trim()}
   * @throws IllegalStateException если конфигурация интерфейсов не инициализирована
   * @see HostInterfacesConfigDTO#getMgmtInterface()
   * @see #requireInterfacesConfig()
   */
  public String getMgmtInterface() {
    return requireInterfacesConfig().getMgmtInterface();
  }

  /**
   * Добавляет интерфейс по имени.
   *
   * <p>Имя нормализуется; при попытке добавить дубликат выбрасывается исключение.
   * Делегирует в {@link HostInterfacesConfigDTO#addInterface(String)}.</p>
   *
   * @param interfaceName имя интерфейса; строго не {@code null} и не пустое после {@code trim()}
   * @throws IllegalStateException если конфигурация интерфейсов не установлена
   * @throws NullPointerException если {@code interfaceName == null}
   * @throws IllegalArgumentException если имя пустое/пробельное или интерфейс уже существует
   */
  public void addInterface(String interfaceName) {
    requireInterfacesConfig().addInterface(interfaceName);
  }

  /**
   * Отмечает тест как «не откатился» для указанного интерфейса.
   *
   * <p>Метод строгий: интерфейс должен существовать. Делегирует в
   * {@link HostInterfacesConfigDTO#addFailedTestForInterface(String, String)}.</p>
   *
   * @param interfaceName имя интерфейса; строго не {@code null}/blank после {@code trim()}
   * @param test название теста; строго не {@code null}/blank после {@code trim()}
   * @throws IllegalStateException если конфигурация интерфейсов не установлена
   * @throws NullPointerException если любой из параметров {@code null}
   * @throws IllegalArgumentException если параметры пустые/пробельные либо интерфейс не существует
   */
  public void addFailedTestForInterface(String interfaceName, String test) {
    requireInterfacesConfig().addFailedTestForInterface(interfaceName, test);
  }

  /**
   * Удаляет интерфейс по имени.
   *
   * <p>Имя нормализуется. Если интерфейс не найден — выбрасывается исключение.
   * Делегирует в {@link HostInterfacesConfigDTO#removeInterfaceByName(String)}.</p>
   *
   * @param interfaceName имя интерфейса; строго не {@code null}/blank после {@code trim()}
   * @throws IllegalStateException если конфигурация интерфейсов не установлена
   * @throws NullPointerException если {@code interfaceName == null}
   * @throws IllegalArgumentException если имя пустое/пробельное или интерфейс отсутствует
   */
  public void removeInterfaceByName(String interfaceName) {
    requireInterfacesConfig().removeInterfaceByName(interfaceName);
  }

  /**
   * Возвращает установленную конфигурацию интерфейсов или бросает исключение.
   *
   * <p>Служебный метод-сторож для всех форвардеров: гарантирует, что конфигурация интерфейсов
   * загружена из {@code host_interfaces_config.json} и установлена через
   * {@link #setInterfacesConfig(HostInterfacesConfigDTO)}.</p>
   *
   * @return установленная конфигурация интерфейсов (живой объект), никогда не {@code null}
   * @throws IllegalStateException если конфигурация интерфейсов не установлена
   * @see #setInterfacesConfig(HostInterfacesConfigDTO)
   * @see HostInterfacesConfigDTO
   */
  private HostInterfacesConfigDTO requireInterfacesConfig() {
    if (interfacesConfig == null) {
      throw new IllegalStateException("interfacesConfig не установлен");
    }
    return interfacesConfig;
  }
}
