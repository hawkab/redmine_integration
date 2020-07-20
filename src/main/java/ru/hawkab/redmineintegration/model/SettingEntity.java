package ru.hawkab.redmineintegration.model;

import javax.persistence.*;

/**
 * Настройка системы
 *
 * @author olshansky
 * @since 05.06.2020
 */
@Entity
@Table(name = "settings")
public class SettingEntity extends AbstractEntity {

    /**
     * Уникальный символьный код настройки
     */
    @Id
    private String code;

    /**
     * Значение настройки
     */
    @Column
    private String value;

    /**
     * Описание настройки
     */
    @Column
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
