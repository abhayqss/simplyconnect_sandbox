package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.ResLeaveOfAbsenceDao;
import com.scnsoft.eldermark.exchange.fk.ResLeaveOfAbsenceForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.*;
import com.scnsoft.eldermark.exchange.model.target.ResLeaveOfAbsence;
import com.scnsoft.eldermark.exchange.model.target.ResLeaveOfAbsence.Updatable;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.EmployeeIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.LoaReasonIdResolver;
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
import java.util.List;
import java.util.Map;

@Service
public class ResLeaveOfAbsenceSyncService extends StandardSyncService<ResLeaveOfAbsenceData, Long, ResLeaveOfAbsenceForeignKeys> {

	@Autowired
	@Qualifier("resLeaveOfAbsenceSourceDao")
	private StandardSourceDao<ResLeaveOfAbsenceData, Long> sourceDao;

	@Autowired
	private ResLeaveOfAbsenceDao resLeaveOfAbsenceDao;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
		dependencies.add(EmployeeSyncService.class);
		dependencies.add(CompanySyncService.class);
		dependencies.add(ResidentSyncService.class);
		dependencies.add(LoaReasonSyncService.class);
		return dependencies;
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
	}

	@Override
	protected StandardSourceDao<ResLeaveOfAbsenceData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(ResLeaveOfAbsenceData.TABLE_NAME, ResLeaveOfAbsenceData.ID_COLUMN, ResLeaveOfAbsenceData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<ResLeaveOfAbsenceData> sourceEntities,
			Map<ResLeaveOfAbsenceData, ResLeaveOfAbsenceForeignKeys> foreignKeysMap) {
		List<ResLeaveOfAbsence> resLeaveOfAbsences = new ArrayList<ResLeaveOfAbsence>();
		for (ResLeaveOfAbsenceData sourceEntity : sourceEntities) {
			ResLeaveOfAbsenceForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			ResLeaveOfAbsence resLeaveOfAbsence = new ResLeaveOfAbsence();
			resLeaveOfAbsence.setLegacyId(sourceEntity.getId());
			resLeaveOfAbsence.setDatabaseId(syncContext.getDatabaseId());
			resLeaveOfAbsence.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

			resLeaveOfAbsences.add(resLeaveOfAbsence);
		}
		resLeaveOfAbsenceDao.insert(resLeaveOfAbsences);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<ResLeaveOfAbsenceData> sourceEntities,
			Map<ResLeaveOfAbsenceData, ResLeaveOfAbsenceForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
		for (ResLeaveOfAbsenceData sourceEntity : sourceEntities) {
			long legacyId = sourceEntity.getId();
			long id = idMapping.getNewIdOrThrowException(legacyId);
			ResLeaveOfAbsenceForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			ResLeaveOfAbsence.Updatable update = createUpdatable(sourceEntity, foreignKeys);
			resLeaveOfAbsenceDao.update(update, id);
		}
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		resLeaveOfAbsenceDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<ResLeaveOfAbsenceForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
			ResLeaveOfAbsenceData entity) {
		DatabaseInfo database = syncContext.getDatabase();

        EmployeeIdResolver employeeIdResolver = syncContext.getSharedObject(EmployeeIdResolver.class);
        LoaReasonIdResolver loaReasonIdResolver = syncContext.getSharedObject(LoaReasonIdResolver.class);
        ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);

        ResLeaveOfAbsenceForeignKeys foreignKeys = new ResLeaveOfAbsenceForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(ResLeaveOfAbsenceData.TABLE_NAME, entity.getId());
        
        String facility = entity.getFacility();
        if (!Utils.isEmpty(facility)) {
            try {
                foreignKeys.setFacilityOrganizationId(companyIdResolver.getId(facility, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facility));
            }
        }

        String lastUpdatedByUserLegacyId = entity.getLastUpdatedBy();
        if (!Utils.isEmpty(lastUpdatedByUserLegacyId)) {
            try {
                foreignKeys.setLastUpdatedEmployeeId(employeeIdResolver.getId(lastUpdatedByUserLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(EmployeeData.TABLE_NAME, lastUpdatedByUserLegacyId));
            }
        }
        
        Long loaReasonLegacyId = entity.getLoaReasonId();
        if (!Utils.isNullOrZero(loaReasonLegacyId)) {
            try {
                foreignKeys.setLoaReasonId(loaReasonIdResolver.getId(loaReasonLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(LoaReasonData.TABLE_NAME, loaReasonLegacyId));
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

        return new FKResolveResult<ResLeaveOfAbsenceForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return resLeaveOfAbsenceDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}

	private Updatable createUpdatable(ResLeaveOfAbsenceData sourceEntity, ResLeaveOfAbsenceForeignKeys foreignKeys) {
		ResLeaveOfAbsence.Updatable updatable = new ResLeaveOfAbsence.Updatable();

		java.sql.Date fromDate = sourceEntity.getFromDate();
		java.sql.Time fromTime = sourceEntity.getFromTime();
		if (fromDate != null) {
			if (fromTime != null) {
				updatable.setFromDate(Utils.mergeDateTime(fromDate, fromTime));
			} else {
				updatable.setFromDate(fromDate);
			}
		}

		java.sql.Date toDate = sourceEntity.getToDate();
		java.sql.Time toTime = sourceEntity.getToTime();
		if (toDate != null) {
			if (toTime != null) {
				updatable.setToDate(Utils.mergeDateTime(toDate, toTime));
			} else {
				updatable.setToDate(toDate);
			}
		}

		updatable.setBedHoldLetterSent(sourceEntity.getBedHoldLetterSent());
		updatable.setFromWhen(sourceEntity.getFromWhen());
		updatable.setHospitalDischargeDiagnosis(sourceEntity.getHospitalDischargeDiagnosis());
		updatable.setHospitalVisitLocation(sourceEntity.getHospitalVisitLocation());
		updatable.setHospitalVisitOutcome(sourceEntity.getHospitalVisitOutcome());
		updatable.setHospitalVisitReason(sourceEntity.getHospitalVisitReason());
		updatable.setLastUpdated(sourceEntity.getLastUpdated());
		updatable.setMedsOnHold(sourceEntity.getMedsOnHold());
		updatable.setOnLeave(sourceEntity.getOnLeave());
		updatable.setPrePourMeds(sourceEntity.getPrePourMeds());
		updatable.setReason(sourceEntity.getReason());
		updatable.setServiceOnHold(sourceEntity.getServiceOnHold());
		updatable.setToWhenFuture(sourceEntity.getToWhenFuture());
		updatable.setWhoRequested(sourceEntity.getWhoRequested());

		updatable.setResidentId(foreignKeys.getResidentId());
		updatable.setOrganizationId(foreignKeys.getFacilityOrganizationId());
		updatable.setLastUpdatedEmployeeId(foreignKeys.getLastUpdatedEmployeeId());
		updatable.setLoaReasonId(foreignKeys.getLoaReasonId());
		return updatable;
	}

}
