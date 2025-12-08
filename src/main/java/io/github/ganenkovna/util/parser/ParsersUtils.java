package io.github.ganenkovna.util.parser;

import static io.github.ganenkovna.util.StringUtils.requireNonBlank;
import static io.github.ganenkovna.util.parser.ParsersTokens.ANSI_ESC;

/**
 * Утилитарные методы для парсеров вывода консольных команд.
 *
 * <p>Содержит вспомогательные операции, используемые при обработке текста —
 * например, очистку вывода от управляющих ANSI/ECMA-48 последовательностей.</p>
 *
 * <p>Класс не предназначен для инстанцирования.</p>
 *
 * @see ParsersTokens
 * @see <a href="https://www.ecma-international.org/publications-and-standards/standards/ecma-48/">
 *     ECMA-48 / ISO 6429 — Control Functions for Coded Character Sets</a>
 */
public final class ParsersUtils {
  /** Запрет инстанцирования. */
  private ParsersUtils() {
    throw new AssertionError("No instances");
  }

  /**
   * Утилитарные методы для парсеров вывода консольных команд.
   *
   * <p>Содержит вспомогательные операции, используемые при обработке текста —
   * например, очистку вывода от управляющих ANSI/ECMA-48 последовательностей.</p>
   *
   * <p>Класс не предназначен для инстанцирования.</p>
   *
   * @see ParsersTokens
   * @see <a href="https://www.ecma-international.org/publications-and-standards/standards/ecma-48/">
   *      ECMA-48 / ISO 6429 — Control Functions for Coded Character Sets</a>
   */
  public static String stripAnsi(String s) {
    s = requireNonBlank(s, "Строка");
    return ANSI_ESC.matcher(s).replaceAll("");
  }
}
