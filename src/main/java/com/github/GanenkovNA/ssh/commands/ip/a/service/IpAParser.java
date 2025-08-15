package com.github.GanenkovNA.ssh.commands.ip.a.service;

import static com.github.GanenkovNA.ssh.commands.ip.a.service.ParserTokens.*;
import static com.github.GanenkovNA.ssh.commands.ip.a.service.ParserUtils.*;

import com.github.GanenkovNA.ssh.commands.ip.a.dto.InterfaceDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.LifeTimeParamsDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v4.InterfaceIpv4ConfigDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v4.IpV4AddressFlag;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v4.IpV4Scope;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v6.GenerationFlags;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v6.InterfaceIpv6ConfigDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v6.IpV6Scope;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v6.RouteFlags;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.base.InterfaceBaseConfigDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.base.InterfaceFlag;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.base.InterfaceState;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.base.QdiscType;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.physical.InterfacePhysicalConfigDto;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

public final class IpAParser {
  public static List<InterfaceDto> parseOutput(String ipAOutput) {
    // Проверка вывода команды на пустую строку
    if (ipAOutput.isBlank()) {
      return Collections.emptyList();
    }

    List<InterfaceDto> interfaces = new java.util.ArrayList<>();
    // Создание объекта на этом этапе - костыль, чтобы не городить проверки в цикле
    InterfaceDto currentInterface = null;

    String[] lines = trimOutputStrings(ipAOutput);
    for (int i = 0; i < lines.length; i++) {
      if (isInterfaceStart(lines[i])){
        currentInterface = new InterfaceDto();
        i = parseInterface(i, lines, currentInterface);
        interfaces.add(currentInterface);
      } else if (currentInterface != null) {
        currentInterface.addUnknownLine("Неизвестная строка " + i + ": " + lines[i] + "\n");
      }
    }
    return interfaces;
  }

  // Проверяет, является ли строка новым интерфейсом
  // Проверка, начинается ли строка с числа (индекс)
  private static boolean isInterfaceStart(String line) {
    return Character.isDigit(line.charAt(0));
  }

  // "Скелет" для вызова методов обработок строк
  private static int parseInterface(int i, String[] lines, InterfaceDto currentInterface) {
    // Базовые параметры интерфейса
    currentInterface.setInterfaceParams(
        parseInterfaceDetails(lines[i]));

    // Физические параметры
    if (i + 1 < lines.length && isLineStart(lines[i + 1], LINK_LINE)){
      currentInterface.setInterfacePhysicalParams(
          parsePhysicalParams(lines[++i]));
    }

    while (i + 1 < lines.length && !isInterfaceStart(lines[i + 1])){
      // IPv-4
      if (isLineStart(lines[i + 1], INET_LINE)) {
        i = parseInetBlock(++i, lines, currentInterface);
      }
      // IPv-6
      else if (isLineStart(lines[i + 1], INET6_LINE)) {
        i = parseInet6Block(++i, lines, currentInterface);
      }
    }
    return i;
  }

