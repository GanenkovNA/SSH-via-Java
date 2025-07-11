package com.github.GanenkovNA.ssh.commands.ip.a.service;

import com.github.GanenkovNA.ssh.commands.ip.a.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс-парсер для вывода команды `ip a`.
 * Отвечает только за преобразование текстового вывода в структурированные DTO.
 */
public class IpAParser {

    /**
     * Парсит вывод команды `ip a` в список DTO интерфейсов.
     *
     * @param ipOutput сырой вывод команды `ip a`
     * @return список распарсенных интерфейсов
     */
    public static List<InterfaceDTO> parse(String ipOutput) {
        List<InterfaceDTO> interfaces = new ArrayList<>();
        String[] lines = ipOutput.split("\n");

        InterfaceDTO currentInterface = null;
        InterfaceIpv4ConfigDTO currentIpv4 = null;
        InterfaceIpv6ConfigDTO currentIpv6 = null;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Обработка основной строки интерфейса
            if (line.matches("^\\d+:.*")) {
                currentInterface = parseInterfaceLine(line);
                interfaces.add(currentInterface);
                continue;
            }

            // Обработка строки link/...
            if (line.startsWith("link/") && currentInterface != null) {
                parseLinkLine(currentInterface, line);
                continue;
            }

            // Обработка IPv4
            if (line.startsWith("inet ") && currentInterface != null) {
                currentIpv4 = parseInetLine(line);
                currentInterface.setIpv4(currentIpv4);
                continue;
            }

            // Обработка IPv6
            if (line.startsWith("inet6 ") && currentInterface != null) {
                currentIpv6 = parseInet6Line(line);
                currentInterface.setIpv6(currentIpv6);
                continue;
            }

            // Обработка времени жизни адреса
            if (line.contains("valid_lft") && currentInterface != null) {
                parseLifetimeInfo(line, currentIpv4, currentIpv6);
            }
        }

        return interfaces;
    }

    private static InterfaceDTO parseInterfaceLine(String line) {
        InterfaceDTO iface = new InterfaceDTO();

        Pattern pattern = Pattern.compile(
                "(\\d+):\\s+(\\w+):\\s+<([^>]+)>\\s+mtu\\s+(\\d+)(?:\\s+qdisc\\s+(\\w+))?" +
                        "(?:\\s+state\\s+(\\w+))?(?:\\s+group\\s+(\\w+))?(?:\\s+qlen\\s+(\\d+))?"
        );

        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            iface.setIndex(Integer.parseInt(matcher.group(1)));
            iface.setName(matcher.group(2));
            iface.setFlags(parseFlags(matcher.group(3))); // Используем новый метод
            iface.setMtu(Integer.parseInt(matcher.group(4)));
            iface.setQdiscType(matcher.group(5));
            if (matcher.group(6) != null) {
                iface.setState(InterfaceState.valueOf(matcher.group(6)));
            }
            iface.setGroup(matcher.group(7));
            if (matcher.group(8) != null) {
                iface.setQlen(Integer.parseInt(matcher.group(8)));
            }
        }

        return iface;
    }

    private static List<InterfaceFlag> parseFlags(String flagsString) {
        List<InterfaceFlag> flags = new ArrayList<>();
        String[] flagNames = flagsString.split(",");

        for (String flagName : flagNames) {
            try {
                flags.add(InterfaceFlag.valueOf(flagName));
            } catch (IllegalArgumentException e) {
                // Логируем неизвестный флаг, но не добавляем его в список
                System.err.println("Unknown interface flag: " + flagName);
            }
        }

        return flags;
    }

    private static void parseLinkLine(InterfaceDTO iface, String line) {
        // Пример: "link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00"
        Pattern pattern = Pattern.compile(
                "link/(\\w+)\\s+([0-9a-fA-F:]+)(?:\\s+brd\\s+([0-9a-fA-F:]+))?"
        );

        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            iface.setLinkType(matcher.group(1));
            iface.setMac(matcher.group(2));
            iface.setBroadcastMac(matcher.group(3));
        }
    }

    private static InterfaceIpv4ConfigDTO parseInetLine(String line) {
        // Пример: "inet 127.0.0.1/8 scope host lo"
        InterfaceIpv4ConfigDTO ipv4 = new InterfaceIpv4ConfigDTO();
        Pattern pattern = Pattern.compile(
                "inet\\s+([^\\s/]+)(?:/(\\d+))?\\s+(?:brd\\s+([^\\s]+))?\\s+scope\\s+(\\w+)"
        );

        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            ipv4.setAddress(matcher.group(1));
            ipv4.setBroadcast(matcher.group(3));
            ipv4.setScope(matcher.group(4));
        }

        return ipv4;
    }

    private static InterfaceIpv6ConfigDTO parseInet6Line(String line) {
        // Пример: "inet6 ::1/128 scope host"
        InterfaceIpv6ConfigDTO ipv6 = new InterfaceIpv6ConfigDTO();
        Pattern pattern = Pattern.compile(
                "inet6\\s+([^\\s/]+)(?:/(\\d+))?\\s+scope\\s+(\\w+)"
        );

        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            ipv6.setAddress(matcher.group(1));
            ipv6.setScope(matcher.group(3));
        }

        return ipv6;
    }

    private static void parseLifetimeInfo(String line, InterfaceIpv4ConfigDTO ipv4, InterfaceIpv6ConfigDTO ipv6) {
        // Пример: "valid_lft forever preferred_lft forever"
        Pattern pattern = Pattern.compile(
                "valid_lft\\s+(\\w+).*preferred_lft\\s+(\\w+)"
        );

        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            if (ipv4 != null) {
                ipv4.setValidLft(matcher.group(1));
                ipv4.setPreferredLft(matcher.group(2));
            } else if (ipv6 != null) {
                ipv6.setValidLft(matcher.group(1));
                ipv6.setPreferredLft(matcher.group(2));
            }
        }
    }
}

