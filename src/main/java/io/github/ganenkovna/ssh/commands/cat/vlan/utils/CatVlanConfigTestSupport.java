package io.github.ganenkovna.ssh.commands.cat.vlan.utils;

import static io.github.ganenkovna.util.StringUtils.normalizeForDto;

import com.jcraft.jsch.Session;
import io.github.ganenkovna.ssh.commands.cat.vlan.CatVlanConfig;
import io.github.ganenkovna.ssh.commands.cat.vlan.dto.VlanInterfaceDto;
import io.github.ganenkovna.util.ip.VlanValidation;
import java.util.List;
import java.util.Objects;

/**
 * Вспомогательные методы для smoke-тестов вывода {@code cat /proc/net/vlan/config}.
 *
 * <p>Предназначен исключительно для тестовых сценариев — не используется
 * в продуктивном коде. Содержит статические методы для проверки наличия
 * VLAN-интерфейсов на удалённой машине.</p>
 *
 * @see CatVlanConfig
 * @see VlanInterfaceDto
 * @see VlanValidation
 */
public final class CatVlanConfigTestSupport {

  /** Запрет инстанцирования. */
  private CatVlanConfigTestSupport() {
    throw new AssertionError("No instances");
  }

  /**
   * Проверяет наличие VLAN-интерфейса с указанным именем базового интерфейса и VLAN-ID.
   *
   * <p>Вызывает {@link CatVlanConfig#showVlanConfig(Session)} и проходит по списку
   * найденных интерфейсов, сравнивая поля {@code interfaceName} и {@code vlanId}.</p>
   *
   * @param session активная SSH-сессия; не {@code null}
   * @param interfaceName имя базового интерфейса; не {@code null}, не пустое/пробельное
   * @param vlanId идентификатор VLAN (диапазон 1–4094)
   * @return {@code true}, если найден VLAN-интерфейс с заданными параметрами;
   *         {@code false} в противном случае
   *
   * @throws NullPointerException если {@code session == null} или {@code interfaceName == null}
   * @throws IllegalArgumentException если {@code interfaceName} пустой/пробельный
   *                                  или {@code vlanId} вне диапазона {@code 1..4094}
   *
   * @see CatVlanConfig#showVlanConfig(Session)
   * @see VlanInterfaceDto
   * @see VlanValidation#validateVlanId(int)
   */
  public static boolean vlanInterfaceExists(Session session, String interfaceName, int vlanId) {
    Objects.requireNonNull(session, "SSH-сессия не может быть null");
    interfaceName = normalizeForDto(interfaceName, "Название интерфейса");
    VlanValidation.validateVlanId(vlanId);

    List<VlanInterfaceDto> vlanInterfaces = CatVlanConfig.showVlanConfig(session);
    for (VlanInterfaceDto it : vlanInterfaces) {
      if (interfaceName.equalsIgnoreCase(it.interfaceName())
          && vlanId == it.vlanId()) {
        return true;
      }
    }
    return false;
  }
}
