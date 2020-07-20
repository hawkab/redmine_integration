package ru.hawkab.redmineintegration.model;

import javax.persistence.*;

/**
 * Категория обращения
 *
 * @author olshansky
 * @since 05.06.2020
 */
@Entity
@Table(name = "appeal_kind")
public class AppealKindEntity extends AbstractEntity {

    /**
     * Идентификатор вложения
     */
    @Id
    @Column
    private String code;

    /**
     * Отображаемое имя категории
     */
    @Column(name = "display_name")
    private String displayName;

    /**
     * Идентификатор категории в redmine
     */
    @Column(name = "redmine_category_id")
    private String redmineCategoryId;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRedmineCategoryId() {
        return redmineCategoryId;
    }

    public void setRedmineCategoryId(String redmineCategoryId) {
        this.redmineCategoryId = redmineCategoryId;
    }
}
