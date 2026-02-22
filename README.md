# NOMA Orchestrator

Микросервис-оркестратор банковской системы **VTB MSA**, реализованный на **Java 17 + Spring Boot**. Отвечает за управление счетами клиентов, проведение транзакций, оформление страховых полисов и генерацию страховых предложений. Координирует взаимодействие между несколькими внешними сервисами через REST и Kafka.

---

## Оглавление

- [Архитектура](#архитектура)
- [Модули проекта](#модули-проекта)
- [API-эндпоинты](#api-эндпоинты)
  - [Account API](#account-api)
  - [Insurance API](#insurance-api)
- [Бизнес-процессы](#бизнес-процессы)
  - [Создание аккаунта](#1-создание-аккаунта)
  - [Перевод средств (транзакция)](#2-перевод-средств-транзакция)
  - [Создание страхового полиса](#3-создание-страхового-полиса-жизни)
  - [Генерация страховых предложений](#4-генерация-страховых-предложений)
  - [Событие обновления аккаунта](#5-событие-обновления-аккаунта-kafka)
- [Внешние интеграции](#внешние-интеграции)
  - [Complex Check Service](#1-complex-check-service-комплексная-проверка)
  - [Fraud Service](#2-fraud-service-проверка-на-мошенничество)
  - [Risk Assessment Service](#3-risk-assessment-service-оценка-рисков)
  - [Info Service (Kafka)](#4-info-service-kafka)
- [Kafka-топики](#kafka-топики)
- [База данных](#база-данных)
- [Мониторинг и метрики](#мониторинг-и-метрики)
- [Retry-политика](#retry-политика)
- [Инфраструктура (Docker Compose)](#инфраструктура-docker-compose)
- [Сборка и запуск](#сборка-и-запуск)
- [Конфигурация](#конфигурация)

---

## Архитектура

```
┌───────────────────────────────────────────────────��─────────────────────────┐
│                            NOMA Orchestrator                                │
│                          (Spring Boot, port 8888)                           │
│                                                                             │
│  ┌──────────────────┐    ┌───────────────────────┐                          │
│  │  AccountApi       │    │  InsuranceApi          │                         │
│  │  Controller       │    │  Controller            │                         │
│  └────────┬─────────┘    └──────────┬────────────┘                          │
│           │                         │                                        │
│  ┌────────▼─────────┐    ┌──────────▼────────────┐                          │
│  │  AccountService   │    │  InsuranceLifeService  │                         │
│  └──┬──┬──┬──┬──┬───┘    └──┬─��────┬─────────────┘                          │
│     │  │  │  │  │           │      │                                         │
│     │  │  │  │  │           │      │                                         │
└─────┼──┼──┼──┼──┼───────────┼──────┼─────────────────────────────────────────┘
      │  │  │  │  │           │      │
      │  │  │  │  │           │      ▼
      │  │  │  │  │           │  ┌──────────────────┐
      │  │  │  │  │           │  │  Risk Service     │
      │  │  │  │  │           │  │  (REST, :8089)    │
      │  │  │  │  │           │  └──────────────────┘
      │  │  │  │  │           │
      │  │  │  │  │           ▼
      │  │  │  │  │   ┌──────────────────────┐
      │  │  │  │  └──►│  Complex Check        │
      │  │  │  │      │  Service (REST, :8081)│
      │  │  │  │      └────────���─────────────┘
      │  │  │  │
      │  │  │  ▼
      │  │  │  ┌──────────────────┐
      │  │  │  │  Fraud Service    │
      │  │  │  │  (REST, :8082)    │
      │  │  │  └──────────────────┘
      │  │  │
      │  │  ▼
      │  │  ┌──────────────────┐
      │  │  │  Kafka            │
      │  │  │  (Info Service)   │
      │  │  │  (:9092)          │
      │  │  └──────────────────┘
      │  │
      │  ▼
      │  ┌──────────────────┐
      │  │  PostgreSQL       │
      │  │  (:5432)          │
      │  └──────────────────┘
      │
      ▼
  ┌──────────────────┐      ┌──────────────────┐
  │  Prometheus       │─────►│  Grafana          │
  │  (:9090)          │      │  (:3000)          │
  └──────────────────┘      └──────────────────┘
```

---

## Модули проекта

| Модуль | Описание |
|--------|----------|
| `noma-orchestrator-api` | Общие интерфейсы API (`AccountApi`, `InsuranceApi`), DTO-модели (`CreateAccountRequest`, `TransactionRequest`, `InsuranceLifeRequest` и др.), утилиты маскирования данных |
| `noma-orchestrator-backend` | Основной модуль: контроллеры, сервисы, интеграционные клиенты, конфигурации, сущности БД, миграции Liquibase, аспекты мониторинга |

---

## API-эндпоинты

### Account API

Базовый путь: `/api/account`

| Метод | Путь | Описание | Заголовки |
|-------|------|----------|-----------|
| `POST` | `/create` | Создание аккаунта клиента | `X-Request-Id` |
| `POST` | `/transactions` | Перевод средств между счетами (возвращает PDF-квитанцию) | `X-Request-Id` |
| `GET` | `/getTransactionsByDate/{date}` | Получение транзакций по дате | `X-Request-Id` |
| `GET` | `/getAccountById/{id}` | Получение аккаунта по ID | `X-Auth-Token` |
| `GET` | `/getAllAccounts/account` | Получение списка всех аккаунтов | `X-Auth-Token` |
| `PUT` | `/updateAccount/{id}` | Обновление аккаунта | `X-Auth-Token` |
| `DELETE` | `/deleteAccount/{id}` | Удаление аккаунта | `X-Auth-Token` |
| `POST` | `/infoaccounts` | Событие обновления аккаунта (отправка в Kafka) | — |
| `GET` | `/account/closing/banks` | Получение списка банков и БИК | — |

### Insurance API

Базовый путь: `/api/v1/insurance`

| Метод | Путь | Описание |
|-------|------|----------|
| `POST` | `/create-policy` | Создание полиса страхования жизни |
| `POST` | `/offers` | Генерация страховых предложений (3 варианта) |

---

## Бизнес-процессы

### 1. Создание аккаунта

```
Клиент ──POST /api/account/create──► AccountApiController
                                          │
                                          ▼
                                    AccountService.createAccount()
                                          │
                                    ┌─────▼──────┐
                                    │ Валидация   │
                                    │ X-Request-Id│
                                    └─────┬────��─┘
                                          │
                                    ┌─────▼──────────────┐
                                    │ ComplexCheckClient  │──REST──► Complex Check Service
                                    │ (комплексная        │◄────────  (APPROVE / DENY /
                                    │  проверка)          │           ARBITRATION)
                                    └─────┬──────────────┘
                                          │
                                    ┌─────▼──────┐
                                    │ Сохранение  │
                                    │ User + Account│
                                    │ в PostgreSQL │
                                    └─────┬──────┘
                                          │
                                          ▼
                                    CreateAccountResponse
```

**Шаги:**
1. Валидация заголовка `X-Request-Id` (формат UUID)
2. Вызов **Complex Check Service** через REST для комплексной проверки клиента
3. Обработка ответа: `DENY` → исключение, `ARBITRATION` → исключение, `APPROVE` → продолжение
4. Сохранение пользователя (`User`) и аккаунта (`Account`) в PostgreSQL
5. Возврат ответа с данными пользователя и статусом аккаунта

---

### 2. Перевод средств (транзакция)

```
Клиент ──POST /api/account/transactions──► AccountApiController
                                                │
                                                ▼
                                          AccountService.transactionsProcess()
                                                │
                                          ┌─────▼──────┐
                                          │ Валидация   │
                                          │ X-Request-Id│
                                          └─────┬──────┘
                                                │
                                          ┌─────▼──────────┐
                                          │ Поиск счетов   │
                                          │ sender/receiver│
                                          │ в PostgreSQL   │
                                          └─────┬──────────┘
                                                │
                                          ┌─────▼──────────────┐
                                          │ FraudClient        │──REST──► Fraud Service
                                          │ (проверка на       │◄────────  (SAFE / FRAUD_CONFIRMED /
                                          │  мошенничество)    │           REVIEW_REQUIRED / ERROR)
                                          └─────┬──────────────┘
                                                │
                                          ┌─────▼──────────────┐
                                          │ TransactionService │
                                          │ - проверка валюты  │
                                          │ - проверка баланса │
                                          │ - списание/зачисл.│
                                          │ - сохранение       │
                                          └─────┬──────────────┘
                                                │
                                          ┌─────▼──────────────┐
                                          │ ReportService      │
                                          │ (JasperReports)    │
                                          │ → PDF-квитанция    │
                                          └─────┬──────────────┘
                                                │
                                                ▼
                                          ResponseEntity<byte[]>
                                          (PDF, Content-Disposition)
```

**Шаги:**
1. Валидация `X-Request-Id`
2. Поиск счёта отправителя и получателя в БД
3. Вызов **Fraud Service** через REST — проверка на мошенничество
4. Обработка ответа: `FRAUD_CONFIRMED` → исключение, `REVIEW_REQUIRED` → исключение, `SAFE` → продолжение
5. Выполнение транзакции (в рамках `@Transactional`):
   - Проверка совпадения валют
   - Проверка достаточности средств
   - Списание со счёта отправителя, зачисление на счёт получателя
   - Сохранение записи транзакции
6. Генерация PDF-квитанции через **JasperReports**
7. Возврат PDF в ответе

---

### 3. Создание страхового полиса жизни

```
Клиент ──POST /api/v1/insurance/create-policy──► InsuranceApiController
                                                       │
                                                       ▼
                                                 InsuranceLifeService.createLifePolicy()
                                                       │
                                                 ┌─────▼──────────────┐
                                                 │ Валидация запроса   │
                                                 │ (сумма, период)    │
                                                 └─────┬──────────────┘
                                                       │
                                                 ┌─────▼──────────────┐
                                                 │ ComplexCheckClient  │──REST──► Complex Check Service
                                                 └─────┬──────────────┘
                                                       │
                                                 ┌─────▼──────────────┐
                                                 │ PremiumCalculator   │
                                                 │ - insuredAmount     │
                                                 │ - premium           │
                                                 └─────┬──────────────┘
                                                       │
                                                 ┌─────▼──────────────┐
                                                 │ Сохранение полиса  │
                                                 │ InsuranceLife в БД │
                                                 └─────┬──────────────┘
                                                       │
                                                       ▼
                                                 InsuranceLifePolicyResponse
```

**Шаги:**
1. Валидация входных данных (базовая страховая сумма, период страхования)
2. Вызов **Complex Check Service** для проверки клиента
3. Расчёт страховой суммы и премии через `PremiumCalculator`
4. Сохранение полиса `InsuranceLife` в PostgreSQL
5. Возврат ответа с данными полиса

---

### 4. Генерация страховых предложений

```
Клиент ──POST /api/v1/insurance/offers──► InsuranceApiController
                                                │
                                                ▼
                                          InsuranceLifeService.generateInsuranceOffers()
                                                │
                                          ┌─────▼──────────────┐
                                          │ Поиск клиента в БД │
                                          │ (ClientRepository)  │
                                          └─────┬──────────────┘
                                                │
                                          ┌─────▼──────────────┐
                                          │ Валидация возраста  │
                                          │ (≥ 18 лет)         │
                                          │ + тип покрытия      │
                                          └─────┬──────────────┘
                                                │
                                          ┌─────▼──────────────┐
                                          │ RiskClient          │──REST──► Risk Assessment Service
                                          │ (оценка риска)      │◄────────  riskFactor, riskLevel
                                          └─────┬──────────────┘
                                                │
                                          ┌─────▼───────────────────────���──┐
                                          │ Генерация 3 предложений:       │
                                          │ 1. Основное (выбранный тип)    │
                                          │ 2. Альтернативное 1            │
                                          │ 3. Альтернативное 2 (80% суммы)│
                                          └─────┬──────────────────────────┘
                                                │
                                                ▼
                                          List<InsuranceOfferResponse>
```

**Шаги:**
1. Поиск клиента в БД по `clientExternalId`
2. Определение реального возраста клиента (из БД или из запроса)
3. Валидация возраста (≥ 18) и типа покрытия (`TERM`, `WHOLE_LIFE`, `INVESTMENT`)
4. Вызов **Risk Assessment Service** для получения коэффициента риска
5. Генерация 3 страховых предложений:
   - **Основное** — по выбранному типу покрытия
   - **Альтернативное 1** — по альтернативному типу покрытия
   - **Альтернативное 2** — по второму альтернативному типу, с уменьшенной суммой (80%) и скорректированным сроком
6. Возврат списка предложений

---

### 5. Событие обновления аккаунта (Kafka)

```
Клиент ──POST /api/account/infoaccounts──► AccountApiController
                                                 │
                                                 ▼
                                           AccountService.accountUpdatedEvent()
                                                 │
                                                 ▼
                                           InfoServiceSender.send()
                                                 │
                                                 ▼
                                           ┌─────────────────┐
                                           │  Kafka Topic:    │
                                           │  noma_info_topic │
                                           └────────┬────────┘
                                                    │
                                                    ▼
                                           Info Service (внешний)
                                                    │
                                                    ▼
                                           ┌─────────────────────┐
                                           │  Kafka Topic:        │
                                           │  info_service_topic  │
                                           └────────┬────────────┘
                                                    │
                                                    ▼
                                           InfoServiceListener (Orchestrator)
                                                    │
                                                    ▼
                                           InfoService.sendDuplicateMessage()
                                                    │
                                              ┌─────▼──────┐
                                              │ Кэш проверка│
                                              │ (Caffeine)  │
                                              └──┬──────┬──┘
                                                 │      │
                                          Первое │      │ Дубликат
                                                 ▼      ▼
                                              Лог    Отправка в
                                                     info_system_error_topic
```

**Шаги:**
1. Получение события обновления аккаунта через REST
2. Формирование `InfoRequestDto` с мнемокодом системы (`NOMA`) и номером (`31311`)
3. Отправка сообщения в Kafka-топик `noma_info_topic`
4. Внешний Info Service обрабатывает и отправляет ответ в `info_service_topic`
5. `InfoServiceListener` получает ответ и проверяет через Caffeine-кэш на дубликаты (TTL 24 часа)
6. Если дубликат — отправляет ошибку в `info_system_error_topic`

---

## Внешние интеграции

### 1. Complex Check Service (Комплексная проверка)

| Параметр | Значение |
|----------|----------|
| **Протокол** | REST (HTTP POST) |
| **URL по умолчанию** | `http://localhost:8081/check` |
| **Переменная окружения** | `COMPLEX_CHECK_URL` |
| **Используется в** | Создание аккаунта, создание страхового полиса |
| **Retry** | До 3 попыток с экспоненциальной задержкой |

**Возможные результаты:**
- `APPROVE` — проверка пройдена, операция продолжается
- `DENY` — отказ, выбрасывается `ComplexCheckDenyException`
- `ARBITRATION` — требуется арбитраж, выбрасывается `ComplexCheckArbitrationException`

---

### 2. Fraud Service (Проверка на мошенничество)

| Параметр | Значение |
|----------|----------|
| **Протокол** | REST (HTTP POST) |
| **URL по умолчанию** | `http://localhost:8082/fraud/check` |
| **Переменная окружения** | `FRAUD_URL` |
| **Используется в** | Перевод средств между счетами |
| **Retry** | До 3 попыток с экспоненциальной задержкой |

**Возможные результаты:**
- `SAFE` / `PENDING` — операция продолжается
- `FRAUD_CONFIRMED` — мошенничество подтверждено, выбрасывается `FraudDetectedException`
- `REVIEW_REQUIRED` — требуется ручная проверка, выбрасывается `FraudReviewRequiredException`
- `ERROR` — ошибка сервиса, выбрасывается `FraudServiceException`

---

### 3. Risk Assessment Service (Оценка рисков)

| Параметр | Значение |
|----------|----------|
| **Протокол** | REST (HTTP POST) |
| **URL по умолчанию** | `http://localhost:8089/api/v1/risk/calculate` |
| **Переменная окружения** | `RISK_SERVICE_URL` |
| **Используется в** | Генерация страховых предложений |
| **Retry** | До 3 попыток с экспоненциальной задержкой |

**Возвращает:** `riskFactor` (коэффициент риска) и `riskLevel` (уровень риска), используемые для расчёта страховой премии.

---

### 4. Info Service (Kafka)

| Параметр | Значение |
|----------|----------|
| **Протокол** | Apache Kafka |
| **Broker** | `localhost:9092` |
| **Направление** | Двустороннее (отправка + получение) |
| **Используется в** | Событие обновления аккаунта |

Оркестратор отправляет события в Kafka и слушает ответы от Info Service, реализуя паттерн асинхронного взаимодействия с дедупликацией через Caffeine-кэш.

---

## Kafka-топики

| Топик | Направление | Описание |
|-------|-------------|----------|
| `noma_info_topic` | **Отправка** (Producer) | Событие обновления аккаунта → Info Service |
| `info_service_topic` | **Получение** (Consumer) | Ответ от Info Service → Orchestrator |
| `info_system_error_topic` | **Отправка** (Producer) | Ошибка дубликата сообщения → Info Service |

---

## База данных

**СУБД:** PostgreSQL  
**Миграции:** Liquibase (автоматически при старте)  
**Схема:** `db.changelog/changelog-master.yaml`

### Основные таблицы

| Таблица | Описание |
|---------|----------|
| `User` | Пользователи системы |
| `Client` | Клиенты (для страхования, содержит возраст) |
| `Account` | Банковские счета (баланс, валюта, статус) |
| `Transaction` | История транзакций |
| `InsuranceLife` | Полисы страхования жизни |
| `BicEntity` | Каталог БИК банков (XML) |

### Миграции (v-1.0.0)

- `initial-schema.sql` — начальная схема (User, Account, Transaction)
- `create-table-client.sql` — таблица клиентов
- `create-table-bic.sql` — таблица БИК
- `create-table-insurance-life-policy.sql` — таблица страховых полисов
- `insert-bic-data.sql` — начальные данные БИК
- `insert-client-data.sql` — начальные данные клиентов

---

## Мониторинг и метрики

### Prometheus + Grafana

- **Prometheus** собирает метрики с порта `8088` (management port)
- **Grafana** визуализирует дашборды (предустановленный дашборд Spring Boot)
- Эндпоинт метрик: `/actuator/prometheus`

### Кастомные метрики (@Monitor)

Каждый эндпоинт контроллера аннотирован `@Monitor`, что через `MonitoringAspect` автоматически считает:

| Метрика | Описание |
|---------|----------|
| `{metricName}` | Общее количество вызовов |
| `{metricName}.success` | Успешные вызовы |
| `{metricName}.error` | Вызовы с ошибкой |

Отслеживаемые операции: `CREATE_ACCOUNT`, `TRANSACTION_PROCESS`, `GET_TRANSACTION_BY_DATE`, `GET_ACCOUNT_BY_ID`, `ALL_ACCOUNTS`, `DELETE_ACCOUNT`, `UPDATE_ACCOUNT`, `ACCOUNT_UPDATED_EVENT`, `GET_BANKS_AND_TYPES`, `CREATE_LIFE_POLICY`, `CREATE_INSURANCE_OFFER`.

### Логирование

- **LoggingAspect** — AOP-аспект для логирования вызовов
- **MaskingInterceptor** — маскирование чувствительных данных в логах (SmartMask)
- Конфигурация: `logback.xml`

---

## Retry-политика

Все интеграционные вызовы (Complex Check, Fraud, Risk) используют единый `RetryTemplate`:

| Параметр | Значение |
|----------|----------|
| Максимум попыток | 3 |
| Начальный интервал | 2000 мс |
| Множитель | 2.0 (экспоненциальный backoff) |
| Максимальный интервал | 10000 мс |
| Connect timeout | 2000 мс |
| Read timeout | 5000 мс |
| Retryable-исключения | `SocketTimeoutException`, `ResourceAccessException`, `IOException` |

---

## Инфраструктура (Docker Compose)

```yaml
# docker-compose.yaml поднимает:
```

| Сервис | Образ | Порт | Описание |
|--------|-------|------|----------|
| `db` | `postgres:latest` | 5432 | Основная БД (noma-orchestrator) |
| `db-info-system` | `postgres:latest` | 5433 | БД Info Service |
| `db-risk` | `postgres:latest` | 5434 | БД Risk Service |
| `zookeeper` | `confluentinc/cp-zookeeper:7.4.4` | 22181 | Zookeeper для Kafka |
| `kafka` | `confluentinc/cp-kafka:7.4.4` | 9092 | Kafka broker |
| `prometheus` | `prom/prometheus:latest` | 9090 | Сбор метрик |
| `grafana` | `grafana/grafana:latest` | 3000 | Визуализация метрик |

---

## Сборка и запуск

### Требования

- **Java 17+**
- **Maven 3.8+**
- **Docker** и **docker-compose** (для инфраструктуры)

### Шаги

```bash
# 1. Поднять инфраструктуру
docker-compose up -d

# 2. Собрать проект
mvn clean package

# 3. Запустить приложение
java -jar noma-orchestrator-backend/target/noma-orchestrator-backend-*.jar

# 4. Запустить тесты
mvn test
```

### Swagger UI

После запуска доступен по адресу: [http://localhost:8888/swagger-ui.html](http://localhost:8888/swagger-ui.html)

---

## Конфигурация

Основной файл: `noma-orchestrator-backend/src/main/resources/application.yaml`

### Переменные окружения

| Переменная | Значение по умолчанию | Описание |
|------------|----------------------|----------|
| `COMPLEX_CHECK_URL` | `http://localhost:8081/check` | URL сервиса комплексной проверки |
| `FRAUD_URL` | `http://localhost:8082/fraud/check` | URL сервиса проверки на мошенничество |
| `RISK_SERVICE_URL` | `http://localhost:8089/api/v1/risk/calculate` | URL сервиса оценки рисков |

### Порты

| Сервис | Порт |
|--------|------|
| Приложение (API) | 8888 |
| Management (метрики) | 8088 |
| PostgreSQL | 5432 |
| Kafka | 9092 |
| Prometheus | 9090 |
| Grafana | 3000 |
