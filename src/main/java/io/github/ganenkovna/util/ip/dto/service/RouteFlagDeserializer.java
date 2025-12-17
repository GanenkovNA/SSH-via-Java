package io.github.ganenkovna.util.ip.dto.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.ganenkovna.util.ip.dto.RouteFlag;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Jackson-десериализатор для преобразования списка строковых флагов маршрута
 * в набор {@link RouteFlag}.
 *
 * <p>Используется как надстройка над enum {@link RouteFlag} и предназначен
 * для обработки поля {@code flags} в JSON-выводе команды {@code ip -j route}.</p>
 *
 * <p>Ожидаемый формат входных данных — JSON-массив строк, каждая из которых
 * представляет флаг маршрута (например: {@code ["onlink", "dead"]}).</p>
 *
 * <p>Каждое значение преобразуется через {@link RouteFlag#getIgnoreCase(String)}.
 * Неизвестные или неподдерживаемые флаги не добавляются в результирующий набор
 * и накапливаются отдельно.</p>
 *
 * <p>Если во время десериализации были обнаружены неизвестные значения,
 * и текущий объект десериализации реализует {@link CaptureUnknown},
 * они передаются через {@link CaptureUnknown#captureUnknown(String, Object)}
 * с ключом {@code flags}.</p>
 *
 * <p>Если входной JSON не является массивом или все значения оказались неизвестными,
 * метод возвращает {@code null}.</p>
 *
 * @implNote
 * Десериализатор реализует fail-soft стратегию:
 * неизвестные флаги не приводят к ошибке, так как вывод утилит и набор флагов
 * может различаться между версиями {@code iproute2} и ядра Linux.
 *
 * @see RouteFlag
 * @see RouteFlag#getIgnoreCase(String)
 * @see CaptureUnknown
 */
public class RouteFlagDeserializer extends StdDeserializer<Set<RouteFlag>> {

  /**
   * Создаёт десериализатор для набора флагов {@link RouteFlag}.
   *
   * <p>Передаёт тип {@link RouteFlag} в базовый конструктор
   * {@link StdDeserializer} для корректной инициализации
   * механизма десериализации Jackson.</p>
   *
   * @see RouteFlag
   */
  public RouteFlagDeserializer() {
    super(RouteFlag.class);
  }

  /**
   * Десериализует JSON-массив строк в набор {@link RouteFlag}.
   *
   * @param p JSON-парсер, указывающий на начало массива
   * @param ctxt контекст десериализации Jackson
   * @return набор флагов маршрута; {@code null}, если входной JSON
   *         не является массивом или не содержит известных значений
   * @throws IOException при ошибке чтения JSON
   */
  @Override
  public Set<RouteFlag> deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    if (!p.isExpectedStartArrayToken()){
      return null;
    }

    Set<RouteFlag> result = new HashSet<>();
    List<String> unknownFlags = new ArrayList<>();

    while (p.nextToken() != JsonToken.END_ARRAY){
      String value = p.getValueAsString();
      try {
        result.add(RouteFlag.getIgnoreCase(value));
      } catch (IllegalArgumentException e) {
        unknownFlags.add(value);
      }
    }

    if (!unknownFlags.isEmpty()) {
      Object current = ctxt.getParser().getCurrentValue();
      if(current instanceof CaptureUnknown cu) {
        cu.captureUnknown("flags", unknownFlags);
      }
    }
    return result.isEmpty() ? null : result;
  }
}
