package com.github.GanenkovNA.ssh.client;

import com.github.GanenkovNA.ssh.host.HostConnectionConfigDto;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.Getter;

public class SshSession {
    private final HostConnectionConfigDto config;
    @Getter
    private Session session;

    public SshSession(HostConnectionConfigDto config) {
        this.config = config;
    }

    // Настройка параметров сессии
    public void createSession() {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(
                    config.getUsername(),
                    config.getHost(),
                    config.getPort());
            session.setPassword(config.getPassword());

            // Отключаем проверку ключа хоста (небезопасно, но для теста подойдет)
            session.setConfig("StrictHostKeyChecking", "no");

            // Начинаем сессию
            System.out.printf("Подключение к %s:%d как %s...%n",
                    config.getHost(), config.getPort(), config.getUsername());
            session.connect();
        } catch (JSchException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void closeSession(){
        session.disconnect();
    }
}
