package io.github.ganenkovna.util.ip.dto.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.ganenkovna.util.ip.dto.RouteType;
import java.io.IOException;

/**
 * Jackson-десериализатор типа маршрута {@link RouteType}.
 *
 * <p>Преобразует строковое значение поля {@code type} (например,
 * {@code local}, {@code unicast}) в соответствующее значение enum
 * {@link RouteType} без учёта регистра.</p>
 *
 * <p>Если значение не распознано, оно не приводит к ошибке десериализации:
 * неизвестное значение сохраняется через {@link CaptureUnknown}, а результатом
 * десериализации становится {@code null}.</p>
 *
 * <p>Такое поведение позволяет:</p>
 * <ul>
 *   <li>сохранять обратную совместимость при появлении новых типов маршрута в выводе;</li>
 *   <li>отлавливать неизвестные значения для диагностики;</li>
 *   <li>не прерывать разбор всего объекта из-за одного поля.</li>
 * </ul>
 *
 * @see RouteType#getIgnoreCase(String)
 * @see CaptureUnknown
 */
public class RouteTypeDeserializer extends StdDeserializer<RouteType>  {

  /** Создаёт десериализатор для типа {@link RouteType}. */
  public RouteTypeDeserializer() {
    super(RouteType.class);
  }

  /**
   * Десериализует строковое представление типа маршрута.
   *
   * <p>Сопоставление выполняется через {@link RouteType#getIgnoreCase(String)},
   * который отвечает за нормализацию и поиск подходящего значения enum.</p>
   *
   * <p>Если значение неизвестно, оно сохраняется у текущего объекта,
   * если тот реализует {@link CaptureUnknown}, после чего возвращается {@code null}.</p>
   *
   * @param p JSON-парсер, указывающий на строковое значение типа маршрута
   * @param ctxt контекст десериализации Jackson
   * @return соответствующее значение {@link RouteType} или {@code null},
   *         если тип маршрута неизвестен
   * @throws IOException если произошла ошибка чтения JSON
   * @see RouteType#getIgnoreCase(String)
   * @see CaptureUnknown#captureUnknown(String, Object)
   */
  @Override
  public RouteType deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    String value = p.getText();

    try {
      return RouteType.getIgnoreCase(value);
    } catch (IllegalArgumentException e) {
      Object current = ctxt.getParser().getCurrentValue();

      if(current instanceof CaptureUnknown cu) {
        cu.captureUnknown("type", value);
      }

      return null;
    }
  }
}
