package io.github.ganenkovna.util.ip.dto.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.ganenkovna.util.ip.dto.LinkMode;
import java.io.IOException;

/**
 * Jackson-десериализатор режима {@code linkmode} сетевого интерфейса ({@link LinkMode}).
 *
 * <p>Преобразует строковое значение JSON-поля {@code linkmode}
 * (например, из вывода {@code ip -j link} или {@code ip -j address})
 * в соответствующее значение enum {@link LinkMode}.</p>
 *
 * <p>Перед сопоставлением выполняется нормализация внутри
 * {@link LinkMode#getIgnoreCase(String)}: регистр игнорируется,
 * формат приводится к каноническому виду.</p>
 *
 * <p>Если значение неизвестно или не поддерживается текущей версией enum,
 * десериализация не завершается ошибкой: при наличии у текущего объекта
 * контракта {@link CaptureUnknown} исходное значение сохраняется через
 * {@link CaptureUnknown#captureUnknown(String, Object)}, после чего
 * метод возвращает {@code null}.</p>
 *
 * @implNote Десериализатор реализует fail-soft стратегию: неизвестные значения
 *     не приводят к падению парсинга, так как вывод утилит и набор значений могут
 *     отличаться между версиями {@code iproute2} и ядра Linux.
 *
 * @see LinkMode
 * @see LinkMode#getIgnoreCase(String)
 * @see CaptureUnknown
 */
public final class LinkModeDeserializer extends StdDeserializer<LinkMode> {

  /**
   * Создаёт десериализатор для {@link LinkMode}.
   *
   * <p>Используется Jackson при разборе поля {@code linkmode}
   * в DTO сетевых интерфейсов.</p>
   *
   * @see LinkMode
   */
  public LinkModeDeserializer() {
    super(LinkMode.class);
  }

  /**
   * Десериализует строковое значение поля {@code linkmode} в {@link LinkMode}.
   *
   * <p>Алгоритм:</p>
   * <ul>
   *   <li>читает строковое значение текущего JSON-токена;</li>
   *   <li>выполняет сопоставление через {@link LinkMode#getIgnoreCase(String)};</li>
   *   <li>при неизвестном значении сохраняет исходную строку в {@link CaptureUnknown}
   *       (если текущий объект поддерживает этот контракт) и возвращает {@code null}.</li>
   * </ul>
   *
   * @param p JSON-парсер Jackson, указывающий на строковое значение {@code linkmode}
   * @param ctxt контекст десериализации Jackson
   * @return значение {@link LinkMode} или {@code null}, если значение неизвестно
   * @throws IOException если не удалось прочитать значение текущего JSON-токена
   * @see CaptureUnknown#captureUnknown(String, Object)
   */
  @Override
  public LinkMode deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    String value = p.getText();

    try {
      return LinkMode.getIgnoreCase(value);
    } catch (IllegalArgumentException e) {
      Object current = ctxt.getParser().getCurrentValue();

      if (current instanceof CaptureUnknown cu) {
        cu.captureUnknown("linkmode", value);
      }

      return null;
    }
  }
}
