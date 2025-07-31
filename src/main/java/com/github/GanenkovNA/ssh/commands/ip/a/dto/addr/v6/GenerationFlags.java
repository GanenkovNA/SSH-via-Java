package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v6;

import java.util.Arrays;

public enum GenerationFlags {

  // Без специальных флагов
  NONE,

  // Сгенерирован из MAC (RFC 4291)
  EUI64,

  // Временный (RFC 4941)
  TEMPORARY,

  // Устойчивый приватный (RFC 7217)
  STABLE_PRIVACY,

  // Получен автоматически (SLAAC/DHCPv6)
  DYNAMIC,

  // Вручную добавлен
  MANUAL;

  private static final String STABLE_PRIVACY_CLI = "stable-privacy";

  public static boolean contains(String input) {
    if (input == null) {
      return false;
    }
    return Arrays.stream(values())
            .anyMatch(e -> e.name().equalsIgnoreCase(input))
        || STABLE_PRIVACY_CLI.equalsIgnoreCase(input);
  }

  public static GenerationFlags getIgnoreCase(String input){
    if (STABLE_PRIVACY_CLI.equalsIgnoreCase(input)){
      return STABLE_PRIVACY;
    }else {
      return GenerationFlags.valueOf(input.toUpperCase());
    }
  }
}
