package com.github.GanenkovNA.ssh.commands.ip.a.service;

import java.util.List;

/**
 * Утилитарный класс для обработки вывода команды {@code ip a}.
 *
 * <p>Содержит методы для:
 *  <ul>
 *     <li>Разбивки многострочного вывода на отдельные строки
 *     <li>Парсинга отдельных строк на токены
 *     <li>Проверки строк на соответствие ожидаемым шаблонам
 *  </ul>
 *
 * <p>Все методы класса являются статическими и потокобезопасными.
 *
 * @see IpAParser Основной парсер, использующий эти утилиты
 */
public final class ParserUtils {

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

  /**
   * Проверяет, начинается ли строка с указанного префикса (без учёта регистра).
   *
   * @param line проверяемая строка
   * @param expected ожидаемый префикс
   * @return {@code true} если строка начинается с префикса (игнорируя регистр),
   *         {@code false} в противном случае
   * @throws NullPointerException если любой из аргументов равен {@code null}
   */
  public static boolean isLineStart(String line, String expected) {
    return line.toLowerCase().startsWith(expected);
  }

  /**
   * Разбивает строку на токены по пробелам и удаляет пустые элементы.
   *
   * <p>Пример:
   *   <pre>{@code
   *   "  eth0:  mtu 1500  " → ["eth0:", "mtu", "1500"]
   *   }</pre>
   *
   * @param line строка для обработки
   * @return массив непустых токенов
   * @throws NullPointerException если line равен {@code null}
   */
  public static String[] trimOutputLine(String line) {
    String[] parts = line.split("\\s+");
    List<String> listOfParts = new java.util.ArrayList<>();

    for (int i = 0; i < parts.length; i++) {
      parts[i] = parts[i].trim();
      if (!parts[i].isBlank()) {
        listOfParts.add(parts[i]);
      }
    }
    return listOfParts.toArray(new String[0]);
  }

  /**
   * Сравнивает строки без учёта регистра.
   *
   * @param input проверяемая строка
   * @param expected ожидаемое значение
   * @return {@code true} если строки равны (игнорируя регистр),
   *         {@code false} в противном случае
   * @throws NullPointerException если любой из аргументов равен {@code null}
   */
  public static boolean isEqualIgnoreCase(String input, String expected) {
    return input.equalsIgnoreCase(expected);
  }
}
