package io.github.ganenkovna.util.ip.dto.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.ganenkovna.util.ip.dto.LinkType;
import java.io.IOException;

/**
 * Jackson-десериализатор типа канального интерфейса ({@code link_type})
 * сетевого интерфейса ({@link LinkType}).
 *
 * <p>Преобразует строковое значение JSON-поля {@code link_type}
 * (например, из вывода {@code ip -j link} или {@code ip -j address})
 * в соответствующее значение enum {@link LinkType}.</p>
 *
 * <p>Перед сопоставлением выполняется нормализация внутри
 * {@link LinkType#getIgnoreCase(String)}: регистр игнорируется,
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
 * @see LinkType
 * @see LinkType#getIgnoreCase(String)
 * @see CaptureUnknown
 */
public final class LinkTypeDeserializer extends StdDeserializer<LinkType> {

  /**
   * Создаёт десериализатор для {@link LinkType}.
   *
   * <p>Регистрируется Jackson и используется при разборе поля {@code link_type}
   * в DTO сетевых интерфейсов.</p>
   *
   * @see LinkType
   */
  public LinkTypeDeserializer() {
    super(LinkType.class);
  }

  /**
   * Десериализует строковое значение поля {@code link_type} в {@link LinkType}.
   *
   * <p>Поведение:</p>
   * <ul>
   *   <li>читает строковое значение текущего JSON-токена;</li>
   *   <li>выполняет сопоставление через {@link LinkType#getIgnoreCase(String)};</li>
   *   <li>при неизвестном значении сохраняет исходную строку в {@link CaptureUnknown}
   *       (если текущий объект поддерживает этот контракт) и возвращает {@code null}.</li>
   * </ul>
   *
   * @param p JSON-парсер Jackson, указывающий на строковое значение {@code link_type}
   * @param ctxt контекст десериализации Jackson
   * @return значение {@link LinkType} или {@code null}, если значение неизвестно
   * @throws IOException если произошла ошибка чтения значения {@code link_type} из JSON-токена
   * @see CaptureUnknown#captureUnknown(String, Object)
   */
  @Override
  public LinkType deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    String value = p.getText();

    try {
      return LinkType.getIgnoreCase(value);
    } catch (IllegalArgumentException e) {
      Object current = ctxt.getParser().getCurrentValue();

      if (current instanceof CaptureUnknown cu) {
        cu.captureUnknown("linkmode", value);
      }

      return null;
    }
  }
}
