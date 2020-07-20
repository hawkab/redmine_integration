package ru.hawkab.redmineintegration.service;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.hawkab.redmineintegration.model.AppealEntity;
import ru.hawkab.redmineintegration.model.enums.AppealStatusEnum;
import ru.hawkab.redmineintegration.repository.AppealRepository;
import ru.hawkab.redmineintegration.util.AppUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ru.hawkab.redmineintegration.Constants.*;

/**
 * Сервис, осуществляющий обработку пользовательских обращений
 *
 * @author olshansky
 * @since 04.06.2020
 */
@Component
public class DatabasePollerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePollerService.class);

    private final RedmineService redmineService;
    private final AppealRepository appealRepository;

    @Autowired
    public DatabasePollerService(RedmineService redmineService,
                                 AppealRepository appealRepository) {
        this.redmineService = redmineService;
        this.appealRepository = appealRepository;
    }

    /**
     * Получить новые необработанные пользовательские обращения
     *
     * @return список новых обращений
     */
    public List<AppealEntity> getActualAppeals() {
        try {
            return appealRepository.findAllByAppealKindIn(
                    Arrays.asList(
                            AppealStatusEnum.CREATED,
                            AppealStatusEnum.SENDING_ERROR
                    )
            );
        } catch (Throwable ex) {
            LOGGER.error(GET_NEW_APPEAL_ERROR_MESSAGE, ex);
        }
        return Collections.emptyList();
    }

    /**
     * Найти обращения пользователей, требующие обработки и обработать их
     */
    @Scheduled(fixedRate = DEFAULT_DELAY_IN_MILLISECONDS_BETWEEN_POLLING_DATABASE)
    @PostConstruct
    public void handleActualAppeals() {
        List<AppealEntity> actualAppeals = getActualAppeals();

        boolean hasActualAppeals = CollectionUtils.isNotEmpty(actualAppeals);

        if (hasActualAppeals) {
            sendAppeals2Redmine(actualAppeals);
        }
    }

    /**
     * Отправить новые заявки в redmine task tracker
     *
     * @param actualAppeals список новых заявок
     */
    void sendAppeals2Redmine(List<AppealEntity> actualAppeals) {

        actualAppeals.forEach(appeal -> {

            try {

                Integer redmineIssueId = redmineService.createIssue(appeal);
                appeal.setRedmineId(redmineIssueId);
                appeal.setAppealStatus(AppealStatusEnum.SENT);
                appeal.setErrorMessage(null);

            } catch (Throwable ex) {

                appeal.setAppealStatus(AppealStatusEnum.SENDING_ERROR);
                appeal.setErrorMessage(AppUtils.convertStackTraceToString(ex));
                LOGGER.error(
                        String.format(
                                RESEND_APPEAL_ERROR_MESSAGE,
                                appeal.getId()
                        ), ex);
            }

            appeal.setUpdatedDatetime(LocalDateTime.now());

        });

        appealRepository.saveAll(actualAppeals);
        appealRepository.flush();
    }
}
