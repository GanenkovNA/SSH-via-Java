package com.github.GanenkovNA.ssh.commands.ip.a;

import com.github.GanenkovNA.ssh.client.SshChannel;
import com.jcraft.jsch.Session;

public class IpA {

    public static void showInterfaces(Session session){
        SshChannel channel = new SshChannel(session);

        String[] result = channel.execChannel("ip a");

        System.out.println("Код завершения: " + result[0]);
        System.out.println("Результат (stdout): \n" + result[1]);
        System.out.println("Ошибки (stderr): \n" + result[2]);
    }
}
