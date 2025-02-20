# Структура Spring Boot микросервиса

## 1. Структура проекта


### 1.1 Основная структура папок

```java
src/
├── main/
│ ├── java/
│ │ └── com/
│ │ └── musicservice/
│ │ ├── MusicServiceApplication.java
│ │ │
│ │ ├── config/
│ │ │ ├── SecurityConfig.java # Конфигурация Spring Security
│ │ │ ├── JwtConfig.java # Настройки JWT токенов
│ │ │ ├── S3Config.java # Конфигурация Amazon S3
│ │ │ └── SwaggerConfig.java # Конфигурация API документации
│ │ │
│ │ ├── controller/
│ │ │ ├── AuthController.java # Эндпоинты авторизации (/auth/)
│ │ │ ├── AdminController.java # Эндпоинты админки (/admin/)
│ │ │ ├── SongController.java # Эндпоинты для работы с песнями (/api/songs/)
│ │ │ └── UserController.java # Эндпоинты для работы с пользователями (/api/users/)
│ │ │
│ │ ├── service/
│ │ │ ├── AuthService.java # Бизнес-логика авторизации
│ │ │ ├── UserService.java # Бизнес-логика работы с пользователями
│ │ │ ├── SongService.java # Бизнес-логика работы с песнями
│ │ │ ├── S3Service.java # Сервис для работы с S3
│ │ │ ├── RecommendationService.java # Сервис для работы с Python-микросервисом
│ │ │ └── StatisticsService.java # Сервис для сбора статистики
│ │ │
│ │ ├── repository/
│ │ │ ├── UserRepository.java # Репозиторий пользователей
│ │ │ ├── SongRepository.java # Репозиторий песен
│ │ │ ├── LikedSongRepository.java # Репозиторий лайкнутых песен
│ │ │ └── DislikedSongRepository.java # Репозиторий дизлайкнутых песен
│ │ │
│ │ ├── model/
│ │ │ ├── User.java # Сущность пользователя
│ │ │ ├── Song.java # Сущность песни
│ │ │ ├── UserVector.java # Сущность вектора пользователя
│ │ │ └── SongVector.java # Сущность вектора песни
│ │ │
│ │ ├── dto/
│ │ │ ├── UserDto.java # DTO для пользователя
│ │ │ ├── SongDto.java # DTO для песни
│ │ │ ├── AuthRequest.java # DTO для запроса авторизации
│ │ │ └── AuthResponse.java # DTO для ответа авторизации
│ │ │
│ │ ├── exception/
│ │ │ ├── GlobalExceptionHandler.java # Обработчик исключений
│ │ │ ├── UserNotFoundException.java
│ │ │ └── SongNotFoundException.java
│ │ │
│ │ └── security/
│ │   ├── JwtTokenProvider.java # Генерация и валидация JWT
│ │   ├── UserDetailsServiceImpl.java
│ │   └── JwtAuthenticationFilter.java
│ │ 
│ ├── resources/
│ │ ├── application.yml # Основные настройки приложения
│ │ ├── application-dev.yml # Настройки для разработки
│ │ └── application-prod.yml # Настройки для продакшена
```
## Описание основных классов

### Controllers
- `AuthController`: POST /auth/login, POST /auth/register, POST /auth/refresh
- `AdminController`: GET /admin/users, GET /admin/statistics
- `SongController`: GET /api/songs, POST /api/songs/{id}/like, POST /api/songs/{id}/dislike
- `UserController`: GET /api/users/me, PUT /api/users/me

### Services
- `AuthService`: Аутентификация, регистрация, управление токенами
- `UserService`: CRUD операции с пользователями
- `SongService`: CRUD операции с песнями, управление лайками/дизлайками
- `RecommendationService`: Взаимодействие с Python-микросервисом
- `S3Service`: Загрузка/получение файлов из S3
- `StatisticsService`: Сбор и обработка статистики

### Models
- `User`: id, username, login, passwordHash, role, sessionToken
- `Song`: id, title, artist, album, description, s3Bucket, s3FilePath
- `UserVector`: userId, vectorData
- `SongVector`: songId, vectorData

### Repositories
Интерфейсы для работы с БД, наследуются от JpaRepository:
- `UserRepository`: Операции с пользователями
- `SongRepository`: Операции с песнями
- `LikedSongRepository`: Операции с лайкнутыми песнями
- `DislikedSongRepository`: Операции с дизлайкнутыми песнями

### Security
- `JwtTokenProvider`: Генерация и валидация JWT токенов
- `UserDetailsServiceImpl`: Реализация UserDetailsService для Spring Security
- `JwtAuthenticationFilter`: Фильтр для проверки JWT токенов

### DTOs
- `UserDto`: Объект для передачи данных о пользователе
- `SongDto`: Объект для передачи данных о песне
- `AuthRequest`: Объект для запроса авторизации (login, password)
- `AuthResponse`: Объект для ответа авторизации (token, refreshToken)

### Конфигурация
- `SecurityConfig`: Настройка Spring Security
- `JwtConfig`: Настройки JWT (secret, expiration)
- `S3Config`: Настройки подключения к Amazon S3
- `SwaggerConfig`: Настройка Swagger для документации API

## Взаимодействие с Python-микросервисом

### Endpoints для взаимодействия:
- GET /recommendations/{userId} - получение рекомендаций для пользователя
- POST /feedback - отправка фидбека (лайки/дизлайки)
- GET /random - получение случайного трека

### Формат данных для обмена:

```json 
{
"userId": "string",
"songId": "string",
"action": "LIKE/DISLIKE",
"timestamp": "datetime"
}
```