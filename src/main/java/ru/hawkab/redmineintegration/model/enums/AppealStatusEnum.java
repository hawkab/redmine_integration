package ru.hawkab.redmineintegration.model.enums;

/**
 * Статус пользовательского обращения
 *
 * @author olshansky
 * @since 04.06.2020
 */
public enum AppealStatusEnum {

    CREATED("Обращение только что создано из системы Pentaho"),
    SENT("Обращение успешно отправлено в Redmine"),
    HANDLING("Задача принята к обработке специалистом тех. поддержки"),
    EXTERNAL_REFUSED("Возвращена на доработку"),
    EXTERNAL_COMMENT("Специалист технической поддержки добавил комментарий"),
    INTERNAL_COMMENT("Пользователь добавил комментарий"),
    SENDING_ERROR("При отправке обращения в Redmine произошла ошибка"),
    READING_ERROR("При получении обращения из Redmine произошла ошибка"),
    FINISHED("Исполнение обращения завершено");

    private String value;

    AppealStatusEnum(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(value);
    }

}
