package ru.hawkab.redmineintegration.service;

import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Journal;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.hawkab.redmineintegration.model.AppealEntity;
import ru.hawkab.redmineintegration.model.CommentEntity;
import ru.hawkab.redmineintegration.model.enums.AppealStatusEnum;
import ru.hawkab.redmineintegration.model.enums.RedmineStatusEnum;
import ru.hawkab.redmineintegration.repository.AppealRepository;
import ru.hawkab.redmineintegration.util.AppUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;

import static ru.hawkab.redmineintegration.Constants.*;

/**
 * Сервис, осуществляющий обработку изменений по созданным задачам в redmine
 *
 * @author olshansky
 * @since 30.06.2020
 */
@Component
public class RedminePollerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedminePollerService.class);
    public static final int NEW_REDMINE_ISSUE_STATUS_ID = 1;
    public static final int CLOSED_REDMINE_ISSUE_STATUS_ID = 3;
    public static final int REFUSED_REDMINE_ISSUE_STATUS_ID = 4;
    public static final EnumSet<AppealStatusEnum> HANDLING_STATUSES = EnumSet.of(AppealStatusEnum.HANDLING,
            AppealStatusEnum.EXTERNAL_COMMENT,
            AppealStatusEnum.INTERNAL_COMMENT);


    private final RedmineService redmineService;
    private final AppealRepository appealRepository;

    @Autowired
    public RedminePollerService(RedmineService redmineService,
                                AppealRepository appealRepository) {
        this.redmineService = redmineService;
        this.appealRepository = appealRepository;
    }

    /**
     * Получить список обращений, по которым необходимо запросить информацию из Redmine
     *
     * @return список обращений, по которым необходимо запросить информацию из Redmine
     */
    public List<AppealEntity> getActualAppeals() {
        try {
            return appealRepository.findAllByAppealKindIn(
                    Arrays.asList(
                            AppealStatusEnum.SENT,
                            AppealStatusEnum.EXTERNAL_COMMENT,
                            AppealStatusEnum.INTERNAL_COMMENT,
                            AppealStatusEnum.HANDLING
                    )
            );
        } catch (Throwable ex) {
            LOGGER.error(GET_APPEAL_COMMENTS_ERROR_MESSAGE, ex);
        }
        return Collections.emptyList();
    }

    /**
     * Найти обращения пользователей, требующие обработки и обработать их
     */
    @Scheduled(fixedRate = DEFAULT_DELAY_IN_MILLISECONDS_BETWEEN_POLLING_REDMINE)
    @PostConstruct
    public void handleActualAppeals() {
        List<AppealEntity> actualAppeals = getActualAppeals();

        boolean hasActualAppeals = CollectionUtils.isNotEmpty(actualAppeals);

        if (hasActualAppeals) {
            downloadAppealStatusesAndComments(actualAppeals);
        }
    }

    /**
     * Получить статусы и комментарии к issues из redmine
     *
     * @param actualAppeals список заявок, требующих уточнения
     */
    void downloadAppealStatusesAndComments(List<AppealEntity> actualAppeals) {

        actualAppeals.forEach(appeal -> {
            try {
                Issue redmineIssue = redmineService.getIssue(appeal);

                if (Objects.nonNull(redmineIssue)) {
                    if (CollectionUtils.isNotEmpty(redmineIssue.getJournals())) {
                        createCommentsByJournal(appeal, redmineIssue);
                    }

                    switch (redmineIssue.getStatusId()) {
                        case NEW_REDMINE_ISSUE_STATUS_ID:
                            if (AppealStatusEnum.SENT.equals(appeal.getAppealStatus())) {
                                return; //Выход из итерации (аналог continue для Stream API)
                            }
                            appeal.setAppealStatus(AppealStatusEnum.SENT);
                            break;

                        case CLOSED_REDMINE_ISSUE_STATUS_ID:
                            if (AppealStatusEnum.FINISHED.equals(appeal.getAppealStatus())) {
                                return; //Выход из итерации (аналог continue для Stream API)
                            }
                            appeal.setAppealStatus(AppealStatusEnum.FINISHED);
                            break;

                        case REFUSED_REDMINE_ISSUE_STATUS_ID:
                            if (AppealStatusEnum.EXTERNAL_REFUSED.equals(appeal.getAppealStatus())) {
                                return; //Выход из итерации (аналог continue для Stream API)
                            }
                            appeal.setAppealStatus(AppealStatusEnum.EXTERNAL_REFUSED);
                            break;

                        default:
                            if (HANDLING_STATUSES.contains(appeal.getAppealStatus())) {
                                return; //Выход из итерации (аналог continue для Stream API)
                            }
                            appeal.setAppealStatus(AppealStatusEnum.HANDLING);
                    }

                    appeal.setErrorMessage(null);
                    appeal.setUpdatedDatetime(LocalDateTime.now());
                }
            } catch (Throwable ex) {
                appeal.setAppealStatus(AppealStatusEnum.READING_ERROR);
                appeal.setErrorMessage(AppUtils.convertStackTraceToString(ex));
                LOGGER.error(
                        String.format(
                                GET_APPEAL_ERROR_MESSAGE,
                                appeal.getId()
                        ), ex);
                appeal.setUpdatedDatetime(LocalDateTime.now());
            }
        });

        appealRepository.saveAll(actualAppeals);
        appealRepository.flush();
    }

    /**
     * Создать комментарии на основе журнала изменений Redmine Issue.
     * Проставить во внутреннем пользовательском обращении
     * соответствующий статус, если последнее сообщение предполагает ожидание ответа
     *
     * @param appeal внутреннее пользовательское обращение
     * @param redmineIssue внешнее пользовательское обращение (Redmine Issue)
     */
    private void createCommentsByJournal(AppealEntity appeal, Issue redmineIssue) {
        Iterator<Journal> iterator = redmineIssue.getJournals().iterator();
        while (iterator.hasNext()) {
            Journal journal = iterator.next();

            boolean isSystemUser = REDMINE_DEFAULT_SYSTEM_USER_NAME.equalsIgnoreCase(journal.getUser().getFullName());

            if (isSystemUser ||
                    appeal.getComments().stream()
                    .anyMatch(f -> journal.getId().equals(f.getRedmineId()))) {
                continue;
            }


            boolean isUserComment = REDMINE_DEFAULT_API_USER_NAME.equalsIgnoreCase(
                    journal.getUser().getFullName());

            CommentEntity comment = new CommentEntity();
            comment.setRedmineId(journal.getId());
            comment.setRegDate(LocalDateTime.now());
            comment.setAppealEntity(appeal);
            comment.setReaded(false);

            if (StringUtils.isBlank(journal.getNotes())) {
                StringBuilder message = new StringBuilder();
                getMessageFromDetails(journal, message);
                comment.setMessage(message.toString());
            } else {
                comment.setMessage(journal.getNotes());
            }

            comment.setAuthorName(isUserComment ?
                    appeal.getUser().getDisplayName() :
                    journal.getUser().getFullName());

            appeal.getComments().add(comment);

            if (!iterator.hasNext()) {
                if (HANDLING_STATUSES.contains(appeal.getAppealStatus())) {
                    appeal.setAppealStatus(isUserComment ?
                            AppealStatusEnum.INTERNAL_COMMENT :
                            AppealStatusEnum.EXTERNAL_COMMENT);
                }
            }
        }
    }

    /**
     * Получить человекопонятный текст сообщения из истории изменений
     *
     * @param journal элемент истории изменений Redmine Issue
     * @param message текстовое сообщение, сформированное на основании {@code journal} элемента истории изменений Redmine Issue
     */
    private void getMessageFromDetails(Journal journal, StringBuilder message) {
        journal.getDetails().forEach(change -> {
            if (REDMINE_STATUS_ATTRIBUTE_NAME.equalsIgnoreCase(change.getName())) {
                message.append(String.format(CHANGE_STATUS_DEFAULT_MESSAGE,
                        RedmineStatusEnum.fromCode(change.getOldValue()),
                        RedmineStatusEnum.fromCode(change.getNewValue())));
            } else if (REDMINE_DESCRIPTION_ATTRIBUTE_NAME.equalsIgnoreCase(change.getName())) {
                message.append(String.format(CHANGE_DESCRIPTION_DEFAULT_MESSAGE,
                        change.getOldValue(),
                        change.getNewValue()));
            } else if (REDMINE_ATTACHMENT_ATTRIBUTE_NAME.equalsIgnoreCase(change.getProperty())) {
                if (Objects.isNull(change.getNewValue())) {
                    message.append(String.format(DELETE_ATTACHMENT_DEFAULT_MESSAGE,
                            change.getOldValue()));
                } else {
                    message.append(String.format(ATTACHMENT_DEFAULT_MESSAGE,
                            change.getNewValue()));
                }
            } else {
                message.append(String.format(CHANGE_ATTRIBUTE_DEFAULT_MESSAGE,
                        change.getProperty(),
                        change.getOldValue(),
                        change.getNewValue()));
            }
        });
    }
}