  /**
   * Обрабатывает базовые параметры интерфейса.
   *
   * @param line Первая строка с параметрами интерфейса
   * @return DTO
   * @see InterfaceBaseConfigDto
   */
  private static InterfaceBaseConfigDto parseInterfaceDetails(String line) {
    InterfaceBaseConfigDto interfaceDetails = new InterfaceBaseConfigDto();
    String[] parts = trimOutputLine(line);

    for (int i = 0; i < parts.length; i++) {
      // Парсинг индекса, имени и флага
      if (tryParseIndex(parts[i], interfaceDetails)
          || tryParseInterfaceName(parts[i], interfaceDetails)
          || tryParseFlags(parts[i], interfaceDetails)) {
        continue;
      }
      // Парсинг MTU
      else if (isEqualIgnoreCase(parts[i], MTU)) {
        try {
          interfaceDetails.setMtu(Integer.parseInt(parts[++i]));
        } catch (IllegalArgumentException e) {
          interfaceDetails.addUnknownParam(e.getMessage());
        }
      }
      // Парсинг дисциплины очереди
      else if (isEqualIgnoreCase(parts[i], QDISC)) {
        if (QdiscType.isValid(parts[i + 1])){
          interfaceDetails.setQdiscType(
              QdiscType.getIgnoreCase(parts[++i]));
        } else {
          interfaceDetails.addUnknownParam("Значение qdisc не найдено: " + parts[i + 1]);
        }
      }
      // Парсинг родительского интерфейса
      else if (isEqualIgnoreCase(parts[i], MASTER)) {
        interfaceDetails.setMaster(parts[++i]);
      }
      // Парсинг состояния интерфейса
      else if (isEqualIgnoreCase(parts[i], STATE)) {
        if(InterfaceState.isValid(parts[i + 1])){
          interfaceDetails.setState(
              InterfaceState.getIgnoreCase(parts[++i]));
        } else {
          interfaceDetails.addUnknownParam("Значение состояния интерфейса не найдено: " + parts[i + 1]);
        }
      }
      // Парсинг группы интерфейса
      else if (isEqualIgnoreCase(parts[i], GROUP)) {
        interfaceDetails.setGroup(parts[++i]);
      }
      // Парсинг длины очереди передачи
      else if (isEqualIgnoreCase(parts[i], QLEN)) {
        try {
          interfaceDetails.setQlen(Integer.parseInt(parts[++i]));
        } catch (IllegalArgumentException e) {
          interfaceDetails.addUnknownParam(e.getMessage());
        }
      }
      // Внесение нераспознанных параметров
      else {
        interfaceDetails.addUnknownParam("Неизвестный параметр: " + parts[i]);
      }
    }
    return interfaceDetails;
  }

  private static boolean tryParseIndex(String line, InterfaceBaseConfigDto dto) {
    Matcher matcher = INDEX_PATTERN.matcher(line);
    if (!matcher.find()) {
      return false;
    }
    int index = Integer.parseInt(matcher.group(1));

    if (Objects.isNull(dto.getIndex())) {
      try {
        dto.setIndex(index);
        return true;
      } catch (IllegalArgumentException e) {
        dto.addUnknownParam(e.getMessage());
      }
    } else {
      dto.addUnknownParam("Попытка установки нового значения индекса `"
          + index + "` вместо текущего `" + dto.getIndex() + "`");
    }
    return false;
  }

  private static boolean tryParseInterfaceName(String line, InterfaceBaseConfigDto dto) {
    Matcher matcher = NAME_PATTERN.matcher(line);
    if (!matcher.find()){
      return false;
    }
    String name = matcher.group(1);

    if(Objects.isNull(dto.getName())) {
      dto.setName(matcher.group(1));
      return true;
    } else {
      dto.addUnknownParam("Попытка установки нового значения имени интерфейса `"
          + name + "` вместо текущего `" + dto.getName() + "`");
    }
    return false;
  }

  private static boolean tryParseFlags(String line, InterfaceBaseConfigDto dto) {
    Matcher matcher = FLAGS_PATTERN.matcher(line);

    if (matcher.find()) {
      String[] flags = matcher.group(1).split(",");
      for (String flag : flags) {
        flag = flag.trim();
        if (!flag.isEmpty()) {
          if (InterfaceFlag.isValid(flag)){
            dto.addFlag(
                InterfaceFlag.getIgnoreCase(flag));
          } else {
            dto.addUnknownParam("Значение флага интерфейса не найдено: " + flag);
          }
        }
      }
      return true;
    }
    return false;
  }

