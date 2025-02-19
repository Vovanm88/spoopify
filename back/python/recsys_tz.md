# Структура Python микросервиса рекомендаций

## 1. Структура проекта

### 1.1 Основная структура папок

```python
src/
├── main.py # Основной файл запуска FastAPI приложения
├── api/
│   ├── __init__.py
│   ├── routes.py # Основные эндпоинты API
│   └── models.py # Pydantic модели для валидации данных
│
├── services/
│   ├── __init__.py
│   ├── recommendation_service.py # Сервис рекомендаций
│   └── database_service.py # Сервис для работы с PostgreSQL
│
├── config/
│   ├── __init__.py
│   └── settings.py # Настройки приложения
│
└── utils/
    ├── __init__.py
    └── db_utils.py # Утилиты для работы с БД
```

## 2. Описание основных файлов

### main.py
- Инициализация FastAPI приложения
- Подключение роутов
- Настройка CORS и middleware

### api/routes.py
Основные эндпоинты:
- GET `/api/recommendations/{user_id}` - получение рекомендаций
- POST `/api/feedback` - обработка лайков/дизлайков
- GET `/api/random` - получение случайного трека

### api/models.py
Pydantic модели:
```python
class FeedbackModel:
    user_id: str
    song_id: str
    action: Literal["LIKE", "DISLIKE"]
    timestamp: datetime

class RecommendationResponse:
    song_ids: List[str]
    scores: List[float]
```

### services/recommendation_service.py
- Логика получения рекомендаций
- Обработка пользовательских действий
- Интерфейс для взаимодействия с рекомендательной системой

### services/database_service.py
- Подключение к PostgreSQL
- Работа с векторными представлениями
- Получение и обновление данных

### config/settings.py
Настройки:
- Параметры подключения к БД
- Конфигурация API
- Параметры рекомендательной системы

## 3. API Спецификация

### Получение рекомендаций
```
GET /api/recommendations/{user_id}
Response 200:
{
    "song_ids": ["id1", "id2", ...],
    "scores": [0.95, 0.85, ...]
}
```

### Отправка фидбека
```
POST /api/feedback
Request:
{
    "user_id": "string",
    "song_id": "string",
    "action": "LIKE" | "DISLIKE",
    "timestamp": "2024-03-20T15:00:00Z"
}
Response 200:
{
    "status": "success"
}
```

### Получение случайного трека
```
GET /api/random
Response 200:
{
    "song_id": "string"
}
```

## 4. Взаимодействие с Java-микросервисом

### Формат обмена данными
- Все запросы и ответы в формате JSON
- Обязательная валидация входящих данных
- Использование HTTP статус кодов для обработки ошибок

### Аутентификация
- Проверка API ключа в заголовках запросов
- Базовая защита эндпоинтов