# SSH Inspector

Инструмент для автотестов, получающий структурированные данные по SSH (сетевые интерфейсы, пользователи, системные параметры).

Первоначально предназначен для инспекции сетевого стека (анализ вывода `ip a`),  
но архитектура позволяет расширять функциональность для любых данных, доступных по SSH.

--- 
## Назначение
Проект решает задачу получения сетевой конфигурации удалённого стенда во время тестов. Позволяет:
- Подключаться к хосту по SSH через библиотеку **JSch**;
- Выполнять системные команды и парсить вывод `ip a`;
- Сериализовать полученные данные в DTO-объекты;
- Проверять корректность сетевых параметров (MAC, MTU, IP-адреса и др.);
- Сохранять и читать конфигурации из JSON-файлов (`host_connection_config.json`, `host_interfaces_config.json`).

---  
## Основные возможности
- **SSH-подключение**: создание и управление сессиями через `SshSession`;
- **Парсинг**: преобразование вывода `ip a` в структуру DTO;
- **JSON-сериализация**: чтение/запись конфигураций с помощью `HostConfigIO`;
- **Удобная интеграция** с тестами (см. примеры в `TestBase` и `IpAInterfacesSmokeTest`);
- **Fail-fast-валидация**: раннее выявление ошибок конфигурации с осмысленными сообщениями.
---  
## Архитектура проекта
| Пакет / класс                                                            | Назначение                                                        |
| :----------------------------------------------------------------------- | :---------------------------------------------------------------- |
| `io.github.ganenkovna.ssh.client.SshSession`                             | Управление SSH-сессией (JSch), жизненный цикл сессии              |
| `io.github.ganenkovna.ssh.client.SshChannel`                             | Выполнение одиночной команды в рамках существующей сессии         |
| `io.github.ganenkovna.ssh.commands.ip.a.service.IpA`                     | Высокоуровневый API для вызова `ip a` и парсинга вывода           |
| `io.github.ganenkovna.ssh.commands.ip.a.dto.*`                           | DTO для описания интерфейсов и их параметров                      |
| `io.github.ganenkovna.ssh.host.HostConfigIO`                             | Чтение/запись JSON-конфигов хоста (connection/interfaces)         |
| `io.github.ganenkovna.ssh.host.dto.*`                                    | DTO для конфигов хоста (если используются соответствующие классы) |
| `io.github.ganenkovna.ssh.util.{IpValidation,MacValidation,StringUtils}` | Утилиты нормализации и валидации значений                         |
| `src/main/resources`                                                     | Статические ресурсы / шаблоны (при необходимости)                 |

---

## Конфигурация путей и файлов

Конфигурация не привязана к фиксированным путям — директория и имена файлов задаются строками, что позволяет гибко адаптировать проект под любое окружение.

Пути к конфигам задаются через **отдельные строковые параметры**:
путь к директории (`configDir`) и имена файлов (`connectionConfigName`, `interfacesConfigName`).  
Поддерживаются **относительные** и **абсолютные** пути.

| Поле                   | Тип      | Обязательность | Описание                                                                                        |
| :--------------------- | :------- | :------------- | :---------------------------------------------------------------------------------------------- |
| `configDir`            | `String` | да             | Путь к директории, где хранятся конфиги (например `src/test/resources` или `/etc/test-configs`) |
| `connectionConfigName` | `String` | да             | Имя файла с параметрами подключения (`host_connection_config.json`)                             |
| `interfacesConfigName` | `String` | нет            | Имя файла с конфигурацией интерфейсов (`host_interfaces_config.json`)                           |
### Пример инициализации
```java
String configDir = "src/test/resources";
String connectionConfigName = "host_connection_config.json";
String interfacesConfigName = "host_interfaces_config.json"; // опционально

Path connectionConfigPath = Path.of(configDir, connectionConfigName);
Path interfacesConfigPath = Path.of(configDir, interfacesConfigName);
```

Особенности:
- Все параметры — строки, не допускаются `null` или пустые значения;
- Пути могут быть абсолютными (`/opt/configs`) или относительными (`src/test/resources`);
- При чтении файлов применяется `Path.of(configDir, fileName)` — гарантированная кроссплатформенность;
- Проверка существования и валидация выполняются при чтении (`HostConfigIO`);
- Если `interfacesConfigName` не задан — функциональность сохранения/сравнения интерфейсов отключена.

