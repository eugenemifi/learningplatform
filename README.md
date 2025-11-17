# Learning Platform – ORM & Hibernate Course Project

Учебный проект: платформа для онлайн-курса по JPA/Hibernate и Spring Boot.  
Система моделирует реальный сервис управления учебными курсами: курсы, модули, уроки, задания, тесты, записи студентов и отзывы.

Проект заточен под отработку:

- проектирования сущностей и связей (1–1, 1–M, M–M);
- работы с JPA/Hibernate (ленивая загрузка, каскады, ограничения);
- реализации бизнес-логики на Spring Boot;
- интеграционного тестирования с PostgreSQL через Testcontainers;
- базовой DevOps-обвязки (Flyway, Docker, docker-compose).

---

## 1. Стек технологий

- **Java 17+**
- **Spring Boot 3.x**
  - spring-boot-starter-web
  - spring-boot-starter-data-jpa
  - spring-boot-starter-validation
- **Hibernate / JPA**
- **PostgreSQL**
- **Flyway** (миграции БД)
- **Testcontainers** (PostgreSQL для интеграционных тестов)
- **Lombok**
- **Gradle (Kotlin DSL)** – `build.gradle.kts`
- **Docker** + **docker-compose**

---

## 2. Модель данных

Реализовано 15 сущностей, покрывающих предметную область учебной платформы:

1. `User` – пользователь (STUDENT / TEACHER / ADMIN)
2. `Profile` – профиль пользователя (1–1 с User)
3. `Category` – категория курса (Programming и т.п.)
4. `Tag` – тег курса (Java, Hibernate и т.д.)
5. `Course` – курс
6. `Module` – модуль курса
7. `Lesson` – урок
8. `Assignment` – задание к уроку
9. `Submission` – решение задания
10. `Enrollment` – запись студента на курс (M–M User–Course)
11. `Quiz` – тест по модулю
12. `Question` – вопрос теста
13. `AnswerOption` – вариант ответа
14. `QuizSubmission` – прохождение теста студентом
15. `CourseReview` – отзыв о курсе

### Основные связи

- `User` 1–1 `Profile`
- `User` 1–M `Course` (как teacher)
- `User` 1–M `Enrollment` / `Submission` / `QuizSubmission` / `CourseReview`
- `Category` 1–M `Course`
- `Tag` M–M `Course` (join-таблица `course_tag`)
- `Course` 1–M `Module`, 1–M `Enrollment`, 1–M `CourseReview`
- `Module` M–1 `Course`, 1–M `Lesson`, 1–1 `Quiz`
- `Lesson` M–1 `Module`, 1–M `Assignment`
- `Assignment` M–1 `Lesson`, 1–M `Submission`
- `Enrollment` M–1 `User` (student), M–1 `Course`
- `Quiz` M–1 `Module`, 1–M `Question`, 1–M `QuizSubmission`
- `Question` M–1 `Quiz`, 1–M `AnswerOption`
- `QuizSubmission` M–1 `Quiz`, M–1 `User` (student)
- `CourseReview` M–1 `Course`, M–1 `User` (student)

Коллекции (`@OneToMany`, `@ManyToMany`) настроены с ленивой загрузкой (`LAZY`), многие `@ManyToOne`/`@OneToOne` также marked as `LAZY`, чтобы в процессе работы можно было столкнуться с `LazyInitializationException`.

---

## 3. Архитектура проекта

### Пакеты

```text
src/main/java/com/example/learningplatform
  ├─ LearningPlatformApplication.java
  ├─ entity/      # JPA-сущности
  ├─ repository/  # Spring Data JPA репозитории
  ├─ service/     # бизнес-логика
  └─ web/         # REST-контроллеры
````

Слои:

* **entity** – чистые JPA-модели с аннотациями.
* **repository** – интерфейсы `JpaRepository`, кастомные запросы по необходимости.
* **service** – транзакционная бизнес-логика (запись на курсы, выдача/проверка заданий, прохождение тетсов и т.д.).
* **web** – REST API, принимают DTO, вызывают сервисы и возвращают сущности/DTO.

---

## 4. Конфигурация и запуск

### 4.1. Переменные окружения

Используются переменные окружения для подключения к БД:

* `DB_HOST` (по умолчанию `localhost`)
* `DB_PORT` (по умолчанию `5432`)
* `DB_NAME` (по умолчанию `learning_platform`)
* `DB_USER` (по умолчанию `lp_user`)
* `DB_PASSWORD` (по умолчанию `lp_password`)

### 4.2. `application.yml`

Главный профиль (dev):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:learning_platform}
    username: ${DB_USER:lp_user}
    password: ${DB_PASSWORD:lp_password}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate   # схема управляется Flyway
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
        default_batch_fetch_size: 16

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    schemas: public
```

