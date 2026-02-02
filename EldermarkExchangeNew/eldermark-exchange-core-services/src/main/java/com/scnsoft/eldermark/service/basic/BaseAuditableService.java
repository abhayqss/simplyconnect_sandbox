package com.scnsoft.eldermark.service.basic;

import com.scnsoft.eldermark.entity.basic.AuditableEntity;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

public abstract class BaseAuditableService<ENTITY extends AuditableEntity> implements AuditableEntityService<ENTITY> {

    @Autowired
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
