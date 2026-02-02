package com.scnsoft.eldermark.consana.sync.client.model.entities;

public interface LegacyIdAwareEntity<T> {

    T getLegacyId();
    void setLegacyId(T legacyId);

}
