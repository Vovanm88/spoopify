#!/bin/bash

# Очистка предыдущих артефактов
./gradlew clean

# Сборка приложения
./gradlew build

# Проверка результата сборки
if [ $? -eq 0 ]; then
    echo "Сборка прошла успешно!"
else
    echo "Ошибка при сборке приложения."
    exit 1
fi

# Сборка Docker-образа
docker build -t music-service .

# Запуск Docker-контейнера
docker run -p 8080:8080 music-service