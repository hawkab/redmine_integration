package ru.hawkab.redmineintegration.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.hawkab.redmineintegration.model.SettingEntity;
import ru.hawkab.redmineintegration.repository.SettingRepository;

import java.util.Optional;

import static ru.hawkab.redmineintegration.Constants.*;

/**
 * Сервис работы с настройками системы
 *
 * @author olshansky
 * @since 05.06.2020
 */
@Component
public class SettingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingService.class);

    private final SettingRepository repository;

    @Autowired
    public SettingService(SettingRepository settingRepository) {
        this.repository = settingRepository;
    }

    /**
     * Получить значение настройки по кодовому идентификатору
     *
     * @param key код настройки
     * @return значением настройки
     */
    public String getSettingValueOrDefault(String key, String defaultVal) {
        Optional<SettingEntity> settingEntity = repository.findById(key);
        if (settingEntity.isEmpty()) {
            LOGGER.error(String.format(SETTING_NOT_EXISTS, key));
            return defaultVal;
        }
        return settingEntity.get().getValue();
    }

}
