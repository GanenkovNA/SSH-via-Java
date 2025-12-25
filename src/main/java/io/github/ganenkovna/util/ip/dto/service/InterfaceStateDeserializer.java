package io.github.ganenkovna.util.ip.dto.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.ganenkovna.util.ip.dto.InterfaceState;
import java.io.IOException;

/**
 * Jackson-десериализатор состояния сетевого интерфейса ({@link InterfaceState}).
 *
 * <p>Преобразует строковое значение поля {@code state} из JSON
 * (например, из вывода {@code ip link} или {@code bridge link})
 * в соответствующее значение enum {@link InterfaceState}.</p>
 *
 * <p>Перед сопоставлением выполняется нормализация строки
 * внутри {@link InterfaceState#getIgnoreCase(String)}:
 * регистр игнорируется, формат приводится к каноническому виду.</p>
 *
 * <p>Если значение состояния неизвестно или не поддерживается текущей версией enum,
 * оно не приводит к ошибке десериализации:
 * <ul>
 *   <li>при наличии у текущего объекта интерфейса {@link CaptureUnknown}
 *       значение сохраняется как неизвестное через
 *       {@link CaptureUnknown#captureUnknown(String, Object)};</li>
 *   <li>метод возвращает {@code null} (или может быть заменён на {@code DEFAULT},
 *       если такая стратегия будет принята).</li>
 * </ul>
 * </p>
 *
 * <p>Такой подход позволяет:</p>
 * <ul>
 *   <li>сохранять forward-compatibility при появлении новых состояний в ядре Linux;</li>
 *   <li>не прерывать парсинг всего DTO из-за одного неизвестного значения;</li>
 *   <li>централизованно анализировать неизвестные параметры.</li>
 * </ul>
 *
 * @see InterfaceState
 * @see InterfaceState#getIgnoreCase(String)
 * @see CaptureUnknown
 */
public final class InterfaceStateDeserializer extends StdDeserializer<InterfaceState> {

  /**
   * Создаёт десериализатор для {@link InterfaceState}.
   *
   * <p>Регистрируется Jackson и используется при разборе поля {@code state}
   * в DTO сетевых интерфейсов.</p>
   *
   * @see InterfaceState
   */
  public InterfaceStateDeserializer() {
    super(InterfaceState.class);
  }

  /**
   * Десериализует строковое значение состояния интерфейса в {@link InterfaceState}.
   *
   * <p>Поведение:</p>
   * <ul>
   *   <li>читает строковое значение из JSON-токена;</li>
   *   <li>пытается сопоставить его с enum через
   *       {@link InterfaceState#getIgnoreCase(String)};</li>
   *   <li>при неизвестном значении сохраняет его в {@link CaptureUnknown},
   *       если текущий объект поддерживает этот интерфейс.</li>
   * </ul>
   *
   * @param p JSON-парсер Jackson
   * @param ctxt контекст десериализации
   * @return значение {@link InterfaceState} или {@code null},
   *         если состояние неизвестно
   * @throws IOException если произошла ошибка чтения JSON
   * @see CaptureUnknown#captureUnknown(String, Object)
   */
  @Override
  public InterfaceState deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    String value = p.getText();

    try {
      return InterfaceState.getIgnoreCase(value);
    } catch (IllegalArgumentException e) {
      Object current = ctxt.getParser().getCurrentValue();

      if(current instanceof CaptureUnknown cu) {
        cu.captureUnknown("operstate", value);
      }

      return null; // или DEFAULT
    }
  }
}
