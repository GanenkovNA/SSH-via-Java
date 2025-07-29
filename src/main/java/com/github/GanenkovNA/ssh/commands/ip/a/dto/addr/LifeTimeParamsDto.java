package com.github.GanenkovNA.ssh.commands.ip.a.dto.addr;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Параметры времени жизни IP-адреса (IPv4/IPv6).
 *
 * <p>Примеры значений:
 * <ul>
 *   <li>`valid_lft forever preferred_lft forever`
 *   <li>`valid_lft 86sec preferred_lft 0sec`
 * </ul>
 */
@Data
public class LifeTimeParamsDto {

  /** Время жизни адреса до недействительности. */
  private String validLft;

  /** Время жизни адреса до "устаревания". */
  private String preferredLft;

  /** Список нераспознанных параметров. */
  private List<String> unknownParams = new ArrayList<>();

  /** Добавление нераспознанного параметра в список. */
  public void addUnknownParam(String unknownParam) {
    unknownParams.add(unknownParam);
  }
}
