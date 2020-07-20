package ru.hawkab.redmineintegration.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.hawkab.redmineintegration.model.enums.AppealStatusEnum;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Пользовательские обращения
 *
 * @author olshansky
 * @since 04.06.2020
 */
@Entity
@Table(name = "user_appeal")
public class AppealEntity extends AbstractEntity {

    /**
     * Идентификатор обращения
     */
    @Id
    private Long id;

    /**
     * Пользователь - автор обращения
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @Fetch(FetchMode.JOIN)
    private UserEntity user;

    /**
     * Отчёт по которому обращение
     */
    @Column
    private String report;

    /**
     * Категория обращения
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "appeal_kind", nullable = false)
    @Fetch(FetchMode.JOIN)
    private AppealKindEntity appealKind;

    /**
     * Текст обращения
     */
    @Column(name = "appeal_message")
    private String appealMessage;

    /**
     * Идентификатор issue в Redmine
     */
    @Column(name = "redmine_id")
    private Integer redmineId;

    /**
     * Статус обращения в Redmine
     */
    @Column(name = "appeal_status")
    @Enumerated(EnumType.STRING)
    private AppealStatusEnum appealStatus;

    /**
     * Дата создания обращения
     */
    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime;

    /**
     * Дата изменения обращения
     */
    @Column(name = "updated_datetime")
    private LocalDateTime updatedDatetime;

    /**
     * Приложенные файлы
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "appealEntity", orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<AttachmentEntity> attachments = new ArrayList<>();

    /**
     * Текст ошибки, которая произошла во время обработки обращения
     */
    @Column(name = "error_message")
    private String errorMessage;

    /**
     * Комментарии из redmine
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "appealEntity", orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<CommentEntity> comments = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public AppealKindEntity getAppealKind() {
        return appealKind;
    }

    public void setAppealKind(AppealKindEntity appealKind) {
        this.appealKind = appealKind;
    }

    public String getAppealMessage() {
        return appealMessage;
    }

    public void setAppealMessage(String appealMessage) {
        this.appealMessage = appealMessage;
    }

    /**
     * Получить идентификатор Issue в Redmine
     * @return идентификатор Issue в Redmine
     */
    public Integer getRedmineId() {
        return redmineId;
    }

    public void setRedmineId(Integer redmineId) {
        this.redmineId = redmineId;
    }

    public AppealStatusEnum getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(AppealStatusEnum appealStatus) {
        this.appealStatus = appealStatus;
    }

    public LocalDateTime getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(LocalDateTime createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public LocalDateTime getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(LocalDateTime updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }

    public List<AttachmentEntity> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentEntity> attachments) {
        this.attachments = attachments;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }
}