  /** Обрабатывает физические параметры интерфейса (MAC, тип канала). */
  private static InterfacePhysicalConfigDto parsePhysicalParams(String line) {
    Matcher matcher;
    InterfacePhysicalConfigDto interfacePhysicalParams = new InterfacePhysicalConfigDto();
    String[] parts = trimOutputLine(line);

    for (int i = 0; i < parts.length; i++) {
      // Парсинг типа канального уровня и MAC-адреса
      if (isLineStart(parts[i], LINK_LINE)) {
        matcher = LINK_TYPE_PATTERN.matcher(parts[i]);
        // Тип канального уровня
        if (matcher.find()) {
          interfacePhysicalParams.setLinkType(matcher.group(1));
          // MAC-адрес
          if (i + 1 < parts.length){
            interfacePhysicalParams.setMac(parts[++i]);
          }
        }
      }
      // Парсинг MAC-broadcast
      else if (isEqualIgnoreCase(parts[i], BROADCAST)) {
        try {
          interfacePhysicalParams.setBroadcastMac(parts[++i]);
        } catch (IllegalArgumentException e) {
          interfacePhysicalParams.addUnknownParam(e.getMessage());
        }
      }
      // Внесение нераспознанных параметров
      else if (!parts[i].isBlank()) {
        interfacePhysicalParams.addUnknownParam("Неизвестный параметр: " + parts[i]);
      }
    }
    return interfacePhysicalParams;
  }

  /** Обрабатывает блок IPv4-конфигурации. */
  private static int parseInetBlock(int i, String[] lines, InterfaceDto currentInterface) {
    // Парсинг строки IPv4-адреса
    InterfaceIpv4ConfigDto config = parseIpV4Config(lines[i]);
    // Парсинг параметров времени жизни адреса
    if (i + 1 < lines.length && isLifetimeLine(lines[i + 1])) {
      config.setLifeTimeParams(parseLifeTimeParams(lines[++i]));
    }
    currentInterface.addIpv4(config);
    return i;
  }

