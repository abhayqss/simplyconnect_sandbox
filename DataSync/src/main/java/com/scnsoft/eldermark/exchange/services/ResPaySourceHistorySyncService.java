package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.MappingUtils;
import com.scnsoft.eldermark.exchange.dao.target.ResPaySourceHistoryDao;
import com.scnsoft.eldermark.exchange.fk.ResPaySourceForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.ResPaySourceHistoryData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.ResidentPaySourceHistory;
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
public class ResPaySourceHistorySyncService extends StandardSyncService<ResPaySourceHistoryData, Long, ResPaySourceForeignKeys>  {

	@Autowired
	@Qualifier("resPaySourceHistorySourceDao")
	private StandardSourceDao<ResPaySourceHistoryData, Long> sourceDao;

	@Autowired
	private ResPaySourceHistoryDao targetDao;

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
	protected StandardSourceDao<ResPaySourceHistoryData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(ResPaySourceHistoryData.TABLE_NAME, ResPaySourceHistoryData.UNIQUE_ID, ResPaySourceHistoryData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<ResPaySourceHistoryData> sourceEntities,
			Map<ResPaySourceHistoryData, ResPaySourceForeignKeys> foreignKeysMap) {
		List<ResidentPaySourceHistory> targetEntities = new ArrayList<ResidentPaySourceHistory>();
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
		for (ResPaySourceHistoryData sourceEntity : sourceEntities) {
            ResPaySourceForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            ResidentPaySourceHistory residentPaySourceHistory = new ResidentPaySourceHistory();
			residentPaySourceHistory.setLegacyId(sourceEntity.getId());
			residentPaySourceHistory.setDatabaseId(syncContext.getDatabaseId());
			residentPaySourceHistory.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

            targetEntities.add(residentPaySourceHistory);

			if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
				DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
				ResidentPaySourceHistory mappedResidentPaySourceHistory = new ResidentPaySourceHistory();
				mappedResidentPaySourceHistory.setLegacyId(sourceEntity.getId());
				mappedResidentPaySourceHistory.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
				mappedResidentPaySourceHistory.setUpdatable(createMappedUpdatable(sourceEntity, foreignKeys, databaseIdWithMappedResidentId.getId()));
				targetEntities.add(mappedResidentPaySourceHistory);
			}
		}
		targetDao.insert(targetEntities);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<ResPaySourceHistoryData> sourceEntities,
			Map<ResPaySourceHistoryData, ResPaySourceForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
		for (ResPaySourceHistoryData sourceEntity : sourceEntities) {
			long legacyId = sourceEntity.getId();
			long id = idMapping.getNewIdOrThrowException(legacyId);
            ResPaySourceForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            ResidentPaySourceHistory.Updatable update = createUpdatable(sourceEntity, foreignKeys);
			targetDao.update(update, id);

			if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
				DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
				long mappedNewId = targetDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), legacyId, databaseIdWithMappedResidentId.getId());
				ResidentPaySourceHistory.Updatable mappedUpdatable = createMappedUpdatable(sourceEntity, foreignKeys, databaseIdWithMappedResidentId.getId());
				targetDao.update(mappedUpdatable, mappedNewId);
			}
		}
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
		Long residentId = targetDao.getResidentId(syncContext.getDatabase(), Long.valueOf(legacyIdString));
		if (MappingUtils.hasMappingForResident(residentId, mappedResidentIds)) {
			DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(residentId);
			targetDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), Long.valueOf(legacyIdString), databaseIdWithMappedResidentId.getId());
		}
		targetDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<ResPaySourceForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext, ResPaySourceHistoryData entity) {
		DatabaseInfo database = syncContext.getDatabase();

        ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);

        ResPaySourceForeignKeys foreignKeys = new ResPaySourceForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(ResPaySourceHistoryData.TABLE_NAME, entity.getId());

        Long residentLegacyId = entity.getResNumber();
        if (!Utils.isNullOrZero(residentLegacyId)) {
            try {
                foreignKeys.setResidentId(residentIdResolver.getId(residentLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResidentData.TABLE_NAME, residentLegacyId));
            }
        }

        return new FKResolveResult<ResPaySourceForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return targetDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}

	private ResidentPaySourceHistory.Updatable createUpdatable(ResPaySourceHistoryData sourceEntity, ResPaySourceForeignKeys foreignKeys) {
		return createUpdatable(sourceEntity, foreignKeys, null);
	}

	private ResidentPaySourceHistory.Updatable createMappedUpdatable(ResPaySourceHistoryData sourceEntity, ResPaySourceForeignKeys foreignKeys, Long mappedResidentId) {
		return createUpdatable(sourceEntity, foreignKeys, mappedResidentId);
	}

	private ResidentPaySourceHistory.Updatable createUpdatable(ResPaySourceHistoryData sourceEntity, ResPaySourceForeignKeys foreignKeys, Long mappedResidentId) {
        ResidentPaySourceHistory.Updatable updatable = new ResidentPaySourceHistory.Updatable();

        updatable.setPaySource(sourceEntity.getPaySource());
        updatable.setEndDate(sourceEntity.getEndDate());
		updatable.setStartDate(sourceEntity.getStartDate());
		updatable.setEndDateFuture(sourceEntity.getEndDateFuture());
		updatable.setResidentId(mappedResidentId != null ? mappedResidentId : foreignKeys.getResidentId());

		return updatable;
	}

}
