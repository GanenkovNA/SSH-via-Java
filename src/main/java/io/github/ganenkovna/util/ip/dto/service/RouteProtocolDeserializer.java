package io.github.ganenkovna.util.ip.dto.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.ganenkovna.util.ip.dto.RouteProtocol;
import java.io.IOException;

/**
 * Jackson-десериализатор протокола источника маршрута {@link RouteProtocol}.
 *
 * <p>Преобразует строковое значение поля {@code protocol} (например,
 * {@code kernel}, {@code static}) в соответствующее значение перечисления
 * {@link RouteProtocol} без учёта регистра.</p>
 *
 * <p>Если значение не распознано, оно не приводит к ошибке десериализации:
 * неизвестное значение сохраняется через {@link CaptureUnknown}, а результатом
 * десериализации становится {@code null}.</p>
 *
 * <p>Такое поведение позволяет:</p>
 * <ul>
 *   <li>сохранять обратную совместимость при появлении новых протоколов в выводе;</li>
 *   <li>отлавливать неизвестные значения для диагностики;</li>
 *   <li>не прерывать разбор всего объекта из-за одного поля.</li>
 * </ul>
 *
 * @see RouteProtocol#getIgnoreCase(String)
 * @see CaptureUnknown
 */
public class RouteProtocolDeserializer extends StdDeserializer<RouteProtocol> {

  /**
   * Создаёт десериализатор для типа {@link RouteProtocol}.
   */
  public RouteProtocolDeserializer() {
    super(RouteProtocol.class);
  }

  /**
   * Десериализует строковое представление протокола источника маршрута.
   *
   * <p>Значение нормализуется и сопоставляется с {@link RouteProtocol}
   * с помощью {@link RouteProtocol#getIgnoreCase(String)}.</p>
   *
   * <p>Если значение неизвестно, оно сохраняется у текущего объекта,
   * если тот реализует {@link CaptureUnknown}, после чего возвращается {@code null}.</p>
   *
   * @param p JSON-парсер, указывающий на строковое значение протокола
   * @param ctxt контекст десериализации Jackson
   * @return соответствующее значение {@link RouteProtocol} или {@code null},
   *         если протокол неизвестен
   * @throws IOException если произошла ошибка чтения JSON
   * @see RouteProtocol#getIgnoreCase(String)
   * @see CaptureUnknown#captureUnknown(String, Object)
   */
  @Override
  public RouteProtocol deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    String value = p.getText();

    try {
      return RouteProtocol.getIgnoreCase(value);
    } catch (IllegalArgumentException e) {
      Object current = ctxt.getParser().getCurrentValue();

      if(current instanceof CaptureUnknown cu) {
        cu.captureUnknown("protocol", value);
      }

      return null;
    }
  }
}
