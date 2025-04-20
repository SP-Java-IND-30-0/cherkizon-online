# Инструкция по развертыванию проекта Cherkizon-Online

## Предварительные требования

1. Установленный Docker и Docker Compose
2. Установленный Java 21 JDK
3. Установленный Maven 3.9.9
4. Доступ к репозиторию проекта

## 1. Подготовка окружения

### Клонирование проекта (если необходимо)
```bash
git clone https://github.com/SP-Java-IND-30-0/cherkizon-online
cd cherkizon-online
```

### Настройка переменных окружения

Для каждого сервиса создайте `.env` файл в соответствующей директории или подготовьте переменные окружения другим способом.

#### auth-service
Создайте файл `auth-service/.env`:
```
DB_PASSWORD=<пароль для БД>
JWT_SECRET_KEY=<секретный ключ для JWT>
```

#### profile-service
Создайте файл `profile-service/.env`:
```
DB_PASSWORD=<пароль для БД>
YANDEX_SECRET_KEY=<секретный ключ Яндекс>
YANDEX_ACCESS_KEY=<ключ доступа Яндекс>
JWT_SECRET_KEY=<секретный ключ для JWT>
```

#### ads-service
Создайте файл `ads-service/.env`:
```
DB_PASSWORD=<пароль для БД>
YANDEX_SECRET_KEY=<секретный ключ Яндекс>
YANDEX_ACCESS_KEY=<ключ доступа Яндекс>
JWT_SECRET_KEY=<секретный ключ для JWT>
```

#### comments-service
Создайте файл `comments-service/.env`:
```
DB_PASSWORD=<пароль для БД>
JWT_SECRET_KEY=<секретный ключ для JWT>
```

#### notification-service
Создайте файл `notification-service/.env`:
```
EMAIL_USER=<email пользователя>
EMAIL_APP_PASSWORD=<пароль приложения>
EMAIL_TEMPLATES_DIR=<путь к шаблонам писем>
```

## 2. Запуск инфраструктуры

### Запуск Kafka и других зависимостей
Из корневой директории проекта выполните:
```bash
docker-compose -f docker-compose-dev.yml up -d
```

Эта команда запустит:
- Apache Kafka
- Zookeeper
- Redis

## 3. Сборка и запуск сервисов

Для каждого сервиса выполните следующие команды:

### Общий процесс для каждого сервиса (auth, profile, ads, comments, notification)
```bash
cd <service-directory>
mvn clean package
java -jar target/<имя-сервиса>-<версия>.jar
```

Например, для auth-service:
```bash
cd auth-service
mvn clean package
java -jar target/auth-service-1.0.0.jar
```

### Запуск api-gateway
```bash
cd api-gateway
mvn clean package
java -jar target/api-gateway-1.0.0.jar
```

## 4. Запуск фронтенда

Выполните команду:
```bash
docker run -p 3000:3000 --rm ghcr.io/dmitry-bizin/front-react-avito:v1.21
```

## 5. Проверка работоспособности

1. Откройте в браузере `http://localhost:3000` для доступа к фронтенду
2. Проверьте доступность API через шлюз (`http://localhost:8080`)

## Дополнительные настройки

### Настройка портов (если необходимо)
По умолчанию:
- Фронтенд: 3000
- API Gateway: 8080
- Остальные сервисы используют свои порты (указаны в их конфигурации)

При необходимости измените порты в конфигурационных файлах соответствующих сервисов.

### Настройка баз данных
Убедитесь, что:
1. Базы данных для каждого сервиса запущены
2. Параметры подключения (URL, имя пользователя) указаны правильно в конфигурации каждого сервиса

## Остановка проекта

1. Остановите фронтенд: Ctrl+C в терминале, где запущен фронтенд
2. Остановите сервисы: Ctrl+C в соответствующих терминалах
3. Остановите инфраструктуру:
```bash
docker-compose -f docker-compose-dev.yml down
```

## Устранение неполадок

1. Если сервисы не могут подключиться к Kafka:
    - Проверьте, что Kafka запущена (`docker ps`)
    - Проверьте настройки подключения к Kafka в конфигах сервисов

2. Если возникают проблемы с JWT:
    - Убедитесь, что `JWT_SECRET_KEY` одинаков во всех сервисах

3. При проблемах с Yandex:
    - Проверьте правильность `YANDEX_SECRET_KEY` и `YANDEX_ACCESS_KEY`

4. При проблемах с email:
    - Проверьте `EMAIL_USER` и `EMAIL_APP_PASSWORD` в notification-service