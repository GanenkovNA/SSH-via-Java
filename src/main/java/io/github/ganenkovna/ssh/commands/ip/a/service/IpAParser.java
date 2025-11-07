package io.github.ganenkovna.ssh.commands.ip.a.service;

import static io.github.ganenkovna.ssh.commands.ip.a.service.ParserTokens.*;
import static io.github.ganenkovna.ssh.commands.ip.a.service.ParserUtils.*;
import static io.github.ganenkovna.util.ip.MacValidation.validateMac;

import io.github.ganenkovna.ssh.commands.ip.a.dto.InterfaceDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.LifeTimeParamsDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v4.InterfaceIpv4ConfigDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v4.IpV4AddressFlag;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v4.IpV4Scope;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v6.GenerationFlag;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v6.InterfaceIpv6ConfigDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v6.IpV6Scope;
import io.github.ganenkovna.ssh.commands.ip.a.dto.addr.v6.RouteFlag;
import io.github.ganenkovna.ssh.commands.ip.a.dto.base.InterfaceBaseConfigDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.base.InterfaceFlag;
import io.github.ganenkovna.ssh.commands.ip.a.dto.base.InterfaceState;
import io.github.ganenkovna.ssh.commands.ip.a.dto.base.QdiscType;
import io.github.ganenkovna.ssh.commands.ip.a.dto.physical.InterfacePhysicalConfigDto;
import io.github.ganenkovna.util.parser.ParsersUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

/**
 * Парсер вывода команды {@code ip a} для получения структурированных данных о сетевых интерфейсах.
 *
 * <p>Основные функции:
 *   <ul>
 *     <li>Разбор многострочного вывода команды {@code ip a}
 *     <li>Преобразование в иерархическую структуру {@link InterfaceDto}
 *     <li>Обработка базовых параметров, физических характеристик и IP-конфигураций
 *     <li>Сохранение нераспознанных параметров для диагностики
 *   </ul>
 *
 * @see InterfaceDto Результирующая структура данных
 * @see ParserTokens Используемые константы и регулярные выражения
 */
public final class IpAParser {

  /** Запрет инстанцирования. */
  private IpAParser() {
    throw new AssertionError("No instances");
  }

  /**
   * Парсит вывод команды {@code ip a} в список DTO интерфейсов.
   *
   * @param ipAOutput сырой вывод команды {@code ip a}
   * @return список распарсенных интерфейсов (не {@code null}, может быть пустым)
   * @throws NullPointerException если ipAOutput равен {@code null}
   */
  public static List<InterfaceDto> parseOutput(String ipAOutput) {
    // Проверка вывода команды на пустую строку
    Objects.requireNonNull(ipAOutput, "Вывод команды `ip a` не может быть null");
    // На случай, если цвет не был отключён на стороне вызова
    ipAOutput = ParsersUtils.stripAnsi(ipAOutput);
    if (ipAOutput.isBlank()) {
      return Collections.emptyList();
    }

    final List<InterfaceDto> interfaces = new ArrayList<>();
    InterfaceDto currentInterface = null;
    // Для нераспознанных строк до опознания первого интерфейса
    final List<String> preamble = new ArrayList<>();

    final String[] lines = trimOutputStrings(ipAOutput);
    for (int i = 0; i < lines.length; i++) {
      if (isInterfaceStart(lines[i])) {
        currentInterface = new InterfaceDto();

        // Внесение нераспознанных строк в первый интерфейс для дебага
        if (!preamble.isEmpty()){
          for (String u : preamble){
            currentInterface.addUnknownLine(u);
          }
          preamble.clear();
        }

        i = parseInterface(i, lines, currentInterface);
        interfaces.add(currentInterface);
      } else{
        if (currentInterface == null) {
          preamble.add("Преамбула/неизвестная строка " + i + ": " + lines[i] + "\n");
        } else {
          currentInterface.addUnknownLine("Неизвестная строка " + i + ": " + lines[i] + "\n");
        }
      }
    }
    return interfaces;
  }

