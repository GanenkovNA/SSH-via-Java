package com.github.GanenkovNA.ssh.commands.ip.a.dto;

import lombok.Data;

/**
 * DTO для хранения IPv4-конфигурации интерфейса.
 */
@Data
public class InterfaceIpv4ConfigDTO {
    /**
     * IPv4-адрес интерфейса в формате "x.x.x.x".
     *
     * @example "192.168.1.10"
     */
    private String address;

    /**
     * Широковещательный IPv4-адрес (broadcast).
     *
     * @example "192.168.1.255"
     */
    private String broadcast;

    /**
     * Область действия адреса (scope).
     * Возможные значения: "global", "link", "host".
     *
     * @example "global"
     */
    private String scope;

    /**
     * Время жизни адреса (valid lifetime).
     * Формат зависит от системы (например, "forever" или "86sec").
     *
     * @example "forever"
     */
    private String validLft;

    /**
     * Предпочитаемое время жизни адреса (preferred lifetime).
     *
     * @example "86sec"
     */
    private String preferredLft;
}