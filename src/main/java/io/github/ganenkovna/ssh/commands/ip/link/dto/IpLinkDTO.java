package io.github.ganenkovna.ssh.commands.ip.link.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.ganenkovna.util.ip.dto.Inet6AddrGenMode;
import io.github.ganenkovna.util.ip.dto.InterfaceFlag;
import io.github.ganenkovna.util.ip.dto.InterfaceState;
import io.github.ganenkovna.util.ip.dto.LinkMode;
import io.github.ganenkovna.util.ip.dto.LinkType;
import io.github.ganenkovna.util.ip.dto.QdiscType;
import java.util.EnumSet;
import java.util.Map;

public record IpLinkDTO(
    Integer ifindex,
    String link,
    String ifname,
    EnumSet<InterfaceFlag> flags,
    Integer mtu,
    QdiscType qdisc,
    String master,
    InterfaceState operstate,
    LinkMode linkmode,
    String group,
    Integer txqlen,
    @JsonProperty("link_type")
    LinkType linkType,
    String address,
    String broadcast,
    Integer promiscuity,
    @JsonProperty("min_mtu")
    Integer minMtu,
    @JsonProperty("max_mtu")
    Integer maxMtu,
    LinkInfoDto linkinfo,
    @JsonProperty("inet6_addr_gen_mode")
    Inet6AddrGenMode inet6AddrGenMode,
    @JsonProperty("num_tx_queues")
    Integer numTxQueues,
    @JsonProperty("num_rx_queues")
    Integer numRxQueues,
    @JsonProperty("gso_max_size")
    Integer gsoMaxSize,
    @JsonProperty("gso_max_segs")
    Integer gsoMaxSegs,
    Map<String, Object> unparsedParams
) {}
