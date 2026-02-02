package com.scnsoft.eldermark.consana.sync.server.model.entity;

public interface LegacyIdAwareEntity<T> {

    T getLegacyId();
    void setLegacyId(T legacyId);

}
