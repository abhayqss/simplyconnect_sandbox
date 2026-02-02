package com.scnsoft.eldermark.exchange.dao.util;

import com.scnsoft.eldermark.framework.IdMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author phomal
 * Created on 3/2/2017.
 */
public class LegacyIdHelper {

    public static List<String> castLegacyIds(List<Long> legacyIdCandidates) {
        List<String> castedLegacyIdCandidates = new ArrayList<String>(legacyIdCandidates.size());
        for (Long legacyIdCandidate : legacyIdCandidates) {
            castedLegacyIdCandidates.add(String.valueOf(legacyIdCandidate));
        }
        return castedLegacyIdCandidates;
    }

    public static IdMapping<Long> uncastIdMapping(IdMapping<String> castedIdMapping) {
        IdMapping<Long> idMapping = new IdMapping<Long>();
        for (String castedLegacyId : castedIdMapping.getLegacyIds()) {
            if (castedIdMapping.containsLegacyId(castedLegacyId)) {
                try {
                    Long legacyId = Long.valueOf(castedLegacyId);
                    Long newId = castedIdMapping.getNewId(castedLegacyId);
                    idMapping.put(legacyId, newId);
                } catch (NumberFormatException ignored) {}
            }
        }
        return idMapping;
    }
}
