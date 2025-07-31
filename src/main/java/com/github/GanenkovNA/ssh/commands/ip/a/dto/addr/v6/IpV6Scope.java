package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v6;

import com.github.GanenkovNA.ssh.commands.ip.a.dto.addr.v4.IpV4Scope;
import java.util.Arrays;

/**
 * Область видимости IPv6-адреса.
 *
 * <p>Аналогичен {@link IpV4Scope}, но с дополнительными значениями для IPv6.
 */
public enum IpV6Scope {
  /** Глобальная маршрутизация (интернет). */
  GLOBAL,

  /** Локальная сеть (L2). */
  LINK,

  /** Только на хосте (loopback). */
  HOST,

  /** Локальная организация (аналог IPv4 SITE). */
  SITE,

  /**
   * Устаревшее значение (полная маршрутизация).
   *
   * @deprecated заменен на GLOBAL в современных реализациях
   */
  UNIVERSE,

  /** Временный управляющий адрес (используется для автоматической конфигурации). */
  MNGTMPADDR,

  /** Устаревшее значение (режим совместимости с IPv4). */
  COMPAT,

  /** Динамический адрес (например, SLAAC). */
  DYNAMIC;

  public static boolean contains(String input) {
    if (input == null) {
      return false;
    }
    return Arrays.stream(values())
        .anyMatch(e -> e.name().equalsIgnoreCase(input));
  }

  public static IpV6Scope getIgnoreCase(String input){
    return IpV6Scope.valueOf(input.toUpperCase());
  }
}
