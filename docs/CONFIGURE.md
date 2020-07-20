Навигация
* [Общее описание](../README.md)
* [Установка](./INSTALLATION.md)
* **Настройка** (Вы здесь)
* [Поставка](./DEPLOYMENT.md)

# CONFIGURE
* Для настройки подключения к БД необходимо поправить файл `application.properties`:
```properties
spring.datasource.url = jdbc:postgresql://11.11.11.11:5432/postgres?currentSchema=cur_presentation
spring.datasource.username = login
spring.datasource.password = password
```
* Для настройки Redmine проекта в который создаётся новый Issue необходимо в БД 
в таблице settings найти запись с `code` = `REDMINE_PROJECT_ID` и исправить поле `value` 

* Для настройки URL Redmine, необходимо в классе `ru.hawkab.redmineintegration.Constants` исправить значение поля `REDMINE_URL`

* Для настройки auth-token в Redmine, необходимо в классе `ru.hawkab.redmineintegration.Constants` исправить значение поля `REDMINE_API_ACCESS_TOKEN`

* Для настройки текста Issue необходимо в БД 
в таблице settings найти запись с `code` = `REDMINE_ISSUE_DESCRIPTION_MESSAGE` и исправить поле value 

* Для настройки интервала между опросами БД на предмет наличия новых пользовательских обращений, необходимо в классе
`ru.hawkab.redmineintegration.Constants` исправить значение поля `DEFAULT_DELAY_IN_MILLISECONDS_BETWEEN_POLLING_DATABASE` 
(миллисекунды)

* Для настройки интервала между опросами Redmine API на предмет наличия новых статусов и комментариев, необходимо в классе
`ru.hawkab.redmineintegration.Constants` исправить значение поля `DEFAULT_DELAY_IN_MILLISECONDS_BETWEEN_POLLING_REDMINE` 
(миллисекунды)