  // Парсинг конфигурации IPv4-адреса
  private static InterfaceIpv4ConfigDto parseIpV4Config(String line) {
    Matcher matcher;
    InterfaceIpv4ConfigDto interfaceIpv4Config = new InterfaceIpv4ConfigDto();
    String[] parts = trimOutputLine(line);

    for (int i = 0; i < parts.length; i++) {
      // Парсинг IPv4-адреса и префикса
      if (isEqualIgnoreCase(parts[i], INET) && i + 1 < parts.length) {
        matcher = IP_ADDR_AND_PREFIX_PATTERN.matcher(parts[++i]);
        if (matcher.find()) {
          // IPv4-адрес
          try{
            interfaceIpv4Config.setAddress(matcher.group(1));
          } catch (IllegalArgumentException e) {
            interfaceIpv4Config.addUnknownParam(e.getMessage());
          }
          // Префикс
          try{
            interfaceIpv4Config.setPrefix(Integer.parseInt(matcher.group(2)));
          } catch (IllegalArgumentException e) {
            interfaceIpv4Config.addUnknownParam(e.getMessage());
          }
        }
      }
      // Парсинг IPv4-broadcast
      else if (isEqualIgnoreCase(parts[i], BROADCAST)) {
        matcher = IP_BROADCAST_ADDR_PATTERN.matcher(parts[++i]);
        if (matcher.find()) {
          try {
            interfaceIpv4Config.setBroadcast(matcher.group(1));
          } catch (IllegalArgumentException e) {
            interfaceIpv4Config.addUnknownParam(e.getMessage());
          }
        }
      }
      // Парсинг области видимости и NET_DEVICE
      else if (isEqualIgnoreCase(parts[i], SCOPE)) {
        // Область видимости
        if (IpV4Scope.isValid(parts[i + 1])){
          try {
            interfaceIpv4Config.setScope(
                IpV4Scope.getIgnoreCase(parts[++i]));
          } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
          }
        } else {
          interfaceIpv4Config.addUnknownParam("Неизвестная область видимости (scope): " + parts[i]);
        }
        // NET_DEVICE
        if (i + 1 < parts.length) {
          interfaceIpv4Config.setNetDevice(parts[++i]);
        }
      }

      else if (IpV4AddressFlag.isValid(parts[i])) {
        interfaceIpv4Config.addIpV4AddressFlag(
            IpV4AddressFlag.getIgnoreCase(parts[i]));
      }
      // Внесение нераспознанных параметров
      else if (parts[i].isBlank()) {
        interfaceIpv4Config.addUnknownParam("Неизвестный параметр: " + parts[i]);
      }
    }
    return interfaceIpv4Config;
  }

  /** Обрабатывает блок IPv6-конфигурации. */
  private static int parseInet6Block(int i, String[] lines, InterfaceDto currentInterface) {
    // Парсинг строки IPv6-адреса
    InterfaceIpv6ConfigDto config = parseIpV6Config(lines[i]);
    // Парсинг параметров времени жизни адреса
    if (i + 1 < lines.length && isLifetimeLine(lines[i + 1])) {
      config.setLifeTimeParams(parseLifeTimeParams(lines[++i]));
    }
    currentInterface.addIpv6(config);
    return i;
  }

  // Парсинг конфигурации IPv6-адреса
  private static InterfaceIpv6ConfigDto parseIpV6Config(String line) {
    Matcher matcher;
    InterfaceIpv6ConfigDto interfaceIpv6Config = new InterfaceIpv6ConfigDto();
    String[] parts = trimOutputLine(line);

    for (int i = 0; i < parts.length; i++) {
      // Парсинг IPv6-адреса и префикса
      if (isEqualIgnoreCase(parts[i], INET6_LINE)) {
        matcher = IPV6_ADDR_AND_PREFIX_PATTERN.matcher(parts[++i]);
        if (matcher.find()) {
          // IPv6-адрес
          try {
            interfaceIpv6Config.setAddress(matcher.group(1));
          } catch (IllegalArgumentException e) {
            interfaceIpv6Config.addUnknownParam(e.getMessage());
          }
          // Префикс
          try {
            interfaceIpv6Config.setPrefix(Integer.parseInt(matcher.group(2)));
          } catch (IllegalArgumentException e) {
            interfaceIpv6Config.addUnknownParam(e.getMessage());
          }

        }
      }
      // Парсинг области видимости
      else if (isEqualIgnoreCase(parts[i], SCOPE)) {
        while (i + 1 < parts.length && IpV6Scope.isValid(parts[i + 1])) {
          interfaceIpv6Config.addScope(
              IpV6Scope.getIgnoreCase(parts[++i]));
        }
      }
      // Парсинг флага генерации
      else if (GenerationFlags.isValid(parts[i])) {
        interfaceIpv6Config.addGenerationFlag(
            GenerationFlags.getIgnoreCase(parts[i]));
      }
      // Парсинг флага маршрутизации
      else if (RouteFlags.contains(parts[i])) {
        interfaceIpv6Config.addRouteFlag(
            RouteFlags.getIgnoreCase(parts[i]));
      }
      // Внесение нераспознанных параметров
      else {
        interfaceIpv6Config.addUnknownParam("Неизвестный параметр: " + parts[i]);
      }
    }
    return interfaceIpv6Config;
  }

  private static boolean isLifetimeLine(String line) {
    return isLineStart(line, VALID_LFT)
        || isLineStart(line, PREFERRED_LFT);
  }

  /** Обрабатывает параметры времени жизни. */
  private static LifeTimeParamsDto parseLifeTimeParams(String line) {
    LifeTimeParamsDto lifeTimeParams = new LifeTimeParamsDto();
    String[] parts = trimOutputLine(line);

    for (int i = 0; i < parts.length; i++) {
      // Парсинг времени жизни адреса до недействительности
      if (parts[i].equalsIgnoreCase("valid_lft")) {
        lifeTimeParams.setValidLft(parts[++i].toLowerCase());
      }
      // Парсинг времени жизни адреса до устаревания
      else if (parts[i].equalsIgnoreCase("preferred_lft")) {
        lifeTimeParams.setPreferredLft(parts[++i].toLowerCase());
      }
      // Внесение нераспознанных параметров
      else if (!parts[i].isBlank()) {
        lifeTimeParams.addUnknownParam("Неизвестный параметр: " + parts[i]);
      }
    }
    return lifeTimeParams;
  }

}
