package io.github.ganenkovna.util.ip.dto.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.ganenkovna.util.ip.dto.Inet6AddrGenMode;
import java.io.IOException;

/**
 * Jackson-десериализатор режима генерации IPv6-адреса ({@link Inet6AddrGenMode}).
 *
 * <p>Преобразует строковое значение JSON-поля {@code inet6_addr_gen_mode}
 * (например, из вывода {@code ip -j link} / {@code ip -j address})
 * в соответствующее значение enum {@link Inet6AddrGenMode}.</p>
 *
 * <p>Перед сопоставлением выполняется нормализация внутри
 * {@link Inet6AddrGenMode#getIgnoreCase(String)}: регистр игнорируется,
 * формат приводится к каноническому виду.</p>
 *
 * <p>Если значение неизвестно или не поддерживается текущей версией enum,
 * десериализация не завершается ошибкой. При наличии у текущего объекта
 * поддержки {@link CaptureUnknown} исходное значение сохраняется через
 * {@link CaptureUnknown#captureUnknown(String, Object)}, после чего
 * метод возвращает {@code null}.</p>
 *
 * @implNote Десериализатор реализует fail-soft стратегию: неизвестные значения не приводят
 *     к падению парсинга, так как вывод утилит и набор значений могут отличаться
 *     между версиями {@code iproute2} и ядра Linux.
 *
 * @see Inet6AddrGenMode
 * @see Inet6AddrGenMode#getIgnoreCase(String)
 * @see CaptureUnknown
 */
public final class Inet6AddrGenModeDeserializer extends StdDeserializer<Inet6AddrGenMode> {

  /**
   * Создаёт десериализатор для {@link Inet6AddrGenMode}.
   *
   * <p>Используется Jackson при разборе поля {@code inet6_addr_gen_mode}
   * в DTO сетевых интерфейсов.</p>
   *
   * @see Inet6AddrGenMode
   */
  public Inet6AddrGenModeDeserializer() {
    super(Inet6AddrGenMode.class);
  }

  /**
   * Десериализует строковое значение поля {@code inet6_addr_gen_mode}
   * в {@link Inet6AddrGenMode}.
   *
   * <p>Алгоритм:</p>
   * <ul>
   *   <li>читает строковое значение текущего JSON-токена;</li>
   *   <li>выполняет сопоставление через {@link Inet6AddrGenMode#getIgnoreCase(String)};</li>
   *   <li>при неизвестном значении сохраняет исходную строку в {@link CaptureUnknown}
   *       (если текущий объект поддерживает этот контракт) и возвращает {@code null}.</li>
   * </ul>
   *
   * @param p JSON-парсер Jackson, указывающий на строковое значение
   * @param ctxt контекст десериализации Jackson
   * @return значение {@link Inet6AddrGenMode} или {@code null}, если значение неизвестно
   * @throws IOException если произошла ошибка чтения JSON-токена
   * @see CaptureUnknown#captureUnknown(String, Object)
   */
  @Override
  public Inet6AddrGenMode deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    String value = p.getText();

    try {
      return Inet6AddrGenMode.getIgnoreCase(value);
    } catch (IllegalArgumentException e) {
      Object current = ctxt.getParser().getCurrentValue();

      if (current instanceof CaptureUnknown cu) {
        cu.captureUnknown("inet6_addr_gen_mode", value);
      }

      return null;
    }
  }
}
