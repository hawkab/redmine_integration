package ru.hawkab.redmineintegration;

/**
 * Класс общих констант
 *
 * @author olshansky
 * @since 04.06.2020
 */
public class Constants {

    public static final String DEFAULT_CATEGORY_CATEGORY_NAME = "Категория";
    public static final Integer DEFAULT_CATEGORY_CATEGORY_ID = 19;

    public static final String DEFAULT_REGION_CATEGORY_NAME = "Регион";
    public static final Integer DEFAULT_REGION_CATEGORY_ID = 20;

    public static final String DEFAULT_SYSTEM_CATEGORY_NAME = "Система";
    public static final Integer DEFAULT_SYSTEM_CATEGORY_ID = 21;
    public static final String DEFAULT_SYSTEM_CATEGORY_VALUE = "113";

    public static final String ISSUE_DESCRIPTION_MESSAGE_SETTING_NAME = "REDMINE_ISSUE_DESCRIPTION_MESSAGE";
    public static final String REDMINE_PROJECT_ID_SETTING_NAME = "REDMINE_PROJECT_ID";

    public static final int DEFAULT_PROJECT_ID = 144;
    public static final int DEFAULT_DELAY_IN_MILLISECONDS_BETWEEN_POLLING_DATABASE = 120_000;
    public static final int DEFAULT_DELAY_IN_MILLISECONDS_BETWEEN_POLLING_REDMINE = 300_000;

    public static final String GET_MAP_FROM_OBJECT_ERROR_MESSAGE =
            "Во время преобразования объекта в карту ключ-значение произошла ошибка, причина";
    public static final String GET_NEW_APPEAL_ERROR_MESSAGE =
            "Во время получения новых обращений произошла ошибка, причина";
    public static final String GET_APPEAL_COMMENTS_ERROR_MESSAGE =
            "Во время получения статуса обращений из Redmine произошла ошибка, причина";
    public static final String GET_APPEAL_ERROR_MESSAGE =
            "Во время получения статуса заявки %s из redmine произошла ошибка, причина";
    public static final String RESEND_APPEAL_ERROR_MESSAGE =
            "Во время отправки новой заявки %s в redmine произошла ошибка, причина";
    public static final String UPLOAD_FILE_ERROR_MESSAGE =
            "Во время загрузки файла %s на сервер Redmine произошла ошибка, причина";
    public static final String DEFAULT_ISSUE_DESCRIPTION_MESSAGE = "<p>Отчёт: <b>${appeal.report}</b></p>\n" +
            "<p>Категория обращения: <b>${appeal.appealKind.displayName}</b></p>\n" +
            "<p>Логин: <b>${appeal.user.login}</b> / email: <b>${appeal.user.email}</b> / тел: <b>+${appeal.user.phoneNumber}</b></p>\n" +
            "<p>Текст обращения:</p>\n" +
            "<blockquote>${appeal.appealMessage}</blockquote>";
    public static final String NOT_SPECIFIED = "(не указано)";

    public static final String SETTING_NOT_EXISTS = "Настройка %s не задана в системе, будет использовано значение по-умолчанию";

    public static final String REDMINE_API_ACCESS_TOKEN = "s2881071e10617e4194cd14ab1f1fac7172f0ed1";
    static final String REDMINE_URL = "https://rm.someserver.ru";

    public static final String INTERPOLATE_REGEX_PATTERN = "(\\w+\\.?\\w*\\[?\\w*\\]?\\w*\\.?\\w*)";

    public static final String REDMINE_DEFAULT_SYSTEM_USER_NAME = "CUR_MLG CUR_MLG";
    public static final String REDMINE_DEFAULT_API_USER_NAME = "CUR_BI CUR_BI";
    public static final String REDMINE_STATUS_ATTRIBUTE_NAME = "status_id";
    public static final String CHANGE_STATUS_DEFAULT_MESSAGE = "<p><i>(сменил статус задачи с '<b>%s</b>' на '<b>%s</b>')</i></p>";
    public static final String CHANGE_DESCRIPTION_DEFAULT_MESSAGE = "<p><i>(сменил описание задачи с '<blockquote>%s</blockquote>' на '<blockquote>%s</blockquote>')</i></p>";
    public static final String DELETE_ATTACHMENT_DEFAULT_MESSAGE = "<p><i>(удалил прикреплённый файл '<b>%s</b>')</i></p>";
    public static final String ATTACHMENT_DEFAULT_MESSAGE = "<p><i>(прикрепил файл '<b>%s</b>')</i></p>";
    public static final String CHANGE_ATTRIBUTE_DEFAULT_MESSAGE = "<p><i>(поменял атрибут %s задачи с '<b>%s</b>' на '<b>%s</b>')</i></p>";
    public static final String REDMINE_DESCRIPTION_ATTRIBUTE_NAME = "description";
    public static final String REDMINE_ATTACHMENT_ATTRIBUTE_NAME = "attachment";
}
