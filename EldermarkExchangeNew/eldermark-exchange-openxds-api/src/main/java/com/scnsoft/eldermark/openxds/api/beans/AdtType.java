package com.scnsoft.eldermark.openxds.api.beans;

import com.scnsoft.eldermark.entity.xds.message.*;

public enum AdtType {
    A01("Patient admit", ADTA01.class),
    A03("Patient discharge", ADTA03.class),
    A04("Patient registration", ADTA04.class),
    A05("Patient pre-admit", ADTA05.class),
    A08("Patient information update", ADTA08.class),
    A60("Update Adverse Reaction Information", ADTA60.class);

    private final String description;
    private final Class<? extends AdtMessage> entityClass;

    AdtType(String description, Class<? extends AdtMessage> entityClass) {
        this.description = description;
        this.entityClass = entityClass;
    }

    public String getDescription() {
        return description;
    }

    public Class<? extends AdtMessage> getEntityClass() {
        return entityClass;
    }
}
