package io.github.ganenkovna.util.ip.dto.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.ganenkovna.util.ip.dto.RouteScope;
import java.io.IOException;

/**
 * Jackson-десериализатор области видимости маршрутизации {@link RouteScope}.
 *
 * <p>Преобразует строковое значение поля {@code scope} (например,
 * {@code host}, {@code link}, {@code global}) в соответствующее значение enum
 * {@link RouteScope} без учёта регистра.</p>
 *
 * <p>Если значение не распознано, оно не приводит к ошибке десериализации:
 * неизвестное значение сохраняется через {@link CaptureUnknown}, а результатом
 * десериализации становится {@code null}.</p>
 *
 * <p>Такое поведение позволяет:</p>
 * <ul>
 *   <li>сохранять обратную совместимость при появлении новых областей видимости в выводе;</li>
 *   <li>отлавливать неизвестные значения для диагностики;</li>
 *   <li>не прерывать разбор всего объекта из-за одного поля.</li>
 * </ul>
 *
 * @see RouteScope#getIgnoreCase(String)
 * @see CaptureUnknown
 */
public class RouteScopeDeserializer extends StdDeserializer<RouteScope> {

  /**
   * Создаёт десериализатор для типа {@link RouteScope}.
   */
  public RouteScopeDeserializer() {
    super(RouteScope.class);
  }

  /**
   * Десериализует строковое представление области видимости маршрутизации.
   *
   * <p>Сопоставление выполняется через {@link RouteScope#getIgnoreCase(String)},
   * который отвечает за нормализацию и поиск подходящего значения enum.</p>
   *
   * <p>Если значение неизвестно, оно сохраняется у текущего объекта,
   * если тот реализует {@link CaptureUnknown}, после чего возвращается {@code null}.</p>
   *
   * @param p JSON-парсер, указывающий на строковое значение области видимости
   * @param ctxt контекст десериализации Jackson
   * @return соответствующее значение {@link RouteScope} или {@code null},
   *         если область видимости неизвестна
   * @throws IOException если произошла ошибка чтения JSON
   * @see RouteScope#getIgnoreCase(String)
   * @see CaptureUnknown#captureUnknown(String, Object)
   */
  @Override
  public RouteScope deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    String value = p.getText();

    try {
      return RouteScope.getIgnoreCase(value);
    } catch (IllegalArgumentException e) {
      Object current = ctxt.getParser().getCurrentValue();

      if(current instanceof CaptureUnknown cu) {
        cu.captureUnknown("scope", value);
      }

      return null;
    }
  }
}