  /**
   * Обрабатывает блок одного интерфейса, начиная с строки-заголовка.
   * <p>Вызывается только из {@link #parseOutput(String)}. Гарантии со стороны вызывающего:</p>
   * <ul>
   *   <li>{@code lines} — строго не {@code null}, строки уже нормализованы</li>
   *   <li>{@code currentInterface} — строго не {@code null}</li>
   *   <li>{@code i} указывает на строку, удовлетворяющую {@code ^\d+:} (см. {@code INDEX_PATTERN})</li>
   * </ul>
   *
   * @param i     индекс строки-заголовка интерфейса
   * @param lines все строки вывода
   * @param currentInterface DTO для накопления данных
   * @return индекс последней строки, относящейся к текущему интерфейсу
   */
  private static int parseInterface(int i, String[] lines, InterfaceDto currentInterface) {
    // Базовые параметры интерфейса
    currentInterface.setInterfaceParams(
        parseInterfaceDetails(lines[i]));

    // Физические параметры
    if (i + 1 < lines.length && isLineStart(lines[i + 1], LINK_LINE)) {
      currentInterface.setInterfacePhysicalParams(
          parsePhysicalParams(lines[++i]));
    }

    while (i + 1 < lines.length && !isInterfaceStart(lines[i + 1])) {
      // IPv-4
      if (isLineStart(lines[i + 1], INET_LINE)) {
        i = parseInetBlock(++i, lines, currentInterface);
      }
      // IPv-6
      else if (isLineStart(lines[i + 1], INET6_LINE)) {
        i = parseInet6Block(++i, lines, currentInterface);
      }
      // Нераспознанные строки
      else {
        currentInterface.addUnknownLine("Неизвестная строка: " + lines[++i]);
      }
    }
    return i;
  }

  /**
   * Парсит строку-заголовок интерфейса из вывода {@code ip a} и извлекает базовые параметры.
   *
   * <p>Вызывается только из {@link #parseInterface(int, String[], InterfaceDto)}.
   * На вход подаётся строка, прошедшая нормализацию и токенизацию
   * в {@link ParserUtils#trimOutputLine(String)}.</p>
   *
   * <p>Метод извлекает индекс интерфейса, имя (с возможным {@code @peer}),
   * флаги, MTU, дисциплину очереди, состояние, группу, длину очереди и родительский интерфейс.
   * Неизвестные или некорректные параметры сохраняются в {@code unknownParams}.</p>
   *
   * @param line строка с параметрами интерфейса (строго не {@code null}, уже нормализована)
   * @return объект {@link InterfaceBaseConfigDto} с базовыми параметрами
   *         (никогда не {@code null}, может содержать частичные данные)
   * @see InterfaceBaseConfigDto
   * @see <a href="https://man7.org/linux/man-pages/man8/ip-link.8.html">man ip-link(8)</a>
   */
  private static InterfaceBaseConfigDto parseInterfaceDetails(String line) {
    InterfaceBaseConfigDto interfaceDetails = new InterfaceBaseConfigDto();
    String[] parts = trimOutputLine(line);

    for (int i = 0; i < parts.length; i++) {
      // Парсинг индекса, имени и флага
      if (tryParseIndex(parts[i], interfaceDetails)
          || tryParseInterfaceName(parts[i], interfaceDetails)
          || tryParseFlags(parts[i], interfaceDetails)) {
        continue;
      }
      // Парсинг MTU
      else if (isEqualIgnoreCase(parts[i], MTU)) {
        try {
          interfaceDetails.setMtu(Integer.parseInt(parts[++i]));
        } catch (IllegalArgumentException e) {
          interfaceDetails.addUnknownParam(e.getMessage());
        }
      }
      // Парсинг дисциплины очереди
      else if (isEqualIgnoreCase(parts[i], QDISC)) {
        if (QdiscType.isValid(parts[i + 1])) {
          interfaceDetails.setQdiscType(
              QdiscType.getIgnoreCase(parts[++i]));
        } else {
          interfaceDetails.addUnknownParam("Значение qdisc не найдено: " + parts[i + 1]);
        }
      }
      // Парсинг родительского интерфейса
      else if (isEqualIgnoreCase(parts[i], MASTER)) {
        interfaceDetails.setMaster(parts[++i]);
      }
      // Парсинг состояния интерфейса
      else if (isEqualIgnoreCase(parts[i], STATE)) {
        if (InterfaceState.isValid(parts[i + 1])) {
          interfaceDetails.setState(
              InterfaceState.getIgnoreCase(parts[++i]));
        } else {
          interfaceDetails.addUnknownParam(
              "Значение состояния интерфейса не найдено: " + parts[i + 1]);
        }
      }
      // Парсинг группы интерфейса
      else if (isEqualIgnoreCase(parts[i], GROUP)) {
        interfaceDetails.setGroup(parts[++i]);
      }
      // Парсинг длины очереди передачи
      else if (isEqualIgnoreCase(parts[i], QLEN)) {
        try {
          interfaceDetails.setQlen(Integer.parseInt(parts[++i]));
        } catch (IllegalArgumentException e) {
          interfaceDetails.addUnknownParam(e.getMessage());
        }
      }
      // Внесение нераспознанных параметров
      else {
        interfaceDetails.addUnknownParam("Неизвестный параметр: " + parts[i]);
      }
    }
    return interfaceDetails;
  }

