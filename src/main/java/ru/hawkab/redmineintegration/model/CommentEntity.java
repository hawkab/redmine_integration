package ru.hawkab.redmineintegration.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Generated;
import ru.hawkab.redmineintegration.annotation.Bijection;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * История комментариев к обращению
 *
 * @author olshansky
 * @since 30.06.2020
 */
@Entity
@Table(name = "redmine_comments")
public class CommentEntity extends AbstractEntity {

    /**
     * Идентификатор комментария
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Имя автора комментария
     */
    @Column(name = "author_name")
    private String authorName;

    /**
     * Текст комментария
     */
    @Column
    private String message;

    /**
     * Ссылка на обращение
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "appeal_id", nullable = false)
    @Fetch(FetchMode.JOIN)
    @Bijection
    private AppealEntity appealEntity;

    /**
     * Дата комментария
     */
    @Column(name = "reg_date")
    private LocalDateTime regDate;

    /**
     * Внешний идентификатор комментария из Redmine
     */
    @Column(name = "redmine_id")
    private Integer redmineId;

    /**
     * Флаг, определяющий, что пользователь надлежащим образом уведомлён
     */
    @Column
    private Boolean readed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AppealEntity getAppealEntity() {
        return appealEntity;
    }

    public void setAppealEntity(AppealEntity appealEntity) {
        this.appealEntity = appealEntity;
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

    public Boolean getReaded() {
        return readed;
    }

    public void setReaded(Boolean readed) {
        this.readed = readed;
    }
}
