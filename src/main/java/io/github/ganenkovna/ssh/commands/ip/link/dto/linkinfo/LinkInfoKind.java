package io.github.ganenkovna.ssh.commands.ip.link.dto.linkinfo;

public enum LinkInfoKind {
  VLAN,
  BRIDGE,
  BOND;

  public static LinkInfoKind fromIpString(String s) {
    if (s == null) return null;
    return switch (s.trim().toLowerCase()) {
      case "vlan" -> VLAN;
      case "bridge" -> BRIDGE;
      case "bond" -> BOND;
      default -> null;
    };
  }
}
