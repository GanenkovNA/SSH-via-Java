package com.github.GanenkovNA.ssh.host;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Чтение конфигурации SSH-подключения из JSON-файла.
 * Чтение конфигурации SSH-подключения из JSON-файла.
 * <p>
 * Пример файла:
 * <pre>{@code
 *   {
 *     "host": "example.com",
 *     "port": 22,
 *     "username": "user",
 *     "password": "secret"
 *   }
 * }</pre>
 *
 * <p>
 * Использует {@link ObjectMapper} для десериализации в {@link HostConnectionConfigDto}.
 */
public final class HostConfigReader {

    /**
     * Считывает конфигурацию SSH-подключения из JSON-файла в ресурсах.
     * <p>
     * <b>Требования к JSON:</b>
     * <ul>
     *   <li>Должен содержать все обязательные поля ({@code host}, {@code username}, etc.)</li>
     *   <li>Поля должны соответствовать структуре {@link HostConnectionConfigDto}</li>
     * </ul>
     *
     * @param pathToJson Путь к файлу относительно classpath. Например: {@code "/ssh_config.json"}.
     * @return Валидный объект конфигурации.
     * @throws RuntimeException если файл не найден, JSON некорректен или данные невалидны.
     * @see HostConnectionConfigDto
     */
    public static HostConnectionConfigDto getHostConnectionConfig(String pathToJson){
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
