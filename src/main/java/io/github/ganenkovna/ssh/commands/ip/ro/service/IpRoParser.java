package io.github.ganenkovna.ssh.commands.ip.ro.service;

import static io.github.ganenkovna.util.StringUtils.requireNonBlank;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ganenkovna.ssh.commands.ip.ro.dto.IpRoDTO;
import io.github.ganenkovna.ssh.commands.ip.ro.dto.IpRoPOJO;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Парсер вывода команды {@code ip -j ro}.
 *
 * <p>Класс предназначен для преобразования JSON-вывода утилиты {@code ip ro}
 * в список строго типизированных DTO {@link IpRoDTO}.</p>
 *
 * <p>Парсинг выполняется в два этапа:</p>
 * <ol>
 *   <li>JSON → {@link IpRoPOJO} с мягкой обработкой неизвестных полей
 *   (неизвестные параметры сохраняются в {@code unparsedParams});</li>
 *   <li>POJO → {@link IpRoDTO} с использованием строгого
 *   {@code record}-маппинга.</li>
 * </ol>
 *
 * <p>Неизвестные поля и частично некорректные значения не приводят к ошибке
 * на этапе разбора JSON, но могут вызвать исключение при строгом
 * преобразовании в DTO.</p>
 *
 * <p>Класс является утилитарным и не предназначен для инстанцирования.</p>
 *
 * @see IpRoPOJO
 * @see IpRoDTO
 * @see <a href="https://man7.org/linux/man-pages/man8/ip-route.8.html">
 *     ip-route(8) — Linux manual</a>
 */
public final class IpRoParser {
  private static final String COMMAND = "ip -j ro";

  /**
   * {@link ObjectMapper} для первичного разбора JSON-вывода команды
   * {@code ip -j ro}.
   *
   * <p>Используется на этапе преобразования JSON → {@link IpRoPOJO}.</p>
   *
   * <p>Особенности конфигурации:</p>
   * <ul>
   *   <li>неизвестные поля игнорируются
   *   ({@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} отключён),
   *   так как вывод {@code ip} может отличаться между версиями iproute2/ядра
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
   * {@code record}-DTO {@link IpRoDTO}.
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
  private IpRoParser() {
    throw new AssertionError("No instances");
  }

  /**
   * Выполняет разбор JSON-вывода команды {@code ip -j ro}.
   *
   * <p>Входная строка должна содержать корректный JSON-массив объектов.
   * Пустые и {@code null}-значения не допускаются.</p>
   *
   * <p>Возвращаемый список никогда не {@code null}, но может быть пустым,
   * если в выводе отсутствуют интерфейсы.</p>
   *
   * @param json
   *     вывод команды {@code ip -j ro}, строго не {@code null}
   *     и не пустая строка
   *
   * @return
   *     список маршрутов, никогда не {@code null}, но может быть пустым
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
  public static List<IpRoDTO> parseOutput(String json) {
    json = requireNonBlank(json, "Вывод команды `" + COMMAND + "`");

    return pojoToDto(jsonToPojo(json));
  }

  /**
   * Выполняет разбор JSON-вывода команды {@code ip -j ro}.
   *
   * <p>Входная строка должна содержать корректный JSON-массив объектов.
   * Пустые и {@code null}-значения не допускаются.</p>
   *
   * <p>Возвращаемый список никогда не {@code null}, но может быть пустым,
   * если в выводе отсутствуют интерфейсы.</p>
   *
   * @param json
   *     вывод команды {@code ip -j ro}, строго не {@code null}
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
  private static List<IpRoPOJO> jsonToPojo(String json) {
    try {
      return JSON_MAPPER.readValue(
          json,
          new TypeReference<List<IpRoPOJO>>() {}
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
   * {@link IpRoDTO} с отключёнными неявными приведениями типов
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
  private static List<IpRoDTO> pojoToDto(List<IpRoPOJO> pojos) {
    Objects.requireNonNull(pojos, "Список POJO не может быть null");

    try {
      return pojos.stream()
          .map(p -> toDtoStrict(Objects.requireNonNull(p, "Элемент POJO не может быть null")))
          .toList();
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("Не удалось преобразовать POJO в DTO (ip -j ro)", e);
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
  private static IpRoDTO toDtoStrict(IpRoPOJO pojo) {
    return STRICT_RECORD_MAPPER.convertValue(pojo, IpRoDTO.class);
  }
}