  /**
   * Пытается извлечь индекс интерфейса из заголовочной строки {@code ip a}.
   *
   * <p>Вызывается только из {@link #parseInterfaceDetails(String)}.
   * Предполагается, что строка уже нормализована.</p>
   *
   * @param line токен строки для анализа (строго не {@code null})
   * @param dto DTO для сохранения результата (строго не {@code null})
   * @return {@code true}, если индекс успешно распознан и сохранён;
   *         {@code false}, если в строке нет индекса или установка не удалась
   * @see ParserTokens#INDEX_PATTERN
   */
  private static boolean tryParseIndex(String line, InterfaceBaseConfigDto dto) {
    Matcher matcher = INDEX_PATTERN.matcher(line);
    if (!matcher.lookingAt()) {
      return false;
    }
    int index = Integer.parseInt(matcher.group(1));

    if (Objects.isNull(dto.getIndex())) {
      try {
        dto.setIndex(index);
        return true;
      } catch (IllegalArgumentException e) {
        dto.addUnknownParam(e.getMessage());
      }
    } else {
      dto.addUnknownParam("Попытка установки нового значения индекса `"
          + index + "` вместо текущего `" + dto.getIndex() + "`");
    }
    return false;
  }

  /**
   * Пытается извлечь имя интерфейса из заголовочной строки {@code ip a}.
   *
   * <p>Вызывается только из {@link #parseInterfaceDetails(String)}.
   * Предполагается, что строка уже нормализована.
   * В текущей реализации имя может содержать {@code @peer}, если он присутствует
   * в выводе утилиты (например, {@code veth123@if5}).</p>
   *
   * @param line токен строки для анализа (строго не {@code null})
   * @param dto DTO для сохранения результата (строго не {@code null})
   * @return {@code true}, если имя успешно распознано и сохранено;
   *         {@code false}, если имя не найдено или установка не удалась
   * @see ParserTokens#NAME_PATTERN
   */
  private static boolean tryParseInterfaceName(String line, InterfaceBaseConfigDto dto) {
    Matcher matcher = NAME_PATTERN.matcher(line);
    if (!matcher.lookingAt()) {
      return false;
    }
    String name = matcher.group(1);

    if (Objects.isNull(dto.getName())) {
      dto.setNameWithoutValidation(matcher.group(1));
      return true;
    } else {
      dto.addUnknownParam("Попытка установки нового значения имени интерфейса `"
          + name + "` вместо текущего `" + dto.getName() + "`");
    }
    return false;
  }

