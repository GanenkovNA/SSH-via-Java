package io.github.ganenkovna.util.ip.dto;

import io.github.ganenkovna.util.StringUtils;

/**
 * STP-состояние порта сетевого моста (Linux bridge).
 *
 * <p>Соответствует полю {@code state} в выводе команды {@code bridge link}.
 * Отражает текущее состояние порта в рамках алгоритма
 * {@abbr Spanning Tree Protocol STP} (IEEE 802.1D) либо его производных.</p>
 *
 * <p>Перед проверкой и преобразованием входного строкового значения
 * выполняется нормализация в
 * {@link StringUtils#normalizeForEnum(String, String)}.</p>
 *
 * <p>Некоторые значения могут появляться только в диагностических
 * или аварийных ситуациях (например, {@link #BROKEN}),
 * либо использоваться как fallback при разборе неизвестных данных
 * ({@link #UNKNOWN}).</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/bridge.8.html">bridge(8)</a>
 * @see <a href="https://man7.org/linux/man-pages/man8/bridge-link.8.html">bridge link</a>
 * @see <a href="https://en.wikipedia.org/wiki/Spanning_Tree_Protocol">Spanning Tree Protocol (IEEE 802.1D)</a>
 */
public enum StpPortState {

  /**
   * Порт отключён и не участвует в передаче трафика.
   *
   * <p>Может означать административное отключение порта
   * либо состояние, при котором порт не задействован
   * в STP-алгоритме.</p>
   */
  DISABLED,

  /**
   * Порт находится в состоянии прослушивания.
   *
   * <p>Порт принимает STP BPDU-пакеты и участвует
   * в вычислении топологии, но не пересылает пользовательский трафик.</p>
   */
  LISTENING,

  /**
   * Порт находится в состоянии обучения.
   *
   * <p>Порт начинает изучать MAC-адреса,
   * но всё ещё не пересылает пользовательский трафик.</p>
   */
  LEARNING,

  /**
   * Порт находится в рабочем состоянии и пересылает трафик.
   *
   * <p>Это нормальное активное состояние порта,
   * при котором разрешена передача пользовательских данных
   * и участие в STP.</p>
   */
  FORWARDING,

  /**
   * Порт заблокирован алгоритмом STP.
   *
   * <p>Порт не пересылает пользовательский трафик,
   * но продолжает принимать BPDU-пакеты для предотвращения петель
   * в топологии сети.</p>
   */
  BLOCKING,

  /**
   * Порт находится в некорректном или аварийном состоянии.
   *
   * <p>Может указывать на внутреннюю ошибку,
   * несогласованность конфигурации или аппаратную проблему.</p>
   *
   * <p>Как правило, не является штатным STP-состоянием
   * и используется для диагностики.</p>
   */
  BROKEN,

  /**
   * Неизвестное или неподдерживаемое состояние порта.
   *
   * <p>Используется как fallback-значение при разборе данных,
   * если полученное состояние не удалось сопоставить
   * ни с одним известным STP-состоянием.</p>
   */
  UNKNOWN;

  /**
   * Проверяет существование указанного STP-состояния порта.
   *
   * <p>Перед проверкой выполняется нормализация
   * в {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * <p>Возвращает {@code true}, если после нормализации значение найдено;
   * возвращает {@code false}, если {@code input == null}, строка пустая после trim()
   * или такого состояния не существует.</p>
   *
   * @param input название состояния (может быть {@code null})
   * @return {@code true}, если состояние существует; иначе {@code false}
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static boolean isValid(String input) {
    try {
      StpPortState.valueOf(
          StringUtils.normalizeForEnum(input, "STP-состояние порта"));
      return true;
    } catch (NullPointerException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Возвращает элемент перечисления по имени, игнорируя регистр и дефисы.
   *
   * <p>Нормализация идентична {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * @param input название STP-состояния порта; не может быть {@code null} или пустым
   * @return соответствующий элемент перечисления
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после trim() или значение не найдено
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static StpPortState getIgnoreCase(String input) {
    input = StringUtils.normalizeForEnum(input, "STP-состояние порта");

    try {
      return StpPortState.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Значение состояния интерфейса не найдено: " + input);
    }
  }
}
