package com.github.GanenkovNA.ssh.commands.ip.a.dto.base;

import java.util.Arrays;

/**
 * Перечисление всех возможных типов дисциплин очередей (qdisc) в Linux.
 *
 * <p>QDisc (Queueing Discipline) определяет алгоритм управления сетевыми пакетами
 * на уровне интерфейса.
 *
 * @see <a href="https://man7.org/linux/man-pages/man8/tc.8.html">man tc</a>
 */
public enum QdiscType {

  /** Простая FIFO очередь (First-In-First-Out). */
  PFIFO,

  /** FIFO с ограничением размера в байтах. */
  BFIFO,

  /** Стандартная очередь Linux с 3 приоритетными бандами. */
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

  public static boolean contains(String input) {
    if (input == null) {
      return false;
    }
    return Arrays.stream(values())
        .anyMatch(e -> e.name().equalsIgnoreCase(input));
  }

  public static QdiscType getIgnoreCase(String input){
    return QdiscType.valueOf(input.toUpperCase());
  }
}
