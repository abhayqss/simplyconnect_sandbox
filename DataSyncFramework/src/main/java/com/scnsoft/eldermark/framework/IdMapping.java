package com.scnsoft.eldermark.framework;

import com.scnsoft.eldermark.framework.exceptions.IdMappingException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @param <LegacyIdType> type of entity legacy id
 */
public class IdMapping<LegacyIdType> {
    private final Map<LegacyIdType, Long> legacyToNewIdMap;

    public IdMapping() {
        legacyToNewIdMap = new HashMap<LegacyIdType, Long>();
    }

    public Long getNewId(LegacyIdType legacyId) {
        return legacyToNewIdMap.get(legacyId);
    }

    public long getNewIdOrThrowException(LegacyIdType legacyId) {
        Long id = legacyToNewIdMap.get(legacyId);
        if (id == null) {
            throw new IdMappingException("No corresponding entry for legacy id '" + legacyId + "'");
        }
        return id;
    }

    public void put(LegacyIdType legacyId, Long newId) {
        legacyToNewIdMap.put(legacyId, newId);
    }

    public void putAll(IdMapping<LegacyIdType> idMapping) {
        this.legacyToNewIdMap.putAll(idMapping.legacyToNewIdMap);
    }

    public boolean containsLegacyId(LegacyIdType legacyId) {
        return legacyToNewIdMap.containsKey(legacyId);
    }

    public Collection<Long> getNewIds() {
        return legacyToNewIdMap.values();
    }

    public Set<LegacyIdType> getLegacyIds() {
        return legacyToNewIdMap.keySet();
    }

    public int size() {
        return legacyToNewIdMap.size();
    }

    @Override
    public String toString() {
        return legacyToNewIdMap.toString();
    }
}
