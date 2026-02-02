package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.MappingUtils;
import com.scnsoft.eldermark.exchange.dao.target.ResCareHistoryDao;
import com.scnsoft.eldermark.exchange.fk.ResCareForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.ResCareHistoryData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.CareHistory;
import com.scnsoft.eldermark.exchange.resolvers.ResidentIdResolver;
import com.scnsoft.eldermark.exchange.services.residents.ResidentSyncService;
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
public class CareHistorySyncService extends StandardSyncService<ResCareHistoryData, Long, ResCareForeignKeys>  {

	@Autowired
	@Qualifier("resCareHistorySourceDao")
	private StandardSourceDao<ResCareHistoryData, Long> sourceDao;

	@Autowired
	private ResCareHistoryDao targetDao;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
		dependencies.add(ResidentSyncService.class);
		return dependencies;
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
	}

	@Override
	protected StandardSourceDao<ResCareHistoryData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(ResCareHistoryData.TABLE_NAME, ResCareHistoryData.UNIQUE_ID, ResCareHistoryData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<ResCareHistoryData> sourceEntities,
			Map<ResCareHistoryData, ResCareForeignKeys> foreignKeysMap) {
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
		List<CareHistory> targetEntities = new ArrayList<CareHistory>();
		for (ResCareHistoryData sourceEntity : sourceEntities) {
            ResCareForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            CareHistory careHistory = new CareHistory();
			careHistory.setLegacyId(sourceEntity.getId());
			careHistory.setDatabaseId(syncContext.getDatabaseId());
			careHistory.setUpdatable(createUpdatable(sourceEntity, foreignKeys.getResidentId()));
            targetEntities.add(careHistory);

            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
				DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
				CareHistory mappedCareHistory = new CareHistory();
				mappedCareHistory.setLegacyId(sourceEntity.getId());
				mappedCareHistory.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
				mappedCareHistory.setUpdatable(createUpdatable(sourceEntity, databaseIdWithMappedResidentId.getId()));
				targetEntities.add(mappedCareHistory);
			}
		}
		targetDao.insert(targetEntities);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<ResCareHistoryData> sourceEntities,
			Map<ResCareHistoryData, ResCareForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
		for (ResCareHistoryData sourceEntity : sourceEntities) {
			long legacyId = sourceEntity.getId();
			long id = idMapping.getNewIdOrThrowException(legacyId);
            ResCareForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            CareHistory.Updatable update = createUpdatable(sourceEntity, foreignKeys.getResidentId());
			targetDao.update(update, id);

			if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
				DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
				long mappedNewId = targetDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), legacyId, databaseIdWithMappedResidentId.getId());
				CareHistory.Updatable mappedUpdate = createUpdatable(sourceEntity, databaseIdWithMappedResidentId.getId());
				targetDao.update(mappedUpdate, mappedNewId);
			}
		}
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		DatabaseInfo database = syncContext.getDatabase();
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
		Long residentId = targetDao.getResidentId(database.getId(), Long.valueOf(legacyIdString));
		if (MappingUtils.hasMappingForResident(residentId, mappedResidentIds)) {
			DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(residentId);
			targetDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), Long.valueOf(legacyIdString), databaseIdWithMappedResidentId.getId());
		}
		targetDao.delete(database, Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<ResCareForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext, ResCareHistoryData entity) {
		DatabaseInfo database = syncContext.getDatabase();

        ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);

        ResCareForeignKeys foreignKeys = new ResCareForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(ResCareHistoryData.TABLE_NAME, entity.getId());

        Long residentLegacyId = entity.getResNumber();
        if (!Utils.isNullOrZero(residentLegacyId)) {
            try {
                foreignKeys.setResidentId(residentIdResolver.getId(residentLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResidentData.TABLE_NAME, residentLegacyId));
            }
        }

        return new FKResolveResult<ResCareForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return targetDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}

	private CareHistory.Updatable createUpdatable(ResCareHistoryData sourceEntity, Long residentId) {
        CareHistory.Updatable updatable = new CareHistory.Updatable();

        updatable.setEndDate(sourceEntity.getCareEnd());
		updatable.setStartDate(sourceEntity.getCareStart());
		updatable.setResidentId(residentId);

		return updatable;
	}

}
