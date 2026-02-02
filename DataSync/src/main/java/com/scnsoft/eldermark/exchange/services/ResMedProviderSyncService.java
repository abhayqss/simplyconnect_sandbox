package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.ResMedProviderDao;
import com.scnsoft.eldermark.exchange.fk.ResMedProviderForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.MedProviderData;
import com.scnsoft.eldermark.exchange.model.source.ResMedProviderData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.ResMedProvider;
import com.scnsoft.eldermark.exchange.model.target.ResMedProvider.Updatable;
import com.scnsoft.eldermark.exchange.resolvers.MedProviderIdResolver;
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
public class ResMedProviderSyncService extends StandardSyncService<ResMedProviderData, Long, ResMedProviderForeignKeys>{

	@Autowired
	@Qualifier("resMedProviderSourceDao")
	private StandardSourceDao<ResMedProviderData, Long> sourceDao;

	@Autowired
	private ResMedProviderDao resMedProviderDao;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(MedProviderSyncService.class);
        dependencies.add(ResidentSyncService.class);
        return dependencies;
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
	}

	@Override
	protected StandardSourceDao<ResMedProviderData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(ResMedProviderData.TABLE_NAME, ResMedProviderData.ID_COLUMN, ResMedProviderData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<ResMedProviderData> sourceEntities,
			Map<ResMedProviderData, ResMedProviderForeignKeys> foreignKeysMap) {
		List<ResMedProvider> resMedProviders = new ArrayList<ResMedProvider>();
		for (ResMedProviderData sourceEntity : sourceEntities) {
			ResMedProviderForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			ResMedProvider resMedProvider = new ResMedProvider();
			resMedProvider.setLegacyId(sourceEntity.getId());
			resMedProvider.setDatabaseId(syncContext.getDatabaseId());
			resMedProvider.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

			resMedProviders.add(resMedProvider);
		}
		resMedProviderDao.insert(resMedProviders);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<ResMedProviderData> sourceEntities,
			Map<ResMedProviderData, ResMedProviderForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
		for (ResMedProviderData sourceEntity : sourceEntities) {
			long legacyId = sourceEntity.getId();
			long id = idMapping.getNewIdOrThrowException(legacyId);
			ResMedProviderForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			ResMedProvider.Updatable update = createUpdatable(sourceEntity, foreignKeys);
			resMedProviderDao.update(update, id);
		}
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		resMedProviderDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<ResMedProviderForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
			ResMedProviderData entity) {
		DatabaseInfo database = syncContext.getDatabase();
        MedProviderIdResolver medProviderIdResolver = syncContext.getSharedObject(MedProviderIdResolver.class);
        ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);

        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(ResMedProviderData.TABLE_NAME, entity.getId());
        ResMedProviderForeignKeys foreignKeys = new ResMedProviderForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();

        Long medProvider = entity.getMedProviderId();
        if (!Utils.isNullOrZero(medProvider)) {
            try {
                foreignKeys.setMedProviderId(medProviderIdResolver.getId(medProvider, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(MedProviderData.TABLE_NAME, medProvider));
            }
        }
        
        Long residentLegacyId = entity.getResNumber();
        if (!Utils.isNullOrZero(residentLegacyId)) {
            try {
                foreignKeys.setResidentId(residentIdResolver.getId(residentLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResidentData.TABLE_NAME, residentLegacyId));
            }
        }

        return new FKResolveResult<ResMedProviderForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return resMedProviderDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}
	
	private Updatable createUpdatable(ResMedProviderData sourceEntity, ResMedProviderForeignKeys foreignKeys) {
		ResMedProvider.Updatable updatable = new ResMedProvider.Updatable();
		
		java.sql.Date createDate = sourceEntity.getCreateDate();
		java.sql.Time createTime = sourceEntity.getCreateTime();
		if (createDate != null) {
			if (createTime != null) {
				updatable.setCreateDate(Utils.mergeDateTime(createDate, createTime));
			} else {
				updatable.setCreateDate(createDate);
			}
		}
		
		updatable.setUnitNumber(sourceEntity.getUnitNumber());
		updatable.setResidentId(foreignKeys.getResidentId());
		updatable.setProviderId(foreignKeys.getMedProviderId());
		return updatable;
	}
}
