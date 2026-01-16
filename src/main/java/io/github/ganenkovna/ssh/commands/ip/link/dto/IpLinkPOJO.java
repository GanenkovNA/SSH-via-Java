package io.github.ganenkovna.ssh.commands.ip.link.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.ganenkovna.ssh.commands.ip.link.dto.linkinfo.pojo.LinkInfoPojo;
import io.github.ganenkovna.ssh.commands.ip.link.dto.linkinfo.pojo.LinkInfoPojoDeserializer;
import io.github.ganenkovna.util.ip.dto.Inet6AddrGenMode;
import io.github.ganenkovna.util.ip.dto.InterfaceFlag;
import io.github.ganenkovna.util.ip.dto.InterfaceState;
import io.github.ganenkovna.util.ip.dto.LinkMode;
import io.github.ganenkovna.util.ip.dto.LinkType;
import io.github.ganenkovna.util.ip.dto.QdiscType;
import io.github.ganenkovna.util.ip.dto.service.CaptureUnknown;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public final class IpLinkPOJO implements CaptureUnknown {
  private Integer ifindex;

  private String link;

  private String ifname;

  private EnumSet<InterfaceFlag> flags;

  private Integer mtu;

  private QdiscType qdisc;

  private String master;

  private InterfaceState operstate;

  private LinkMode linkmode;

  private String group;

  private Integer txqlen;

  @JsonProperty("link_type")
  private LinkType linkType;

  private String address;

  private String broadcast;

  private Integer promiscuity;

  @JsonProperty("min_mtu")
  private Integer minMtu;

  @JsonProperty("max_mtu")
  private Integer maxMtu;

  @JsonDeserialize(using = LinkInfoPojoDeserializer.class)
  private LinkInfoPojo linkinfo;

  @JsonProperty("inet6_addr_gen_mode")
  private Inet6AddrGenMode inet6AddrGenMode;

  @JsonProperty("num_tx_queues")
  private Integer numTxQueues;

  @JsonProperty("num_rx_queues")
  private Integer numRxQueues;

  @JsonProperty("gso_max_size")
  private Integer gsoMaxSize;

  @JsonProperty("gso_max_segs")
  private Integer gsoMaxSegs;

  private Map<String, Object> unparsedParams = new HashMap<>();

  @JsonAnySetter
  @Override
  public void captureUnknown(String key, Object value) {
    unparsedParams.put(key, value);
  }
}