Профиль `test` активируется в интеграционных тестах (`@ActiveProfiles("test")`), там также используется Flyway + PostgreSQL (через Testcontainers).

### 4.3. Миграции Flyway

Миграции лежат в `src/main/resources/db/migration`:

* **`V1__init_schema.sql`** – создание всех таблиц и ограничений.
* **`V2__demo_data.sql`** – демо-данные:

    * категория `Programming`,
    * теги `Java`, `Hibernate`, `Beginner`,
    * преподаватель и студенты,
    * курс «Основы Hibernate» с модулем, уроком, заданием и простым тестом.

---

## 5. Запуск приложения

### Вариант 1: через Docker + docker-compose (рекомендуется)

В корне лежат:

* `Dockerfile` – multi-stage сборка (Gradle → JAR → JRE-образ).
* `docker-compose.yml` – поднимает PostgreSQL и приложение.

Команды:

```bash
# собрать и запустить контейнеры
docker compose up --build
```

После успешного старта:

* приложение доступно на `http://localhost:8080`;
* PostgreSQL – на `localhost:5432`, БД `learning_platform`, пользователь/пароль `lp_user/lp_password`.

### Вариант 2: локально (без Docker)

1. Поднять PostgreSQL локально:

    * создать БД `learning_platform`;
    * пользователя `lp_user`/`lp_password` (или свои, и указать их через env).

2. Запустить миграции и приложение:

```bash
./gradlew bootRun
```

---

## 6. REST API (основные эндпойнты)

Формат: JSON.

### 6.1. Пользователи

`/api/users`

* `POST /api/users`
  Создать пользователя.

  ```json
  {
    "name": "John Doe",
    "email": "john@example.com",
    "role": "STUDENT"
  }
  ```

* `GET /api/users/{id}` – получить пользователя.

* `GET /api/users` – список пользователей.

* `PUT /api/users/{id}` – обновить имя.

* `DELETE /api/users/{id}` – удалить.

---

### 6.2. Категории и теги

`/api/categories`

* `POST /api/categories`

  ```json
  { "name": "Programming" }
  ```
* `GET /api/categories`

`/api/tags`

* `POST /api/tags`

  ```json
  { "name": "Java" }
  ```
* `GET /api/tags`

---

### 6.3. Курсы, модули, уроки

`/api/courses`

* `POST /api/courses` – создать курс.

  ```json
  {
    "teacherId": 1,
    "categoryId": 1,
    "title": "Курс по Hibernate",
    "description": "Введение в ORM",
    "durationInHours": 20,
    "startDate": "2025-01-10",
    "tagIds": [1, 2]
  }
  ```

* `GET /api/courses` – список курсов.

* `GET /api/courses/{id}` – курс по id.

* `PUT /api/courses/{id}` – изменить название/описание.

* `DELETE /api/courses/{id}` – удалить курс (поведение зависит от связей/данных).

Модули:

* `POST /api/courses/{courseId}/modules`

  ```json
  { "title": "Введение в ORM", "orderIndex": 1 }
  ```

* `GET /api/courses/{courseId}/modules` – модули курса.

Уроки:

* `POST /api/courses/modules/{moduleId}/lessons`

  ```json
  {
    "title": "Что такое ORM",
    "content": "Текст урока",
    "videoUrl": null
  }
  ```

* `GET /api/courses/modules/{moduleId}/lessons` – уроки модуля.

---

### 6.4. Запись на курс

`/api/enrollments`

* `POST /api/enrollments` – записать студента на курс.

  ```json
  {
    "courseId": 1,
    "studentId": 2
  }
  ```

* `DELETE /api/enrollments?courseId={courseId}&studentId={studentId}` – отписать.

* `GET /api/enrollments/by-student/{studentId}` – все записи студента.

* `GET /api/enrollments/by-course/{courseId}` – все студенты на курсе.

---

### 6.5. Задания и решения

`/api/assignments`

* `POST /api/assignments` – создать задание к уроку.

  ```json
  {
    "lessonId": 10,
    "title": "Домашнее задание 1",
    "description": "Сделайте CRUD с Hibernate",
    "dueDate": "2025-01-20T23:59:00",
    "maxScore": 100
  }
  ```

* `GET /api/assignments/{id}` – задание.

* `GET /api/assignments/by-lesson/{lessonId}` – задания урока.

`/api/submissions`

* `POST /api/submissions` – отправить решение.

  ```json
  {
    "assignmentId": 5,
    "studentId": 2,
    "content": "Моё решение"
  }
  ```

* `POST /api/submissions/{id}/grade` – выставить оценку.

  ```json
  {
    "score": 95,
    "feedback": "Хорошая работа"
  }
  ```

* `GET /api/submissions/by-assignment/{assignmentId}` – все решения по заданию.

