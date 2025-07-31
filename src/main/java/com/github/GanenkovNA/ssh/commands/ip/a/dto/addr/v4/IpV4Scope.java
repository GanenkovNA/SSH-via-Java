package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v4;

import java.util.Arrays;

/**
 * Область видимости IPv4-адреса.
 *
 * <p>Определяет, где маршрутизируется адрес. </p>
 */
public enum IpV4Scope {

  /** Глобальная маршрутизация (интернет). */
  GLOBAL,

  /** Локальная сеть (сайт, организация). */
  SITE,

  /** Только link-local (например, 169.254.0.0/16). */
  LINK,

  /** Только на хосте (loopback). */
  HOST;

  public static boolean contains(String input) {
    if (input == null) {
      return false;
    }
    return Arrays.stream(values())
        .anyMatch(e -> e.name().equalsIgnoreCase(input));
  }

  public static IpV4Scope getIgnoreCase(String input){
    return IpV4Scope.valueOf(input.toUpperCase());
  }
}
