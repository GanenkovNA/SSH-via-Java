package io.github.ganenkovna.util.ip.dto.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.ganenkovna.util.ip.dto.QdiscType;
import java.io.IOException;

public final class QdiscTypeDeserializer extends StdDeserializer<QdiscType> {

  /**
   * Создаёт десериализатор для типа {@link QdiscType}.
   */
  public QdiscTypeDeserializer() {
    super(QdiscType.class);
  }

  /**
   * Десериализует строковое представление типа дисциплины очереди.
   *
   * <p>Сопоставление выполняется через {@link QdiscType#getIgnoreCase(String)},
   * который отвечает за нормализацию и поиск подходящего значения enum.</p>
   *
   * <p>Если значение неизвестно, оно сохраняется у текущего объекта,
   * если тот реализует {@link CaptureUnknown}, после чего возвращается {@code null}.</p>
   *
   * @param p JSON-парсер, указывающий на строковое значение области видимости
   * @param ctxt контекст десериализации Jackson
   * @return соответствующее значение {@link QdiscType} или {@code null},
   *         если область видимости неизвестна
   * @throws IOException если произошла ошибка чтения JSON
   * @see QdiscType#getIgnoreCase(String)
   * @see CaptureUnknown#captureUnknown(String, Object)
   */
  @Override
  public QdiscType deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    String value = p.getText();

    try {
      return QdiscType.getIgnoreCase(value);
    } catch (IllegalArgumentException e) {
      Object current = ctxt.getParser().getCurrentValue();

      if(current instanceof CaptureUnknown cu) {
        cu.captureUnknown("qdisc", value);
      }

      return null;
    }
  }
}
