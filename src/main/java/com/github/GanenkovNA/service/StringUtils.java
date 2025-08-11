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
   * Нормализует строку для поиска в перечислениях (enum).
   *
   * <p><b>Порядок преобразований:</b></p>
   * <ol>
   *   <li>Удаление пробелов в начале и конце строки ({@link String#trim()})</li>
   *   <li>Приведение к верхнему регистру ({@link String#toUpperCase()})</li>
   *   <li>Замена всех тире ('-') на подчёркивания ('_')</li>
   * </ol>
   *
   * @param input строка для нормализации (может быть null)
   * @return нормализованную строку или null, если input == null
   *
   * @implSpec
   * Результат соответствует требованиям для поиска через {@link Enum#valueOf(Class, String)}.
   *
   * @implNote Примеры:
   * <ul>
   *   <li>normalizeForEnum(" eth-port ") → "ETH_PORT"</li>
   *   <li>normalizeForEnum(null) → null</li>
   *   <li>normalizeForEnum("WAN") → "WAN"</li>
   * </ul>
   *
   * @see Enum#valueOf(Class, String)
   */
  public static String normalizeForEnum(String input, String nullErrorMessage, String emptyErrorMessage)
      throws NullPointerException, IllegalArgumentException {
    Objects.requireNonNull(input, nullErrorMessage);
    input = input.trim();
    if (input.isEmpty()) {
      throw new IllegalArgumentException(emptyErrorMessage);
    }
    return input
        .toUpperCase()
        .replace('-', '_');
  }
}
