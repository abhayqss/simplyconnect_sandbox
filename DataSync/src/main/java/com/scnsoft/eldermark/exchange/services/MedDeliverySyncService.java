package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.MedDeliveryDao;
import com.scnsoft.eldermark.exchange.fk.MedDeliveryForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.*;
import com.scnsoft.eldermark.exchange.model.target.MedDelivery;
import com.scnsoft.eldermark.exchange.model.target.MedDelivery.Updatable;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.EmployeeIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.MedicationIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.ResidentIdResolver;
import com.scnsoft.eldermark.exchange.services.employees.EmployeeSyncService;
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
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Service
public class MedDeliverySyncService extends StandardSyncService<MedDeliveryData, Long, MedDeliveryForeignKeys> {

	@Autowired
	@Qualifier("medDeliverySourceDao")
	private StandardSourceDao<MedDeliveryData, Long> sourceDao;

	@Autowired
	private MedDeliveryDao medDeliveryDao;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
		dependencies.add(CompanySyncService.class);
		dependencies.add(ResidentSyncService.class);
		dependencies.add(MedicationSyncService.class);
		dependencies.add(EmployeeSyncService.class);
		return dependencies;
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
	}

	@Override
	protected StandardSourceDao<MedDeliveryData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(MedDeliveryData.TABLE_NAME, MedDeliveryData.ID_COLUMN, MedDeliveryData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<MedDeliveryData> sourceEntities,
			Map<MedDeliveryData, MedDeliveryForeignKeys> foreignKeysMap) {
		List<MedDelivery> medDeliveries = new ArrayList<MedDelivery>();
		for (MedDeliveryData sourceEntity : sourceEntities) {
			MedDeliveryForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			MedDelivery medDelivery = new MedDelivery();
			medDelivery.setLegacyId(sourceEntity.getId());
			medDelivery.setDatabaseId(syncContext.getDatabaseId());
			medDelivery.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

			medDeliveries.add(medDelivery);
		}
		medDeliveryDao.insert(medDeliveries);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<MedDeliveryData> sourceEntities,
			Map<MedDeliveryData, MedDeliveryForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
		for (MedDeliveryData sourceEntity : sourceEntities) {
			long legacyId = sourceEntity.getId();
			long id = idMapping.getNewIdOrThrowException(legacyId);
			MedDeliveryForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			MedDelivery.Updatable update = createUpdatable(sourceEntity, foreignKeys);
			medDeliveryDao.update(update, id);
		}
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		medDeliveryDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<MedDeliveryForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext, MedDeliveryData entity) {
		DatabaseInfo database = syncContext.getDatabase();

        ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);
        MedicationIdResolver medicationIdResolver = syncContext.getSharedObject(MedicationIdResolver.class);
        EmployeeIdResolver employeeIdResolver = syncContext.getSharedObject(EmployeeIdResolver.class);

        MedDeliveryForeignKeys foreignKeys = new MedDeliveryForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(MedDeliveryData.TABLE_NAME, entity.getId());
        
        String facility = entity.getFacility();
        if (!Utils.isEmpty(facility)) {
            try {
                foreignKeys.setFacilityOrganizationId(companyIdResolver.getId(facility, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facility));
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
        
        Long medicationLegacyId = entity.getResMedId();
        if (!Utils.isNullOrZero(medicationLegacyId)) {
            try {
                foreignKeys.setMedicationId(medicationIdResolver.getId(medicationLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResMedicationData.TABLE_NAME, medicationLegacyId));
            }
        }
        
        String personLegacyId = entity.getGivenOrRecordedPersonId();
        if (!Utils.isEmpty(personLegacyId)) {
            try {
                foreignKeys.setGivenOrRecordedPersonId(employeeIdResolver.getId(personLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(EmployeeData.TABLE_NAME, personLegacyId));
            }
        }

        return new FKResolveResult<MedDeliveryForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return medDeliveryDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}
	
	private Updatable createUpdatable(MedDeliveryData sourceEntity, MedDeliveryForeignKeys foreignKeys) {
		MedDelivery.Updatable updatable = new MedDelivery.Updatable();
		
		java.sql.Date scheduledDate = sourceEntity.getScheduledDate();
		java.sql.Time scheduledTime = sourceEntity.getScheduledTime();
		if (scheduledDate != null) {
			if (scheduledTime != null) {
				updatable.setScheduledDate(Utils.mergeDateTime(scheduledDate, scheduledTime));
			} else {
				updatable.setScheduledDate(scheduledDate);
			}
		}
		
		java.sql.Date givenDate = sourceEntity.getGivenOrRecordedDate();
		java.sql.Time givenTime = sourceEntity.getGivenOrRecordedTime();
		if (givenDate != null) {
			if (givenTime != null) {
				updatable.setGivenOrRecordedDate(Utils.mergeDateTime(givenDate, givenTime));
			} else {
				updatable.setGivenOrRecordedDate(givenDate);
			}
		}
		
		updatable.setAttemptsLastWhen(sourceEntity.getAttemptsLastWhen());
		updatable.setGiven(sourceEntity.getGiven());
		updatable.setNotGivenReason(sourceEntity.getNotGivenReason());
		updatable.setOnHold(sourceEntity.getOnHold());
		updatable.setPouredWhen(sourceEntity.getPouredWhen());
		updatable.setPrn(sourceEntity.getPrn());
		updatable.setPrnReasonGiven(sourceEntity.getPrnReasonGiven());
		updatable.setPrnResults(sourceEntity.getPrnResults());
		
		Long scheduledEarliestWhen = sourceEntity.getScheduledEarliestWhen();
		if (!Utils.isNullOrZero(scheduledEarliestWhen)) {
			updatable.setScheduledEarliestWhen(convertMinsFrom1990ToDate(scheduledEarliestWhen));
		}
		Long scheduledLatestWhen = sourceEntity.getScheduledLatestWhen();
		if (!Utils.isNullOrZero(scheduledLatestWhen)) {
			updatable.setScheduledLatestWhen(convertMinsFrom1990ToDate(scheduledLatestWhen));
		}

		updatable.setResidentId(foreignKeys.getResidentId());
		updatable.setOrganizationId(foreignKeys.getFacilityOrganizationId());
		updatable.setMedicationId(foreignKeys.getMedicationId());
		updatable.setGivenOrRecordedPersonId(foreignKeys.getGivenOrRecordedPersonId());
		return updatable;
	}
	
	
    private java.util.Date convertMinsFrom1990ToDate (Long minsFrom1990) {
    	Long millisFrom1990 = minsFrom1990 * 60L * 1000L;
    	
        Calendar dateCal = Calendar.getInstance();
        dateCal.set(Calendar.YEAR, 1990);
        dateCal.set(Calendar.MONTH, Calendar.JANUARY);
        dateCal.set(Calendar.DAY_OF_MONTH, 1);
        dateCal.set(Calendar.HOUR_OF_DAY, 0);
        dateCal.set(Calendar.MINUTE, 0);
        dateCal.set(Calendar.SECOND, 0);
        dateCal.set(Calendar.MILLISECOND, 0);
        
        return new java.util.Date(dateCal.getTimeInMillis() + millisFrom1990);
    }
	
}
