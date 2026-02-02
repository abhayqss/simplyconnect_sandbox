package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.MedProviderDao;
import com.scnsoft.eldermark.exchange.fk.MedProviderForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.MedProviderData;
import com.scnsoft.eldermark.exchange.model.target.MedProvider;
import com.scnsoft.eldermark.exchange.model.target.MedProvider.Updatable;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.MedProviderIdResolver;
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
public class MedProviderSyncService extends StandardSyncService<MedProviderData, Long, MedProviderForeignKeys> {

	@Autowired
	@Qualifier("medProviderSourceDao")
	private StandardSourceDao<MedProviderData, Long> sourceDao;

	@Autowired
	private MedProviderDao medProviderDao;

	@Value("${medprovider.idmapping.cache.size}")
	private int idMappingSizeLimit;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
		dependencies.add(CompanySyncService.class);
		return dependencies;
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
		final IdMapping<Long> idMapping = medProviderDao.getIdMapping(context.getDatabase(), idMappingSizeLimit);
		context.putSharedObject(MedProviderIdResolver.class, new MedProviderIdResolver() {
			@Override
			public long getId(long legacyId, DatabaseInfo database) {
				Long newId = idMapping.getNewId(legacyId);
				if (newId == null) {
					newId = medProviderDao.getId(database, legacyId);
				}
				return newId;
			}
		});
	}

	@Override
	protected StandardSourceDao<MedProviderData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(MedProviderData.TABLE_NAME, MedProviderData.ID_COLUMN, MedProviderData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<MedProviderData> sourceEntities,
			Map<MedProviderData, MedProviderForeignKeys> foreignKeysMap) {
		List<MedProvider> medProviders = new ArrayList<MedProvider>();
		for (MedProviderData sourceEntity : sourceEntities) {
			MedProviderForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			MedProvider medProvider = new MedProvider();
			medProvider.setLegacyId(sourceEntity.getId());
			medProvider.setDatabaseId(syncContext.getDatabaseId());
			medProvider.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

			medProviders.add(medProvider);
		}
		medProviderDao.insert(medProviders);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<MedProviderData> sourceEntities,
			Map<MedProviderData, MedProviderForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
		for (MedProviderData sourceEntity : sourceEntities) {
			long legacyId = sourceEntity.getId();
			long id = idMapping.getNewIdOrThrowException(legacyId);
			MedProviderForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			MedProvider.Updatable update = createUpdatable(sourceEntity, foreignKeys);
			medProviderDao.update(update, id);
		}
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		medProviderDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<MedProviderForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext, MedProviderData entity) {
		DatabaseInfo database = syncContext.getDatabase();
		CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);

		FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(MedProviderData.TABLE_NAME, entity.getId());
		MedProviderForeignKeys foreignKeys = new MedProviderForeignKeys();
		List<FKResolveError> errors = new ArrayList<FKResolveError>();

		String facility = entity.getFacility();
		if (!Utils.isEmpty(facility)) {
			try {
				foreignKeys.setFacilityOrganizationId(companyIdResolver.getId(facility, database));
			} catch (IdMappingException e) {
				errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facility));
			}
		}

		return new FKResolveResult<MedProviderForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return medProviderDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}

	private Updatable createUpdatable(MedProviderData sourceEntity, MedProviderForeignKeys foreignKeys) {
		MedProvider.Updatable updatable = new MedProvider.Updatable();
		updatable.setIsANurse(sourceEntity.getIsANurse());
		updatable.setShiftStart(sourceEntity.getShiftStart());
		updatable.setShiftEnd(sourceEntity.getShiftEnd());
		updatable.setUnits(sourceEntity.getUnits());
		updatable.setInactive(sourceEntity.getInactive());
		updatable.setName(sourceEntity.getName());
		updatable.setOrganizationId(foreignKeys.getFacilityOrganizationId());
		return updatable;
	}

}