* `GET /api/submissions/by-student/{studentId}` – все решения студента.

---

### 6.6. Тесты (Quiz) и результаты

`/api/quizzes`

* `POST /api/quizzes` – создать тест для модуля.

  ```json
  {
    "moduleId": 3,
    "title": "Тест по модулю 1",
    "timeLimitMinutes": 15
  }
  ```

* `POST /api/quizzes/{quizId}/questions` – добавить вопрос.

  ```json
  {
    "text": "Что делает ORM?",
    "type": "SINGLE_CHOICE",
    "options": [
      { "text": "Маппит объекты на таблицы БД", "correct": true },
      { "text": "Является веб-фреймворком", "correct": false }
    ]
  }
  ```

* `GET /api/quizzes/{id}` – получить тест.

`/api/quiz-submissions`

* `POST /api/quiz-submissions` – пройти тест.

  ```json
  {
    "quizId": 1,
    "studentId": 2,
    "answers": {
      "10": [100]  // questionId -> [answerOptionId...]
    }
  }
  ```

* `GET /api/quiz-submissions/by-student/{studentId}` – результаты студента.

* `GET /api/quiz-submissions/by-quiz/{quizId}` – результаты по тесту.

---

### 6.7. Отзывы о курсе

`/api/course-reviews`

* `POST /api/course-reviews` – оставить отзыв.

  ```json
  {
    "courseId": 1,
    "studentId": 2,
    "rating": 5,
    "comment": "Отличный курс"
  }
  ```

* `GET /api/course-reviews/by-course/{courseId}` – отзывы по курсу.

---

## 7. Тестирование

### 7.1. Интеграционные тесты (PostgreSQL Testcontainers)

Интеграционные тесты лежат в `src/test/java/com/example/learningplatform/integration` и наследуются от `BaseIntegrationTest`.

Используется:

* `PostgreSQLContainer` (Testcontainers) – поднимает реальный PostgreSQL в Docker.
* `DynamicPropertySource` – подставляет URL/логин/пароль БД в контекст Spring.
* Flyway миграции применяются автоматически при старте тестов.

Покрытые контроллеры:

* `UserControllerIT` – создание/чтение/обновление пользователя.
* `CategoryControllerIT` – создание и список категорий.
* `TagControllerIT` – создание и список тегов.
* `CourseControllerIT` – создание курса, модулей и уроков.
* `EnrollmentControllerIT` – запись на курс и выборка.
* `AssignmentControllerIT` – создание задания и выборка по уроку.
* `SubmissionControllerIT` – отправка и оценка решения.
* `QuizControllerIT` – создание теста и добавление вопросов.
* `QuizSubmissionControllerIT` – прохождение теста и выборка результатов.
* `CourseReviewControllerIT` – добавление и выборка отзывов.

Запуск тестов:

```bash
./gradlew test
```

Для этого должны быть доступны Docker (Testcontainers сам подтянет образ PostgreSQL).

---

## 8. Пример сценария проверки

Минимальный сценарий, который можно повторить через Postman/curl:

1. Создать преподавателя и студента (`POST /api/users`).
2. Создать категорию (`POST /api/categories`).
3. Создать курс (`POST /api/courses`) с `teacherId` и `categoryId`.
4. Добавить модуль в курс (`POST /api/courses/{courseId}/modules`).
5. Добавить урок в модуль (`POST /api/courses/modules/{moduleId}/lessons`).
6. Создать задание к уроку (`POST /api/assignments`).
7. Записать студента на курс (`POST /api/enrollments`).
8. Отправить решение по заданию (`POST /api/submissions`).
9. Оценить решение (`POST /api/submissions/{id}/grade`).
10. Создать тест для модуля (`POST /api/quizzes` + `POST /api/quizzes/{quizId}/questions`).
11. Пройти тест (`POST /api/quiz-submissions`).
12. Оставить отзыв о курсе (`POST /api/course-reviews`).
13. Посмотреть:

    * курсы и структуру модулей/уроков,
    * записи на курс,
    * задания и решения,
    * результаты теста,
    * отзывы.

---

## 9. Ленивая загрузка и ORM-аспекты

* Все коллекции связей (модули курса, уроки модуля, задания урока, варианты ответа и т.п.) настроены как `LAZY`.
* Многие `@ManyToOne`/`@OneToOne` тоже `LAZY`, чтобы можно было поймать `LazyInitializationException`, если обращаться к ленивым полям вне транзакции / после закрытия сессии.
* Для загрузки теста вместе с вопросами и вариантами ответов используется `join fetch` (`QuizRepository.findByIdWithQuestionsAndOptions`).

Это позволяет в коде сервиса/тестов демонстрировать типичные проблемы работы с ORM и способы их решения (join fetch, EntityGraph, работа в пределах транзакции).
