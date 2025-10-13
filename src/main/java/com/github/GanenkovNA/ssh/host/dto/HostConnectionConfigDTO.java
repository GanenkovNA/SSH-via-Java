package com.github.GanenkovNA.ssh.host.dto;

import static com.github.GanenkovNA.service.StringUtils.normalizeForDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;


/**
 * DTO параметров подключения к SSH-серверу.
 *
 * <p>Поля:
 * {@code host} — адрес сервера (IP/DNS),
 * {@code port} — порт (по умолчанию {@code 22}),
 * {@code username} — имя пользователя,
 * {@code password} — пароль (может быть пустой строкой; {@code null} запрещён).</p>
 *
 * <p>Валидация:
 * {@code host}/{@code username} — строго не {@code null} и не пустые (после {@code trim()}).
 * {@code port} — {@code null} означает {@code 22}, иначе диапазон {@code 1..65_535}.</p>
 *
 * @see com.github.GanenkovNA.service.StringUtils#normalizeForDto(String, String)
 */
public record HostConnectionConfigDTO(
    String host,
    Integer port,
    String username,
    String password
) {
  @JsonCreator
  public HostConnectionConfigDTO(
      @JsonProperty("host") String host,
      @JsonProperty("port") Integer port,
      @JsonProperty("username") String username,
      @JsonProperty("password") String password
  ) {
    //host
    this.host = normalizeForDto(host, "host");

    // port
    int resolvedPort = (port == null) ? 22 : port;
    if (resolvedPort < 1 || resolvedPort > 65_535) {
      throw new IllegalArgumentException("port вне диапазона 1..65_535: " + resolvedPort);
    }
    this.port = resolvedPort;

    // username
    this.username = normalizeForDto(username, "username");

    // password (NULL запрещён, пустая строка допускается; пробелы сохраняем как есть)
    this.password = Objects.requireNonNull(password, "password не может быть null");
  }
}
