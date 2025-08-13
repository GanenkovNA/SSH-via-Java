package com.github.GanenkovNA.ssh.commands.ip.a.service;

import java.util.List;

public final class ParserUtils {

  // Метод для разбиения вывода на строки
  // Заодно удаляет пробелы (trim) и проверяет на пустоту
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

  public static boolean isLineStart(String line, String expected) {
    return line.toLowerCase().startsWith(expected);
  }

  public static String[] trimOutputLine(String line){
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

  public static boolean isEqualIgnoreCase (String input, String expected){
    return input.equalsIgnoreCase(expected);
  }
}
