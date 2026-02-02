package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.target.CommunicationTypeDao;
import com.scnsoft.eldermark.exchange.model.source.CommunicationTypeData;
import com.scnsoft.eldermark.exchange.model.target.CommunicationType;
import com.scnsoft.eldermark.exchange.resolvers.CommunicationTypeIdResolver;
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
public class CommunicationTypeSyncService extends StandardSyncService<CommunicationTypeData, String, Void> {
    @Autowired
    @Qualifier("communicationTypeSourceDao")
    private StandardSourceDao<CommunicationTypeData, String> sourceDao;

    @Value("${communicationtypes.idmapping.cache.size}")
    private int idMappingPreloadSize;

    @Autowired
    private CommunicationTypeDao communicationTypeDao;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        return Collections.emptyList();
    }

    @Override
    protected StandardSourceDao<CommunicationTypeData, String> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(CommunicationTypeData.TABLE_NAME, CommunicationTypeData.CODE,
                CommunicationTypeData.class);
    }

    @Override
    protected IdMapping<String> getIdMapping(DatabaseSyncContext syncContext, List<String> legacyIds) {
        return communicationTypeDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<Void> resolveForeignKeys(DatabaseSyncContext syncContext,
                                                       CommunicationTypeData entity) {
        return null;
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<CommunicationTypeData> sourceEntities,
                                       Map<CommunicationTypeData, Void> foreignKeysMap) {
        List<CommunicationType> communicationTypes = new ArrayList<CommunicationType>();
        for (CommunicationTypeData sourceEntity : sourceEntities) {
            CommunicationType communicationType = new CommunicationType();
            communicationType.setLegacyId(sourceEntity.getId());
            communicationType.setDatabaseId(syncContext.getDatabaseId());
            communicationType.setUpdatable(createUpdatable(sourceEntity));

            communicationTypes.add(communicationType);
        }
        communicationTypeDao.insert(communicationTypes);
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<CommunicationTypeData> sourceEntities,
                                    Map<CommunicationTypeData, Void> foreignKeysMap, IdMapping<String> idMapping) {
        for (CommunicationTypeData sourceEntity : sourceEntities) {
            CommunicationType.Updatable updatable = createUpdatable(sourceEntity);
            long newId = idMapping.getNewIdOrThrowException(sourceEntity.getId());
            communicationTypeDao.update(updatable, newId);
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        communicationTypeDao.delete(syncContext.getDatabase(), legacyIdString);
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
        final IdMapping<String> idMapping = communicationTypeDao.getIdMapping(context.getDatabase(), idMappingPreloadSize);
        CommunicationTypeIdResolver idResolver = new CommunicationTypeIdResolver() {
            @Override
            public long getId(String legacyId, DatabaseInfo database) {
                Long newId = idMapping.getNewId(legacyId);
                if (newId == null) {
                    newId = communicationTypeDao.getId(database, legacyId);
                }
                return newId;
            }
        };

        context.putSharedObject(CommunicationTypeIdResolver.class, idResolver);
    }

    private CommunicationType.Updatable createUpdatable(CommunicationTypeData sourceEntity) {
        CommunicationType.Updatable updatable = new CommunicationType.Updatable();
        updatable.setInactive(sourceEntity.getInactive());
        updatable.setName(sourceEntity.getName());
        updatable.setTypeCode(sourceEntity.getTypeCode());
        updatable.setTypeName(sourceEntity.getTypeName());
        return updatable;
    }
}