  /**
   * Пытается распарсить токен флагов интерфейса вида {@code <FLAG1,FLAG2,...>}.
   *
   * <p>Вызывается только из {@link #parseInterfaceDetails(String)}; на вход подаётся
   * отдельный токен строки заголовка, уже нормализованный.</p>
   *
   * <p>Неизвестные значения флагов добавляются в {@code unknownParams}.
   * Коллекция флагов в DTO — никогда не {@code null}, может быть пустой.</p>
   *
   * @param line токен вида {@code <...>} (строго не {@code null})
   * @param dto  DTO для сохранения результата (строго не {@code null})
   * @return {@code true}, если токен флагов распознан; {@code false} — если токен не соответствует формату
   */
  private static boolean tryParseFlags(String line, InterfaceBaseConfigDto dto) {
    final Matcher matcher = FLAGS_PATTERN.matcher(line);

    if (matcher.matches()) {
      final String[] flags = matcher.group(1).split(",");
      final List<String> unknownFlags = new ArrayList<>();

      for (String flag : flags) {
        flag = flag.trim();
        if (!flag.isEmpty()) {
          if (InterfaceFlag.isValid(flag)) {
            dto.addFlag(
                InterfaceFlag.getIgnoreCase(flag));
          } else {
            unknownFlags.add(flag);
          }
        }
      }

      if (!unknownFlags.isEmpty()){
        dto.addUnknownParam("Неизвестные флаги интерфейса: "
            + String.join(", ", unknownFlags));
      }
      return true;
    }
    return false;
  }

  /**
   * Обрабатывает физические параметры интерфейса.
   *
   * @param line строка с физическими параметрами
   * @return DTO с физическими характеристиками
   * @see InterfacePhysicalConfigDto
   */
  private static InterfacePhysicalConfigDto parsePhysicalParams(String line) {
    Matcher matcher;
    InterfacePhysicalConfigDto interfacePhysicalParams = new InterfacePhysicalConfigDto();
    String[] parts = trimOutputLine(line);

    for (int i = 0; i < parts.length; i++) {
      // Парсинг типа канального уровня и MAC-адреса
      if (isLineStart(parts[i], LINK_LINE)) {
        matcher = LINK_TYPE_PATTERN.matcher(parts[i]);
        // Тип канального уровня
        if (matcher.matches()) {
          interfacePhysicalParams.setLinkType(matcher.group(1));
          // MAC-адрес
          if (i + 1 < parts.length) {
            i++;
            if (validateMac(parts[i])){
              interfacePhysicalParams.setMac(parts[i]);
            } else if (!isBroadcastKey(parts[i])) {
              interfacePhysicalParams.addUnknownParam("Ожидался MAC сразу после " + parts[i-1] + ", получено: " + parts[i]);
            }
          }
        }
      }
      // Парсинг MAC-broadcast
      else if (isBroadcastKey(parts[i])) {
        try {
          interfacePhysicalParams.setBroadcastMac(parts[++i]);
        } catch (IllegalArgumentException e) {
          interfacePhysicalParams.addUnknownParam(e.getMessage());
        }
      }
      // Внесение нераспознанных параметров
      else if (!parts[i].isBlank()) {
        interfacePhysicalParams.addUnknownParam("Неизвестный параметр: " + parts[i]);
      }
    }
    return interfacePhysicalParams;
  }

