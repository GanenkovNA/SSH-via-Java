package com.github.GanenkovNA.ssh.commands.ip.a.dto.base;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Базовые параметры сетевого интерфейса.
 *
 * <p>Содержит информацию, полученную из первой строки вывода для каждого интерфейса в `ip a`.
 */
@Data
public class InterfaceBaseConfigDto {

  /**Порядковый номер интерфейса в выводе. */
  private Integer index;

  /**Название интерфейса (например, "eth0", "wlan0"). */
  private String name;

  /**
   * Список флагов интерфейса.
   *
   * @see InterfaceFlag
   */
  private List<InterfaceFlag> flags = new ArrayList<>();

  /**
   * MTU (Maximum Transmission Unit) в байтах.
   *
   * <p>Допустимый диапазон: 64-9000.</p>
   */
  private Integer mtu;

  /**
   * Дисциплина очереди (qdisc), применяемая к интерфейсу.
   *
   * @see QdiscType
   */
  private QdiscType qdiscType;

  /**
   * Текущий статус интерфейса.
   *
   * @see InterfaceState
   */
  private InterfaceState state;

  /**Группа интерфейса (если применимо). */
  private String group;

  /**
   * Длина очереди пакетов (qlen).
   *
   * <p>Если null — очередь не настроена.</p>
   */
  private Integer qlen;

  /** Список нераспознанных параметров. */
  private List<String> unknownParams = new ArrayList<>();

  /** Добавление флага в список. */
  public void addFlag(InterfaceFlag flag) {
    flags.add(flag);
  }

  /** Добавление нераспознанного параметра в список. */
  public void addUnknownParam(String unknownParam) {
    unknownParams.add(unknownParam);
  }
}
