package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.fk.ResidentIdAware;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.framework.IdMapping;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ResidentUpdateQueueService {
    <T extends ResidentIdAware, Y> void insert(Map<Y, T> foreignKeysMap, List<Y> sourceEntities, String updateType);
    void insert(Long residentId, String updateType);
    void insert(IdMapping<Long> idMapping, List<ResidentData> sourceEntities, String updateType);
    void insert(Collection<Long> residentIds, String updateType);
}
