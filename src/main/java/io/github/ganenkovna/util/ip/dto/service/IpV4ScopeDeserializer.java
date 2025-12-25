package io.github.ganenkovna.util.ip.dto.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.ganenkovna.util.ip.dto.IpV4Scope;
import java.io.IOException;

public final class IpV4ScopeDeserializer extends StdDeserializer<IpV4Scope> {

  /**
   * Создаёт десериализатор для типа {@link IpV4Scope}.
   */
  public IpV4ScopeDeserializer() {
    super(IpV4Scope.class);
  }

  /**
   * Десериализует строковое представление области видимости IPv4-адреса.
   *
   * <p>Значение нормализуется и сопоставляется с {@link IpV4Scope}
   * с помощью {@link IpV4Scope#getIgnoreCase(String)}.</p>
   *
   * <p>Если значение неизвестно, оно сохраняется у текущего объекта,
   * если тот реализует {@link CaptureUnknown}, после чего возвращается {@code null}.</p>
   *
   * @param p JSON-парсер, указывающий на строковое значение протокола
   * @param ctxt контекст десериализации Jackson
   * @return соответствующее значение {@link IpV4Scope} или {@code null},
   *         если область неизвестна
   * @throws IOException если произошла ошибка чтения JSON
   * @see IpV4Scope#getIgnoreCase(String)
   * @see CaptureUnknown#captureUnknown(String, Object)
   */
  @Override
  public IpV4Scope deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    String value = p.getText();

    try {
      return IpV4Scope.getIgnoreCase(value);
    } catch (IllegalArgumentException e) {
      Object current = ctxt.getParser().getCurrentValue();

      if(current instanceof CaptureUnknown cu) {
        cu.captureUnknown("scope", value);
      }

      return null;
    }
  }
}
