package io.github.ganenkovna.util;

import java.util.List;
import java.util.Objects;

/**
 * Утилитарный класс для нормализации и валидации строковых значений.
 *
 * <p>Основные направления:</p>
 * <ul>
 *   <li>{@link #normalizeForDto(String, String, String)} —
 *       нормализация и валидация для DTO (кастомные сообщения);</li>
 *   <li>{@link #normalizeForDto(String, String)} —
 *       упрощённая перегрузка для DTO (дефолтные сообщения);</li>
 *   <li>{@link #normalizeForEnum(String, String, String)} —
 *       нормализация строк для {@code enum}-значений (кастомные сообщения);</li>
 *   <li>{@link #normalizeForEnum(String, String)} —
 *       упрощённая перегрузка для {@code enum} (дефолтные сообщения).</li>
 * </ul>
 *
 * <p>Методы не модифицируют исходную строку и всегда возвращают новый объект.
 * Все возвращаемые строки никогда не {@code null}.</p>
 */
public final class StringUtils {
  private static final String PREFIX = "Значение \"";
  private static final String NULL_SUFFIX = "\" не может быть null";
  private static final String EMPTY_SUFFIX = "\" не может быть пустым/пробельным";

  private StringUtils() {
    throw new AssertionError("No instances");
  }

  /**
   * Нормализует строку для использования в DTO с валидацией
   * и пользовательскими сообщениями об ошибках.
   *
   * <p>Выполняет:</p>
   * <ol>
   *   <li>Проверку, что строка не {@code null}</li>
   *   <li>Удаление пробелов в начале и конце ({@link String#trim()})</li>
   *   <li>Проверку, что строка не пустая после {@code trim()}</li>
   * </ol>
   *
   * @param input строка для нормализации
   * @param nullErrorMessage сообщение для {@link NullPointerException}, если {@code input == null}
   * @param emptyErrorMessage сообщение для {@link IllegalArgumentException},
   *                          если строка пуста после {@code trim()}
   * @return нормализованная строка без ведущих и конечных пробелов
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после {@code trim()}
   * @implNote Используется для валидации и нормализации строковых полей DTO.
   */
  public static String normalizeForDto(
      String input, String nullErrorMessage, String emptyErrorMessage) {
    return requireNonBlank(input, nullErrorMessage, emptyErrorMessage);
  }

  /**
   * Упрощённая версия {@link #normalizeForDto(String, String, String)}
   * с дефолтными сообщениями об ошибках.
   *
   * <p>Сообщения формируются автоматически в формате:
   * {@code "Значение \"<имя_поля>\" не может быть null/пустым/пробельным"}.</p>
   *
   * @param input строка для нормализации
   * @param stringName логическое имя проверяемого значения (используется в сообщениях об ошибках)
   * @return нормализованная строка без ведущих и конечных пробелов
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после {@code trim()}
   *
   * @see #normalizeForDto(String, String, String)
   */
  public static String normalizeForDto(String input, String stringName) {
    return normalizeForDto(
        input,
        PREFIX + stringName + NULL_SUFFIX,
        PREFIX + stringName + EMPTY_SUFFIX);
  }

  /**
   * Нормализует строку для использования в {@code enum}-значениях
   * с пользовательскими сообщениями об ошибках.
   *
   * <p>Выполняет следующие шаги:</p>
   * <ol>
   *   <li>Проверку, что строка не {@code null}</li>
   *   <li>{@link String#trim()}</li>
   *   <li>Проверку пустоты</li>
   *   <li>{@link String#toUpperCase()}</li>
   *   <li>Замену всех {@code '-'} на {@code '_'}</li>
   * </ol>
   *
   * <p>Результат гарантированно подходит для {@link Enum#valueOf(Class, String)}.</p>
   *
   * @param input строка для нормализации
   * @param nullErrorMessage сообщение для {@link NullPointerException}, если {@code input == null}
   * @param emptyErrorMessage сообщение для {@link IllegalArgumentException},
   *                          если строка пуста после {@code trim()}
   * @return нормализованная строка в верхнем регистре с заменой {@code '-'} на {@code '_'}
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после {@code trim()}
   * @see Enum#valueOf(Class, String)
   */
  public static String normalizeForEnum(
      String input, String nullErrorMessage, String emptyErrorMessage) {
    input = requireNonBlank(input, nullErrorMessage, emptyErrorMessage);
    return input
        .toUpperCase()
        .replace('-', '_');
  }

  /**
   * Упрощённая версия {@link #normalizeForEnum(String, String, String)}
   * с дефолтными сообщениями об ошибках.
   *
   * <p>Сообщения формируются автоматически в формате:
   * {@code "Значение \"<имя_поля>\" не может быть null/пустым/пробельным"}.</p>
   *
   * @param input строка для нормализации
   * @param stringName логическое имя проверяемого значения (используется в сообщениях об ошибках)
   * @return нормализованная строка в верхнем регистре с заменой {@code '-'} на {@code '_'}
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после {@code trim()}
   *
   * @see #normalizeForEnum(String, String, String)
   * @see Enum#valueOf(Class, String)
   */
  public static String normalizeForEnum(String input, String stringName) {
    return normalizeForEnum(input,
        PREFIX + stringName + NULL_SUFFIX,
        PREFIX + stringName + EMPTY_SUFFIX);
  }

  /**
   * Проверяет, что строка не {@code null} и не пустая после {@link String#trim()}, с автоматической
   * генерацией сообщений об ошибках.
   *
   * @param input проверяемая строка
   * @param stringName логическое имя проверяемого значения (используется в сообщениях об ошибках)
   * @return нормализованная строка без ведущих/конечных пробелов; никогда не {@code null}
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после {@code trim()}
   * @see #requireNonBlank(String, String, String)
   */
  public static String requireNonBlank(String input, String stringName) {
    return requireNonBlank(input,
        PREFIX + stringName + NULL_SUFFIX,
        PREFIX + stringName + EMPTY_SUFFIX);
  }

  /**
   * Проверяет, что строка не {@code null} и не пустая после {@link String#trim()}.
   *
   * @param input проверяемая строка
   * @param nullMsg сообщение для {@link NullPointerException}
   * @param emptyMsg сообщение для {@link IllegalArgumentException}
   * @return нормализованная строка без ведущих/конечных пробелов
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после trim()
   */
  private static String requireNonBlank(String input, String nullMsg, String emptyMsg) {
    Objects.requireNonNull(input, nullMsg);
    String trimmed = input.trim();
    if (trimmed.isEmpty()) {
      throw new IllegalArgumentException(emptyMsg);
    }
    return trimmed;
  }

  /**
   * Разбивает многострочный вывод команды на чистые строки.
   *
   * <p>Выполняет:
   *   <ol>
   *     <li>Разделение по символу новой строки
   *     <li>Удаление пробелов по краям (trim)
   *     <li>Фильтрацию пустых строк
   *   </ol>
   *
   * @param ipAOutput многострочный вывод команды {@code ip a}
   * @return массив непустых строк без пробельных символов по краям
   * @throws NullPointerException если ipAOutput равен {@code null}
   */
  public static String[] trimOutputStrings(String ipAOutput) {
    String[] lines = ipAOutput.split("\n");
    List<String> listOfLines = new java.util.ArrayList<>();

    for (int i = 0; i < lines.length; i++) {
      lines[i] = lines[i].trim();
      if (!lines[i].isEmpty()) {
        listOfLines.add(lines[i]);
      }
    }
    return listOfLines.toArray(new String[0]);
  }
}
