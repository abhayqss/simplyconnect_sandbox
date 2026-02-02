package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;
import com.scnsoft.eldermark.entity.basic.StringLegacyIdAwareEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Set;

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
    public static Long ADMIT_DATE_FROM_INTAKE_DATE_ID = 0L;
    public static final int ONE_MB = 1024 * 1024;
    public static final int MAX_FILE_SIZE_MB = 20;
    public final static String EXTERNAL_COMPANY_ID = "EXT";

    public final static String FAMILY_APP_ALTERNATIVE_ID = "FAMILY";

    public static final String COMMUNITY_ELIGIBLE_FOR_DISCOVERY_LEGACY_TABLE = "Company";

    public static String OTHER_RACE_DISPLAY_NAME = "Other Race";
    public static Set<String> AVAILABLE_RACES = Set.of("American Indian or Alaska Native", "Asian",
            "Black or African American", "Native Hawaiian or Other Pacific Islander", "White", OTHER_RACE_DISPLAY_NAME);

    public static void setLegacyId(StringLegacyIdAwareEntity entity) {
        setLegacyId(LEGACY_ID_PREFIX, entity);
    }

    public static void setLegacyId(String legacyIdPrefix, StringLegacyIdAwareEntity entity) {
        if (entity.getId() == null) {
            entity.setLegacyId(legacyIdPrefix);
        } else if (entity.getLegacyId().contains(legacyIdPrefix)) {
            entity.setLegacyId(legacyIdPrefix + entity.getId());
        }
    }

    public static void setLegacyIdFromParent(StringLegacyIdAwareEntity entity, StringLegacyIdAwareEntity parent) {
        String parentId = parent.getId() == null ? "_" : parent.getId().toString();
        if (entity.getId() == null) {
            entity.setLegacyId(LEGACY_ID_PREFIX + parentId + "_" + System.currentTimeMillis());
        } else if (entity.getLegacyId() != null && entity.getLegacyId().contains(LEGACY_ID_PREFIX)) {
            entity.setLegacyId(LEGACY_ID_PREFIX + parentId + "_" + +entity.getId());
        }
    }

    public static void setLegacyId(LegacyIdAwareEntity entity) {
        if (entity.getId() == null) {
            entity.setLegacyId(LEGACY_ID_THRESHOLD);
        } else if (entity.getLegacyId() >= LEGACY_ID_THRESHOLD) {
            entity.setLegacyId(LEGACY_ID_THRESHOLD + entity.getId());
        }
    }

    public static boolean updateLegacyId(StringLegacyIdAwareEntity entity) {
        return updateLegacyId(LEGACY_ID_PREFIX, entity);
    }

    public static boolean updateLegacyId(String legacyIdPrefix, StringLegacyIdAwareEntity entity) {
        String oldLegacyId = entity.getLegacyId();
        setLegacyId(legacyIdPrefix, entity);
        return !StringUtils.equals(oldLegacyId, entity.getLegacyId());
    }

    public static boolean updateLegacyIdFromParent(StringLegacyIdAwareEntity entity, StringLegacyIdAwareEntity parent) {
        String oldLegacyId = entity.getLegacyId();
        setLegacyIdFromParent(entity, parent);
        return !StringUtils.equals(oldLegacyId, entity.getLegacyId());
    }

    public static boolean updateLegacyId(LegacyIdAwareEntity entity) {
        Long oldLegacyId = entity.getLegacyId();
        setLegacyId(entity);
        return !Objects.equals(oldLegacyId, entity.getLegacyId());
    }
}