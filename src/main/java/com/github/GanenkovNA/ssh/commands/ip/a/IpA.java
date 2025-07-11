package com.github.GanenkovNA.ssh.commands.ip.a;

import com.github.GanenkovNA.ssh.client.SshChannel;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.InterfaceDTO;
import com.github.GanenkovNA.ssh.commands.ip.a.service.IpAParser;
import com.jcraft.jsch.Session;

import java.util.List;

public class IpA {

    public static List<InterfaceDTO> showInterfaces(Session session){
        SshChannel channel = new SshChannel(session);

        String[] result = channel.execChannel("ip a");

        if (result[0].equals(Integer.toString(0))){
            return IpAParser.parse(result[1]);
        } else {
            throw new RuntimeException("Команда 'ip a' не была успешно выполнена" +
                    "\nКод завершения: " + result[0] +
                    "\nВывод stderr: " + result[2]);
        }
    }
}
