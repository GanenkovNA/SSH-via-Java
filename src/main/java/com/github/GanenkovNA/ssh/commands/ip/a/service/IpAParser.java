package com.github.GanenkovNA.ssh.commands.ip.a.service;

import com.github.GanenkovNA.ssh.commands.ip.a.dto.InterfaceDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.InterfaceIpv4ConfigDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.InterfaceIpv6ConfigDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.IpV4Scope;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.IpV6Scope;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.LifeTimeParamsDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.base.InterfaceBaseConfigDto;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.base.InterfaceFlag;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.base.InterfaceState;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.base.QdiscType;
import com.github.GanenkovNA.ssh.commands.ip.a.dto.physical.InterfacePhysicalConfigDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Парсер вывода команды `ip a` в структурированные DTO.
 *
 * <p>Поддерживает парсинг:
 * <ul>
 *   <li> базовых параметров интерфейса (индекс, имя, флаги);
 *   <li> физических параметров (MAC-адрес, тип канала);
 *   <li> IPv4 и IPv6 конфигураций;
 * </ul>
 */
public class IpAParser {
  // Регулярные выражения для парсинга
  // parseInterfaceDetails
  private static final Pattern INDEX_PATTERN = Pattern.compile("^(\\d+):");
  // Оставил просто как обычную строку, так как кастомные имена могут не вписаться в [a-z0-9]
  private static final Pattern NAME_PATTERN = Pattern.compile("^(\\S+):");
  private static final Pattern FLAGS_PATTERN = Pattern.compile("^<([^>]+)>");
  // parsePhysicalParams
  private static final Pattern LINK_TYPE_PATTERN = Pattern.compile("^link/(\\S+)");
  // parseIpV4Config
  private static final Pattern IP_ADDR_AND_PREFIX_PATTERN = Pattern.compile("^(\\S+)/(\\d+)");
  private static final Pattern IP_BROADCAST_ADDR_PATTERN = Pattern.compile("^(\\S+)");
  // parseIpV6Config
  private static final Pattern IPV6_ADDR_AND_PREFIX_PATTERN = Pattern.compile("^(\\S+)/(\\d+)");
  private static final Set<String> IPV6_SCOPE_VALUES =
      Arrays.stream(IpV6Scope.values())
          .map(Enum::name)
          .collect(Collectors.toSet());

  /**
   * Преобразует текстовый вывод команды `ip a` в список интерфейсов.
   *
   * @param ipAOutput сырой вывод команды, не должен быть пустым или `null`
   * @return Список распарсенных интерфейсов (возвращает пустой список при пустом вводе)
   * @throws IllegalArgumentException если вывод команды содержит некорректные данные
   * @see InterfaceDto
   */
  public static List<InterfaceDto> parse(String ipAOutput) {
    // Проверка вывода команды на пустую строку
    if (ipAOutput.isBlank()) {
      return java.util.Collections.emptyList();
    }

    List<InterfaceDto> interfaces = new ArrayList<>();
    // Создание объекта на этом этапе - костыль, чтобы не городить проверки в цикле
    InterfaceDto currentInterface = null;
    // Нужны для добавления параметров LifeTime
    InterfaceIpv4ConfigDto ipv4Config;
    InterfaceIpv6ConfigDto ipv6Config;

    String[] lines = trimOutputStrims(ipAOutput);

    for (int i = 0; i < lines.length; i++) {
      if (Character.isDigit(lines[i].charAt(0))) {
        currentInterface = new InterfaceDto();
        interfaces.add(currentInterface);

        currentInterface.setInterfaceParams(parseInterfaceDetails(lines[i]));
        //начинаем работу со строкой с физическими параметрами
        if (i + 1 < lines.length
            && lines[i + 1].toLowerCase().startsWith("link")) {
          currentInterface.setInterfacePhysicalParams(parsePhysicalParams(lines[++i]));
        }
      } else if (lines[i].toLowerCase().startsWith(("inet "))) {
        // Переменная нужна для добавления
        ipv4Config = parseIpV4Config(lines[i]);
        if (i + 1 < lines.length
            && isLifetimeLine(lines[i + 1])) {
          ipv4Config.setLifeTimeParams(parseLifeTimeParams(lines[++i]));
        }
        currentInterface.addIpv4(ipv4Config);
      } else if (lines[i].toLowerCase().startsWith("inet6")) {
        ipv6Config = parseIpV6Config(lines[i]);
        if (i + 1 < lines.length
            && isLifetimeLine(lines[i + 1])) {
          ipv6Config.setLifeTimeParams(parseLifeTimeParams(lines[++i]));
        }
        currentInterface.addIpv6(ipv6Config);
      } else {
        currentInterface.addUnknownLine("Неизвестная строка " + i + ": " + lines[i] + "\n");
      }
    }
    return interfaces;
  }

