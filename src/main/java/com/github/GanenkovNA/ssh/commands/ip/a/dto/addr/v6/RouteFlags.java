package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v6;

import java.util.Arrays;

public enum RouteFlags {
  NONE,

  // Не создавать маршрут для префикса
  NOPREFIXROUTE,

  // Отключена проверка дубликатов (DAD)
  NODAD,

  // Оптимистичный DAD (RFC 4429)
  OPTIMISTIC,

  // Автоматически настроен (SLAAC)
  AUTOCONF;

  public static boolean contains(String input) {
    if (input == null) {
      return false;
    }
    return Arrays.stream(values())
        .anyMatch(e -> e.name().equalsIgnoreCase(input));
  }

  public static RouteFlags getIgnoreCase(String input) {
    return RouteFlags.valueOf(input.toUpperCase());
  }
}
