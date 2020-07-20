Навигация
* [Общее описание](../README.md)
* [Установка](./INSTALLATION.md)
* [Настройка](./CONFIGURE.md)
* **Поставка** (Вы здесь)

# DEPLOYMENT
Для сборки проекта необходимо запустить команду 
```sh
mvn clean install spring-boot:repackage
```
И скопировать jar файл на целевой сервер в папку `/home/pentaho/service`
```sh
scp redmine-integration.jar root@11.11.11.11:\home\pentaho\service
```
Так же можно использовать IDEA плагин `FTP\SFTP Connectivity` для поставки
jar файла на сервер после сборки:
* [Create a remote server configuration](https://www.jetbrains.com/help/phpstorm/creating-a-remote-server-configuration.html)

Сервис перезагрузится автоматически после загрузки файла, если был выполнены шаги про `Direvent` из раздела [Установка](./INSTALLATION.md).

Если это не было сделано, необходимо вручную выполнить на сервере команду 
```
sudo systemctl restart redmine-integration
```