  /**
   * Удаляет пробелы в начале и конце строк.
   *
   * <p>Если строка пустая - не добавляет её в массив вывода. </p>
   *
   * @param ipAOutput - сырой вывод `ip a`
   * @return Массив строк вывода
   */
  private static String[] trimOutputStrims(String ipAOutput) {
    String[] lines = ipAOutput.split("\n");
    List<String> listOfLines = new ArrayList<>();

    for (int i = 0; i < lines.length; i++) {
      lines[i] = lines[i].trim();
      if (!lines[i].isBlank()) {
        listOfLines.add(lines[i]);
      }
    }

    return listOfLines.toArray(new String[0]);
  }

  private static boolean isLifetimeLine(String line) {
    return line.toLowerCase().startsWith("valid_lft")
        || line.toLowerCase().startsWith("preferred_lft");
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
    String[] parts = line.split("\\s+");

    for (int i = 0; i < parts.length; i++) {
      switch (parts[i].toLowerCase()) {
        case "mtu":
          interfaceDetails.setMtu(Integer.parseInt(parts[++i]));
          break;
        case "qdisc":
          try {
            interfaceDetails.setQdiscType(QdiscType.valueOf(parts[++i].toUpperCase()));
          } catch (IllegalArgumentException e) {
            interfaceDetails.addUnknownParam("Неизвестный тип qdisc: " + parts[i]);
          }
          break;
        case "state":
          try {
            interfaceDetails.setState(InterfaceState.valueOf(parts[++i].toUpperCase()));
          } catch (IllegalArgumentException e) {
            interfaceDetails.addUnknownParam("Неизвестное состояние(state): " + parts[i]);
          }
          break;
        case "group":
          interfaceDetails.setGroup(parts[++i].toLowerCase());
          break;
        case "qlen":
          interfaceDetails.setQlen(Integer.parseInt(parts[++i]));
          break;
        default:
          if (tryParseIndex(parts[i], interfaceDetails)
              || tryParseInterfaceName(parts[i], interfaceDetails)
              || tryParseFlags(parts[i], interfaceDetails)) {
          } else if (!parts[i].isBlank()) {
            interfaceDetails.addUnknownParam(parts[i]);
          }
      }
    }

    return interfaceDetails;
  }

  private static boolean tryParseIndex(String line, InterfaceBaseConfigDto dto) {
    Matcher matcher = INDEX_PATTERN.matcher(line);

    if (Objects.isNull(dto.getIndex()) && matcher.find()) {
      dto.setIndex(Integer.parseInt(matcher.group(1)));
      return true;
    }
    return false;
  }

  private static boolean tryParseInterfaceName(String line, InterfaceBaseConfigDto dto) {
    Matcher matcher = NAME_PATTERN.matcher(line);

    if (Objects.isNull(dto.getName()) && matcher.find()) {
      dto.setName(matcher.group(1));
      return true;
    }
    return false;
  }

