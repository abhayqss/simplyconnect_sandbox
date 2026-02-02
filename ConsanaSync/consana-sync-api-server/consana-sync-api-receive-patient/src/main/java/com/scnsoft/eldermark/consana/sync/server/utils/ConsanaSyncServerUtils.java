package com.scnsoft.eldermark.consana.sync.server.utils;

import com.scnsoft.eldermark.consana.sync.server.model.entity.StringLegacyIdAwareEntity;

import static com.scnsoft.eldermark.consana.sync.server.constants.FhirConstants.MAX_LENGTH_UUID;
import static java.util.UUID.randomUUID;
import static com.scnsoft.eldermark.consana.sync.server.constants.ConsanaSyncApiReceivePatientConstants.LEGACY_ID_PREFIX;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

public class ConsanaSyncServerUtils {

    public static boolean updateLegacyId(StringLegacyIdAwareEntity entity, String newLegacyId) {
        if (!equalsIgnoreCase(newLegacyId, entity.getLegacyId())) {
            entity.setLegacyId(newLegacyId);
            return true;
        }
        return false;
    }

    public static String createLegacyId(StringLegacyIdAwareEntity entity) {
        return createLegacyId(LEGACY_ID_PREFIX, entity);
    }

    public static String createLegacyId(String legacyIdPrefix, StringLegacyIdAwareEntity entity) {
        if (entity.getId() == null) {
            return legacyIdPrefix + randomUUID().toString().substring(0, MAX_LENGTH_UUID);
        } else if (entity.getLegacyId().contains(legacyIdPrefix)) {
            return legacyIdPrefix + entity.getId();
        }
        return entity.getLegacyId();
    }

    public static String createLegacyIdFromParent(StringLegacyIdAwareEntity entity, StringLegacyIdAwareEntity parent) {
        String parentId = ofNullable(parent.getId()).map(parId -> parId.toString()).orElse( "_");
        if (entity.getId() == null) {
            return LEGACY_ID_PREFIX + parentId + "_" + System.currentTimeMillis();
        } else if (entity.getLegacyId() != null && entity.getLegacyId().contains(LEGACY_ID_PREFIX)) {
            return LEGACY_ID_PREFIX + parentId + "_" + entity.getId();
        }
        return entity.getLegacyId();
    }

    public static void setLegacyIdFromParent(StringLegacyIdAwareEntity entity, StringLegacyIdAwareEntity parent) {
        String parentId = ofNullable(parent.getId()).map(parId -> parId.toString()).orElse( "_");
        if (entity.getId() == null) {
            entity.setLegacyId(LEGACY_ID_PREFIX + parentId + "_" + System.currentTimeMillis());
        } else if (entity.getLegacyId().contains(LEGACY_ID_PREFIX)) {
            entity.setLegacyId(LEGACY_ID_PREFIX + parentId + "_" + entity.getId());
        }
    }

    public static String normalizeName(String str) {
        return ofNullable(str)
                .map(s -> s.toLowerCase().replaceAll("[' \\-]", ""))
                .orElse(null);
    }

}
