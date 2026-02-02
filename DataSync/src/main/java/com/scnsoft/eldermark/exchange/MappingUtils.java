package com.scnsoft.eldermark.exchange;

import com.scnsoft.eldermark.exchange.fk.ResidentForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.framework.DatabaseIdWithId;

import java.util.Map;

public class MappingUtils {

    public static boolean hasMappingForResidentOrganization(ResidentData sourceResident, Map<ResidentData, ResidentForeignKeys> foreignKeysMap,
                                                             Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        ResidentForeignKeys foreignKeys = foreignKeysMap.get(sourceResident);
        if (foreignKeys == null || foreignKeys.getFacilityOrganizationId() == null) {
            return false;
        }
        return targetOrganizationsIdMapping.containsKey(foreignKeys.getFacilityOrganizationId());
    }

    public static boolean hasMappingForResidentOrganization(Long organizationId,
                                                            Map<Long, DatabaseIdWithId> targetOrganizationsIdMapping) {
        return targetOrganizationsIdMapping.containsKey(organizationId);
    }

    public static String generateMappedLegacyId(long sourceLegacyId, long sourceId) {
        return String.valueOf(sourceLegacyId) + "_M_" + String.valueOf(sourceId);
    }

    public static String generateMappedLegacyId(String sourceLegacyId, long sourceId) {
        return sourceLegacyId + "_M_" + String.valueOf(sourceId);
    }

    public static boolean hasMappingForResident(Long residentId, Map<Long, DatabaseIdWithId> mapping) {
        if (residentId == null) {
            return false;
        }
        return mapping.containsKey(residentId);
    }

}
