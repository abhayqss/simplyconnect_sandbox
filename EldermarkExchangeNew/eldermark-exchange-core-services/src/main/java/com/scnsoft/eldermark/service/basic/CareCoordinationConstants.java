package com.scnsoft.eldermark.service.basic;

import com.scnsoft.eldermark.entity.basic.StringLegacyIdAwareEntity;
import com.scnsoft.eldermark.entity.basic.StringLegacyTableAwareEntity;

import java.util.Date;

public class CareCoordinationConstants {

	public static final String LEGACY_ID_PREFIX = "CCN_";
	public static final String CCN_MANUAL_LEGACY_TABLE = "CCN_MANUAL";
	public static final String RBA_PERSON_TELECOM_LEGACY_TABLE = "RBA_PersonTelecom";
	public static final String RBA_NAME_LEGACY_TABLE = "RBA_Name";

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

	public static void setLegacyId(StringLegacyTableAwareEntity entity) {
		setLegacyId(LEGACY_ID_PREFIX, entity);
	}

	public static void setLegacyId(String legacyIdPrefix, StringLegacyTableAwareEntity entity) {
		if (entity.getId() == null) {			
			entity.setLegacyId(legacyIdPrefix + new Date().getTime());
		} else if (entity.getLegacyId().contains(legacyIdPrefix)) {
			entity.setLegacyId(legacyIdPrefix + entity.getId());
		}
	}
	
	public static void setLegacyIdFromParent(StringLegacyIdAwareEntity entity, StringLegacyIdAwareEntity parent) {
        String parentId = parent.getId()==null ? "_" : parent.getId().toString();
        if (entity.getId() == null) {
            entity.setLegacyId(LEGACY_ID_PREFIX+parentId+"_"+ System.currentTimeMillis());
        } else if (entity.getLegacyId().contains(LEGACY_ID_PREFIX)) {
            entity.setLegacyId(LEGACY_ID_PREFIX + parentId+"_"+ + entity.getId());
        }
    }

}
