package com.github.GanenkovNA.ssh.host;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) для параметров подключения к SSH-серверу.
 * <p>
 * Содержит все необходимые параметры для установки SSH-соединения:
 * <ul>
 *   <li><b>host</b> - адрес сервера (IP или доменное имя)</li>
 *   <li><b>port</b> - номер порта (по умолчанию: 22)</li>
 *   <li><b>username</b> - имя пользователя для аутентификации</li>
 *   <li><b>password</b> - пароль пользователя (может быть пустой строкой)</li>
 * </ul>
 *
 * <p><b>Требования к данным:</b>
 * <ul>
 *   <li>Все поля обязательны (кроме порта, который имеет значение по умолчанию)</li>
 *   <li>Номер порта должен быть в диапазоне 1-65535</li>
 *   <li>Строковые поля не могут быть null</li>
 * </ul>
 *
 * <p><b>Особенности обработки:</b>
 * <ul>
 *   <li>При отсутствии порта (null) используется значение по умолчанию 22</li>
 *   <li>При указании недопустимого порта генерируется IllegalArgumentException</li>
 *   <li>Пустой пароль допустим при использовании аутентификации по ключу</li>
 * </ul>
 *
 * @see IllegalArgumentException при недопустимых значениях порта
 * @see NullPointerException при отсутствии обязательных полей
 */
public record HostConnectionConfigDto(
    String host,
    Integer port,
    String username,
    String password) {

  /**
   * Создает новый экземпляр DTO для SSH-подключения.
   *
   * @param host адрес сервера (не может быть null)
   * @param port номер порта (1-65535, null для значения по умолчанию 22)
   * @param username имя пользователя (не может быть null)
   * @param password пароль (не может быть null, может быть пустым)
   * @throws IllegalArgumentException если порт вне допустимого диапазона
   * @throws NullPointerException если обязательные строковые поля null
   */
  @JsonCreator
  public HostConnectionConfigDto(
      @JsonProperty("host") String host,
      @JsonProperty("port") Integer port,
      @JsonProperty("username") String username,
      @JsonProperty("password") String password
  ) {
    this.host = Objects.requireNonNull(host, "Поле `host` не может быть пустым!");
    this.username = Objects.requireNonNull(username, "Поле `username` не может быть пустым!");
    this.password = Objects.requireNonNull(password, "Поле `password` не может быть пустым!");

    port = (port == null) ? 22 : port;
    if (port < 1 || port > 65535) {
      throw new IllegalArgumentException("Значение SSH-порта должно быть в диапазоне 1-65535!");
    } else {
      this.port = port;
    }
  }
}
