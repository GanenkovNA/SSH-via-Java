package com.github.GanenkovNA.ssh.host;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NonNull;

/**
 * DTO для параметров подключения к SSH-серверу.
 * <p>
 * Содержит все необходимые данные для установки соединения:
 * <ul>
 *   <li>Адрес сервера</li>
 *   <li>Порт (с значением по умолчанию)</li>
 *   <li>Учётные данные пользователя</li>
 * </ul>
 * <p>
 * <b>Валидация полей:</b>
 * <ul>
 *   <li>{@code host} — не может быть {@code null} или пустым</li>
 *   <li>{@code port} — должен быть в диапазоне 1-65535 (по умолчанию 22)</li>
 *   <li>{@code username} — не может быть {@code null} или пустым</li>
 *   <li>{@code password} — не может быть {@code null} (может быть пустым, если используется аутентификация по ключу)</li>
 * </ul>
 *
 * @see jakarta.validation.constraints.Min
 * @see jakarta.validation.constraints.Max
 * @see lombok.NonNull
 */
@Data
public class HostConnectionConfigDto {
    @NonNull
    private String host;
    @Min(1)
    @Max(65535)
    private int port = 22;

    // Данные пользователя
    @NonNull
    private String username;
    @NonNull
    private String password;
}
