package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.*;


/**
 * Created by pzhurba on 09-Nov-15.
 */
public class CareCoordinationConstants {
    public static final String RBA_DATABASE = "RBA";
    public static final String RBA_DEFAULT_ORGANIZATION = "Altair ACH";
    public static final String RBA_PERSON_LEGACY_TABLE = "RBA_Person";
    public static final String RBA_ADDRESS_LEGACY_TABLE = "RBA_Address";
    public static final String RBA_PERSON_TELECOM_LEGACY_TABLE = "RBA_PersonTelecom";
    public static final String SIMPLYCONNECT_PHR_PERSON_LEGACY_TABLE = "SCPHR_Person";
    public static final String SIMPLYCONNECT_PHR_ADDRESS_LEGACY_TABLE = "SCPHR_Address";
    public static final String SIMPLYCONNECT_PHR_PERSON_TELECOM_LEGACY_TABLE = "SCPHR_PersonTelecom";
    public static final String CCN_MANUAL_LEGACY_TABLE = "CCN_MANUAL";
    public static final String RBA_NAME_LEGACY_TABLE = "RBA_Name";
    public static final String SIMPLYCONNECT_PHR_NAME_LEGACY_TABLE = "SCPHR_Name";
    public static final String LEGACY_ID_PREFIX = "CCN_";
    public static final String SIMPLYCONNECT_PHR_LEGACY_ID_PREFIX = "SCPHR_";
    public static final Long LEGACY_ID_THRESHOLD = Long.MAX_VALUE / 2;

    public static void setLegacyId(StringLegacyIdAwareEntity entity) {
        entity.setLegacyId(createLegacyId(LEGACY_ID_PREFIX, entity));
    }

    public static String createLegacyId(StringLegacyIdAwareEntity entity) {
        return createLegacyId(LEGACY_ID_PREFIX, entity);
    }

    public static void setLegacyId(String legacyIdPrefix, StringLegacyIdAwareEntity entity) {
        if (entity.getId() == null) {
            entity.setLegacyId(legacyIdPrefix);
        } else if (entity.getLegacyId().contains(legacyIdPrefix)) {
            entity.setLegacyId(legacyIdPrefix + entity.getId());
        }
    }

    public static String createLegacyId(String legacyIdPrefix, StringLegacyIdAwareEntity entity) {
        if (entity.getId() == null) {
            return legacyIdPrefix;
        } else if (entity.getLegacyId().contains(legacyIdPrefix)) {
            return legacyIdPrefix + entity.getId();
        }
        return entity.getLegacyId();
    }

    public static void setLegacyIdFromParent(StringLegacyIdAwareEntity entity, StringLegacyIdAwareEntity parent) {
        String parentId = parent.getId()==null ? "_" : parent.getId().toString();
        if (entity.getId() == null) {
            entity.setLegacyId(LEGACY_ID_PREFIX+parentId+"_"+ System.currentTimeMillis());
        } else if (entity.getLegacyId().contains(LEGACY_ID_PREFIX)) {
            entity.setLegacyId(LEGACY_ID_PREFIX + parentId+"_"+ + entity.getId());
        }
    }

    public static String createLegacyIdFromParent(StringLegacyIdAwareEntity entity, StringLegacyIdAwareEntity parent) {
        String parentId = parent.getId()==null ? "_" : parent.getId().toString();
        if (entity.getId() == null) {
            return LEGACY_ID_PREFIX+parentId+"_"+ System.currentTimeMillis();
        } else if (entity.getLegacyId().contains(LEGACY_ID_PREFIX)) {
            return LEGACY_ID_PREFIX + parentId+"_"+ + entity.getId();
        }
        return entity.getLegacyId();
    }

    public static void setLegacyId(LegacyIdAwareEntity entity) {
        if (entity.getId() == null) {
            entity.setLegacyId(LEGACY_ID_THRESHOLD);
        } else if (entity.getLegacyId() >= LEGACY_ID_THRESHOLD) {
            entity.setLegacyId(LEGACY_ID_THRESHOLD + entity.getId());
        }
    }
}
