package ru.hawkab.redmineintegration.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.hawkab.redmineintegration.annotation.Bijection;

import javax.persistence.*;

/**
 * Вложения к пользовательскому обращению (файлы)
 *
 * @author olshansky
 * @since 04.06.2020
 */
@Entity
@Table(name = "attachments")
public class AttachmentEntity extends AbstractEntity {

    /**
     * Идентификатор вложения
     */
    @Id
    private Long id;

    /**
     * Идентификатор обращения
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "appeal_id", nullable = false)
    @Fetch(FetchMode.JOIN)
    @Bijection
    private AppealEntity appealEntity;

    /**
     * Имя файла
     */
    @Column(name = "file_name")
    private String fileName;

    /**
     * Внешний идентификатор файла в системе Redmine
     */
    @Column(name = "uploaded_token")
    private String uploadedToken;

    /**
     * Текст ошибки, которая произошла во время загрузки файла на Redmine сервер
     */
    @Column(name = "error_message")
    private String errorMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppealEntity getAppealEntity() {
        return appealEntity;
    }

    public void setAppealEntity(AppealEntity appealEntity) {
        this.appealEntity = appealEntity;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUploadedToken() {
        return uploadedToken;
    }

    public void setUploadedToken(String uploadedToken) {
        this.uploadedToken = uploadedToken;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
