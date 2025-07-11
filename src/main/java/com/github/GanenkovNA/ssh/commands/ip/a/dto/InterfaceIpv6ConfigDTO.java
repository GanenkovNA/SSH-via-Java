package com.github.GanenkovNA.ssh.commands.ip.a.dto;

import lombok.Data;

/**
 * DTO для хранения IPv6-конфигурации интерфейса.
 */
@Data
public class InterfaceIpv6ConfigDTO {
    /**
     * IPv6-адрес интерфейса в формате RFC 5952.
     *
     * @example "2001:db8::1"
     */
    private String address;

    /**
     * Широковещательный IPv6-адрес (если применимо).
     *
     * @example "ff02::1"
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