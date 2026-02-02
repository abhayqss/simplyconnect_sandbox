package org.openhealthtools.openxds.dao;


import org.openhealthtools.openxds.entity.LegacyIdAwareEntity;
import org.openhealthtools.openxds.entity.StringLegacyIdAwareEntity;

import java.util.Random;


public class LegacyIdResolver {
    public static final String LEGACY_ID_PREFIX = "ADT_";
    public static final Long LEGACY_ID_THRESHOLD = Long.MAX_VALUE / 2;
    private static Random rand = new Random();

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
