package com.github.GanenkovNA.ssh.host;

import lombok.Data;

@Data
public class HostConnectionConfigDto {
    // IP или имя устройства
    private String host;
    // Порт SSH (обычно 22)
    private int port = 22;

    // Данные пользователя
    private String username;
    private String password;
}
