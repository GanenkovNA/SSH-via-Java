package com.github.GanenkovNA.ssh.commands.ip.a.dto.base;

import java.util.Arrays;

/**
 * Состояние сетевого интерфейса из вывода `ip link`.
 *
 * <p>Соответствует полю `state` в выводе команды `ip a`.
 */
public enum InterfaceState {

  /** Интерфейс включён и активен. */
  UP,

  /** Интерфейс выключен административно. */
  DOWN,

  /** Состояние неизвестно (редкий случай). */
  UNKNOWN,

  /** Физический уровень активен (например, кабель подключён). */
  LOWER_UP,

  /** Нет соединения на физическом уровне (кабель отключён). */
  NO_CARRIER,

  /** Интерфейс в режиме ожидания (например, PPPoE перед подключением). */
  DORMANT;

  public static boolean contains(String input) {
    if (input == null) {
      return false;
    }
    return Arrays.stream(values())
        .anyMatch(e -> e.name().equalsIgnoreCase(input));
  }

  public static InterfaceState getIgnoreCase(String input){
    return InterfaceState.valueOf(input.toUpperCase());
  }
}
