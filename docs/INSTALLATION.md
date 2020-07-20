Навигация
* [Общее описание](../README.md)
* **Установка** (Вы здесь)
* [Настройка](./CONFIGURE.md)
* [Поставка](./DEPLOYMENT.md)

# INSTALLATION
>##### REQUIREMENTS
>| Name | version |
>| ------ | ------ |
>| JDK | 11+ |
>| postgres | 9+ |

## Установить `java` и назначить новую версию актуальной в системе:</a>
>```sh
>sudo yum install java-11-openjdk
>sudo alternatives --config java
>```
>Если есть другие приложения, которые используют более старые версии java - необходимо применить другой java_home для них
>
## Создать скрипт ручного запуска сервиса
>```sh
>nano /home/pentaho/run-service.sh
>```
>* Добавить в файл следующее содержимое, сохранить 
>```sh
>#!/bin/sh
>sudo /usr/bin/java -jar /home/security/security-service/target/security-service.jar server
>```
>* Сделать исполняемым
>```sh
>chmod +x /home/pentaho/run-service.sh
>```
## Создать скрипт ручного перезапуска сервиса
>```sh
>nano /home/pentaho/restart-service.sh
>```
>* Добавить в файл следующее содержимое, сохранить 
>```sh
>#!/bin/sh
>kill $(lsof -t -i:8090)
>systemctl restart redmine-integration.service
>```
>* Сделать исполняемым
>```sh
>chmod +x /home/pentaho/restart-service.sh
>```
## <a id="systemd-service"></a>Создать `systemd` сервис `redmine-integration.service`
>```sh
>sudo nano /etc/systemd/system/redmine-integration.service
>```
>* Добавить в файл следующее содержимое, сохранить 
>```text
>[Unit]
>    Description=Redmine Integration Java Service
>[Service]
>    User=root
>    WorkingDirectory=/home/pentaho/service/target
>    ExecStart=/home/pentaho/run-service.sh
>    SuccessExitStatus=143
>    TimeoutStopSec=10
>    Restart=on-failure
>    RestartSec=5
>[Install]
>    WantedBy=multi-user.target
>```
## Установить Direvent
>>[Direvent](https://www.gnu.org.ua/software/direvent/) - утилита, которая позволяет настроить выполнение скриптов при возникновении событий файловой системы. 
>>В данном проекте используется для автоматического перезапуска сервиса в случае, если redmine-integration.jar изменился 
>```sh
>sudo yum install direvent
>```
>Или из исходников:
>```sh
>wget http://ftp.gnu.org/gnu/direvent/direvent-5.2.tar.gz
>tar -zxvf direvent-5.2.tar.gz
>cd direvent-5.2
>./configure && make && make install
>```
>* Отредактировать файл с настройками
>```sh
>sudo nano /usr/local/etc/direvent.conf
>```
>* Добавить в файл следующее содержимое, сохранить 
>```text
>syslog {
>    facility local0;
>    tag "direvent";
>    print-priority yes;
>}
>watcher {
>    path /home/pentaho/service/target recursive;
>    file "redmine-integration.jar";
>    event attrib;
>    command "/home/pentaho/restart-service.sh";
>    option (stdout, stderr, wait);
>}
>```
## Создать `systemd` сервис `direvent`
>```sh
>sudo nano /etc/systemd/system/direvent.service
>```
>* Добавить в файл следующее содержимое, сохранить 
>```text
>[Unit]
>    Description=monitors events in the file system directories
>    Documentation=man:direvent(1p)
>
>[Service]
>    ExecStart=/usr/local/bin/direvent --foreground
>
>[Install]
>    WantedBy=multi-user.target
>```
## Выполнить SQL-скрипты
* Ознакомиться со скриптами из папки `build\scripts` исправить схему БД, на ту, в которой предполагается
создание таблиц для работы приложения
* В БД выполнить последовательно скрипты из папки `build\scripts`

## Выполнить шаги из раздела [Настройки](./CONFIGURE.md) и [Поставка](./DEPLOYMENT.md), после чего:
>* Перезагрузить кэш сервисов `system control` 
>```sh
>sudo systemctl daemon-reload
>```
>* Сделать созданные сервисы автозапускаемыми после перезагрузки, запустить немедленно и проверить статус
>```sh
>sudo systemctl enable redmine-integration.service
>sudo systemctl start redmine-integration.service
>sudo systemctl status redmine-integration.service
>
>sudo systemctl enable direvent
>sudo systemctl start direvent
>sudo systemctl status direvent
>```
