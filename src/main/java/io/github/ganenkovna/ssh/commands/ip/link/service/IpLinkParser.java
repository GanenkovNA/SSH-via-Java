package io.github.ganenkovna.ssh.commands.ip.link.service;

public final class IpLinkParser {
  private static final String COMMAND = "ip -j link";

  /** Запрет инстанцирования. */
  private IpLinkParser() {
    throw new AssertionError("No instances");
  }
}
