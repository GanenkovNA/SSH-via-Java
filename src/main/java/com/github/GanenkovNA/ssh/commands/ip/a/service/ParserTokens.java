package com.github.GanenkovNA.ssh.commands.ip.a.service;

import java.util.regex.Pattern;

/**
 * Константы и регулярные выражения для парсинга вывода команды {@code ip a}.
 *
 * <p>Содержит:
 *   <ul>
 *     <li>Ключевые слова для идентификации секций вывода
 *     <li>Регулярные выражения для извлечения параметров интерфейсов
 *     <li>Шаблоны для разбора IP-адресов и MAC-адресов
 *   </ul>
 *
 * @see IpAParser Основной класс парсера, использующий эти токены
 */
public final class ParserTokens {
  /**
   * Шаблон для извлечения индекса интерфейса.
   *
   * <p>Соответствует формату: {@code "1:"}, {@code "2:"}, и т.д.
   */
  public static final Pattern INDEX_PATTERN = Pattern.compile("^(\\d+):");

  /**
   * Шаблон для извлечения имени интерфейса.
   *
   * <p>Соответствует формату: {@code "eth0:"}, {@code "wlan1:"}, и т.д.
   *
   * <p>Поддерживает кастомные имена интерфейсов.
   */
  public static final Pattern NAME_PATTERN = Pattern.compile("^(\\S+):");

  /**
   * Шаблон для извлечения флагов интерфейса.
   *
   * <p>Соответствует формату: {@code "<UP,BROADCAST,RUNNING>"}
   */
  public static final Pattern FLAGS_PATTERN = Pattern.compile("^<([^>]+)>");

  /** Ключевое слово для поля MTU. */
  public static final String MTU = "mtu";

  /** Ключевое слово для дисциплины очереди. */
  public static final String QDISC = "qdisc";

  /** Ключевое слово для родительского интерфейса. */
  public static final String MASTER = "master";

  /** Ключевое слово для состояния интерфейса. */
  public static final String STATE = "state";

  /** Ключевое слово для группы интерфейса. */
  public static final String GROUP = "group";

  /** Ключевое слово для длины очереди передачи. */
  public static final String QLEN = "qlen";

  /** Идентификатор начала строки с физическими параметрами интерфейса. */
  public static final String LINK_LINE = "link";

  /**
   * Шаблон для извлечения типа канального уровня.
   *
   * <p>Соответствует форматам: {@code "link/ether"}, {@code "link/loopback"}
   */
  public static final Pattern LINK_TYPE_PATTERN = Pattern.compile("^link/(\\S+)");

  /** Ключевое слово для широковещательного адреса. */
  public static final String BROADCAST = "brd";

  /** Идентификатор начала блока IPv4-конфигурации. */
  public static final String INET_LINE = "inet ";

  /** Ключевое слово для IPv4-адреса. */
  public static final String INET = "inet";

  /** Ключевое слово для области видимости. */
  public static final String SCOPE = "scope";

  /**
   * Шаблон для извлечения IPv4-адреса с префиксом.
   *
   * <p>Соответствует форматам: {@code "192.168.1.1/24"}, {@code "10.0.0.1/8"}
   */
  public static final Pattern IP_ADDR_AND_PREFIX_PATTERN = Pattern.compile("^(\\S+)/(\\d+)");

  /** Шаблон для извлечения broadcast IPv4-адреса. */
  public static final Pattern IP_BROADCAST_ADDR_PATTERN = Pattern.compile("^(\\S+)");

  /** Идентификатор начала блока IPv6-конфигурации. */
  public static final String INET6_LINE = "inet6";

  /** Шаблон для извлечения IPv6-адреса с префиксом. */
  public static final Pattern IPV6_ADDR_AND_PREFIX_PATTERN = Pattern.compile("^(\\S+)/(\\d+)");

  /** Ключевое слово для времени жизни адреса (valid lifetime). */
  public static final String VALID_LFT = "valid_lft";

  /** Ключевое слово для времени жизни адреса (preferred lifetime). */
  public static final String PREFERRED_LFT = "preferred_lft";

  /** Запрет инстанцирования. */
  private ParserTokens() {
    throw new AssertionError("ParserTokens is a utility class and cannot be instantiated");
  }
}
