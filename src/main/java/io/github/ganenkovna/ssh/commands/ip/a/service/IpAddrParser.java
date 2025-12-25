package io.github.ganenkovna.ssh.commands.ip.a.service;

import static io.github.ganenkovna.util.StringUtils.requireNonBlank;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ganenkovna.ssh.commands.ip.a.dto.AddrInfoDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.Inet6AddrInfoDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.InetAddrInfoDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.IpAddrDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.IpAddrPojo;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Парсер JSON-вывода команды {@code ip -j a}.
 *
 * <p>Класс выполняет разбор результата {@code ip -j a} и преобразует его
 * в список {@link IpAddrDto}.</p>
 *
 * <p>Пайплайн обработки:
 * <ul>
 *   <li>проверка, что вывод команды не {@code null} и не пустой;</li>
 *   <li>десериализация JSON в список POJO;</li>
 *   <li>преобразование POJO в DTO.</li>
 * </ul>
 * </p>
 *
 * <p>Класс не хранит состояния и предназначен для повторного использования.</p>
 *
 * @see IpAddrDto
 * @see IpAddrPojo
 */
public final class IpAddrParser {
  private static final String COMMAND = "ip -j a";

  /**
   * {@link ObjectMapper} для первичного разбора JSON-вывода команды
   * {@code ip -j a}.
   *
   * <p>Используется на этапе преобразования JSON → {@link IpAddrPojo}.</p>
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
   * {@link ObjectMapper} для строгого преобразования POJO в DTO (record).
   *
   * <p>Используется на этапе преобразования {@link IpAddrPojo} → {@link IpAddrDto}
   * через {@link ObjectMapper#convertValue(Object, Class)}.</p>
   *
   * <p>Особенности конфигурации:</p>
   * <ul>
   *   <li>отключены неявные приведения скалярных типов
   *   ({@link MapperFeature#ALLOW_COERCION_OF_SCALARS}),
   *   что предотвращает автоматическое преобразование, например,
   *   строки в число или {@code null} в значение по умолчанию;</li>
   *   <li>включена проверка наличия всех обязательных параметров
   *   конструктора record
   *   ({@link DeserializationFeature#FAIL_ON_MISSING_CREATOR_PROPERTIES}),
   *   что гарантирует полноту и согласованность DTO;</li>
   *   <li>зарегистрированы подтипы {@link AddrInfoDto} для корректной
   *   полиморфной десериализации адресной информации
   *   (IPv4 / IPv6).</li>
   * </ul>
   *
   * <p>Данный маппер предназначен для fail-fast валидации структуры и типов
   * данных: любые несоответствия между POJO и DTO приводят к немедленному
   * выбросу исключения.</p>
   */
  private static final ObjectMapper POJO_TO_DTO_MAPPER = new ObjectMapper()
      .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
      .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);

  static {
    POJO_TO_DTO_MAPPER.registerSubtypes(InetAddrInfoDto.class, Inet6AddrInfoDto.class);
  }


  /** Запрет инстанцирования. */
  private IpAddrParser() {
    throw new AssertionError("No instances");
  }

  /**
   * Парсит вывод команды {@code ip -j a} и возвращает DTO-представление.
   *
   * <p>Входная строка должна содержать JSON-массив объектов интерфейсов,
   * полученный командой {@code ip -j a}.</p>
   *
   * @param json вывод команды {@code ip -j a}; не {@code null} и не пустой
   * @return список интерфейсов в виде {@link IpAddrDto}; никогда не {@code null}, может быть пустым
   * @throws NullPointerException если {@code json == null}
   * @throws IllegalArgumentException если {@code json} пустой
   *                                  или состоит только из пробельных символов
   * @throws IllegalStateException если не удалось разобрать JSON или преобразовать данные в DTO
   * @see #jsonToPojo(String)
   * @see #pojoToDto(List)
   */
  public static List<IpAddrDto> parseOutput(String json) {
    json = requireNonBlank(json, "Вывод команды `" + COMMAND + "`");

    return pojoToDto(jsonToPojo(json));
  }

  /**
   * Десериализует JSON-вывод команды {@code ip -j a} в список POJO.
   *
   * <p>Ожидается JSON-массив объектов интерфейсов. Неизвестные поля могут
   * быть сохранены в POJO при наличии соответствующей поддержки (например,
   * через механизм capture unknown).</p>
   *
   * @param json JSON-строка, полученная командой {@code ip -j a}; не {@code null}
   * @return список {@link IpAddrPojo}; никогда не {@code null}, может быть пустым
   * @throws NullPointerException если {@code json == null}
   * @throws IllegalStateException если JSON невозможно разобрать
   *         (например, сообщение: {@code "Не удалось разобрать JSON вывода ip -j a: ..."})
   * @see IpAddrPojo
   */
  private static List<IpAddrPojo> jsonToPojo(String json) {
    try {
      return JSON_MAPPER.readValue(
          json,
          new TypeReference<List<IpAddrPojo>>() {}
      );
    } catch (IOException e) {
      throw new IllegalStateException("Не удалось обработать вывод `"
          + COMMAND + "`: " + json, e);
    }
  }

  /**
   * Преобразует список POJO в список DTO.
   *
   * <p>Преобразование выполняется через {@link ObjectMapper#convertValue(Object, Class)}.</p>
   *
   * @param pojos список {@link IpAddrPojo}; не {@code null}
   * @return список {@link IpAddrDto}; никогда не {@code null}, может быть пустым
   * @throws NullPointerException если {@code pojos == null}
   * @throws IllegalStateException если не удалось преобразовать хотя бы один элемент
   *         (например, сообщение: {@code "Не удалось преобразовать POJO в DTO (ip -j a)"})
   * @see #toDtoStrict(IpAddrPojo)
   */
  private static List<IpAddrDto> pojoToDto(List<IpAddrPojo> pojos) {
    Objects.requireNonNull(pojos, "Список POJO не может быть null");

    try {
      return pojos.stream()
          .map(p -> toDtoStrict(Objects.requireNonNull(p, "Элемент POJO не может быть null")))
          .toList();
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("Не удалось преобразовать POJO в DTO (ip -j a)", e);
    }
  }

  /**
   * Строго преобразует POJO в DTO.
   *
   * <p>Преобразование выполняется без неявных приведений скалярных типов
   * и с требованием наличия обязательных параметров конструктора record.</p>
   *
   * @param pojo исходный {@link IpAddrPojo}; не {@code null}
   * @return DTO-объект {@link IpAddrDto}
   * @throws NullPointerException если {@code pojo == null}
   * @throws IllegalArgumentException если входные данные несовместимы с DTO
   *         (например, отсутствуют обязательные поля или типы не совпадают)
   * @see IpAddrDto
   * @see IpAddrPojo
   */
  private static IpAddrDto toDtoStrict(IpAddrPojo pojo) {
    return POJO_TO_DTO_MAPPER.convertValue(pojo, IpAddrDto.class);
  }
}
