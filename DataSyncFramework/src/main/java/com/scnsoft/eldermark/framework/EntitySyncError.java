package com.scnsoft.eldermark.framework;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;

public final class EntitySyncError<E extends IdentifiableSourceEntity<LegacyId>, LegacyId extends Comparable<LegacyId>> {
    private final E entity;
    private final String errorMessage;

    public EntitySyncError(E entity, String errorMessage) {
        this.entity = entity;
        this.errorMessage = errorMessage;
    }

    public E getEntity() {
        return entity;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
