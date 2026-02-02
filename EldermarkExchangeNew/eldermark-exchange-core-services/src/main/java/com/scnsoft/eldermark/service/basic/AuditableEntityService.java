package com.scnsoft.eldermark.service.basic;

import com.scnsoft.eldermark.entity.basic.AuditableEntity;
import com.scnsoft.eldermark.entity.basic.AuditableEntityStatus;
import com.scnsoft.eldermark.exception.BusinessException;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.time.Instant;

public interface AuditableEntityService<ENTITY extends AuditableEntity> {

    Logger logger = LoggerFactory.getLogger(AuditableEntityService.class);

    ENTITY save(ENTITY entity);

    ENTITY findById(Long id);

    default Long createAuditableEntity(ENTITY entity) {
        entity.setArchived(Boolean.FALSE);
        entity.setAuditableStatus(AuditableEntityStatus.CREATED);
        entity.setLastModifiedDate(Instant.now());
        ENTITY result = save(entity);
        return result.getId();
    }

    default Long updateAuditableEntity(ENTITY entity) {
        Long previousId = entity.getId();

        if (getEntityManager().contains(entity)) {
            //in case entity is in persistent state - transient copy should be created. Otherwise save will fail
            //saying that id was altered. Simply detaching entity from entityManager will not work
            //in case there are OneToMany relations.
            logger.info("Auditable entity is already persistent, creating a transient clone...");
            entity = createTransientClone(entity);
        }

        updateRelatedTrackedEntities(entity);
        archiveEntity(previousId);
        entity.setId(null);
        entity.setArchived(Boolean.FALSE);
        entity.setAuditableStatus(AuditableEntityStatus.UPDATED);
        entity.setLastModifiedDate(Instant.now());
        ENTITY previousEntity = findById(previousId);
        if (previousEntity.getChainId() == null)
            entity.setChainId(previousId);
        else
            entity.setChainId(previousEntity.getChainId());

        ENTITY result = save(entity);
        return result.getId();
    }

    default void updateRelatedTrackedEntities(ENTITY entity) {}

    default ENTITY createTransientClone(ENTITY entity) {
        throw new NotImplementedException("Please implement entity transient state copy creation.");
    }

    EntityManager getEntityManager();

    default Long deleteAuditableEntity(ENTITY entity) {
        Long previousId = entity.getId();

        if (getEntityManager().contains(entity)) {
            //in case entity is in persistent state - transient copy should be created. Otherwise save will fail
            //saying that id was altered. Simply detaching entity from entityManager will not work
            //in case there are OneToMany relations.
            logger.info("Auditable entity is already persistent, creating a transient clone...");
            entity = createTransientClone(entity);
        }
        archiveEntity(previousId);
        entity.setId(null);
        entity.setAuditableStatus(AuditableEntityStatus.DELETED);
        entity.setArchived(Boolean.TRUE);
        entity.setLastModifiedDate(Instant.now());
        ENTITY previousEntity = findById(previousId);
        if (previousEntity.getChainId() == null)
            entity.setChainId(previousId);
        else
            entity.setChainId(previousEntity.getChainId());
        return save(entity).getId();
    }

    private void archiveEntity(Long id) {
        archiveEntity(findById(id));
    }

    private void archiveEntity(ENTITY entity) {
        if (Boolean.TRUE.equals(entity.getArchived())) {
            throw new BusinessException("Modified already archived entity with id = " + entity.getId());
        }
        entity.setArchived(Boolean.TRUE);
        save(entity);
    }
}
