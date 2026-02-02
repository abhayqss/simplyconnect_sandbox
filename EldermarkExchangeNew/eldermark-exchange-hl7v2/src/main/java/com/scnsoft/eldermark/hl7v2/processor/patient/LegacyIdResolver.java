package com.scnsoft.eldermark.hl7v2.processor.patient;

import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;
import com.scnsoft.eldermark.entity.basic.StringLegacyIdAwareEntity;

public class LegacyIdResolver {
    public static final String LEGACY_ID_PREFIX = "ADT_";
    public static final Long LEGACY_ID_THRESHOLD = Long.MAX_VALUE / 2;

    public static void setLegacyId(StringLegacyIdAwareEntity entity) {
        if (entity == null)
            return;

        if (entity.getId() == null) {
            entity.setLegacyId(LEGACY_ID_PREFIX + System.nanoTime());
        } else if (entity.getLegacyId().contains(LEGACY_ID_PREFIX)) {
            entity.setLegacyId(LEGACY_ID_PREFIX + entity.getId());
        }
    }

    public static void setLegacyId(LegacyIdAwareEntity entity) {
        if (entity == null)
            return;

        if (entity.getId() == null) {
            entity.setLegacyId(LEGACY_ID_THRESHOLD + System.nanoTime());
        } else if (entity.getLegacyId() >= LEGACY_ID_THRESHOLD) {
            entity.setLegacyId(LEGACY_ID_THRESHOLD + entity.getId());
        }
    }
}
