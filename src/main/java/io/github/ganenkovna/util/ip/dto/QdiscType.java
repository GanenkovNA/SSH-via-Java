package io.github.ganenkovna.util.ip.dto;

import io.github.ganenkovna.util.StringUtils;

/**
 * Перечисление всех возможных типов дисциплин очередей (qdisc) в Linux.
 *
 * <p>QDisc (Queueing Discipline) определяет алгоритм управления сетевыми пакетами
 * на уровне интерфейса.</p>
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/tc.8.html">Документация tc(8)</a>
 * @see <a href="https://tldp.org/HOWTO/Traffic-Control-HOWTO/components.html">Traffic Control HOWTO</a>
 * @see <a href="https://www.kernel.org/doc/html/latest/networking/index.html">Linux Networking Documentation</a>
 */
public enum QdiscType {

  /** Простая FIFO очередь (First-In-First-Out). */
  PFIFO,

  /** FIFO с ограничением размера в байтах. */
  BFIFO,

  /**
   * Стандартная очередь Linux с 3 приоритетными бандами.
   *
   * <p>Используется по умолчанию на сетевых интерфейсах.
   */
  PFIFO_FAST,

  /** Token Bucket Filter (ограничение пропускной способности). */
  TBF,

  /** Stochastic Fair Queueing (стохастическое честное распределение). */
  SFQ,

  /** Random Early Detection (рандомизированное раннее обнаружение перегрузки). */
  RED,

  /** Fair Queueing (честное распределение между потоками). */
  FQ,

  /** Fair Queueing with Controlled Delay (оптимизация задержек). */
  FQ_CODEL,

  /** Common Applications Kept Enhanced (продвинутый FQ_CODEL). */
  CAKE,

  /** Отсутствие очереди (используется для loopback). */
  NOQUEUE,

  /** Пустая дисциплина (для отключенных интерфейсов). */
  NOOP,

  /** Controlled Delay (только алгоритм контроля задержки). */
  CODEL,

  /** Временная остановка передачи пакетов. */
  PLUG,

  /** Generic Random Early Detection. */
  GRED,

  /** Differentiated Services Marker (маркировка DSCP). */
  DSMARK,

  /** Quick Fair Queueing (альтернатива SFQ/FQ). */
  QFQ,

  /** Hierarchy Token Bucket (иерархическое ограничение скорости). */
  HTB,

  /** Class Based Queueing (устаревший). */
  CBQ,

  /** Приоритетная очередь. */
  PRIO,

  /** Multi-Queue (для многоядерных систем). */
  MQ,

  /** Deficit Round Robin. */
  DRR,

  /** Hierarchical Fair Service Curve. */
  HFSC,

  /** ATM-эмуляция. */
  ATM,

  /** Network Emulator (эмуляция сетевых условий). */
  NETEM,

  /** Traffic Equalizer (балансировка). */
  TEQL,

  /** Входящая очередь (для фильтрации). */
  INGRESS,

  /** Улучшенная версия INGRESS с eBPF. */
  CLSACT,

  /** Multi-Queue Priority. */
  MQPRIO,

  /** Earliest TxTime First (для TSN). */
  ETF,

  /** Time Aware Priority Shaper (для TSN). */
  TAPRIO;

  /**
   * Проверяет существование указанной дисциплины очереди.
   *
   * <p>Перед проверкой выполняется нормализация
   * в {@link StringUtils#normalizeForEnum(String, String)}.</p>
   *
   * <p>Возвращает {@code true}, если после нормализации значение найдено;
   * возвращает {@code false}, если {@code input == null}, строка пустая после trim()
   * или такой дисциплины очереди не существует.</p>
   *
   * @param input название дисциплины очереди (может быть {@code null})
   * @return {@code true}, если дисциплина существует; иначе {@code false}
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static boolean isValid(String input) {
    try {
      QdiscType.valueOf(
          StringUtils.normalizeForEnum(input, "qdisc"));
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
   * @param input название дисциплины очереди; не может быть {@code null} или пустым
   * @return соответствующий элемент перечисления
   * @throws NullPointerException если {@code input == null}
   * @throws IllegalArgumentException если строка пуста после trim() или значение не найдено
   * @see StringUtils#normalizeForEnum(String, String)
   */
  public static QdiscType getIgnoreCase(String input) {
    input = StringUtils.normalizeForEnum(input, "qdisc");
    try {
      return QdiscType.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Значение qdisc не найдено: " + input);
    }
  }
}
