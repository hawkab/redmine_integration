package ru.hawkab.redmineintegration.service;

import com.taskadapter.redmineapi.Include;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.Attachment;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.internal.Transport;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hawkab.redmineintegration.model.AppealEntity;
import ru.hawkab.redmineintegration.model.AttachmentEntity;
import ru.hawkab.redmineintegration.repository.AttachmentRepository;
import ru.hawkab.redmineintegration.util.AppUtils;
import ru.hawkab.redmineintegration.util.InterpolateUtil;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ru.hawkab.redmineintegration.Constants.*;


/**
 * Сервис, реализующий функции доставки сообщений в redmine task tracker посредством redmine api
 *
 * @author olshansky
 * @since 04.06.2020
 */
@Service
public class RedmineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedmineService.class);

    private final RedmineManager redmineApi;
    private final AttachmentRepository attachmentRepository;
    private final SettingService settingService;

    @Autowired
    public RedmineService(RedmineManager redmineApi,
                          AttachmentRepository appealRepository,
                          SettingService settingService) {
        this.redmineApi = redmineApi;
        this.attachmentRepository = appealRepository;
        this.settingService = settingService;
    }

    /**
     * Получить issue из системы управления задачами - Redmine
     *
     * @param appeal            Обращение пользователя
     * @return                  Объект redmine issue в случае успеха
     * @throws RedmineException Исключение, возбуждаемое в случае ошибки интеграции с Redmine
     */
    public Issue getIssue(AppealEntity appeal) throws RedmineException {
        Issue redmineIssue = redmineApi.getIssueManager().getIssueById(appeal.getRedmineId(), Include.journals);
        return redmineIssue;
    }

    /**
     * Создать issue в системе управления задачами - Redmine
     *
     * @param appeal            Обращение пользователя
     * @return                  Идентификатор созданного issue в случае успеха
     * @throws RedmineException Исключение, возбуждаемое в случае ошибки интеграции с Redmine
     */
    public Integer createIssue(AppealEntity appeal) throws RedmineException {
        Transport transport = redmineApi.getTransport();

        Issue issue = new Issue(transport,
                Integer.parseInt(settingService.getSettingValueOrDefault(
                        REDMINE_PROJECT_ID_SETTING_NAME,
                        String.valueOf(DEFAULT_PROJECT_ID)))
                )
                .setDescription(getIssueDescriptionByAppeal(appeal))
                .setSubject(appeal.getAppealKind().getDisplayName())
                .addCustomField(getCategoryByAppeal(appeal))
                .addCustomField(getRegionByAppeal(appeal))
                .addCustomField(getSystemKindByAppeal());

        List<AttachmentEntity> internalAttachments = appeal.getAttachments();

        boolean hasAttachments = CollectionUtils.isNotEmpty(internalAttachments);
        if (hasAttachments) {
            uploadAttachments(transport, issue, internalAttachments);
        }

        return issue.create().getId();
    }

    /**
     * Получить категорию обращения в терминологии Redmine
     *
     * @param appeal пользовательская заявка из Pentaho BI
     * @return Redmine категория обращения
     */
    CustomField getCategoryByAppeal(AppealEntity appeal) {
        return new CustomField()
                .setId(DEFAULT_CATEGORY_CATEGORY_ID)
                .setName(DEFAULT_CATEGORY_CATEGORY_NAME)
                .setValue(appeal.getAppealKind().getRedmineCategoryId());
    }

    /**
     * Получить идентификатор региона в терминологии Redmine
     *
     * @param appeal пользовательская заявка из Pentaho BI
     * @return Redmine идентификатор региона
     */
    private CustomField getRegionByAppeal(AppealEntity appeal) {
        return new CustomField()
                .setId(DEFAULT_REGION_CATEGORY_ID)
                .setName(DEFAULT_REGION_CATEGORY_NAME)
                .setValue(appeal.getUser().getRedmineId().toString());
    }

    /**
     * Получить идентификатор системы-источника ошибки в терминологии Redmine
     *
     * @return Redmine идентификатор системы-источника ошибки
     */
    private CustomField getSystemKindByAppeal() {
        return new CustomField()
                .setId(DEFAULT_SYSTEM_CATEGORY_ID)
                .setName(DEFAULT_SYSTEM_CATEGORY_NAME)
                .setValue(DEFAULT_SYSTEM_CATEGORY_VALUE);
    }

    /**
     * Загрузить вложения в Redmine и сохранить внешние идентификаторы файлов
     *
     * @param transport           Транспортная компонента Redmine API
     * @param issue               Создаваемый issue (задача) в Redmine
     * @param internalAttachments Список вложений, подлежащий загрузке на сервер Redmine
     */
    void uploadAttachments(Transport transport,
                           Issue issue,
                           List<AttachmentEntity> internalAttachments) {

        internalAttachments.stream()
                .filter(f -> StringUtils.isBlank(f.getUploadedToken()))
                .forEach(attachment -> {
            byte[] content = attachmentRepository.getContent(attachment.getId());
            if (Objects.nonNull(content)) {
                try {
                    String uploadedFileToken = transport.upload(
                            new ByteArrayInputStream(content),
                            content.length
                    );

                    attachment.setUploadedToken(uploadedFileToken);
                    attachment.setErrorMessage(StringUtils.EMPTY);
                    Attachment externalAttachment = new Attachment(transport)
                            .setToken(uploadedFileToken)
                            .setFileName(attachment.getFileName())
                            .setFileSize((long) content.length);

                    issue.addAttachment(externalAttachment);

                } catch (RedmineException ex) {
                    attachment.setUploadedToken(null);
                    attachment.setErrorMessage(AppUtils.convertStackTraceToString(ex));
                    LOGGER.error(
                            String.format(
                                    UPLOAD_FILE_ERROR_MESSAGE,
                                    attachment.getId()
                            ), ex);
                }
            }
        });

        attachmentRepository.saveAll(internalAttachments);
        attachmentRepository.flush();
    }

    /**
     * Сформировать текст Issue исходя из объекта обращения
     *
     * @param appeal    обращение
     * @return          текст issue, который включает в себя контактные данные пользователя
     * для обратной связи и текст обращения
     */
    String getIssueDescriptionByAppeal(AppealEntity appeal) {
        String issueText = settingService.getSettingValueOrDefault(
                ISSUE_DESCRIPTION_MESSAGE_SETTING_NAME,
                DEFAULT_ISSUE_DESCRIPTION_MESSAGE);
        return InterpolateUtil.interpolate(issueText, appeal, "appeal");
    }
}
