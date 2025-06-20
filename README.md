# NOMA Orchestrator

Проект представляет собой систему оркестрации для Non-Orthogonal Multiple Access (NOMA), основанную на Java и Spring Boot. Репозиторий состоит из двух основных модулей:

- `noma-orchestrator-api` – общие модели и интерфейсы API.
- `noma-orchestrator-backend` – основной сервис с бизнес‑логикой и интеграциями.

## Требования

- **Java 17** и выше
- **Maven 3.8** и выше
- Опционально **Docker** и **docker-compose** для локального запуска инфраструктуры

## Сборка

```bash
mvn clean package
```

После сборки артефакт `noma-orchestrator-backend/target/*.jar` можно запустить как обычное Spring Boot приложение:

```bash
java -jar noma-orchestrator-backend/target/noma-orchestrator-backend-*.jar
```

## Тесты

Для запуска модульных тестов выполните:

```bash
mvn test
```

## Запуск с использованием Docker

В проекте присутствует `Dockerfile` для сборки контейнера и `docker-compose.yaml` для поднятия PostgreSQL:

```bash
docker-compose up -d   # поднимает базу данных
docker build -t noma-orchestrator .
docker run -p 8080:8080 noma-orchestrator
```

## Конфигурация

Основные настройки приложения находятся в `noma-orchestrator-backend/src/main/resources/application.yaml`. При необходимости можно переопределить переменную `COMPLEX_CHECK_URL` для интеграции с внешним сервисом комплексной проверки.

## Миграции базы данных

Структура базы описана через Liquibase. Скрипты расположены в директории `noma-orchestrator-backend/src/main/resources/db.changelog` и автоматически выполняются при старте приложения.

## Документация API

После запуска приложения Swagger UI доступен по адресу [`/swagger-ui.html`](http://localhost:9999/swagger-ui.html). Здесь можно ознакомиться со всеми доступными эндпоинтами и моделями данных.

## Структура репозитория

```
├── noma-orchestrator-api      # модели и интерфейсы API
├── noma-orchestrator-backend  # реализация сервисов, контроллеры и конфигурации
├── Dockerfile                 # сборка образа приложения
├── docker-compose.yaml        # инфраструктура для запуска PostgreSQL
└── pom.xml                    # общий Maven-модуль
```