### Формат `host_connection_config.json` (строго)

Требования к полям:

| Поле       | Тип    | Обязательность | Значение/валидация                                       |
| ---------- | ------ | -------------- | -------------------------------------------------------- |
| `host`     | string | да             | Не пустое. IP (v4/v6) или DNS-имя.                       |
| `port`     | number | нет            | Целое `1..65535`. По умолчанию `22`, если не задан.      |
| `username` | string | да             | Не пустое.                                               |
| `password` | string | да*            | Не пустое. _Если используется парольная аутентификация._ |

Пример валидного JSON:
```json
{
  "host": "192.168.1.10",
  "port": 22,
  "username": "tester",
  "password": "s3cr3t"
}
```

**Правила парсинга и валидации:**
- Пустые/пробельные строки запрещены.
- `port` вне диапазона `1..65535` — ошибка.
- Лишние поля в JSON — ошибка (fail-fast).
- Сообщения об ошибках — предметные (указывать, что именно не так).

---
## Пример использования в тестах

Ниже приведён типичный сценарий получения информации об интерфейсах хоста в тестах.  
Тест опирается на `TestBase`, где инициализируется SSH-сессия по данным из конфига подключения.

```java
import com.jcraft.jsch.Session;
import io.github.ganenkovna.ssh.commands.ip.a.dto.InterfaceDto;
import io.github.ganenkovna.ssh.commands.ip.a.service.IpA;
import io.github.ganenkovna.ssh.host.HostConfigIO;
import java.nio.file.Path;
import java.util.List;

class IpAInterfacesSmokeTest extends TestBase {

  @Test
  void shouldFetchAndValidateInterfaces() throws Exception {
    // 1️⃣ Задаём параметры конфигов (все строки, допускаются относительные пути)
    String configDir = "src/test/resources";
    String connectionConfigName = "host_connection_config.json";
    String interfacesConfigName = "host_interfaces_config.json"; // опционально

    // 2️⃣ Формируем пути
    Path connectionConfigPath = Path.of(configDir, connectionConfigName);
    Path interfacesConfigPath = Path.of(configDir, interfacesConfigName);

    // 3️⃣ Инициализируем SSH-сессию (TestBase берёт данные из connectionConfigPath)
    Session session = currentSession;

    // 4️⃣ Получаем список интерфейсов через `ip a`
    List<InterfaceDto> interfaces = IpA.showInterfaces(session);

    // 5️⃣ Базовые проверки
    assertFalse(interfaces.isEmpty(), "Список интерфейсов не должен быть пустым");
    assertNotNull(interfaces.getFirst().getName(), "Имя интерфейса не может быть null");

    // 6️⃣ (Опционально) сохраняем JSON для отладки, если указан путь конфигурации интерфейсов
    if (interfacesConfigName != null && !interfacesConfigName.isBlank()) {
      HostConfigIO.writeInterfaces(interfaces, interfacesConfigPath);
    }
  }
}
```

Особенности:
- Пути к конфигам задаются строками (`String`), могут быть относительными или абсолютными;
- `interfacesConfigName` используется **только при необходимости сохранения/сравнения интерфейсов**;
- Валидаторы (`IpValidation`, `MacValidation`, `StringUtils`) автоматически применяются при создании DTO;
- Все коллекции внутри DTO — **никогда не `null`**, могут быть пустыми.

> 💡 Для CI можно переопределить пути через JVM-параметры:
> `mvn test -DconfigDir=/opt/configs -DconnectionConfigName=prod_conn.json`

Полные примеры см. в классах:
- `TestBase` — инициализация SSH-сессии;
- `IpAInterfacesSmokeTest` — пример получения и сериализации интерфейсов.
-  `HostInterfacesConfigIoTest` — пример записи/чтения файла с конфигурацией интерфейсов

---
## Сборка и запуск

```bash
mvn clean verify
```

**Требования:**

- Java 21+
- Maven 3.9+
- Доступ по SSH к тестовому хосту

---

## Дополнительные сведения

- DTO и парсеры **не потокобезопасны**;
- Все коллекции **никогда не `null`**, но могут быть пустыми;
- Проект не зависит от конкретного CI и может использоваться как часть любой тестовой инфраструктуры.

---

**Автор:** [GanenkovNA](https://github.com/GanenkovNA)  
**Лицензия:** [MIT](./LICENSE)