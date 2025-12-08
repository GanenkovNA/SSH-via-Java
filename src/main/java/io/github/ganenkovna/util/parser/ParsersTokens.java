package io.github.ganenkovna.util.parser;

import java.util.regex.Pattern;

/**
 * Общий набор предкомпилированных токенов и шаблонов,
 * используемых различными парсерами вывода команд.
 *
 * <p>Класс служит единым хранилищем для регулярных выражений и
 * других повторно используемых конструкций при разборе текстовых данных
 * (например, вывода утилит {@code ip}, {@code ifconfig}, {@code cat /proc/*} и т.п.).</p>
 *
 * <p>На текущий момент содержит только шаблон {@link #ANSI_ESC},
 * предназначенный для удаления управляющих ANSI/ECMA-48 последовательностей
 * из вывода терминала.</p>
 *
 * <p>Класс не предназначен для инстанцирования.</p>
 *
 * @see <a href="https://www.ecma-international.org/publications-and-standards/standards/ecma-48/">
 *     ECMA-48 / ISO 6429 — Control Functions for Coded Character Sets</a>
 */
public final class ParsersTokens {
  /** Запрет инстанцирования. */
  private ParsersTokens() {
    throw new AssertionError("No instances");
  }

  /**
   * Паттерн для обнаружения ANSI/ECMA-48 управляющих последовательностей
   * (CSI — Control Sequence Introducer), начинающихся с {@code ESC[}.
   *
   * <p>Покрывает большинство escape-команд, используемых для изменения цвета,
   * позиционирования курсора и очистки экрана.</p>
   *
   * <p>Не охватывает OSC, DCS и двухсимвольные ESC-команды.</p>
   *
   * @see <a href="https://www.ecma-international.org/publications-and-standards/standards/ecma-48/">
   *     ECMA-48 — описание управляющих последовательностей</a>
   */
  public static final Pattern ANSI_ESC =
      Pattern.compile("\u001B\\[[0-?]*[ -/]*[@-~]");
}
