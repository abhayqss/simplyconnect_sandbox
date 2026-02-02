package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.MedIncidentDao;
import com.scnsoft.eldermark.exchange.fk.MedIncidentForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.EmployeeData;
import com.scnsoft.eldermark.exchange.model.source.MedIncidentData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.MedIncident;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.EmployeeIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.ResidentIdResolver;
import com.scnsoft.eldermark.exchange.services.employees.EmployeeSyncService;
import com.scnsoft.eldermark.exchange.services.residents.ResidentSyncService;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.exceptions.IdMappingException;
import com.scnsoft.eldermark.framework.fk.FKResolveError;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Service
public class MedIncidentSyncService extends StandardSyncService<MedIncidentData, Long, MedIncidentForeignKeys> {
	
	private static final Logger logger = LoggerFactory.getLogger(MedIncidentSyncService.class);

	@Autowired
	@Qualifier("medIncidentSourceDao")
	private StandardSourceDao<MedIncidentData, Long> sourceDao;

	@Autowired
	private MedIncidentDao medIncidentDao;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
		dependencies.add(CompanySyncService.class);
		dependencies.add(ResidentSyncService.class);
		dependencies.add(EmployeeSyncService.class);
		return dependencies;
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
	}

	@Override
	protected StandardSourceDao<MedIncidentData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(MedIncidentData.TABLE_NAME, MedIncidentData.ID_COLUMN, MedIncidentData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<MedIncidentData> sourceEntities,
			Map<MedIncidentData, MedIncidentForeignKeys> foreignKeysMap) {
		List<MedIncident> medIncidents = new ArrayList<MedIncident>();
		for (MedIncidentData sourceEntity : sourceEntities) {
			MedIncidentForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			MedIncident medIncident = new MedIncident();
			medIncident.setLegacyId(sourceEntity.getId());
			medIncident.setDatabaseId(syncContext.getDatabaseId());
			medIncident.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

			medIncidents.add(medIncident);
		}
		medIncidentDao.insert(medIncidents);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<MedIncidentData> sourceEntities,
			Map<MedIncidentData, MedIncidentForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
		for (MedIncidentData sourceEntity : sourceEntities) {
			long legacyId = sourceEntity.getId();
			long id = idMapping.getNewIdOrThrowException(legacyId);
			MedIncidentForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			MedIncident.Updatable update = createUpdatable(sourceEntity, foreignKeys);
			medIncidentDao.update(update, id);
		}
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		medIncidentDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<MedIncidentForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext, MedIncidentData entity) {
		DatabaseInfo database = syncContext.getDatabase();

        ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);
        EmployeeIdResolver employeeIdResolver = syncContext.getSharedObject(EmployeeIdResolver.class);

        MedIncidentForeignKeys foreignKeys = new MedIncidentForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(MedIncidentData.TABLE_NAME, entity.getId());
        
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
        
        String personDiscoverIncidentLegacyId = entity.getPersonDiscoverIncidentId();
        if (!Utils.isEmpty(personDiscoverIncidentLegacyId)) {
            try {
                foreignKeys.setPersonDiscoverIncidentId(employeeIdResolver.getId(personDiscoverIncidentLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(EmployeeData.TABLE_NAME, personDiscoverIncidentLegacyId));
            }
        }
        
        String signHlthSrvsDirLegacyId = entity.getSignHlthSrvsDirId();
        if (!Utils.isEmpty(signHlthSrvsDirLegacyId)) {
            try {
                foreignKeys.setSignHlthSrvsDirId(employeeIdResolver.getId(signHlthSrvsDirLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(EmployeeData.TABLE_NAME, signHlthSrvsDirLegacyId));
            }
        }
        
        String signExecutiveDirectorLegacyId = entity.getSignExecutiveDirectorId();
        if (!Utils.isEmpty(signExecutiveDirectorLegacyId)) {
            try {
                foreignKeys.setSignExecutiveDirectorId(employeeIdResolver.getId(signExecutiveDirectorLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(EmployeeData.TABLE_NAME, signExecutiveDirectorLegacyId));
            }
        }

        return new FKResolveResult<MedIncidentForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return medIncidentDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}
	
	private MedIncident.Updatable createUpdatable(MedIncidentData sourceEntity, MedIncidentForeignKeys foreignKeys) {
		MedIncident.Updatable updatable = new MedIncident.Updatable();
		
		updatable.setUnitNumber(sourceEntity.getUnitNumber());
		updatable.setTypeOfIncident(sourceEntity.getTypeOfIncident());
		updatable.setSentinelEventYn(sourceEntity.getSentinelEventYn());
		updatable.setMedName(sourceEntity.getMedName());
		updatable.setMedDose(sourceEntity.getMedDose());
		updatable.setPersonDiscoverIncidentName(sourceEntity.getPersonDiscoverIncidentName());
		updatable.setPersonDiscoverIncidentTitle(sourceEntity.getPersonDiscoverIncidentTitle());
		updatable.setFinalResidentOutcome(sourceEntity.getFinalResidentOutcome());
		updatable.setPersonCompletingReportName(sourceEntity.getPersonCompletingReportName());
		updatable.setPossibleContributingFactors(sourceEntity.getPossibleContributingFactors());
		updatable.setCorrectiveActionTaken(sourceEntity.getCorrectiveActionTaken());
		updatable.setSignHlthSrvsDirName(sourceEntity.getSignHlthSrvsDirName());
		updatable.setSignExecutiveDirectorName(sourceEntity.getSignExecutiveDirectorName());
		
		java.sql.Date incidentDate = sourceEntity.getIncidentDate();
		String incidentTime = sourceEntity.getIncidentTime();
		if (incidentDate != null) {
			if (incidentTime != null) {
				updatable.setIncidentDate(mergeDateAndStringTime(incidentDate, incidentTime));
			} else {
				updatable.setIncidentDate(incidentDate);
			}
		}
		

		
		java.sql.Date personCompletingReportDate = sourceEntity.getPersonCompletingReportDate();
		String personCompletingReportTime = sourceEntity.getPersonCompletingReportTime();
		if (personCompletingReportDate != null) {
			if (personCompletingReportTime != null) {
				updatable.setPersonCompletingReportDate(mergeDateAndStringTime(personCompletingReportDate, personCompletingReportTime));
			} else {
				updatable.setPersonCompletingReportDate(personCompletingReportDate);
			}
		}
		
		java.sql.Date signExecutiveDirectorDate = sourceEntity.getSignExecutiveDirectorDate();
		String signExecutiveDirectorTime = sourceEntity.getSignExecutiveDirectorTime();
		if (signExecutiveDirectorDate != null) {
			if (signExecutiveDirectorTime != null) {
				updatable.setSignExecutiveDirectorDate(mergeDateAndStringTime(signExecutiveDirectorDate, signExecutiveDirectorTime));
			} else {
				updatable.setSignExecutiveDirectorDate(signExecutiveDirectorDate);
			}
		}
		
		java.sql.Date signHlthSrvsDirDate = sourceEntity.getSignHlthSrvsDirDate();
		String signHlthSrvsDirTime = sourceEntity.getSignHlthSrvsDirTime();
		if (signHlthSrvsDirDate != null) {
			if (signHlthSrvsDirTime != null) {
				updatable.setSignHlthSrvsDirDate(mergeDateAndStringTime(signHlthSrvsDirDate, signHlthSrvsDirTime));
			} else {
				updatable.setSignHlthSrvsDirDate(signHlthSrvsDirDate);
			}
		}

		updatable.setResidentId(foreignKeys.getResidentId());
		updatable.setOrganizationId(foreignKeys.getFacilityOrganizationId());
		updatable.setPersonDiscoverIncidentId(foreignKeys.getPersonDiscoverIncidentId());
		updatable.setSignHlthSrvsDirId(foreignKeys.getSignHlthSrvsDirId());
		updatable.setSignExecutiveDirectorId(foreignKeys.getSignExecutiveDirectorId());
		return updatable;
	}
	
	private java.util.Date mergeDateAndStringTime(Date date, String time) {
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        
        try {
        	Calendar timeCal = Calendar.getInstance();
        	SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
			timeCal.setTime(sdf.parse(time));
			dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
	        dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
	        dateCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
	        dateCal.set(Calendar.MILLISECOND, timeCal.get(Calendar.MILLISECOND));
		} catch (ParseException e) {
			logger.error("Incorrect format of time record in Med_Incident table " + e.getMessage(), e);
		}
        
        return dateCal.getTime();
    }
}
