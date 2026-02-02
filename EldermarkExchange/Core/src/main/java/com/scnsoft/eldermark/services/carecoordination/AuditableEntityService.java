package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.AuditableEntity;

public interface AuditableEntityService<ENTITY extends AuditableEntity, DTO> {
    Long createAuditableEntityFromDto(final DTO dto);
    Long updateAuditableEntityFromDto(final DTO dto);
    void deleteAuditableEntity(final Long entityId);
    Long createAuditableEntityWithoutPostCreate(final ENTITY entity);
    Long updateAuditableEntityWithoutPostCreate(final ENTITY entity);
    Long persistAuditableEntityWithoutPostCreate(final ENTITY entity);
}
