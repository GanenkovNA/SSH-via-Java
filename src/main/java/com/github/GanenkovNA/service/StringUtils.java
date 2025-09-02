package com.github.GanenkovNA.service;

import java.util.Objects;

/** Утилитарный класс для работы со строками. */
public class StringUtils {

  /**
   * Нормализует строку для использования в DTO с валидацией.
   *
   * <p><b>Выполняет следующие действия:</b></p>
   * <ol>
   *   <li>Проверяет что строка не null</li>
   *   <li>Удаляет пробелы в начале и конце строки</li>
   *   <li>Проверяет что строка не пустая после trim()</li>
   * </ol>
   *
   * @param input строка для нормализации
   * @param nullErrorMessage сообщение об ошибке для null-значения
   * @param emptyErrorMessage сообщение об ошибке для пустой строки
   * @return нормализованную строку (без ведущих/конечных пробелов)
   * @throws NullPointerException если input == null
   * @throws IllegalArgumentException если строка пустая после trim()
   *
   * @implNote Пример использования:
   * <pre>{@code
   * normalizeForDto(" value ", "Name cannot be null", "Name cannot be empty")
   * }</pre>
   */
  public static String normalizeForDto(String input, String nullErrorMessage, String emptyErrorMessage)
      throws NullPointerException, IllegalArgumentException {
    Objects.requireNonNull(input, nullErrorMessage);
    input = input.trim();
    if (input.isEmpty()) {
      throw new IllegalArgumentException(emptyErrorMessage);
    }
    return input;
  }

    /**
     * Нормализует строку для поиска значений в перечислениях (enum).
     *
     * <p><b>Выполняет следующие шаги:</b></p>
     * <ol>
     *   <li>Проверяет, что строка не {@code null}</li>
     *   <li>Удаляет пробелы в начале и конце ({@link String#trim()})</li>
     *   <li>Проверяет, что строка не пустая после trim()</li>
     *   <li>Приводит к верхнему регистру ({@link String#toUpperCase()})</li>
     *   <li>Заменяет все тире ({@code -}) на подчёркивания ({@code _})</li>
     * </ol>
     *
     * <p>Результат гарантированно подходит для использования с
     * {@link Enum#valueOf(Class, String)}.</p>
     *
     * <h3>Перегрузки:</h3>
     * <ul>
     *   <li>{@link #normalizeForEnum(String, String, String)} — позволяет задать собственные сообщения для исключений</li>
     *   <li>{@link #normalizeForEnum(String)} — использует дефолтные сообщения для ошибок</li>
     * </ul>
     *
     * @param input строка для нормализации
     * @param nullErrorMessage сообщение для {@link NullPointerException}, если input == null
     * @param emptyErrorMessage сообщение для {@link IllegalArgumentException}, если строка пуста
     * @return нормализованная строка
     * @throws NullPointerException если input == null
     * @throws IllegalArgumentException если строка пустая после trim()
     *
     * @implNote Примеры:
     * <pre>{@code
     * normalizeForEnum(" link-local ", "null запрещён", "пусто запрещено");
     * // → "LINK_LOCAL"
     *
     * normalizeForEnum("global"); // перегрузка с дефолтными сообщениями
     * // → "GLOBAL"
     * }</pre>
     */
  public static String normalizeForEnum(String input, String nullErrorMessage, String emptyErrorMessage) {
      Objects.requireNonNull(input, nullErrorMessage);
      input = input.trim();
      if (input.isEmpty()) {
          throw new IllegalArgumentException(emptyErrorMessage);
      }
      return input
              .toUpperCase()
              .replace('-', '_');
  }

    /**
     * Упрощённая версия {@link #normalizeForEnum(String, String, String)}.
     * <p>Выполняет те же шаги нормализации (trim → проверка пустоты → upper → '-'→'_').</p>
     * <p>Использует дефолтные сообщения об ошибках.</p>
     *
     * @param input строка для нормализации (не {@code null})
     * @return нормализованная строка
     * @throws NullPointerException если {@code input == null}
     * @throws IllegalArgumentException если строка пуста после trim()
     * @see #normalizeForEnum(String, String, String)
     * @see Enum#valueOf(Class, String)
     */
  public static String normalizeForEnum(String input) {
      Objects.requireNonNull(input, "Строка не может быть null");
      input = input.trim();
      if (input.isEmpty()) {
          throw new IllegalArgumentException("Строка не может быть пустой");
      }
      return input
              .toUpperCase()
              .replace('-', '_');
  }
}
