package com.github.GanenkovNA;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.GanenkovNA.ssh.client.SshSession;
import com.github.GanenkovNA.ssh.commands.ip.a.IpA;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.InterfaceDto;
import com.github.GanenkovNA.ssh.host.HostConfigReader;
import com.github.GanenkovNA.ssh.host.HostConnectionConfigDto;
import com.jcraft.jsch.Session;

import java.util.List;

public class Main {
  public static void main(String[] args) {
    String hostConnectionConfigPath = "/host_connection_config.json";
    String command = "ip ro";    // Команда для выполнения

    HostConnectionConfigDto config = HostConfigReader.getHostConnectionConfig(hostConnectionConfigPath);
    SshSession sessionManage = new SshSession(config);
    Session currentSession = sessionManage.createSession();
        /*
        SshChannel channel = new SshChannel(currentSession);
        String[] result = channel.execChannel(command);

        System.out.println("Код завершения" + result[0]);
        System.out.println("Вывод stdout: \n" + result[1]);
        System.out.println("Вывод stderr: \n" + result[2]);
         */

    List<InterfaceDto> interfaces = IpA.showInterfaces(currentSession);
    //System.out.println("=== " + interfaces.get(1).getInterfaceParams().getName() + " ===\n" + interfaces.get(1).getIpv6().toString());

    String json;
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    for (InterfaceDto interfaceDTO : interfaces) {
      try {
        json = mapper.writeValueAsString(interfaceDTO);  // Передаем объект, а не класс
        System.out.println("=== " + interfaceDTO.getInterfaceParams().getName() + " ===\n" + json);
      } catch (JsonProcessingException e) {
        System.err.println("Ошибка при преобразовании в JSON: " + e.getMessage());
      }
    }


    sessionManage.closeSession();
  }
}