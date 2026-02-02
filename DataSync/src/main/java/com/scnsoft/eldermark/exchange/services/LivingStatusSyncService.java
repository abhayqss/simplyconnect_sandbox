package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.target.LivingStatusDao;
import com.scnsoft.eldermark.exchange.model.source.LivingStatusData;
import com.scnsoft.eldermark.exchange.model.target.LivingStatus;
import com.scnsoft.eldermark.exchange.resolvers.LivingStatusIdResolver;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class LivingStatusSyncService extends
        StandardSyncService<LivingStatusData, Long, Void> {
    @Autowired
    @Qualifier("livingStatusSourceDao")
    private StandardSourceDao<LivingStatusData, Long> sourceDao;

    @Autowired
    private LivingStatusDao targetDao;

    @Value("${livingstatus.idmapping.cache.size}")
    private int idMappingSizeLimit;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        return Collections.emptyList();
    }

    @Override
    protected StandardSourceDao<LivingStatusData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(LivingStatusData.TABLE_NAME, LivingStatusData.UNIQUE_ID, LivingStatusData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return targetDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<Void> resolveForeignKeys(DatabaseSyncContext syncContext, LivingStatusData sourceEntity) {
        return null;
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<LivingStatusData> sourceEntities,
                                       Map<LivingStatusData, Void> foreignKeysMap) {
        List<LivingStatus> livingStatusList = new ArrayList<LivingStatus>();
        for (LivingStatusData sourceEntity : sourceEntities) {

            LivingStatus livingStatus = new LivingStatus();
            livingStatus.setLegacyId(sourceEntity.getId());
            livingStatus.setDatabaseId(syncContext.getDatabaseId());
            livingStatus.setUpdatable(createUpdatable(sourceEntity));

            livingStatusList.add(livingStatus);
        }
        targetDao.insert(livingStatusList);
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<LivingStatusData> sourceEntities,
                                    Map<LivingStatusData, Void> foreignKeysMap,
                                    IdMapping<Long> idMapping) {
        for (LivingStatusData sourceEntity : sourceEntities) {
            long id = idMapping.getNewIdOrThrowException(sourceEntity.getId());
            targetDao.update(createUpdatable(sourceEntity), id);
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        targetDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
        final IdMapping<Long> idMapping = targetDao.getIdMapping(context.getDatabase(), idMappingSizeLimit);
        context.putSharedObject(LivingStatusIdResolver.class, new LivingStatusIdResolver() {
            @Override
            public long getId(long legacyId, DatabaseInfo database) {
                Long newId = idMapping.getNewId(legacyId);
                if (newId == null) {
                    newId = targetDao.getId(database, legacyId);
                }
                return newId;
            }
        });
    }

    private LivingStatus.Updatable createUpdatable(LivingStatusData sourceEntity) {
        LivingStatus.Updatable updatable = new LivingStatus.Updatable();

        updatable.setDescription(sourceEntity.getDescription());

        return updatable;
    }
}
