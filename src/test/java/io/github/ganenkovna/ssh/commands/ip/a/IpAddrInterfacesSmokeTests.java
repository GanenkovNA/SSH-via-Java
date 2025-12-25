package io.github.ganenkovna.ssh.commands.ip.a;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.ganenkovna.ssh.TestBase;
import io.github.ganenkovna.ssh.commands.ip.a.dto.AddrInfoDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.Inet6AddrInfoDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.InetAddrInfoDto;
import io.github.ganenkovna.ssh.commands.ip.a.dto.IpAddrDto;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Smoke-тест для проверки корректности выполнения команды {@code ip a}
 * и парсинга списка сетевых интерфейсов.
 *
 * <p>Тест проверяет базовые инварианты результата выполнения команды:
 * <ul>
 *   <li>команда {@code ip a} успешно выполняется по SSH;</li>
 *   <li>список интерфейсов не {@code null} и содержит как минимум один элемент;</li>
 *   <li>в DTO интерфейсов отсутствуют нераспознанные параметры;</li>
 *   <li>адреса {@code inet} и {@code inet6} корректно распознаны и приведены
 *       к соответствующим DTO.</li>
 * </ul>
 * </p>
 *
 * <p>Тест не валидирует конкретные значения полей (MTU, IP-адреса, флаги),
 * а служит ранним индикатором ошибок парсинга, изменений формата вывода
 * {@code ip a} или появления новых необработанных полей.</p>
 *
 * <p>Используется как smoke / sanity-проверка перед выполнением
 * более детальных интеграционных тестов.</p>
 *
 * @see IpAddr
 * @see IpAddr#showInterfaces(com.jcraft.jsch.Session)
 * @see io.github.ganenkovna.ssh.commands.ip.a.dto.IpAddrDto
 * @see InetAddrInfoDto
 * @see Inet6AddrInfoDto
 */
public class IpAddrInterfacesSmokeTests extends TestBase {

  /**
   * Список сетевых интерфейсов, полученных из вывода команды {@code ip a}
   * для текущей SSH-сессии.
   *
   * <p>Инициализируется один раз при загрузке класса
   * с помощью {@link IpAddr#showInterfaces(com.jcraft.jsch.Session)}
   * на основе {@link #currentSession}.</p>
   *
   * <p>Никогда не {@code null}, может быть пустым,
   * если парсер не распознал ни одного интерфейса.</p>
   *
   * @see IpAddr#showInterfaces(com.jcraft.jsch.Session)
   * @see io.github.ganenkovna.ssh.commands.ip.a.dto.IpAddrDto
   */
  private static final List<IpAddrDto> interfaces = IpAddr.showInterfaces(currentSession);

  private static final ObjectMapper mapper = new ObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT);

  /**
   * Проверяет базовые инварианты списка сетевых интерфейсов
   * перед выполнением каждого тестового метода.
   *
   * <p>Убеждается, что список {@link #interfaces}:
   * <ul>
   *   <li>успешно инициализирован;</li>
   *   <li>содержит как минимум один интерфейс.</li>
   * </ul>
   * </p>
   *
   * <p>Падение на этом этапе означает критическую ошибку выполнения
   * команды {@code ip a} или сбой парсинга результата.</p>
   *
   * @throws AssertionError если список {@link #interfaces} равен {@code null}
   *                        или не содержит элементов
   * @see org.junit.jupiter.api.BeforeEach
   */
  @BeforeEach
  public void checkListOfInterfaces() {
    assertNotNull(interfaces, "Список интерфейсов равен null");
    assertFalse(interfaces.isEmpty(), "Список интерфейсов пуст");
  }

  /**
   * Выполняет сериализацию результата выполнения {@code ip a}
   * в формат JSON и выводит его в стандартный вывод.
   *
   * <p>Используется в диагностических целях для визуальной проверки
   * структуры DTO и отладки проблем парсинга.</p>
   *
   * @throws JsonProcessingException если сериализация DTO завершилась ошибкой
   */
  @Test
  public void shouldExecIpAddrAndOutput() throws JsonProcessingException {
    System.out.println(mapper.writeValueAsString(interfaces));
  }

  /**
   * Проверяет отсутствие нераспознанных параметров
   * в DTO сетевых интерфейсов и их IP-адресов.
   *
   * <p>Тест проходит по всем интерфейсам и их адресам и убеждается,
   * что все поля, присутствующие в выводе {@code ip a},
   * были корректно распознаны парсером.</p>
   *
   * <p>Наличие элементов в {@code unparsedParams} означает:
   * <ul>
   *   <li>изменение формата вывода {@code ip a};</li>
   *   <li>неподдерживаемое поле;</li>
   *   <li>ошибку или неполноту парсинга.</li>
   * </ul>
   * </p>
   */
  @Test
  public void shouldNotContainsUnparsedParams() {
    SoftAssertions softly = new SoftAssertions();

    for(IpAddrDto iface : interfaces) {
      final String ifaceId = identifyInterface(iface);
      if(ifaceId == null) {
        softly.fail("Невозможно идентифицировать интерфейс: `ifname` и `ifindex` пустые/null");
        continue;
      }

      softly.assertThat(iface.unparsedParams())
          .as("Нераспознанные параметры в интерфейсе `%s`", ifaceId)
          .isEmpty();

      List<AddrInfoDto> addresses = iface.addrInfo();
      for (AddrInfoDto addr : addresses) {
        if (addr instanceof InetAddrInfoDto ip4) {
          softly.assertThat(ip4.unparsedParams())
              .as("Нераспознанные параметры в интерфейсе `%s`", ifaceId)
              .isEmpty();
        } else if (addr instanceof Inet6AddrInfoDto ip6) {
          softly.assertThat(ip6.unparsedParams())
              .as("Нераспознанные параметры в интерфейсе `%s`", ifaceId)
              .isEmpty();
        } else {
          softly.fail("Неизвестный класс адреса в интерфейсе `%s`", ifaceId);
        }
      }

      softly.assertAll();
    }
  }

  /**
   * Формирует человекочитаемый идентификатор сетевого интерфейса
   * для использования в сообщениях об ошибках.
   *
   * <p>Приоритет:
   * <ol>
   *   <li>{@code ifname}, если задан и не пуст;</li>
   *   <li>{@code ifindex}, если имя отсутствует.</li>
   * </ol>
   * </p>
   *
   * @param iface DTO сетевого интерфейса
   * @return строковый идентификатор интерфейса
   *         или {@code null}, если идентификация невозможна
   */
  private static String identifyInterface(IpAddrDto iface){
    String ifname = iface.ifname();
    if (ifname != null && !ifname.isBlank()) {
      return ifname;
    }

    String ifindex = String.valueOf(iface.ifindex());
    if (ifindex != null && !ifindex.isBlank()){
      return "Индекс: " + ifindex;
    }

    return null;
  }
}
