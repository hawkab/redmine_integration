package ru.hawkab.redmineintegration.util;

import org.junit.jupiter.api.Test;
import ru.hawkab.redmineintegration.annotation.Bijection;
import ru.hawkab.redmineintegration.model.AppealEntity;
import ru.hawkab.redmineintegration.model.AppealKindEntity;
import ru.hawkab.redmineintegration.model.AttachmentEntity;
import ru.hawkab.redmineintegration.model.UserEntity;
import ru.hawkab.redmineintegration.model.enums.AppealStatusEnum;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестирование {@link InterpolateUtil}
 *
 * @author olshansky
 * @since 06.06.2020
 */
public class InterpolateUtilTest {

    /**
     * Проверить, что {@link InterpolateUtil#interpolate} выполняется, как ожидается
     */
    @Test
    public void interpolate() {

        checkRealTargetMessageTemplateInterpolate();
        checkNotExistsFieldInTemplate();
        checkNullObject();

    }

    private void checkNullObject() {
        String messageTemplate = "${appeal.report}";
        String expectedMessage = "(не указано)";
        checkInterpolate(messageTemplate, expectedMessage, null);
    }

    private void checkNotExistsFieldInTemplate() {
        String messageTemplate = "${appeal.notExistsField}";
        String expectedMessage = "(не указано)";
        checkInterpolate(messageTemplate, expectedMessage, getNewAppeal());
    }

    private void checkRealTargetMessageTemplateInterpolate() {
        String messageTemplate = "Отчёт: ${appeal.report}\n" +
                "Категория обращения: <b>${appeal.appealKind.displayName}</b>\n" +
                "Логин: <b>${appeal.user.login}/b> / email: <b>${appeal.user.email}</b> / тел: <b>+${appeal.user.phoneNumber}</b>\n\n" +
                "Текст обращения:\n<p>${appeal.appealMessage}</p>";

        String expectedMessage = "Отчёт: some report name\n" +
                "Категория обращения: <b>Предложение по улучшению</b>\n" +
                "Логин: <b>admin/b> / email: <b>email@mail.ru</b> / тел: <b>+79782043063</b>\n\n" +
                "Текст обращения:\n" +
                "<p>Просьба сделать кнопку чтобы делала всё хорошо, подробности на скриншотах.</p>";

        checkInterpolate(messageTemplate, expectedMessage, getNewAppeal());
    }

    private void checkInterpolate(String messageTemplate, String expectedMessage, AppealEntity appeal) {
        String resultMessage = InterpolateUtil.interpolate(messageTemplate, appeal, "appeal");
        assertEquals(expectedMessage, resultMessage);
    }

    /**
     * Проверить, что {@link InterpolateUtil#getFieldValue} выполняется, как ожидается
     */
    @Test
    public void getFieldValue() throws NoSuchFieldException, IllegalAccessException {
        AppealEntity newAppeal = getNewAppeal();
        //check happy path
        checkFieldValue(newAppeal, "some report name");

        //check npe
        checkFieldValue(null, null);
    }

    private void checkFieldValue(AppealEntity newAppeal, String expectedFieldValue) throws IllegalAccessException, NoSuchFieldException {
        Object resultFieldValue = InterpolateUtil.getFieldValue(AppealEntity.class.getDeclaredField("report"), newAppeal);
        assertEquals(expectedFieldValue, resultFieldValue);
    }


    /**
     * Проверить, что {@link InterpolateUtil#replaceAll} выполняется, как ожидается
     */
    @Test
    public void replaceAll() {

        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("user.displayName", "Иван");

        String expectedTemplate = "Привет, Иван.";
        String resultTemplate = InterpolateUtil.replaceAll(
                "Привет, ${user.displayName}.",
                inputMap,
                "\\$\\{",
                "\\}");

        //check real target case
        assertEquals(expectedTemplate, resultTemplate);

        //check npe
        assertEquals("", InterpolateUtil.replaceAll(
                null,
                null,
                null,
                null));

        //check not leading and trailing not specified
        assertEquals("Привет, ${user.displayName}.", InterpolateUtil.replaceAll(
                "Привет, ${user.displayName}.",
                null,
                null,
                null));

        //check default non-exists message if params not specified
        assertEquals("Привет, (не указано).", InterpolateUtil.replaceAll(
                "Привет, ${user.displayName}.",
                null,
                "\\$\\{",
                "\\}"));

    }

