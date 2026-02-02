package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.UnitTypeRateHistoryDao;
import com.scnsoft.eldermark.exchange.fk.UnitTypeRateHistoryForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.UnitTypeData;
import com.scnsoft.eldermark.exchange.model.source.UnitTypesRateHistData;
import com.scnsoft.eldermark.exchange.model.target.UnitTypeRateHistory;
import com.scnsoft.eldermark.exchange.model.target.UnitTypeRateHistory.Updatable;
import com.scnsoft.eldermark.exchange.resolvers.UnitTypeIdResolver;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.exceptions.IdMappingException;
import com.scnsoft.eldermark.framework.fk.FKResolveError;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UnitTypeRateHistorySyncService extends StandardSyncService<UnitTypesRateHistData, Long, UnitTypeRateHistoryForeignKeys> {

	@Autowired
    @Qualifier("unitTypeRateHistorySourceDao")
    private StandardSourceDao<UnitTypesRateHistData, Long> sourceDao;

    @Autowired
    private UnitTypeRateHistoryDao unitTypeRateHistoryDao;
    
	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(UnitTypeSyncService.class);
        return dependencies;
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
	}

	@Override
	protected StandardSourceDao<UnitTypesRateHistData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(UnitTypesRateHistData.TABLE_NAME, UnitTypesRateHistData.ID_COLUMN,
				UnitTypesRateHistData.class);
	}

	@Override
	protected void doEntitiesInsertion(
			DatabaseSyncContext syncContext,
			List<UnitTypesRateHistData> sourceEntities,
			Map<UnitTypesRateHistData, UnitTypeRateHistoryForeignKeys> foreignKeysMap) {
		List<UnitTypeRateHistory> unitTypeRateHistories = new ArrayList<UnitTypeRateHistory>();
        for (UnitTypesRateHistData sourceEntity : sourceEntities) {
            UnitTypeRateHistoryForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            UnitTypeRateHistory unitTypeRateHistory = new UnitTypeRateHistory();
            unitTypeRateHistory.setLegacyId(sourceEntity.getId());
            unitTypeRateHistory.setDatabaseId(syncContext.getDatabaseId());
            unitTypeRateHistory.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

            unitTypeRateHistories.add(unitTypeRateHistory);
        }
        unitTypeRateHistoryDao.insert(unitTypeRateHistories);
	}

	@Override
	protected void doEntitiesUpdate(
			DatabaseSyncContext syncContext,
			List<UnitTypesRateHistData> sourceEntities,
			Map<UnitTypesRateHistData, UnitTypeRateHistoryForeignKeys> foreignKeysMap,
			IdMapping<Long> idMapping) {
		for (UnitTypesRateHistData sourceEntity : sourceEntities) {
            UnitTypeRateHistoryForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);
            UnitTypeRateHistory.Updatable updatable = createUpdatable(sourceEntity, foreignKeys);
            long unitTypeRateHistoryId = idMapping.getNewIdOrThrowException(sourceEntity.getId());
            unitTypeRateHistoryDao.update(updatable, unitTypeRateHistoryId);
        }
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		unitTypeRateHistoryDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<UnitTypeRateHistoryForeignKeys> resolveForeignKeys(
			DatabaseSyncContext syncContext, UnitTypesRateHistData sourceEntity) {
        UnitTypeIdResolver unitTypeIdResolver = syncContext.getSharedObject(UnitTypeIdResolver.class);

        DatabaseInfo database = syncContext.getDatabase();

        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(UnitTypesRateHistData.TABLE_NAME,
                sourceEntity.getId());
        UnitTypeRateHistoryForeignKeys foreignKeys = new UnitTypeRateHistoryForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();

        String unitTypeLegacyId = sourceEntity.getUnitTypesCode();
        if (!Utils.isEmpty(unitTypeLegacyId)) {
            try {
                foreignKeys.setUnitTypeId(unitTypeIdResolver.getId(unitTypeLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(UnitTypeData.TABLE_NAME, unitTypeLegacyId));
            }
        }
        return new FKResolveResult<UnitTypeRateHistoryForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext,
			List<Long> legacyIds) {
		return unitTypeRateHistoryDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}
	
	private Updatable createUpdatable(UnitTypesRateHistData sourceEntity,
			UnitTypeRateHistoryForeignKeys foreignKeys) {
		UnitTypeRateHistory.Updatable updatable = new UnitTypeRateHistory.Updatable();
        updatable.setStartDate(sourceEntity.getStartDate());
        updatable.setEndDate(sourceEntity.getEndDate());
        updatable.setMonthlyRate(sourceEntity.getMonthlyRate());
        updatable.setDailyRate(sourceEntity.getDailyRate());
        updatable.setEndDateFuture(sourceEntity.getEndDateFuture());
        updatable.setUnitTypeId(foreignKeys.getUnitTypeId());
        return updatable;
	}

}