  /**
   * Обрабатывает блок IPv4-конфигурации интерфейса.
   *
   * <p>Метод парсит строку IPv4-адреса и при наличии — параметры времени жизни
   * (строка {@code valid_lft}/{@code preferred_lft}). Все распознанные данные
   * добавляются в {@link InterfaceDto}.</p>
   *
   * <p><b>Гарантии вызова:</b></p>
   * <ul>
   *   <li>{@code lines} — не {@code null}, предварительно нормализованы</li>
   *   <li>{@code currentInterface} — не {@code null}</li>
   *   <li>{@code i} указывает на строку, начинающуюся с ключа {@code inet}</li>
   * </ul>
   *
   * @param i текущий индекс строки с IPv4-адресом
   * @param lines все строки вывода команды
   * @param currentInterface DTO для накопления данных по интерфейсу
   * @return индекс последней обработанной строки (может совпадать с {@code i}
   *         либо указывать на строку с параметрами времени жизни)
   */
  private static int parseInetBlock(int i, String[] lines, InterfaceDto currentInterface) {
    // Парсинг строки IPv4-адреса
    final InterfaceIpv4ConfigDto config = parseIpV4Config(lines[i]);
    // Парсинг параметров времени жизни адреса
    if (i + 1 < lines.length && isLifetimeLine(lines[i + 1])) {
      config.setLifeTimeParams(parseLifeTimeParams(lines[++i]));
    }
    currentInterface.addIpv4(config);
    return i;
  }

  /**
   * Парсит строку конфигурации IPv4-адреса из вывода {@code ip a}.
   *
   * <p>Метод извлекает и заполняет параметры:
   * <ul>
   *   <li>основной IPv4-адрес и префикс (через {@link ParserTokens#IP_ADDR_AND_PREFIX_PATTERN});</li>
   *   <li>широковещательный адрес (broadcast);</li>
   *   <li>область видимости (scope) и связанный {@code netDevice};</li>
   *   <li>флаги состояния IPv4-адреса ({@link IpV4AddressFlag});</li>
   *   <li>нераспознанные параметры (сохраняются для диагностики).</li>
   * </ul>
   *
   * <p>Некорректные значения (например, неверный формат адреса или префикса) не приводят
   * к выбросу исключения — они сохраняются в {@code unknownParams} результирующего DTO.</p>
   *
   * @param line строка с IPv4-конфигурацией (не {@code null}, предварительно нормализована)
   * @return DTO с параметрами IPv4; никогда не {@code null}, может содержать только частичные данные
   * @see InterfaceIpv4ConfigDto
   * @see IpV4Scope
   * @see IpV4AddressFlag
   * @see <a href="https://man7.org/linux/man-pages/man8/ip-address.8.html">man ip-address(8)</a>
   */
  private static InterfaceIpv4ConfigDto parseIpV4Config(String line) {
    Matcher matcher;
    InterfaceIpv4ConfigDto interfaceIpv4Config = new InterfaceIpv4ConfigDto();
    String[] parts = trimOutputLine(line);

    for (int i = 0; i < parts.length; i++) {
      // Парсинг IPv4-адреса и префикса
      if (isEqualIgnoreCase(parts[i], INET) && i + 1 < parts.length) {
        matcher = IP_ADDR_AND_PREFIX_PATTERN.matcher(parts[++i]);
        if (matcher.matches()) {
          // IPv4-адрес
          try {
            interfaceIpv4Config.setAddress(matcher.group(1));
          } catch (IllegalArgumentException e) {
            interfaceIpv4Config.addUnknownParam(e.getMessage());
          }
          // Префикс
          try {
            interfaceIpv4Config.setPrefix(Integer.parseInt(matcher.group(2)));
          } catch (IllegalArgumentException e) {
            interfaceIpv4Config.addUnknownParam(e.getMessage());
          }
        }
      }
      // Парсинг IPv4-broadcast
      else if (isBroadcastKey(parts[i])) {
        matcher = IP_BROADCAST_ADDR_PATTERN.matcher(parts[++i]);
        if (matcher.matches()) {
          try {
            interfaceIpv4Config.setBroadcast(matcher.group(1));
          } catch (IllegalArgumentException e) {
            interfaceIpv4Config.addUnknownParam(e.getMessage());
          }
        }
      }
      // Парсинг области видимости и NET_DEVICE
      else if (isEqualIgnoreCase(parts[i], SCOPE)) {
        // Область видимости
        if (IpV4Scope.isValid(parts[i + 1])) {
          interfaceIpv4Config.setScope(
            IpV4Scope.getIgnoreCase(parts[++i]));
        } else {
          interfaceIpv4Config.addUnknownParam("Неизвестная область видимости (scope): " + parts[++i]);
        }
        // NET_DEVICE
        if (i + 1 < parts.length) {
          interfaceIpv4Config.setNetDevice(parts[++i]);
        }
      }

      else if (IpV4AddressFlag.isValid(parts[i])) {
        interfaceIpv4Config.addIpV4AddressFlag(
            IpV4AddressFlag.getIgnoreCase(parts[i]));
      }
      // Внесение нераспознанных параметров
      else if (!parts[i].isBlank()) {
        interfaceIpv4Config.addUnknownParam("Неизвестный параметр: " + parts[i]);
      }
    }
    return interfaceIpv4Config;
  }

