package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.target.UnitHistoryDao;
import com.scnsoft.eldermark.exchange.fk.UnitHistoryForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.UnitData;
import com.scnsoft.eldermark.exchange.model.source.UnitHistoryData;
import com.scnsoft.eldermark.exchange.model.source.UnitTypeData;
import com.scnsoft.eldermark.exchange.model.target.UnitHistory;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.UnitIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.UnitTypeIdResolver;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.exceptions.IdMappingException;
import com.scnsoft.eldermark.framework.fk.FKResolveError;
import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UnitHistorySyncService extends StandardSyncService<UnitHistoryData, Long, UnitHistoryForeignKeys> {
    @Autowired
    @Qualifier("unitHistorySourceDao")
    private StandardSourceDao<UnitHistoryData, Long> sourceDao;

    @Autowired
    private UnitHistoryDao unitHistoryDao;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(CompanySyncService.class);
        dependencies.add(UnitSyncService.class);
        dependencies.add(UnitTypeSyncService.class);
        return dependencies;
    }

    @Override
    protected StandardSourceDao<UnitHistoryData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(UnitHistoryData.TABLE_NAME, UnitHistoryData.UNIT_HISTORY_ID,
                UnitHistoryData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return unitHistoryDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<UnitHistoryForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
                                                                         UnitHistoryData sourceEntity) {
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);
        UnitIdResolver unitIdResolver = syncContext.getSharedObject(UnitIdResolver.class);
        UnitTypeIdResolver unitTypeIdResolver = syncContext.getSharedObject(UnitTypeIdResolver.class);

        DatabaseInfo database = syncContext.getDatabase();

        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(UnitHistoryData.TABLE_NAME,
                sourceEntity.getId());
        UnitHistoryForeignKeys foreignKeys = new UnitHistoryForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();

        String facility = sourceEntity.getFacility();
        if (!Utils.isEmpty(facility)) {
            try {
                foreignKeys.setOrganizationId(companyIdResolver.getId(facility, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facility));
            }
        }

        Long unitLegacyId = sourceEntity.getUnitId();
        if (!Utils.isNullOrZero(unitLegacyId)) {
            try {
                foreignKeys.setUnitId(unitIdResolver.getId(unitLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(UnitData.TABLE_NAME, unitLegacyId));
            }
        }

        String unitTypeLegacyId = sourceEntity.getUnitType();
        if (!Utils.isEmpty(unitTypeLegacyId)) {
            try {
                foreignKeys.setUnitTypeId(unitTypeIdResolver.getId(unitTypeLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(UnitTypeData.TABLE_NAME, unitTypeLegacyId));
            }
        }
        return new FKResolveResult<UnitHistoryForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<UnitHistoryData> sourceEntities,
                                       Map<UnitHistoryData, UnitHistoryForeignKeys> foreignKeysMap) {
        List<UnitHistory> unitHistories = new ArrayList<UnitHistory>();
        for (UnitHistoryData sourceEntity : sourceEntities) {
            UnitHistoryForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            UnitHistory unitHistory = new UnitHistory();
            unitHistory.setLegacyId(sourceEntity.getUnitHistoryId());
            unitHistory.setDatabaseId(syncContext.getDatabaseId());
            unitHistory.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

            unitHistories.add(unitHistory);
        }
        unitHistoryDao.insert(unitHistories);
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<UnitHistoryData> sourceEntities,
                                    Map<UnitHistoryData, UnitHistoryForeignKeys> foreignKeysMap,
                                    IdMapping<Long> idMapping) {
        for (UnitHistoryData sourceEntity : sourceEntities) {
            UnitHistoryForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);
            UnitHistory.Updatable updatable = createUpdatable(sourceEntity, foreignKeys);
            long unitHistoryId = idMapping.getNewIdOrThrowException(sourceEntity.getUnitHistoryId());
            unitHistoryDao.update(updatable, unitHistoryId);
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        unitHistoryDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
    }

    private UnitHistory.Updatable createUpdatable(UnitHistoryData sourceEntity, UnitHistoryForeignKeys foreignKeys) {
        UnitHistory.Updatable updatable = new UnitHistory.Updatable();
        updatable.setUnitNumber(sourceEntity.getUnitNumber());
        updatable.setSubDivideType(sourceEntity.getSubDivideType());
        updatable.setStartDate(sourceEntity.getStartDate());
        updatable.setEndDate(sourceEntity.getEndDate());
        updatable.setInMaintenance(sourceEntity.getInMaintenance());
        updatable.setSemiPrivate(sourceEntity.getSemiPrivate());
        updatable.setOrganizationId(foreignKeys.getOrganizationId());
        updatable.setUnitId(foreignKeys.getUnitId());
        updatable.setUnitTypeId(foreignKeys.getUnitTypeId());
        updatable.setProductType(sourceEntity.getProductType());
        return updatable;
    }
}
