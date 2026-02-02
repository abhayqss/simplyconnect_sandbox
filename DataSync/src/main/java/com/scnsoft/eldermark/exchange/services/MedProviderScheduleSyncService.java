package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.MedProviderScheduleDao;
import com.scnsoft.eldermark.exchange.fk.MedProviderScheduleForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.EmployeeData;
import com.scnsoft.eldermark.exchange.model.source.MedProviderData;
import com.scnsoft.eldermark.exchange.model.source.MedProviderScheduleData;
import com.scnsoft.eldermark.exchange.model.target.MedProviderSchedule;
import com.scnsoft.eldermark.exchange.model.target.MedProviderSchedule.Updatable;
import com.scnsoft.eldermark.exchange.resolvers.EmployeeIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.MedProviderIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.MedProviderScheduleIdResolver;
import com.scnsoft.eldermark.exchange.services.employees.EmployeeSyncService;
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
public class MedProviderScheduleSyncService extends StandardSyncService<MedProviderScheduleData, Long, MedProviderScheduleForeignKeys> {
	
	@Autowired
	@Qualifier("medProviderScheduleSourceDao")
	private StandardSourceDao<MedProviderScheduleData, Long> sourceDao;

	@Autowired
	private MedProviderScheduleDao medProviderScheduleDao;
	
	@Value("${medproviderschedule.idmapping.cache.size}")
	private int idMappingSizeLimit;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(MedProviderSyncService.class);
        dependencies.add(EmployeeSyncService.class);
        return dependencies;
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
		final IdMapping<Long> idMapping = medProviderScheduleDao.getIdMapping(context.getDatabase(), idMappingSizeLimit);
		context.putSharedObject(MedProviderScheduleIdResolver.class, new MedProviderScheduleIdResolver() {
			@Override
			public long getId(long legacyId, DatabaseInfo database) {
				Long newId = idMapping.getNewId(legacyId);
				if (newId == null) {
					newId = medProviderScheduleDao.getId(database, legacyId);
				}
				return newId;
			}
		});
	}

	@Override
	protected StandardSourceDao<MedProviderScheduleData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(MedProviderScheduleData.TABLE_NAME, MedProviderScheduleData.ID_COLUMN, MedProviderScheduleData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<MedProviderScheduleData> sourceEntities,
			Map<MedProviderScheduleData, MedProviderScheduleForeignKeys> foreignKeysMap) {
		List<MedProviderSchedule> medProvidersSchedule = new ArrayList<MedProviderSchedule>();
		for (MedProviderScheduleData sourceEntity : sourceEntities) {
			MedProviderScheduleForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			MedProviderSchedule medProviderSchedule = new MedProviderSchedule();
			medProviderSchedule.setLegacyId(sourceEntity.getId());
			medProviderSchedule.setDatabaseId(syncContext.getDatabaseId());
			medProviderSchedule.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

			medProvidersSchedule.add(medProviderSchedule);
		}
		medProviderScheduleDao.insert(medProvidersSchedule);
		
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<MedProviderScheduleData> sourceEntities,
			Map<MedProviderScheduleData, MedProviderScheduleForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
		for (MedProviderScheduleData sourceEntity : sourceEntities) {
			long legacyId = sourceEntity.getId();
			long id = idMapping.getNewIdOrThrowException(legacyId);
			MedProviderScheduleForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			MedProviderSchedule.Updatable update = createUpdatable(sourceEntity, foreignKeys);
			medProviderScheduleDao.update(update, id);
		}
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		medProviderScheduleDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<MedProviderScheduleForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
			MedProviderScheduleData entity) {
		DatabaseInfo database = syncContext.getDatabase();
        MedProviderIdResolver medProviderIdResolver = syncContext.getSharedObject(MedProviderIdResolver.class);
        EmployeeIdResolver employeeIdResolver = syncContext.getSharedObject(EmployeeIdResolver.class);

        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(MedProviderScheduleData.TABLE_NAME, entity.getId());
        MedProviderScheduleForeignKeys foreignKeys = new MedProviderScheduleForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();

        Long medProvider = entity.getProviderId();
        if (!Utils.isNullOrZero(medProvider)) {
            try {
                foreignKeys.setMedProviderId(medProviderIdResolver.getId(medProvider, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(MedProviderData.TABLE_NAME, medProvider));
            }
        }

        String employeeLegacyId = entity.getCheckedOutByEmpId();
        if (!Utils.isEmpty(employeeLegacyId)) {
            try {
                foreignKeys.setCheckedOutByEmpId(employeeIdResolver.getId(employeeLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(EmployeeData.TABLE_NAME, employeeLegacyId));
            }
        }
        return new FKResolveResult<MedProviderScheduleForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return medProviderScheduleDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}
	
	private Updatable createUpdatable(MedProviderScheduleData sourceEntity, MedProviderScheduleForeignKeys foreignKeys) {
		MedProviderSchedule.Updatable updatable = new MedProviderSchedule.Updatable();
		updatable.setCheckedOutByEmpId(foreignKeys.getCheckedOutByEmpId());
		updatable.setProviderId(foreignKeys.getMedProviderId());
		updatable.setCheckedOut(sourceEntity.getCheckedOut());
		updatable.setLog(sourceEntity.getLog());
		updatable.setLoginExternalId(sourceEntity.getLoginExternalId());
		updatable.setPrePourCheckedOut(sourceEntity.getPrePourCheckedOut());
		updatable.setPrePourCheckedOutEmpId(sourceEntity.getPrePourCheckedOutEmpId());
		updatable.setPrePourSmLoginId(sourceEntity.getPrePourSmLoginId());
		updatable.setProviderDate(sourceEntity.getProviderDate());
		updatable.setSmLoginId(sourceEntity.getSmLoginId());
		updatable.setStartDate(sourceEntity.getStartDate());
		return updatable;
	}

}
