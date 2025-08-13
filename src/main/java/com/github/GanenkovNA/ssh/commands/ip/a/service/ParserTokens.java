package com.github.GanenkovNA.ssh.commands.ip.a.service;

import java.util.regex.Pattern;

public final class ParserTokens {
  // parseInterface
  public static final String LINK_LINE = "link";
  public static final String INET_LINE = "inet ";
  public static final String INET6_LINE = "inet6";

  // parseInterfaceDetails
  public static final String MTU = "mtu";
  public static final String QDISC = "qdisc";
  public static final String MASTER = "master";
  public static final String STATE = "state";
  public static final String GROUP = "group";
  public static final String QLEN = "qlen";

  // tryParseIndex
  public static final Pattern INDEX_PATTERN = Pattern.compile("^(\\d+):");

  // tryParseInterfaceName
  // Оставил просто как обычную строку, так как кастомные имена могут не вписаться в [a-z0-9]
  public static final Pattern NAME_PATTERN = Pattern.compile("^(\\S+):");

  // tryParseFlags
  public static final Pattern FLAGS_PATTERN = Pattern.compile("^<([^>]+)>");

  // parsePhysicalParams
  public static final String BROADCAST = "brd";
  public static final Pattern LINK_TYPE_PATTERN = Pattern.compile("^link/(\\S+)");

  // parseIpV4Config
  public static final String INET = "inet";
  public static final String SCOPE = "scope";
  public static final Pattern IP_ADDR_AND_PREFIX_PATTERN = Pattern.compile("^(\\S+)/(\\d+)");
  public static final Pattern IP_BROADCAST_ADDR_PATTERN = Pattern.compile("^(\\S+)");

  // isLifetimeLine
  public static final String VALID_LFT = "valid_lft";
  public static final String PREFERRED_LFT = "preferred_lft";

  // parseIpV6Config
  public static final Pattern IPV6_ADDR_AND_PREFIX_PATTERN = Pattern.compile("^(\\S+)/(\\d+)");

  // Блокирование конструктора
  private ParserTokens() {}
}
