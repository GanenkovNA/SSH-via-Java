package io.github.ganenkovna.ssh.commands.cat.vlan.service;

import java.util.regex.Pattern;

/**
 * Набор регулярных выражений для разбора вывода {@code cat /proc/net/vlan/config}.
 *
 * <p>Используется парсером {@link VlanConfigParser} для фильтрации служебных строк
 * и выделения данных VLAN-интерфейсов.</p>
 *
 * <p>Формат вывода ядра Linux (пример):</p>
 * <pre>
 * VLAN Dev name | VLAN ID
 * Name-Type: VLAN_NAME_TYPE_RAW_PLUS_VID_NO_PAD
 * enp0s8.5      | 5 | enp0s8
 * </pre>
 *
 * @see io.github.ganenkovna.ssh.commands.cat.vlan.dto.VlanInterfaceDto
 * @see VlanConfigParser
 * @see <a href="https://ieeexplore.ieee.org/document/7428776">IEEE 802.1Q-2018</a>
 */
public final class ParserTokens {
  /** Запрет инстанцирования. */
  private ParserTokens() {
    throw new AssertionError("No instances");
  }

  /**
   * Регулярное выражение для обнаружения служебных строк.
   *
   * <p>Игнорирует заголовок {@code VLAN Dev name | VLAN ID}
   * и строку с типом именования {@code Name-Type: ...}.
   * Сопоставление без учёта регистра.</p>
   *
   * <p>Пример совпадений:</p>
   * <pre>
   * VLAN Dev name | VLAN ID         → true
   * Name-Type: VLAN_NAME_TYPE_RAW_PLUS_VID_NO_PAD → true
   * enp0s8.5      | 5 | enp0s8      → false
   * </pre>
   */
  public static final Pattern SERVICE_LINE = Pattern.compile(
      "^(?i)(VLAN\\s+Dev\\s+name|Name-Type:).*");

  /**
   * Регулярное выражение для строки с данными VLAN-интерфейса.
   *
   * <p>Ожидаемый формат: {@code <iface> | <vid> | <parent>}.
   * Пробелы вокруг разделителей допускаются.</p>
   *
   * <p>Группы:</p>
   * <ul>
   *   <li>{@code group(1)} — полное имя VLAN-интерфейса (например, {@code enp0s8.5@enp0s8});</li>
   *   <li>{@code group(2)} — идентификатор VLAN (число в диапазоне 1–4094);</li>
   *   <li>{@code group(3)} — имя базового интерфейса (например, {@code enp0s8}).</li>
   * </ul></p>
   *
   * <p>Пример совпадений:</p>
   * <pre>
   * enp0s8.5      | 5 | enp0s8   → true
   * VLAN Dev name | VLAN ID      → false
   * </pre>
   */
  public static final Pattern DATA_LINE = Pattern.compile(
      "^\\s*([^|\\s]+)\\s*\\|\\s*(\\d{1,4})\\s*\\|\\s*([^|\\s]+)\\s*$"
  );
}
