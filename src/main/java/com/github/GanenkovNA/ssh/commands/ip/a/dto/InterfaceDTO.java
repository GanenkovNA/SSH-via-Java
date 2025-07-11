package com.github.GanenkovNA.ssh.commands.ip.a.dto;

import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object (DTO) для представления сетевого интерфейса.
 * Содержит основную информацию о интерфейсе, его настройках IPv4 и IPv6.
 *
 * @see InterfaceIpv4ConfigDTO
 * @see InterfaceIpv6ConfigDTO
 */
@Data
public class InterfaceDTO {

    /**
     * Порядковый номер интерфейса в выводе
     *
     * @example 0
     */
    private Integer index;

    /**
     * Название интерфейса (например, "eth0", "wlan0").
     * Обязательное поле.
     *
     * @example "eth0"
     */
    private String name;

    /**
     * Список флагов интерфейса (из `<FLAG1,FLAG2>` в выводе `ip link`).
     *
     * @see InterfaceFlag
     * @example ["UP", "BROADCAST", "LOWER_UP"]
     */
    private List<InterfaceFlag> flags;

    /**
     * MTU (Maximum Transmission Unit) в байтах.
     * Допустимый диапазон: 64-9000.
     *
     * @example 1500
     */
    private Integer mtu;

    /**
     * Дисциплина очереди (qdisc), применяемая к интерфейсу.
     * Если null — используется qdisc по умолчанию (pfifo_fast).
     */
    private String qdiscType;

    /**
     * Текущий статус интерфейса.
     *
     * @see InterfaceState
     * @example "UP"
     */
    private InterfaceState state;

    /**
     * Группа интерфейса (если применимо).
     *
     * @example "lan"
     */
    private String group;

    /**
     * Длина очереди пакетов (qlen).
     * Если null — очередь не настроена.
     *
     * @example 1000
     */
    private Integer qlen;

    /**
     * Тип канала (link layer type).
     *
     * @example "ether"
     */
    private String linkType;

    /**
     * MAC-адрес интерфейса в формате XX:XX:XX:XX:XX:XX.
     *
     * @example "00:1a:2b:3c:4d:5e"
     */
    private String mac;

    /**
     * Широковещательный MAC-адрес (если отличается от стандартного).
     *
     * @example "ff:ff:ff:ff:ff:ff"
     */
    private String broadcastMac;

    /**
     * Конфигурация IPv4 интерфейса.
     */
    private InterfaceIpv4ConfigDTO ipv4;

    /**
     * Конфигурация IPv6 интерфейса.
     */
    private InterfaceIpv6ConfigDTO ipv6;
}