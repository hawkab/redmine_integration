package ru.hawkab.redmineintegration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hawkab.redmineintegration.model.AppealEntity;
import ru.hawkab.redmineintegration.model.AttachmentEntity;
import ru.hawkab.redmineintegration.model.enums.AppealStatusEnum;

import java.util.List;


/**
 * Репозиторий для работы с сущностью пользовательского обращения
 *
 * @author olshansky
 * @since 04.06.2020
 */
@Repository
public interface AttachmentRepository extends JpaRepository<AttachmentEntity, Long> {

    /**
     * Получить контент вложения
     *
     * @param id Идентификатор вложения
     * @return контент
     */
    @Query(value = "SELECT file_content from {h-schema}attachments where id=:id",
            nativeQuery = true)
    byte[] getContent(@Param("id") Long id);

}
