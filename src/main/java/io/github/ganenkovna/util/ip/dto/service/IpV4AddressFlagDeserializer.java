package io.github.ganenkovna.util.ip.dto.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.ganenkovna.util.ip.dto.IpV4AddressFlag;
import io.github.ganenkovna.util.ip.dto.RouteFlag;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public final class IpV4AddressFlagDeserializer extends StdDeserializer<EnumSet<IpV4AddressFlag>> {

  /**
   * Создаёт десериализатор для набора флагов {@link IpV4AddressFlag}.
   *
   * <p>Передаёт тип {@link IpV4AddressFlag} в базовый конструктор
   * {@link StdDeserializer} для корректной инициализации
   * механизма десериализации Jackson.</p>
   *
   * @see RouteFlag
   */
  public IpV4AddressFlagDeserializer() {
    super(IpV4AddressFlag.class);
  }

  /**
   * Десериализует JSON-массив строк в набор {@link IpV4AddressFlag}.
   *
   * @param p JSON-парсер, указывающий на начало массива
   * @param ctxt контекст десериализации Jackson
   * @return набор флагов маршрута; {@code null}, если входной JSON
   *         не является массивом или не содержит известных значений
   * @throws IOException при ошибке чтения JSON
   */
  @Override
  public EnumSet<IpV4AddressFlag> deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    if (!p.isExpectedStartArrayToken()){
      return null;
    }

    EnumSet<IpV4AddressFlag> result = EnumSet.noneOf(IpV4AddressFlag.class);
    List<String> unknownFlags = new ArrayList<>();

    while (p.nextToken() != JsonToken.END_ARRAY){
      String value = p.getValueAsString();
      try {
        result.add(IpV4AddressFlag.getIgnoreCase(value));
      } catch (IllegalArgumentException e) {
        unknownFlags.add(value);
      }
    }

    if (!unknownFlags.isEmpty()) {
      Object current = ctxt.getParser().getCurrentValue();
      if(current instanceof CaptureUnknown cu) {
        cu.captureUnknown("flags", unknownFlags);
      }
    }
    return result.isEmpty() ? null : result;
  }

}