  /**
   * Обрабатывает блок IPv6-конфигурации интерфейса.
   *
   * <p>Метод парсит строку {@code inet6 ...} и при наличии — сразу следующую
   * строку с параметрами времени жизни ({@code valid_lft}/{@code preferred_lft}).
   * Все распознанные данные добавляются в {@link InterfaceDto}.</p>
   *
   * <p><b>Гарантии вызова:</b></p>
   * <ul>
   *   <li>{@code lines} — не {@code null}, предварительно нормализованы;</li>
   *   <li>{@code currentInterface} — не {@code null};</li>
   *   <li>{@code i} указывает на строку, начинающуюся с {@code inet6}.</li>
   * </ul>
   *
   * @param i текущий индекс строки с IPv6-адресом
   * @param lines все строки вывода команды
   * @param currentInterface DTO для накопления данных по интерфейсу
   * @return индекс последней обработанной строки (совпадает с {@code i} либо со строкой lifetime)
   */
  private static int parseInet6Block(int i, String[] lines, InterfaceDto currentInterface) {
    // Парсинг строки IPv6-адреса
    InterfaceIpv6ConfigDto config = parseIpV6Config(lines[i]);
    // Парсинг параметров времени жизни адреса
    if (i + 1 < lines.length && isLifetimeLine(lines[i + 1])) {
      config.setLifeTimeParams(parseLifeTimeParams(lines[++i]));
    }
    currentInterface.addIpv6(config);
    return i;
  }

  /**
   * Парсит строку конфигурации IPv6-адреса из вывода {@code ip a}.
   *
   * <p>Метод извлекает и заполняет параметры:
   * <ul>
   *   <li>основной IPv6-адрес и префикс (через {@code IPV6_ADDR_AND_PREFIX_PATTERN});</li>
   *   <li>область видимости ({@code scope}) — один или несколько значений подряд;</li>
   *   <li>флаги адреса (генерации/маршрутизации);</li>
   *   <li>нераспознанные параметры сохраняются для диагностики.</li>
   * </ul>
   *
   * <p>Вызывается только из {@code parseInet6Block(int, String[], InterfaceDto)}.
   * На вход подаётся уже нормализованная строка. Метод не выбрасывает исключений:
   * все аномалии отражаются в {@code unknownParams} результирующего DTO.</p>
   *
   * @param line строка с IPv6-конфигурацией (не {@code null}, нормализована)
   * @return объект {@link InterfaceIpv6ConfigDto} с параметрами IPv6
   *         (никогда не {@code null}, поля могут быть частично заполнены)
   * @see InterfaceIpv6ConfigDto
   * @see IpV6Scope
   * @see GenerationFlag
   * @see RouteFlag
   * @see <a href="https://man7.org/linux/man-pages/man8/ip-address.8.html">man ip-address(8)</a>
   */
  private static InterfaceIpv6ConfigDto parseIpV6Config(String line) {
    Matcher matcher;
    InterfaceIpv6ConfigDto interfaceIpv6Config = new InterfaceIpv6ConfigDto();
    final String[] parts = trimOutputLine(line);

    for (int i = 0; i < parts.length; i++) {
      // Парсинг IPv6-адреса и префикса
      if (isEqualIgnoreCase(parts[i], INET6_LINE)) {
        matcher = IPV6_ADDR_AND_PREFIX_PATTERN.matcher(parts[++i]);
        if (matcher.matches()) {
          // IPv6-адрес
          try {
            interfaceIpv6Config.setAddress(matcher.group(1));
          } catch (IllegalArgumentException e) {
            interfaceIpv6Config.addUnknownParam(e.getMessage());
          }
          // Префикс
          try {
            interfaceIpv6Config.setPrefix(Integer.parseInt(matcher.group(2)));
          } catch (IllegalArgumentException e) {
            interfaceIpv6Config.addUnknownParam(e.getMessage());
          }
        }
      }
      // Парсинг области видимости
      else if (isEqualIgnoreCase(parts[i], SCOPE)) {
        while (i + 1 < parts.length && IpV6Scope.isValid(parts[i + 1])) {
          interfaceIpv6Config.addScope(
              IpV6Scope.getIgnoreCase(parts[++i]));
        }
      }
      // Парсинг флага генерации
      else if (GenerationFlag.isValid(parts[i])) {
        interfaceIpv6Config.addGenerationFlag(
            GenerationFlag.getIgnoreCase(parts[i]));
      }
      // Парсинг флага маршрутизации
      else if (RouteFlag.isValid(parts[i])) {
        interfaceIpv6Config.addRouteFlag(
            RouteFlag.getIgnoreCase(parts[i]));
      }
      // Внесение нераспознанных параметров
      else {
        interfaceIpv6Config.addUnknownParam("Неизвестный параметр: " + parts[i]);
      }
    }
    return interfaceIpv6Config;
  }

