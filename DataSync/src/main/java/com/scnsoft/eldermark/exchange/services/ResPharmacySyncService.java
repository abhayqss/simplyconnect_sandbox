package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.MappingUtils;
import com.scnsoft.eldermark.exchange.dao.target.ResPharmacyDao;
import com.scnsoft.eldermark.exchange.fk.ResPharmacyForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.PharmacyData;
import com.scnsoft.eldermark.exchange.model.source.ResPharmacyData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.ResPharmacy;
import com.scnsoft.eldermark.exchange.model.target.ResPharmacy.Updatable;
import com.scnsoft.eldermark.exchange.resolvers.PharmacyIdResolver;
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
public class ResPharmacySyncService extends StandardSyncService<ResPharmacyData, Long, ResPharmacyForeignKeys>  {

	@Autowired
	@Qualifier("resPharmacySourceDao")
	private StandardSourceDao<ResPharmacyData, Long> sourceDao;

	@Autowired
	private ResPharmacyDao resPharmacyDao;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
		dependencies.add(ResidentSyncService.class);
		dependencies.add(PharmacySyncService.class);
		return dependencies;
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
	}

	@Override
	protected StandardSourceDao<ResPharmacyData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(ResPharmacyData.TABLE_NAME, ResPharmacyData.ID_COLUMN, ResPharmacyData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<ResPharmacyData> sourceEntities,
			Map<ResPharmacyData, ResPharmacyForeignKeys> foreignKeysMap) {
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
		List<ResPharmacy> resPharmacies = new ArrayList<ResPharmacy>();
		for (ResPharmacyData sourceEntity : sourceEntities) {
			ResPharmacyForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			ResPharmacy resPharmacy = new ResPharmacy();
			resPharmacy.setLegacyId(sourceEntity.getId());
			resPharmacy.setDatabaseId(syncContext.getDatabaseId());
			resPharmacy.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

			resPharmacies.add(resPharmacy);
			if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
				DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
				ResPharmacy mappedResPharmacy = new ResPharmacy();
				mappedResPharmacy.setLegacyId(sourceEntity.getId());
				mappedResPharmacy.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
				mappedResPharmacy.setUpdatable(createMappedUpdatable(sourceEntity, foreignKeys, databaseIdWithMappedResidentId.getId()));
				resPharmacies.add(mappedResPharmacy);
			}
		}
		resPharmacyDao.insert(resPharmacies);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<ResPharmacyData> sourceEntities,
			Map<ResPharmacyData, ResPharmacyForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
		for (ResPharmacyData sourceEntity : sourceEntities) {
			long legacyId = sourceEntity.getId();
			long id = idMapping.getNewIdOrThrowException(legacyId);
			ResPharmacyForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);
			ResPharmacy.Updatable update = createUpdatable(sourceEntity, foreignKeys);
			resPharmacyDao.update(update, id);
			if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
				DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
				long mappedNewId = resPharmacyDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), legacyId, databaseIdWithMappedResidentId.getId());
				ResPharmacy.Updatable mappedUpdatable = createMappedUpdatable(sourceEntity, foreignKeys, databaseIdWithMappedResidentId.getId());
				resPharmacyDao.update(mappedUpdatable, mappedNewId);
			}
		}
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
		Long residentId = resPharmacyDao.getResidentId(syncContext.getDatabase(), Long.valueOf(legacyIdString));
		if (MappingUtils.hasMappingForResident(residentId, mappedResidentIds)) {
			DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(residentId);
			resPharmacyDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), Long.valueOf(legacyIdString), databaseIdWithMappedResidentId.getId());
		}
		resPharmacyDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<ResPharmacyForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext, ResPharmacyData entity) {
		DatabaseInfo database = syncContext.getDatabase();

        ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);
        PharmacyIdResolver pharmacyIdResolver = syncContext.getSharedObject(PharmacyIdResolver.class);

        ResPharmacyForeignKeys foreignKeys = new ResPharmacyForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(ResPharmacyData.TABLE_NAME, entity.getId());
        
        Long residentLegacyId = entity.getResNumber();
        if (!Utils.isNullOrZero(residentLegacyId)) {
            try {
                foreignKeys.setResidentId(residentIdResolver.getId(residentLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResidentData.TABLE_NAME, residentLegacyId));
            }
        }
        
        Long pharmacyLegacyId = entity.getPharmacyId();
        if (!Utils.isNullOrZero(pharmacyLegacyId)) {
            try {
                foreignKeys.setPharmacyId(pharmacyIdResolver.getId(pharmacyLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(PharmacyData.TABLE_NAME, pharmacyLegacyId));
            }
        }

        return new FKResolveResult<ResPharmacyForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return resPharmacyDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}
	
	private Updatable createUpdatable(ResPharmacyData sourceEntity, ResPharmacyForeignKeys foreignKeys) {
		return createUpdatable(sourceEntity, foreignKeys, null);
	}

	private Updatable createMappedUpdatable(ResPharmacyData sourceEntity, ResPharmacyForeignKeys foreignKeys, Long residentId) {
		return createUpdatable(sourceEntity, foreignKeys, residentId);
	}

	private Updatable createUpdatable(ResPharmacyData sourceEntity, ResPharmacyForeignKeys foreignKeys, Long residentId) {
		ResPharmacy.Updatable updatable = new ResPharmacy.Updatable();
		updatable.setRank(sourceEntity.getRank());
		updatable.setResidentId(residentId != null ? residentId : foreignKeys.getResidentId());
		updatable.setPharmacyId(foreignKeys.getPharmacyId());
		return updatable;
	}

}
