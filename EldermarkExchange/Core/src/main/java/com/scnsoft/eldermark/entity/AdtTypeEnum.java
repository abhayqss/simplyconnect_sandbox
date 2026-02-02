package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.xds.message.*;

public enum AdtTypeEnum {
    A01(1L, "Patient admit", ADTA01.class),
    A03(3L, "Patient discharge", ADTA03.class),
    A04(4L, "Patient registration", ADTA04.class),
    A08(8L, "Patient information update", ADTA08.class);

    private final long code;
    private final String description;
    private final Class<? extends AdtMessage> entityClass;

    AdtTypeEnum(long code, String description, Class<? extends AdtMessage> entityClass) {
        this.code = code;
        this.description = description;
        this.entityClass = entityClass;
    }

    public long getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Class<? extends AdtMessage> getEntityClass() {
        return entityClass;
    }

    public static AdtTypeEnum byCode(Long code) {
        if (code == null) {
            return null;
        }
        for (AdtTypeEnum adtType: AdtTypeEnum.values()) {
            if (adtType.getCode() == code) {
                return adtType;
            }
        }
        return null;
    }

    public static AdtTypeEnum byEntityClass(Class<? extends AdtMessage> entityClass) {
        if (entityClass == null) {
            return null;
        }
        for (AdtTypeEnum adtType: AdtTypeEnum.values()) {
            if (adtType.getEntityClass().equals(entityClass)) {
                return adtType;
            }
        }
        return null;
    }

}
