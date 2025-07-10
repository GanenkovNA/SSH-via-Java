package com.github.GanenkovNA.ssh.commands.ip.a;

public enum InterfaceState {
    /**
     * Активен
     */
    UP,
    /**
     * Выключен
     */
    DOWN,
    /**
     * Неизвестен
     */
    UNKNOWN,
    /**
     * Физически активен (Linux)
     */
    LOWER_UP,
    /**
     * Нет соединения (Linux)
     */
    NO_CARRIER,
    /**
     * Ожидание (Linux)
     */
    DORMANT
}
