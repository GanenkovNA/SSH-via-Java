package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr;

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
  HOST
}
