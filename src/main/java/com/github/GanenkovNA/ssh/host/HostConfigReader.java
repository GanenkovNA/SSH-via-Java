package com.github.GanenkovNA.ssh.host;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;

/**
 * SSH-клиент для выполнения команд на удаленных Linux-устройствах.
 *
 * <p>Использует библиотеку JSch для установки SSH-соединений. Поддерживает конфигурацию подключения
 * через JSON-файл.</p>
 *
 * <p>Пример файла:
 * <pre>{@code
 *   {
 *     "host": "example.com",
 *     "port": 22,
 *     "username": "user",
 *     "password": "secret"
 *   }
 * }</pre></p>
 *
 * <p>Использует {@link ObjectMapper} для десериализации в {@link HostConnectionConfigDto}.</p>
 */
public final class HostConfigReader {

  /**
   * Считывает конфигурацию SSH-подключения из JSON-файла.
   *
   * <p><b>Требования к JSON:</b>
   * <ul>
   *   <li>Должен содержать все обязательные поля ({@code host}, {@code username}, etc.)</li>
   *   <li>Поля должны соответствовать структуре {@link HostConnectionConfigDto}</li>
   * </ul></p>
   *
   * @param pathToJson путь к файлу конфигурации относительно classpath
   * @return объект с параметрами подключения
   * @throws RuntimeException если файл не найден или содержит некорректные данные
   * @see HostConnectionConfigDto
   */
  public static HostConnectionConfigDto getHostConnectionConfig(String pathToJson) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      InputStream inputStream = HostConfigReader.class
          .getResourceAsStream(pathToJson);
      return mapper.readValue(inputStream, HostConnectionConfigDto.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
