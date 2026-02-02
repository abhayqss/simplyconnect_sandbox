package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.FuneralHomeDao;
import com.scnsoft.eldermark.exchange.fk.FuneralHomeForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.FuneralHomeData;
import com.scnsoft.eldermark.exchange.model.target.FuneralHome;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
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
public class FuneralHomeSyncService extends StandardSyncService<FuneralHomeData, Long, FuneralHomeForeignKeys> {

	@Autowired
	@Qualifier("funeralHomeSourceDao")
	private StandardSourceDao<FuneralHomeData, Long> sourceDao;

	@Autowired
	private FuneralHomeDao funeralHomeDao;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(CompanySyncService.class);
        return dependencies;
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
	}

	@Override
	protected StandardSourceDao<FuneralHomeData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(FuneralHomeData.TABLE_NAME, FuneralHomeData.ID_COLUMN, FuneralHomeData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<FuneralHomeData> sourceEntities,
			Map<FuneralHomeData, FuneralHomeForeignKeys> foreignKeysMap) {
		List<FuneralHome> funeralHomes = new ArrayList<FuneralHome>();
        for (FuneralHomeData sourceEntity : sourceEntities) {
            FuneralHomeForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            FuneralHome funeralHome = new FuneralHome();
            funeralHome.setLegacyId(sourceEntity.getId());
            funeralHome.setDatabaseId(syncContext.getDatabaseId());
            funeralHome.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

            funeralHomes.add(funeralHome);
        }
        funeralHomeDao.insert(funeralHomes);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<FuneralHomeData> sourceEntities,
			Map<FuneralHomeData, FuneralHomeForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
		for (FuneralHomeData sourceEntity : sourceEntities) {
            long legacyId = sourceEntity.getId();
            long id = idMapping.getNewIdOrThrowException(legacyId);
            FuneralHomeForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            FuneralHome.Updatable update = createUpdatable(sourceEntity, foreignKeys);
            funeralHomeDao.update(update, id);
        }
		
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		funeralHomeDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<FuneralHomeForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext, FuneralHomeData entity) {
		DatabaseInfo database = syncContext.getDatabase();
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);

        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(FuneralHomeData.TABLE_NAME, entity.getId());
        FuneralHomeForeignKeys foreignKeys = new FuneralHomeForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();

        String facility = entity.getFacility();
        if (!Utils.isEmpty(facility)) {
            try {
                foreignKeys.setFacilityOrganizationId(companyIdResolver.getId(facility, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facility));
            }
        }

        return new FKResolveResult<FuneralHomeForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return funeralHomeDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}
	
	private FuneralHome.Updatable createUpdatable(FuneralHomeData sourceEntity, FuneralHomeForeignKeys foreignKeys) {
		FuneralHome.Updatable updatable = new FuneralHome.Updatable();
		updatable.setAddress(sourceEntity.getAddress());
		updatable.setCity(sourceEntity.getCity());
		updatable.setInactive(sourceEntity.getInactive());
		updatable.setName(sourceEntity.getName());
		updatable.setOrganizationId(foreignKeys.getFacilityOrganizationId());
		updatable.setPhone(sourceEntity.getPhone());
		updatable.setState(sourceEntity.getState());
		updatable.setZip(sourceEntity.getZip());
		return updatable;
	}
}
