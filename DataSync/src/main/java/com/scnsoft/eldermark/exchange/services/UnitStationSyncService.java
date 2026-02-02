package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.UnitStationDao;
import com.scnsoft.eldermark.exchange.fk.UnitStationForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.UnitStationData;
import com.scnsoft.eldermark.exchange.model.target.UnitStation;
import com.scnsoft.eldermark.exchange.model.target.UnitStation.Updatable;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.UnitStationIdResolver;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.exceptions.IdMappingException;
import com.scnsoft.eldermark.framework.fk.FKResolveError;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UnitStationSyncService extends StandardSyncService<UnitStationData, Long, UnitStationForeignKeys> {

	@Autowired
	@Qualifier("unitStationSourceDao")
	private StandardSourceDao<UnitStationData, Long> sourceDao;

	@Autowired
	private UnitStationDao unitStationDao;
	
	@Value("${unitstation.idmapping.cache.size}")
	private int idMappingSizeLimit;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
		dependencies.add(CompanySyncService.class);
		return dependencies;
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
		final IdMapping<Long> idMapping = unitStationDao.getIdMapping(context.getDatabase(), idMappingSizeLimit);
		context.putSharedObject(UnitStationIdResolver.class, new UnitStationIdResolver() {
			@Override
			public long getId(long legacyId, DatabaseInfo database) {
				Long newId = idMapping.getNewId(legacyId);
				if (newId == null) {
					newId = unitStationDao.getId(database, legacyId);
				}
				return newId;
			}
		});
	}

	@Override
	protected StandardSourceDao<UnitStationData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(UnitStationData.TABLE_NAME, UnitStationData.ID_COLUMN, UnitStationData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<UnitStationData> sourceEntities,
			Map<UnitStationData, UnitStationForeignKeys> foreignKeysMap) {
		List<UnitStation> unitStations = new ArrayList<UnitStation>();
		for (UnitStationData sourceEntity : sourceEntities) {
			UnitStationForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			UnitStation unitStation = new UnitStation();
			unitStation.setLegacyId(sourceEntity.getId());
			unitStation.setDatabaseId(syncContext.getDatabaseId());
			unitStation.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

			unitStations.add(unitStation);
		}
		unitStationDao.insert(unitStations);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<UnitStationData> sourceEntities,
			Map<UnitStationData, UnitStationForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
		for (UnitStationData sourceEntity : sourceEntities) {
			long legacyId = sourceEntity.getId();
			long id = idMapping.getNewIdOrThrowException(legacyId);
			UnitStationForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			UnitStation.Updatable update = createUpdatable(sourceEntity, foreignKeys);
			unitStationDao.update(update, id);
		}
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		unitStationDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<UnitStationForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext, UnitStationData entity) {
		DatabaseInfo database = syncContext.getDatabase();

        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);

        UnitStationForeignKeys foreignKeys = new UnitStationForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(UnitStationData.TABLE_NAME, entity.getId());
        
        String facility = entity.getFacility();
        if (!Utils.isEmpty(facility)) {
            try {
                foreignKeys.setOrganizationId(companyIdResolver.getId(facility, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facility));
            }
        }

        return new FKResolveResult<UnitStationForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return unitStationDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}

	private Updatable createUpdatable(UnitStationData sourceEntity, UnitStationForeignKeys foreignKeys) {
		UnitStation.Updatable updatable = new UnitStation.Updatable();
		
		updatable.setCode(sourceEntity.getCode());
		updatable.setDescription(sourceEntity.getDescription());
		updatable.setFacilityCode(sourceEntity.getFacilityCode());
		updatable.setInactive(sourceEntity.getInactive());
		updatable.setPharmacyGroupCode(sourceEntity.getPharmacyGroupCode());

		updatable.setOrganizationId(foreignKeys.getOrganizationId());
		return updatable;
	}
	
}
