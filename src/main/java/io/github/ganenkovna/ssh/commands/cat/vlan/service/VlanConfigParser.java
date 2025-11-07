package io.github.ganenkovna.ssh.commands.cat.vlan.service;

import static io.github.ganenkovna.ssh.commands.ip.a.service.ParserUtils.trimOutputStrings;
import io.github.ganenkovna.ssh.commands.cat.vlan.dto.VlanInterfaceDto;
import io.github.ganenkovna.util.ip.VlanValidation;
import io.github.ganenkovna.util.parser.ParsersUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

/**
 * Парсер вывода команды {@code cat /proc/net/vlan/config}.
 *
 * <p>Преобразует вывод системного файла VLAN-конфигурации в список DTO
 * {@link VlanInterfaceDto}. Фильтрует служебные строки и разбирает данные
 * формата {@code <iface> | <vid> | <parent>}.</p>
 *
 * @see VlanInterfaceDto
 * @see ParserTokens
 */
public final class VlanConfigParser {
  private static final String COMMAND = "cat /proc/net/vlan/config";

  /** Запрет инстанцирования. */
  private VlanConfigParser() {
    throw new AssertionError("No instances");
  }

  /**
   * Разбирает вывод команды {@code cat /proc/net/vlan/config} в список DTO.
   *
   * @param vlanConfigOutput текстовый вывод команды; не {@code null}
   * @return неизменяемый список VLAN-интерфейсов; никогда не {@code null}, может быть пустым
   * @throws NullPointerException если {@code vlanConfigOutput == null}
   * @throws IllegalArgumentException если поля в строках пустые/некорректные
   * @throws NumberFormatException если значение VLAN-ID не является числом
   * @implNote Диапазон VLAN-ID проверяется в {@link VlanValidation#validateVlanId(int)}.
   */
  public static List<VlanInterfaceDto> parseOutput(String vlanConfigOutput){
    // Проверка вывода команды на пустую строку
    Objects.requireNonNull(vlanConfigOutput, "Вывод команды `" + COMMAND + "` не может быть null");
    // На случай, если цвет не был отключён на стороне вызова
    vlanConfigOutput = ParsersUtils.stripAnsi(vlanConfigOutput);
    if (vlanConfigOutput.isBlank()) {
      return Collections.emptyList();
    }

    final String[] lines = trimOutputStrings(vlanConfigOutput);
    final List<VlanInterfaceDto> vlanInterfaces = new ArrayList<>();

    for (String line : lines){
      line = line.trim();

      if (line.isBlank() || ParserTokens.SERVICE_LINE.matcher(line).matches()){
        continue;
      }

      Matcher m = ParserTokens.DATA_LINE.matcher(line);
      if (m.matches()){
        vlanInterfaces.add(
            new VlanInterfaceDto(
                m.group(1),
                Integer.parseInt(m.group(2)),
                m.group(3))
        );
      }
    }
    return List.copyOf(vlanInterfaces);
  }
}
