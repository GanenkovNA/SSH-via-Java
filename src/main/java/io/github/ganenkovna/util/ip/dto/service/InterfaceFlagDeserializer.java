package io.github.ganenkovna.util.ip.dto.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.ganenkovna.util.ip.dto.InterfaceFlag;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Jackson-десериализатор для преобразования списка строковых флагов
 * сетевого интерфейса в набор {@link InterfaceFlag}.
 *
 * <p>Используется как надстройка над enum {@link InterfaceFlag} и
 * предназначен для обработки поля {@code flags} в JSON-выводе
 * сетевых утилит (например, {@code ip -j link}).</p>
 *
 * <p>Ожидаемый формат входных данных — JSON-массив строк,
 * каждая из которых представляет флаг интерфейса
 * (например: {@code ["UP", "LOWER_UP", "BROADCAST"]}).</p>
 *
 * <p>Каждое значение нормализуется и преобразуется через
 * {@link InterfaceFlag#getIgnoreCase(String)}.
 * Неизвестные или неподдерживаемые флаги не добавляются в результирующий
 * набор и накапливаются отдельно.</p>
 *
 * <p>Если во время десериализации были обнаружены неизвестные значения,
 * и текущий объект десериализации реализует {@link CaptureUnknown},
 * они передаются через
 * {@link CaptureUnknown#captureUnknown(String, Object)}
 * с ключом {@code interfaceFlags}.</p>
 *
 * <p>Если входной JSON не является массивом или все значения оказались
 * неизвестными, метод возвращает {@code null}.</p>
 *
 * @implNote
 * Десериализатор реализует fail-soft стратегию:
 * неизвестные флаги не приводят к ошибке, так как вывод сетевых утилит
 * может различаться между версиями ядра Linux.
 *
 * @see InterfaceFlag
 * @see InterfaceFlag#getIgnoreCase(String)
 * @see CaptureUnknown
 */
public class InterfaceFlagDeserializer extends StdDeserializer<Set<InterfaceFlag>> {

  /**
   * Создаёт десериализатор для набора флагов {@link InterfaceFlag}.
   *
   * <p>Передаёт тип {@link InterfaceFlag} в базовый конструктор
   * {@link StdDeserializer} для корректной инициализации
   * механизма десериализации Jackson.</p>
   *
   * @see InterfaceFlag
   */
  public InterfaceFlagDeserializer() {
    super(InterfaceFlag.class);
  }

  /**
   * Десериализует JSON-массив строк в набор {@link InterfaceFlag}.
   *
   * @param p JSON-парсер, указывающий на начало массива
   * @param ctxt контекст десериализации Jackson
   * @return набор флагов интерфейса; {@code null}, если входной JSON
   *         не является массивом или не содержит известных значений
   * @throws IOException при ошибке чтения JSON
   */
  @Override
  public Set<InterfaceFlag> deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    if (!p.isExpectedStartArrayToken()){
      return null;
    }

    Set<InterfaceFlag> result = new HashSet<>();
    List<String> unknownFlags = new ArrayList<>();

    while (p.nextToken() != JsonToken.END_ARRAY){
      String value = p.getValueAsString();
      try {
        result.add(InterfaceFlag.getIgnoreCase(value));
      } catch (IllegalArgumentException e) {
        unknownFlags.add(value);
      }
    }

    if (!unknownFlags.isEmpty()) {
      Object current = ctxt.getParser().getCurrentValue();
      if(current instanceof CaptureUnknown cu) {
        cu.captureUnknown("interfaceFlags", unknownFlags);
      }
    }
    return result.isEmpty() ? null : result;
  }
}
