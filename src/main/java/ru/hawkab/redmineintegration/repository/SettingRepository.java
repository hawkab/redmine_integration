package ru.hawkab.redmineintegration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hawkab.redmineintegration.model.SettingEntity;


/**
 * Репозиторий для работы с настройками системы
 *
 * @author olshansky
 * @since 05.06.2020
 */
@Repository
public interface SettingRepository extends JpaRepository<SettingEntity, String> { }
