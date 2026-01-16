package io.github.ganenkovna.ssh.commands.ip.link.dto.linkinfo.pojo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ganenkovna.ssh.commands.ip.link.dto.linkinfo.LinkInfoKind;
import lombok.Data;

@Data
public final class UnknownLinkInfoPojo implements LinkInfoPojo {
  private final String kindRaw;
  private final boolean slave;
  private final ObjectNode raw;

  public UnknownLinkInfoPojo(String kindRaw, boolean slave, ObjectNode raw) {
    this.kindRaw = kindRaw;
    this.slave = slave;
    this.raw = raw;
  }

  @Override
  public LinkInfoKind getKind() {
    return null;
  }
}
