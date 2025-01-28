[EN](README.md) | RU

Навигация
* **Общее описание** (Вы здесь)
* [Установка](./docs/INSTALLATION.md)
* [Настройка](./docs/CONFIGURE.md)
* [Поставка](./docs/DEPLOYMENT.md)

# ABOUT
Данное приложение создаёт новые задачи в системе Redmine, на основе данных из таблицы в БД.

## Схема базы данных
![schema](./docs/database_diagram.png)

## Словесное описание алгоритма работы

### Выгрузка обращений
1. Периодически ищет новые (статус `CREATED`) или необработанные (статус `SENT_ERROR`) записи в таблице `user_appeal` в БД 
2. В случае нахождения, вызывает [Redmine API](https://www.redmine.org/projects/redmine/wiki/Rest_api):
3. Загружает прикреплённые к обращению файлы - вызывает `POST` метод [files](https://www.redmine.org/projects/redmine/wiki/Rest_Files), сохраняет полученные идентификаторы
4. Создаёт объект Issue на основе данных в таблицах обращения и загружает их на сервер Redmine - вызывает `POST` [issues](https://www.redmine.org/projects/redmine/wiki/Rest_Issues) 

### Загрузка обращений
1. Периодически ищет необработанные (статусы `SENT`, `EXTERNAL_COMMENT`, `EXTERNAL_COMMENT`, `INTERNAL_COMMENT`, `HANDLING`) записи в таблице `user_appeal` в БД 
2. В случае нахождения, вызывает [Redmine API](https://www.redmine.org/projects/redmine/wiki/Rest_api):
3. Загружает issue - вызывает `GET` метод [issues](https://www.redmine.org/projects/redmine/wiki/Rest_Issues) с флагом `include journals`, сохраняет полученные статусы в БД
4. Создаёт объект `CommentEntity` на основе полученных `Journal` и сохраняет в таблицу `redmine_comments`
