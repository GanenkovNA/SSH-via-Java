package io.github.ganenkovna.ssh.commands.ip.link.dto.linkinfo.pojo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.ganenkovna.ssh.commands.ip.link.dto.linkinfo.LinkInfoKind;
import java.io.IOException;

public final class LinkInfoPojoDeserializer extends JsonDeserializer<LinkInfoPojo> {

  @Override
  public LinkInfoPojo deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    ObjectCodec codec = p.getCodec();
    JsonNode root = codec.readTree(p);

    if (root == null || root.isNull() || !root.isObject()) {
      return null;
    }

    ObjectNode obj = (ObjectNode) root;

    // 1) kind + slave
    String kindRaw = textOrNull(obj, "info_kind");
    boolean slave = false;

    if (kindRaw == null) {
      kindRaw = textOrNull(obj, "info_slave_kind");
      slave = (kindRaw != null);
    }

    LinkInfoKind kind = LinkInfoKind.fromIpString(kindRaw);
    if (kind == null) {
      // Вариант 1: вернуть null (linkinfo будет null)
      return null;

      // Вариант 2 (лучше для диагностики): сделать UnknownLinkInfoPojo (ниже покажу)
      // return new UnknownLinkInfoPojo(kindRaw, slave, obj);
    }

    // 2) payload
    JsonNode dataNode = obj.get(slave ? "info_slave_data" : "info_data");
    ObjectNode data = (dataNode != null && dataNode.isObject())
        ? (ObjectNode) dataNode
        : (ObjectNode) codec.createObjectNode();

    return new KindedLinkInfoPojo(kind, slave, data);
  }

  private static String textOrNull(ObjectNode n, String field) {
    JsonNode v = n.get(field);
    return (v != null && v.isTextual()) ? v.asText() : null;
  }
}
