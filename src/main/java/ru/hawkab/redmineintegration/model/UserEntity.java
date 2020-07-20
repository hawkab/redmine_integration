package ru.hawkab.redmineintegration.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Пользователь системы
 *
 * @author olshansky
 * @since 04.06.2020
 */
@Entity
@Table(name = "users")
public class UserEntity extends AbstractEntity {

    /**
     * Идентификатор пользователя
     */
    @Id
    private Long id;

    /**
     * Логин пользователя
     */
    @Column
    private String login;

    /**
     * Отображаемое имя пользователя
     */
    @Column(name = "display_name")
    private String displayName;

    /**
     * Электронный адрес пользователя
     */
    @Column
    private String email;

    /**
     * Номер телефона пользователя
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Дата создания пользователя
     */
    @Column(name = "reg_date")
    private LocalDateTime regDate;

    /**
     * Идентификатор региона в Redmine
     */
    @Column(name = "redmine_id")
    private Integer redmineId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getRegDate() {
        return regDate;
    }

    public void setRegDate(LocalDateTime regDate) {
        this.regDate = regDate;
    }

    public Integer getRedmineId() {
        return redmineId;
    }

    public void setRedmineId(Integer redmineId) {
        this.redmineId = redmineId;
    }
}