  /**
   * Определяет, содержит ли строка параметры времени жизни IPv4/IPv6-адреса
   * ({@code valid_lft} или {@code preferred_lft}).
   *
   * <p>Используется для распознавания строки, следующей после блока
   * {@code inet}/{@code inet6} в выводе команды {@code ip a}.</p>
   *
   * @param line строка для проверки (не {@code null}, нормализована)
   * @return {@code true}, если строка начинается с {@code valid_lft}
   *         или {@code preferred_lft}; {@code false} в противном случае
   */
  private static boolean isLifetimeLine(String line) {
    return isLineStart(line, VALID_LFT)
        || isLineStart(line, PREFERRED_LFT);
  }

  /**
   * Парсит строку параметров времени жизни адреса ({@code valid_lft} и {@code preferred_lft})
   * из вывода команды {@code ip a}.
   *
   * <p>Извлекает значения времени жизни до недействительности и до устаревания.
   * Пример входной строки: {@code valid_lft forever preferred_lft 42920sec}.</p>
   *
   * <p>Некорректные или неизвестные токены сохраняются в {@code unknownParams} результирующего DTO.
   * Метод не выбрасывает исключений.</p>
   *
   * @param line строка с параметрами времени жизни (не {@code null}, нормализована)
   * @return объект {@link LifeTimeParamsDto} с распознанными параметрами
   *         (никогда не {@code null}, может содержать частичные данные)
   * @see LifeTimeParamsDto
   */
  private static LifeTimeParamsDto parseLifeTimeParams(String line) {
    LifeTimeParamsDto lifeTimeParams = new LifeTimeParamsDto();
    String[] parts = trimOutputLine(line);

    for (int i = 0; i < parts.length; i++) {
      // Парсинг времени жизни адреса до недействительности
      if (parts[i].equalsIgnoreCase(VALID_LFT)) {
        lifeTimeParams.setValidLft(parts[++i]);
      }
      // Парсинг времени жизни адреса до устаревания
      else if (parts[i].equalsIgnoreCase(PREFERRED_LFT)) {
        lifeTimeParams.setPreferredLft(parts[++i]);
      }
      // Внесение нераспознанных параметров
      else if (!parts[i].isBlank()) {
        lifeTimeParams.addUnknownParam("Неизвестный параметр: " + parts[i]);
      }
    }
    return lifeTimeParams;
  }
}
