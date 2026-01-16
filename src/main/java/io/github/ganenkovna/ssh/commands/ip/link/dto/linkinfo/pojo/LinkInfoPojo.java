package io.github.ganenkovna.ssh.commands.ip.link.dto.linkinfo.pojo;

import io.github.ganenkovna.ssh.commands.ip.link.dto.linkinfo.LinkInfoKind;

public sealed interface LinkInfoPojo permits KindedLinkInfoPojo, UnknownLinkInfoPojo {
  LinkInfoKind getKind();
  boolean isSlave();
}
