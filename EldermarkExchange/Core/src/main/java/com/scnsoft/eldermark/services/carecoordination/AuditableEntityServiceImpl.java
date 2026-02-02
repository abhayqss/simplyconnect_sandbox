package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.AuditableEntity;
import com.scnsoft.eldermark.entity.AuditableEntityStatus;
import com.scnsoft.eldermark.services.exceptions.BusinessException;

import java.util.Date;

public abstract class AuditableEntityServiceImpl<ENTITY extends AuditableEntity, DTO> implements AuditableEntityService<ENTITY, DTO> {

    protected abstract ENTITY save(ENTITY entity);

    protected abstract ENTITY findById(Long id);

    protected abstract ENTITY dtoToEntity(DTO dto);

    /**
     * Performs actions after creating an entity
     *
     * @param entity - recently created entity
     * @param dto    - dto that comes from UI
     */
    //todo refactor - make this method not abstract and implement with empty body?
    protected abstract void postCreate(final ENTITY entity, final DTO dto);

    @Override
    public Long createAuditableEntityFromDto(final DTO dto) {
        final ENTITY entity = dtoToEntity(dto);
        Long result = createAuditableEntityWithoutPostCreate(entity);
        postCreate(entity, dto);
        return result;
    }

    @Override
    public Long updateAuditableEntityFromDto(final DTO dto) {
        final ENTITY entity = dtoToEntity(dto);
        Long result = updateAuditableEntityWithoutPostCreate(entity);
        postCreate(entity, dto);
        return result;
    }

    @Override
    public Long createAuditableEntityWithoutPostCreate(ENTITY entity) {
        entity.setArchived(Boolean.FALSE);
        entity.setStatus(AuditableEntityStatus.CREATED);
        entity.setLastModifiedDate(new Date());
//        entity.setModifiedBy(SecurityUtils.getAuthenticatedUser().getEmployee());
        ENTITY result = save(entity);
        return result.getId();
    }

    @Override
    public Long updateAuditableEntityWithoutPostCreate(ENTITY entity) {
        updateRelatedTrackedEntities(entity);
        final ENTITY previousEntity = findById(entity.getId());
        archiveEntity(previousEntity);
        entity.setId(null);
        entity.setArchived(Boolean.FALSE);
        entity.setStatus(AuditableEntityStatus.UPDATED);
        entity.setLastModifiedDate(new Date());
//        entity.setModifiedBy(SecurityUtils.getAuthenticatedUser().getEmployee());
        entity.setChainId(previousEntity.getChainId() == null ? previousEntity.getId() : previousEntity.getChainId());
        ENTITY result = save(entity);
        return result.getId();
    }

    protected void updateRelatedTrackedEntities(ENTITY entity) {
    }

    @Override
    public Long persistAuditableEntityWithoutPostCreate(ENTITY entity) {
        if (entity.getId() == null) {
            return createAuditableEntityWithoutPostCreate(entity);
        } else {
            return updateAuditableEntityWithoutPostCreate(entity);
        }
    }

    @Override
    public void deleteAuditableEntity(final Long entityId) {
        final ENTITY entity = findById(entityId);
        archiveEntity(entity);
        entity.setId(null);
        entity.setStatus(AuditableEntityStatus.DELETED);
        entity.setArchived(Boolean.FALSE);
//        entity.setModifiedBy(SecurityUtils.getAuthenticatedUser().getEmployee());
        save(entity);
    }

    private void archiveEntity(ENTITY entity) {
        if (entity.getArchived()) {
            throw new BusinessException("Modified already archived entity with id = " + entity.getId());
        }
        entity.setArchived(Boolean.TRUE);
        save(entity);
    }
}