    /**
     * Проверить, что {@link InterpolateUtil#getMapByObject} выполняется, как ожидается
     */
    @Test
    public void getMapByObject() {
        AppealEntity newAppeal = getNewAppeal();

        Map<String, String> expectedMap = new HashMap<>();

        expectedMap.put("appeal.appealMessage", newAppeal.getAppealMessage());
        expectedMap.put("appeal.user.phoneNumber", newAppeal.getUser().getPhoneNumber());
        expectedMap.put("appeal.appealStatus", newAppeal.getAppealStatus().toString());
        expectedMap.put("appeal.user.login", newAppeal.getUser().getLogin());
        expectedMap.put("appeal.attachments[0].uploadedToken", String.valueOf(newAppeal.getAttachments().get(0).getUploadedToken()));
        expectedMap.put("appeal.appealKind.displayName", newAppeal.getAppealKind().getDisplayName());
        expectedMap.put("appeal.errorMessage", String.valueOf(newAppeal.getErrorMessage()));
        expectedMap.put("appeal.user.id", newAppeal.getUser().getId().toString());
        expectedMap.put("appeal.attachments[0].id", newAppeal.getAttachments().get(0).getId().toString());
        expectedMap.put("appeal.report", newAppeal.getReport());
        expectedMap.put("appeal.user.email", newAppeal.getUser().getEmail());
        expectedMap.put("appeal.appealKind.redmineCategoryId", newAppeal.getAppealKind().getRedmineCategoryId());
        expectedMap.put("appeal.appealKind.code", newAppeal.getAppealKind().getCode());
        expectedMap.put("appeal.redmineId", String.valueOf(newAppeal.getRedmineId()));
        expectedMap.put("appeal.id", newAppeal.getId().toString());
        expectedMap.put("appeal.user.displayName", newAppeal.getUser().getDisplayName());
        expectedMap.put("appeal.user.redmineId", newAppeal.getUser().getRedmineId().toString());
        expectedMap.put("appeal.createdDatetime", newAppeal.getCreatedDatetime().toString());
        expectedMap.put("appeal.attachments[0].fileName", newAppeal.getAttachments().get(0).getFileName());
        expectedMap.put("appeal.updatedDatetime", newAppeal.getUpdatedDatetime().toString());
        expectedMap.put("appeal.user.regDate", newAppeal.getUser().getRegDate().toString());
        expectedMap.put("appeal.attachments[0].errorMessage", String.valueOf(newAppeal.getAttachments().get(0).getErrorMessage()));

        Map<String, String> resultMap = InterpolateUtil.getMapByObject(newAppeal, "appeal", new HashSet<>());
        //check happy path
        assertEquals(expectedMap, resultMap);

        //check null prefix
        Map<String, String> mapByObject = InterpolateUtil.getMapByObject(newAppeal, null, null);
        Map<String, String> expectedMapNullPrefix = new HashMap<>();
        expectedMap.keySet().forEach(key ->
            expectedMapNullPrefix.put(key.replace("appeal.", "appealEntity."), expectedMap.get(key))
        );
        assertEquals(expectedMapNullPrefix, mapByObject);

        //check null object
        Map<String, String> resultEmptyMap = InterpolateUtil.getMapByObject(null, null, null);
        assertEquals(new HashMap<String, String>(), resultEmptyMap);
    }

    /**
     * Проверить, что {@link InterpolateUtil#isNotTopLevelHierarchy} выполняется, как ожидается
     */
    @Test
    public void isNotTopLevelHierarchy() {
        //check some entities
        assertTrue(InterpolateUtil.isNotTopLevelHierarchy(new UserEntity()));
        assertTrue(InterpolateUtil.isNotTopLevelHierarchy(new AttachmentEntity()));
        assertTrue(InterpolateUtil.isNotTopLevelHierarchy(new AppealEntity()));

        //check standard java classes in java.lang package
        assertFalse(InterpolateUtil.isNotTopLevelHierarchy(1L));
        assertFalse(InterpolateUtil.isNotTopLevelHierarchy(new Object()));
        assertFalse(InterpolateUtil.isNotTopLevelHierarchy(""));

        //check npe
        assertFalse(InterpolateUtil.isNotTopLevelHierarchy(null));
    }


    public static class SomeEntityWithoutBijection {
        private UserEntity user;
    }

    public static class SomeEntityWithBijection {
        @Bijection
        private UserEntity user;
    }

    /**
     * Проверить, что {@link InterpolateUtil#isBijection} выполняется, как ожидается
     */
    @Test
    public void isBijection() throws NoSuchFieldException {

        //check happy path
        assertTrue(InterpolateUtil.isBijection(SomeEntityWithBijection.class.getDeclaredField("user")));
        assertFalse(InterpolateUtil.isBijection(SomeEntityWithoutBijection.class.getDeclaredField("user")));

        //check npe
        assertFalse(InterpolateUtil.isBijection(null));

    }

    private AppealEntity getNewAppeal() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setLogin("admin");
        user.setDisplayName("Администратор системы");
        user.setEmail("email@mail.ru");
        user.setPhoneNumber("79782043063");
        user.setRegDate(LocalDateTime.now());
        user.setRedmineId(123);

        AppealKindEntity appealKind = new AppealKindEntity();
        appealKind.setCode("FEATURE");
        appealKind.setDisplayName("Предложение по улучшению");
        appealKind.setRedmineCategoryId("111");


        AppealEntity appeal = new AppealEntity();
        appeal.setId(1L);
        appeal.setCreatedDatetime(LocalDateTime.now());
        appeal.setUpdatedDatetime(LocalDateTime.now());
        appeal.setAppealStatus(AppealStatusEnum.CREATED);
        appeal.setAppealKind(appealKind);
        appeal.setAppealMessage("Просьба сделать кнопку чтобы делала всё хорошо, подробности на скриншотах.");
        appeal.setUser(user);
        appeal.setReport("some report name");

        AttachmentEntity attachment = new AttachmentEntity();
        attachment.setId(1L);
        attachment.setAppealEntity(appeal);
        attachment.setFileName("screenshot1.jpg");

        appeal.setAttachments(Collections.singletonList(attachment));
        return appeal;
    }
}