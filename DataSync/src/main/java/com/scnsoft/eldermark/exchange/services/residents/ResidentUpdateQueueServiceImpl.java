package com.scnsoft.eldermark.exchange.services.residents;

import com.scnsoft.eldermark.exchange.dao.target.ResidentUpdateQueueDao;
import com.scnsoft.eldermark.exchange.fk.ResidentIdAware;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.ResidentUpdateQueue;
import com.scnsoft.eldermark.framework.IdMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ResidentUpdateQueueServiceImpl implements ResidentUpdateQueueService {

    @Autowired
    private ResidentUpdateQueueDao residentUpdateQueueDao;

    @Override
    public <T extends ResidentIdAware, Y> void insert(Map<Y, T> foreignKeysMap, List<Y> sourceEntities, String updateType) {
        List<ResidentUpdateQueue> residentUpdateQueue = new ArrayList<ResidentUpdateQueue>();
        Date updatedTime = new Date();
        Set<Long> residentIds = new HashSet<Long>();
        for (Y sourceEntity : sourceEntities) {
            residentIds.add(foreignKeysMap.get(sourceEntity).getResidentId());
        }
        for (Long residentId : residentIds) {
            residentUpdateQueue.add(ResidentUpdateQueue.of(residentId, updateType, updatedTime));
        }
        residentUpdateQueueDao.insert(residentUpdateQueue);
    }

    @Override
    public void insert(Long residentId, String updateType) {
        Date updatedTime = new Date();
        ResidentUpdateQueue residentUpdateQueue = ResidentUpdateQueue.of(residentId, updateType, updatedTime);
        residentUpdateQueueDao.insert(Collections.singletonList(residentUpdateQueue));
    }

    @Override
    public void insert(IdMapping<Long> idMapping, List<ResidentData> sourceEntities, String updateType) {
        List<ResidentUpdateQueue> residentUpdateQueue = new ArrayList<ResidentUpdateQueue>();
        Date updatedTime = new Date();
        Set<Long> residentIds = new HashSet<Long>();
        for (ResidentData sourceEntity : sourceEntities) {
            residentIds.add(idMapping.getNewIdOrThrowException(sourceEntity.getId()));
        }
        for (Long residentId : residentIds) {
            residentUpdateQueue.add(ResidentUpdateQueue.of(residentId, updateType, updatedTime));
        }
        residentUpdateQueueDao.insert(residentUpdateQueue);
    }

    @Override
    public void insert(Collection<Long> residentIds, String updateType) {
        List<ResidentUpdateQueue> residentUpdateQueue = new ArrayList<ResidentUpdateQueue>();
        Date updatedTime = new Date();
        for (Long residentId : residentIds) {
            residentUpdateQueue.add(ResidentUpdateQueue.of(residentId, updateType, updatedTime));
        }
        residentUpdateQueueDao.insert(residentUpdateQueue);
    }
}
