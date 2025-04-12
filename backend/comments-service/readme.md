## Работа с БД:

1. Зайти в корневую папку модуля
```
cd backend/comments-service
```
2. Запустить скрипт init-comments-service.sql
```
 psql -U dev -d comments_service_db -f init-comments-service.sql
```
Пароль пользователя dev:
12345

* app - размещение таблиц приложения
* service - размещение служебных таблиц liquibase