  private static boolean tryParseFlags(String line, InterfaceBaseConfigDto dto) {
    Matcher matcher = FLAGS_PATTERN.matcher(line);

    if (matcher.find()) {
      String[] flags = matcher.group(1).split(",");
      for (String flag : flags) {
        flag = flag.trim(); // на случай наличия пробелов
        try {
          dto.addFlag(InterfaceFlag.valueOf(flag));
        } catch (IllegalArgumentException e) {
          dto.addUnknownParam("Неизвестный флаг: " + flag);
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
    String[] parts = line.split("\\s+");

    for (int i = 0; i < parts.length; i++) {
      if (parts[i].toLowerCase().startsWith("link")) {
        matcher = LINK_TYPE_PATTERN.matcher(parts[i]);

        if (matcher.find()) {
          interfacePhysicalParams.setLinkType(matcher.group(1).toLowerCase());
          try {
            interfacePhysicalParams.setMac(parts[++i].toLowerCase());
          } catch (IllegalArgumentException e) {
            interfacePhysicalParams.addUnknownParam(e.getMessage());
          }
        }
      } else if (parts[i].equalsIgnoreCase("brd")) {
        try {
          interfacePhysicalParams.setBroadcastMac(parts[++i].toLowerCase());
        } catch (IllegalArgumentException e) {
          interfacePhysicalParams.addUnknownParam(e.getMessage() + " (broadcast)");
        }
      } else if (!parts[i].isBlank()) {
        interfacePhysicalParams.addUnknownParam("Неизвестный параметр: " + parts[i]);
      }
    }

    return interfacePhysicalParams;
  }

  /** Обрабатывает IPv4-конфигурацию. */
  private static InterfaceIpv4ConfigDto parseIpV4Config(String line) {
    Matcher matcher;
    InterfaceIpv4ConfigDto interfaceIpv4Config = new InterfaceIpv4ConfigDto();
    String[] parts = line.split("\\s+");

    for (int i = 0; i < parts.length; i++) {
      if (parts[i].equalsIgnoreCase("inet")) {
        matcher = IP_ADDR_AND_PREFIX_PATTERN.matcher(parts[++i]);

        if (matcher.find()) {
          try {
            interfaceIpv4Config.setAddress(matcher.group(1));
          } catch (IllegalArgumentException e) {
            interfaceIpv4Config.addUnknownParam(e.getMessage());
          }
          interfaceIpv4Config.setPrefix(Integer.parseInt(matcher.group(2)));
        }
      } else if (parts[i].equalsIgnoreCase("brd")) {
        matcher = IP_BROADCAST_ADDR_PATTERN.matcher(parts[++i]);

        if (matcher.find()) {
          try {
            interfaceIpv4Config.setBroadcast(matcher.group(1));
          } catch (IllegalArgumentException e) {
            interfaceIpv4Config.addUnknownParam(e.getMessage() + " (broadcast)");
          }
        }
      } else if (parts[i].equalsIgnoreCase("scope")) {
        try {
          interfaceIpv4Config.setScope(IpV4Scope.valueOf(parts[++i].toUpperCase()));
        } catch (IllegalArgumentException e) {
          interfaceIpv4Config.addUnknownParam("Неизвестная область видимости (scope): " + parts[i]);
        }
        if (i + 1 < parts.length) {
          interfaceIpv4Config.setNetDevice(parts[++i]);
        }
      } else if (parts[i].isBlank()) {
        interfaceIpv4Config.addUnknownParam("Неизвестный параметр: " + parts[i]);
      }
    }
    return interfaceIpv4Config;
  }

  /** Обрабатывает IPv6-конфигурацию. */
  private static InterfaceIpv6ConfigDto parseIpV6Config(String line) {
    Matcher matcher;
    InterfaceIpv6ConfigDto interfaceIpv6Config = new InterfaceIpv6ConfigDto();
    String[] parts = line.split("\\s+");

    for (int i = 0; i < parts.length; i++) {
      if (parts[i].equalsIgnoreCase("inet6")) {
        matcher = IPV6_ADDR_AND_PREFIX_PATTERN.matcher(parts[++i]);
        if (matcher.find()) {
          try {
            interfaceIpv6Config.setAddress(matcher.group(1));
          } catch (IllegalArgumentException e) {
            interfaceIpv6Config.addUnknownParam(e.getMessage());
          }
          interfaceIpv6Config.setPrefix(Integer.parseInt(matcher.group(2)));
        }
      } else if (parts[i].equalsIgnoreCase("scope")) {
        while (i + 1 < parts.length) {
          i++;
          if (IPV6_SCOPE_VALUES.contains(parts[i].toUpperCase())) {
            interfaceIpv6Config.addScope(IpV6Scope.valueOf(parts[i].toUpperCase()));
          } else if (parts[i].equalsIgnoreCase("noprefixroute")) {
            interfaceIpv6Config.setIsNoPrefixRoute(true);
          } else {
            interfaceIpv6Config.addUnknownParam("Неизвестный параметр: " + parts[i]);
          }
        }
      } else if (!parts[i].isBlank()) {
        interfaceIpv6Config.addUnknownParam("Неизвестный параметр: " + parts[i]);
      }
    }
    return interfaceIpv6Config;
  }

  /** Обрабатывает параметры времени жизни. */
  private static LifeTimeParamsDto parseLifeTimeParams(String line) {
    LifeTimeParamsDto lifeTimeParams = new LifeTimeParamsDto();
    String[] parts = line.split("\\s+");

    for (int i = 0; i < parts.length; i++) {
      if (parts[i].equalsIgnoreCase("valid_lft")) {
        lifeTimeParams.setValidLft(parts[++i].toLowerCase());
      } else if (parts[i].equalsIgnoreCase("preferred_lft")) {
        lifeTimeParams.setPreferredLft(parts[++i].toLowerCase());
      } else if (!parts[i].isBlank()) {
        lifeTimeParams.addUnknownParam("Неизвестный параметр: " + parts[i]);
      }
    }
    return lifeTimeParams;
  }
}

