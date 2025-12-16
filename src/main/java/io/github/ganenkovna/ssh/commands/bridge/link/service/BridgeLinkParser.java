package io.github.ganenkovna.ssh.commands.bridge.link.service;

import static io.github.ganenkovna.util.StringUtils.requireNonBlank;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ganenkovna.ssh.commands.bridge.link.dto.BridgeLinkPortDTO;
import io.github.ganenkovna.ssh.commands.bridge.link.dto.BridgeLinkPortPOJO;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Парсер вывода команды {@code bridge -j link}.
 *
 * <p>Класс предназначен для преобразования JSON-вывода утилиты {@code bridge}
 * в список строго типизированных DTO {@link BridgeLinkPortDTO}.</p>
 *
 * <p>Парсинг выполняется в два этапа:</p>
 * <ol>
 *   <li>JSON → {@link BridgeLinkPortPOJO} с мягкой обработкой неизвестных полей
 *   (неизвестные параметры сохраняются в {@code unparsedParams});</li>
 *   <li>POJO → {@link BridgeLinkPortDTO} с использованием строгого
 *   {@code record}-маппинга.</li>
 * </ol>
 *
 * <p>Неизвестные поля и частично некорректные значения не приводят к ошибке
 * на этапе разбора JSON, но могут вызвать исключение при строгом
 * преобразовании в DTO.</p>
 *
 * <p>Класс является утилитарным и не предназначен для инстанцирования.</p>
 *
 * @see BridgeLinkPortPOJO
 * @see BridgeLinkPortDTO
 * @see <a href="https://man7.org/linux/man-pages/man8/bridge.8.html">
 *     bridge(8) — Linux manual</a>
 */
public final class BridgeLinkParser {
  private static final String COMMAND = "bridge -j link";

  /**
   * {@link ObjectMapper} для первичного разбора JSON-вывода команды
   * {@code bridge -j link}.
   *
   * <p>Используется на этапе преобразования JSON → {@link BridgeLinkPortPOJO}.</p>
   *
   * <p>Особенности конфигурации:</p>
   * <ul>
   *   <li>неизвестные поля игнорируются
   *   ({@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} отключён),
   *   так как вывод {@code bridge} может отличаться между версиями ядра
   *   и конфигурациями;</li>
   *   <li>включена проверка на наличие лишних токенов после JSON
   *   ({@link DeserializationFeature#FAIL_ON_TRAILING_TOKENS}),
   *   что позволяет выявлять «загрязнённый» вывод (приглашение шелла,
   *   лог-сообщения и т.п.).</li>
   * </ul>
   *
   * <p>Данный маппер намеренно работает в «мягком» режиме и не выполняет
   * строгую валидацию структуры данных — эта ответственность
   * возлагается на следующий этап преобразования в DTO.</p>
   */
  private static final ObjectMapper JSON_MAPPER = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

