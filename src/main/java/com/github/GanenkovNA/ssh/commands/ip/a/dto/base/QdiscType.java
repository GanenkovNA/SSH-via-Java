package com.github.GanenkovNA.ssh.commands.ip.a.dto.base;

import com.github.GanenkovNA.service.StringUtils;

/**
 * Перечисление всех возможных типов дисциплин очередей (qdisc) в Linux.
 *
 * <p>QDisc (Queueing Discipline) определяет алгоритм управления сетевыми пакетами
 * на уровне интерфейса.
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/tc.8.html">Документация tc(8)</a>
 * @see <a href="https://tldp.org/HOWTO/Traffic-Control-HOWTO/components.html">Traffic Control HOWTO</a>
 * @see <a href="https://tldp.org/HOWTO/Traffic-Control-HOWTO/components.html">Classless Queuing Disciplines HOWTO</a>
 */
public enum QdiscType {

  /** Простая FIFO очередь (First-In-First-Out). */
  PFIFO,

  /** * FIFO с ограничением размера в байтах. */
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
   * Проверяет, существует ли указанная дисциплина очереди в перечислении QdiscType.
   *
   * <p>Нормализация имени выполняется через {@link StringUtils#normalizeForEnum}:
   * <ul>
   *   <li>Приведение к верхнему регистру</li>
   *   <li>Замена тире на подчёркивания</li>
   *   <li>Удаление пробелов по краям</li>
   * </ul>
   *
   * @param input название дисциплины (может быть null)
   * @return true если значение существует, false если:
   *         <ul>
   *           <li>input == null</li>
   *           <li>строка пустая</li>
   *           <li>значение не найдено</li>
   *         </ul>
   *
   * @implNote Примеры:
   * <ul>
   *   <li>isValid("htb") → true</li>
   *   <li>isValid(null) → false</li>
   *   <li>isValid("invalid") → false</li>
   * </ul>
   */
  public static boolean isValid(String input) {
    try {
      QdiscType.valueOf(
          StringUtils.normalizeForEnum(input, "", ""));
      return true;
    } catch (NullPointerException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Возвращает элемент перечисления QdiscType по имени (без учёта регистра и с заменой тире).
   *
   * <p><b>Нормализация имени:</b></p>
   * <ol>
   *   <li>Проверка на null и пустую строку</li>
   *   <li>Удаление пробелов по краям</li>
   *   <li>Приведение к верхнему регистру</li>
   *   <li>Замена '-' на '_'</li>
   * </ol>
   *
   * @param input название дисциплины очереди
   * @return соответствующий элемент перечисления
   * @throws NullPointerException если input == null
   * @throws IllegalArgumentException если:
   *         <ul>
   *           <li>строка пустая после trim()</li>
   *           <li>дисциплина с указанным именем не существует</li>
   *         </ul>
   *
   * @implNote Примеры:
   * <ul>
   *   <li>getIgnoreCase("htb") → QdiscType.HTB</li>
   *   <li>getIgnoreCase("fq-codel") → QdiscType.FQ_CODEL</li>
   *   <li>getIgnoreCase(" pfifo_fast ") → QdiscType.PFIFO_FAST</li>
   * </ul>
   *
   * @see StringUtils#normalizeForEnum(String, String, String)
   */
  public static QdiscType getIgnoreCase(String input)
      throws IllegalArgumentException, NullPointerException {
    input = StringUtils.normalizeForEnum(input,
        "Значение qdisc не может быть null",
        "Значение qdisc не может быть пустым");

    try {
      return QdiscType.valueOf(input);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Значение qdisc не найдено: " + input);
    }
  }
}
