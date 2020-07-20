package ru.hawkab.redmineintegration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hawkab.redmineintegration.model.AppealEntity;
import ru.hawkab.redmineintegration.model.enums.AppealStatusEnum;

import java.util.List;


/**
 * Репозиторий для работы с сущностью пользовательского обращения
 *
 * @author olshansky
 * @since 04.06.2020
 */
@Repository
public interface AppealRepository extends JpaRepository<AppealEntity, Long> {

    /**
     * Получить список пользовательских обращений по указанным статусам
     *
     * @param statuses статусы обращения
     * @return список пользовательских обращений, соответствующих указанным статусам
     */
    @Query(value = "FROM AppealEntity a " +
            "WHERE a.appealStatus IN :statuses")
    List<AppealEntity> findAllByAppealKindIn(List<AppealStatusEnum> statuses);

}
