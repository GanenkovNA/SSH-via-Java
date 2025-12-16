package io.github.ganenkovna.util.ip.dto.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.ganenkovna.util.ip.dto.StpPortState;
import java.io.IOException;

/**
 * Jackson-десериализатор состояния STP-порта {@link StpPortState}.
 *
 * <p>Преобразует строковое значение поля {@code state} (например,
 * {@code forwarding}, {@code blocking}) в соответствующее значение enum
 * {@link StpPortState} без учёта регистра.</p>
 *
 * <p>Если значение не распознано, оно не приводит к ошибке десериализации:
 * вместо этого неизвестное значение сохраняется через интерфейс
 * {@link CaptureUnknown}, а результатом десериализации становится {@code null}.</p>
 *
 * <p>Такое поведение позволяет:
 * <ul>
 *   <li>сохранять обратную совместимость при появлении новых состояний STP,</li>
 *   <li>отлавливать неизвестные значения для диагностики,</li>
 *   <li>не прерывать разбор всего объекта из-за одного поля.</li>
 * </ul>
 * </p>
 *
 * @see StpPortState#getIgnoreCase(String)
 * @see CaptureUnknown
 */
public class StpPortStateDeserializer extends StdDeserializer<StpPortState> {

  /**
   * Создаёт десериализатор для типа {@link StpPortState}.
   */
  public StpPortStateDeserializer() {
    super(StpPortState.class);
  }

  /**
   * Десериализует строковое представление состояния STP-порта.
   *
   * <p>Значение нормализуется и сопоставляется с enum {@link StpPortState}
   * с помощью {@link StpPortState#getIgnoreCase(String)}.</p>
   *
   * <p>Если значение неизвестно, оно сохраняется как «unknown» у текущего
   * объекта, если тот реализует {@link CaptureUnknown}, после чего
   * возвращается {@code null}.</p>
   *
   * @param p  JSON-парсер, указывающий на строковое значение состояния
   * @param ctxt контекст десериализации Jackson
   * @return соответствующее значение {@link StpPortState} или {@code null},
   *         если состояние неизвестно
   * @throws IOException если возникает ошибка чтения JSON
   *
   * @see StpPortState#getIgnoreCase(String)
   * @see CaptureUnknown#captureUnknown(String, Object)
   */
  @Override
  public StpPortState deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    String value = p.getText();

    try {
      return StpPortState.getIgnoreCase(value);
    } catch (IllegalArgumentException e) {
      Object current = ctxt.getParser().getCurrentValue();

      if(current instanceof CaptureUnknown cu) {
        cu.captureUnknown("state", value);
      }

      return null;
    }
  }
}
