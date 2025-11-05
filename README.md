# Quizizz Application API Documentation
## Общая информация
### Аутентификация
- Для публичных методов аутентификация не требуется
- Для приватных методов требуются заголовки:
    - X-Username - имя пользователя
    - X-Token - токен авторизации
### Формат ответов
Успешный ответ:
```json
{
  "timestamp": "2023-11-15T10:30:00",
  "status": 200,
  "message": "Success message"
}
```
Ошибка:
```json
{
  "timestamp": "2023-11-15T10:30:00",
  "status": 400,
  "message": "Error message"
}
```
## Публичные эндпоинты (не требуют аутентификации)
### Авторизация
#### 🔐 Логин
POST /login

Тело запроса:
```json
{
  "login": "string",
  "password": "string"
}
```
Успешный ответ:
```json
{
  "status": 200,
  "message": "Login successful",
  "username": "username",
  "token": "auth_token_here"
}
```
#### 📝 Регистрация
POST /register

Тело запроса:
```json
{
  "login": "string",
  "password": "string",
  "email": "string",
}
```
Успешный ответ:
```json
{
  "timestamp": "2023-11-15T10:30:00",
  "status": 200,
  "message": "Register successful. Wait for the employer's confirmation"
}
```
#### ✅ Подтверждение регистрации
GET /confirmed?login={login}&token={token}

Успешный ответ:
```json
{
  "timestamp": "2023-11-15T10:30:00",
  "status": 200,
  "message": "User registration {login} has been successfully completed"
}
```
#### ✏️ Обновление данных пользователя
POST /update

Тело запроса:
```json
{
  "login": "string",
  "password": "string",
  "email": "string",
  "isAdmin": "boolean"
}
```
Успешный ответ:
```json
{
  "timestamp": "2023-11-15T10:30:00",
  "status": 200,
  "message": "Updating successful"
}
```
## Приватные эндпоинты (требуют заголовки X-Username и X-Token)
### 📁 Управление директориями
#### Создание директории
POST /directory/create

Заголовки:
- X-Username: string
- X-Token: string

Тело запроса:
```json
{
  "path": "string"
}
```
Успешный ответ:
```json
{
  "timestamp": "2023-11-15T10:30:00",
  "status": 200,
  "message": "Directory created"
}
```
#### Получение директории
GET /directory/get?path={path}

Заголовки:
- X-Username: string
- X-Token: string

Успешный ответ:
```json
{
  "path": "string",
  "children": [
    {
      "name": "string",
      "isDirectory": "boolean"
    }
  ]
}
```
#### Переименование директории
PATCH /directory/rename

Заголовки:
- X-Username: string
- X-Token: string

Тело запроса:
```json
{
  "path": "string",
  "newName" : "string"
}
```
Успешный ответ:
```json
{
  "timestamp": "2023-11-15T10:30:00",
  "status": 200,
  "message": "Directory renamed"
}
```
#### Удаление директории
DELETE /directory/remove?path={path}

Заголовки:
- X-Username: string
- X-Token: string

Успешный ответ:
```json
{
  "timestamp": "2023-11-15T10:30:00",
  "status": 200,
  "message": "Directory removed"
}
```
### 📝 Управление тестами
#### Создание теста
POST /tests/create

Заголовки:
- X-Username: string
- X-Token: string

Тело запроса:
```json
{
  "path": "string",
  "questions": [
    {
      "question": "string",
      "answers": [
        {
          "answer": "string",
          "number": "integer",
          "isRight": "boolean"
        }
      ]
    }
  ]
}
```
Успешный ответ:
```json
{
  "timestamp": "2023-11-15T10:30:00",
  "status": 200,
  "message": "Test created"
}
```
#### Получение теста
GET /tests/get?path={path}

Заголовки:
- X-Username: string
- X-Token: string

Успешный ответ:
```json
{
  "path": "string",
  "questions": [
    {
      "question": "string",
      "answers": [
        {
          "answer": "string",
          "number": "integer",
          "isRight": "boolean"
        }
      ]
    }
  ]
}
```
#### Удаление теста
DELETE /tests/remove?path={path}

Заголовки:
- X-Username: string
- X-Token: string

Успешный ответ:
```json
{
  "timestamp": "2023-11-15T10:30:00",
  "status": 200,
  "message": "Test deleted"
}
```
#### Переименование теста
PATCH /tests/rename

Заголовки:
- X-Username: string
- X-Token: string

Тело запроса:
```json
{
  "path": "string",
  "newName": "string"
}
```
Успешный ответ:
```json
{
  "timestamp": "2023-11-15T10:30:00",
  "status": 200,
  "message": "Test renamed"
}
```
## Коды статусов
- 200 - Успешный запрос
- 400 - Ошибка в запросе (неверные параметры, данные не найдены)
- 401 - Ошибка аутентификации (неверные или отсутствующие заголовки X-Username/X-Token)
## Важные замечания
1. Токен авторизации действителен в течение 24 часов
2. Все приватные методы возвращают 401 если:
   - Не переданы заголовки X-Username и X-Token
   - Передан неверный токен
   - Токен устарел
3. Публичные методы доступны без аутентификации
4. Для работы с API сначала необходимо выполнить логин и сохранить полученные username и token