  /**
   * {@link ObjectMapper} для строгого преобразования POJO в
   * {@code record}-DTO {@link BridgeLinkPortDTO}.
   *
   * <p>Используется на этапе {@code POJO → DTO} и предназначен для
   * выявления структурных и типовых ошибок данных.</p>
   *
   * <p>Особенности конфигурации:</p>
   * <ul>
   *   <li>запрещены неявные приведения скалярных типов
   *   ({@link MapperFeature#ALLOW_COERCION_OF_SCALARS} отключён),
   *   что предотвращает «тихие» преобразования некорректных значений;</li>
   *   <li>включена проверка наличия всех параметров конструктора
   *   {@code record} ({@link DeserializationFeature#FAIL_ON_MISSING_CREATOR_PROPERTIES}),
   *   что гарантирует полноту структуры DTO.</li>
   * </ul>
   *
   * <p>Маппер намеренно допускает {@code null}-значения для полей DTO,
   * если они присутствуют в исходных данных, — семантическая валидация
   * выполняется за пределами данного класса.</p>
   */
  private static final ObjectMapper STRICT_RECORD_MAPPER = new ObjectMapper()
      .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
      .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);

  /** Запрет инстанцирования. */
  private BridgeLinkParser() {
    throw new AssertionError("No instances");
  }

  /**
   * Выполняет разбор JSON-вывода команды {@code bridge -j link}.
   *
   * <p>Входная строка должна содержать корректный JSON-массив объектов.
   * Пустые и {@code null}-значения не допускаются.</p>
   *
   * <p>Возвращаемый список никогда не {@code null}, но может быть пустым,
   * если в выводе отсутствуют интерфейсы.</p>
   *
   * @param json
   *     вывод команды {@code bridge -j link}, строго не {@code null}
   *     и не пустая строка
   *
   * @return
   *     список портов моста (slave-интерфейсы), никогда не {@code null}
   *
   * @throws NullPointerException
   *     если {@code json == null}
   *
   * @throws IllegalArgumentException
   *     если {@code json} является пустой или состоит только из пробелов
   *
   * @throws IllegalStateException
   *     если вывод не является валидным JSON или не удалось
   *     преобразовать данные в DTO
   */
  public static List<BridgeLinkPortDTO> parseOutput(String json) {
    json = requireNonBlank(json, "Вывод команды `" + COMMAND + "`");

    return pojoToDto(jsonToPojo(json));
  }

  /**
   * Выполняет разбор JSON-вывода команды {@code bridge -j link}.
   *
   * <p>Входная строка должна содержать корректный JSON-массив объектов.
   * Пустые и {@code null}-значения не допускаются.</p>
   *
   * <p>Возвращаемый список никогда не {@code null}, но может быть пустым,
   * если в выводе отсутствуют интерфейсы.</p>
   *
   * @param json
   *     вывод команды {@code bridge -j link}, строго не {@code null}
   *     и не пустая строка
   *
   * @return
   *     список портов моста (slave-интерфейсы), никогда не {@code null}
   *
   * @throws NullPointerException
   *     если {@code json == null}
   *
   * @throws IllegalArgumentException
   *     если {@code json} является пустой или состоит только из пробелов
   *
   * @throws IllegalStateException
   *     если вывод не является валидным JSON или не удалось
   *     преобразовать данные в DTO
   */
  private static List<BridgeLinkPortPOJO> jsonToPojo(String json) {
    try {
      return JSON_MAPPER.readValue(
          json,
          new TypeReference<List<BridgeLinkPortPOJO>>() {}
      );
    } catch (IOException e) {
      throw new IllegalStateException("Не удалось обработать вывод `"
          + COMMAND + "`: " + json, e);
    }
  }

  /**
   * Преобразует список POJO в список строго типизированных DTO.
   *
   * <p>Для каждого элемента выполняется строгое преобразование в
   * {@link BridgeLinkPortDTO} с отключёнными неявными приведениями типов
   * и обязательным наличием всех параметров конструктора {@code record}.</p>
   *
   * @param pojos
   *     список POJO-объектов, строго не {@code null},
   *     элементы списка также строго не {@code null}
   *
   * @return
   *     список DTO, никогда не {@code null}
   *
   * @throws NullPointerException
   *     если {@code pojos == null} или содержит {@code null}-элемент
   *
   * @throws IllegalStateException
   *     если преобразование хотя бы одного элемента завершилось ошибкой
   */
  private static List<BridgeLinkPortDTO> pojoToDto(List<BridgeLinkPortPOJO> pojos) {
    Objects.requireNonNull(pojos, "Список POJO не может быть null");

    try {
      return pojos.stream()
          .map(p -> toDtoStrict(Objects.requireNonNull(p, "Элемент POJO не может быть null")))
          .toList();
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("Не удалось преобразовать POJO в DTO (bridge -j link)", e);
    }
  }

  /**
   * Выполняет строгое преобразование одного POJO-объекта в DTO.
   *
   * <p>Используется {@link ObjectMapper} с отключёнными неявными приведениями
   * скалярных типов и включённой проверкой обязательных параметров конструктора.</p>
   *
   * @param pojo
   *     исходный POJO-объект, строго не {@code null}
   *
   * @return
   *     DTO-представление интерфейса
   *
   * @throws IllegalArgumentException
   *     если данные POJO не соответствуют контракту DTO
   */
  private static BridgeLinkPortDTO toDtoStrict(BridgeLinkPortPOJO pojo) {
    return STRICT_RECORD_MAPPER.convertValue(pojo, BridgeLinkPortDTO.class);
  }
}
