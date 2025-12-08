package io.github.ganenkovna.ssh.commands.cat.vlan.dto;

import static io.github.ganenkovna.util.StringUtils.normalizeForDto;

import io.github.ganenkovna.ssh.commands.cat.vlan.CatVlanConfig;
import io.github.ganenkovna.util.StringUtils;
import io.github.ganenkovna.util.ip.VlanValidation;

/**
 * DTO строки из {@code /proc/net/vlan/config}.
 *
 * <p>Отражает три колонки стандартного вывода:
 * <ul>
 *   <li>{@code vlanInterfaceName} — имя VLAN-интерфейса (например, {@code enp0s8.5});</li>
 *   <li>{@code vlanId} — идентификатор VLAN (IEEE 802.1Q);</li>
 *   <li>{@code interfaceName} — базовый (родительский) интерфейс (например, {@code enp0s8}).</li>
 * </ul></p>
 *
 * <p>Все строковые поля проходят нормализацию через
 * {@link StringUtils#normalizeForDto(String, String)}:
 * не {@code null}, без пустых/пробельных значений. Значение {@code vlanId}
 * валидируется по {@link VlanValidation#validateVlanId(int)}.</p>
 *
 * <p>Гарантии nullability:
 * <ul>
 *   <li>{@code vlanInterfaceName} — никогда не {@code null}, не пустая строка;</li>
 *   <li>{@code vlanId} — всегда в диапазоне {@code 1..4094};</li>
 *   <li>{@code interfaceName} — никогда не {@code null}, не пустая строка.</li>
 * </ul></p>
 *
 * @param vlanInterfaceName полное имя VLAN-интерфейса; не {@code null}, не пустое/пробельное
 * @param vlanId идентификатор VLAN; допустимый диапазон {@code 1..4094}
 * @param interfaceName базовый (родительский) интерфейс; не {@code null}, не пустой/пробельный
 *
 * @throws NullPointerException если любой из строковых аргументов равен {@code null}
 * @throws IllegalArgumentException если строковый аргумент пустой/пробельный
 *                                  или {@code vlanId} вне диапазона {@code 1..4094}
 *
 * @see io.github.ganenkovna.ssh.commands.cat.vlan.service.VlanConfigParser
 * @see io.github.ganenkovna.ssh.commands.cat.vlan.service.ParserTokens
 * @see VlanValidation#validateVlanId(int)
 * @see CatVlanConfig#showVlanConfig(com.jcraft.jsch.Session)
 */
public record VlanInterfaceDto(String vlanInterfaceName, int vlanId, String interfaceName) {
  public VlanInterfaceDto(String vlanInterfaceName, int vlanId, String interfaceName) {
    this.vlanInterfaceName = normalizeForDto(vlanInterfaceName, "Название VLAN интерфейса");
    VlanValidation.validateVlanId(vlanId);
    this.vlanId = vlanId;
    this.interfaceName = normalizeForDto(interfaceName, "Название интерфейса");
  }
}
