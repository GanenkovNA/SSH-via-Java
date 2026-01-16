package io.github.ganenkovna.ssh.commands.ip.link.dto.linkinfo.pojo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ganenkovna.ssh.commands.ip.link.dto.linkinfo.LinkInfoKind;
import lombok.Getter;

public final class KindedLinkInfoPojo implements LinkInfoPojo {
  private final LinkInfoKind kind;
  private final boolean slave;
  @Getter
  private final ObjectNode data;

  public KindedLinkInfoPojo(LinkInfoKind kind, boolean slave,
                            com.fasterxml.jackson.databind.node.ObjectNode data) {
    this.kind = kind;
    this.slave = slave;
    this.data = data;
  }

  @Override
  public LinkInfoKind getKind() {
    return kind;
  }

  @Override
  public boolean isSlave() {
    return slave;
  }
}
