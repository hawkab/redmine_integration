package ru.hawkab.redmineintegration.model.enums;

import java.util.Objects;

/**
 * Статус пользовательского обращения в системе Redmine
 *
 * @author olshansky
 * @since 30.06.2020
 */
public enum RedmineStatusEnum {

    NEW("1", "Новая"),
    IN_PROGRESS("2", "В работе"),
    CLOSED("3", "Закрыта"),
    REFUSED("4", "Возвращена на доработку"),
    CHECKING("5", "На проверке"),
    MODERATION("7", "Модерация"),
    SUPPORT_BI("8", "Техподдержка BI"),
    SUPPORT_MLG("9", "Техподдержка МЛГ");

    private String code;
    private String value;

    RedmineStatusEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Получить конкретный элемент перечисления enum по коду
     * @param code код enum
     * @return элемент перечисления
     */
    public static RedmineStatusEnum fromCode(String code) {
        if (Objects.isNull(code)) {
            return null;
        }
        for (RedmineStatusEnum b : RedmineStatusEnum.values()) {
            if (b.code.equals(code)) {
                return b;
            }
        }
        return null;
    }
}
