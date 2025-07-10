package com.github.GanenkovNA;

import com.github.GanenkovNA.ssh.client.SshChannel;
import com.github.GanenkovNA.ssh.client.SshSession;
import com.github.GanenkovNA.ssh.host.HostConnectionConfigDto;
import com.github.GanenkovNA.ssh.host.HostConfigReader;

public class Main {
    public static void main(String[] args) {
        String hostConnectionConfigPath = "/host_connection_config.json";
        String command = "ip ro";    // Команда для выполнения

        HostConnectionConfigDto config = HostConfigReader.getHostConnectionConfig(hostConnectionConfigPath);
        SshSession session = new SshSession(config);
        session.createSession();

        SshChannel channel = new SshChannel(session.getSession());
        String[] result = channel.execChannel(command);

        System.out.println("Код завершения" + result[0]);
        System.out.println("Вывод stdout: \n" + result[1]);
        System.out.println("Вывод stderr: \n" + result[2]);

        session.closeSession();
    }
}