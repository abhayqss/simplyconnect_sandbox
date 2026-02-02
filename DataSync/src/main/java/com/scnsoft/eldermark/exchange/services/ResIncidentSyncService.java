package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.ResIncidentDao;
import com.scnsoft.eldermark.exchange.fk.ResIncidentForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.EmployeeData;
import com.scnsoft.eldermark.exchange.model.source.ResIncidentData;
import com.scnsoft.eldermark.exchange.model.source.ResidentData;
import com.scnsoft.eldermark.exchange.model.target.ResIncident;
import com.scnsoft.eldermark.exchange.model.target.ResIncident.Updatable;
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
public class ResIncidentSyncService extends StandardSyncService<ResIncidentData, Long, ResIncidentForeignKeys> {
	
	private static final Logger logger = LoggerFactory.getLogger(ResIncidentSyncService.class);

	@Autowired
	@Qualifier("resIncidentSourceDao")
	private StandardSourceDao<ResIncidentData, Long> sourceDao;

	@Autowired
	private ResIncidentDao resIncidentDao;

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
	protected StandardSourceDao<ResIncidentData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(ResIncidentData.TABLE_NAME, ResIncidentData.ID_COLUMN, ResIncidentData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<ResIncidentData> sourceEntities,
			Map<ResIncidentData, ResIncidentForeignKeys> foreignKeysMap) {
		List<ResIncident> resIncidents = new ArrayList<ResIncident>();
		for (ResIncidentData sourceEntity : sourceEntities) {
			ResIncidentForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			ResIncident resIncident = new ResIncident();
			resIncident.setLegacyId(sourceEntity.getId());
			resIncident.setDatabaseId(syncContext.getDatabaseId());
			resIncident.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

			resIncidents.add(resIncident);
		}
		resIncidentDao.insert(resIncidents);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<ResIncidentData> sourceEntities,
			Map<ResIncidentData, ResIncidentForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
		for (ResIncidentData sourceEntity : sourceEntities) {
			long legacyId = sourceEntity.getId();
			long id = idMapping.getNewIdOrThrowException(legacyId);
			ResIncidentForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			ResIncident.Updatable update = createUpdatable(sourceEntity, foreignKeys);
			resIncidentDao.update(update, id);
		}
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		resIncidentDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<ResIncidentForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext, ResIncidentData entity) {
		DatabaseInfo database = syncContext.getDatabase();

        ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);
        EmployeeIdResolver employeeIdResolver = syncContext.getSharedObject(EmployeeIdResolver.class);

        ResIncidentForeignKeys foreignKeys = new ResIncidentForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(ResIncidentData.TABLE_NAME, entity.getId());
        
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
        
        String personCompletingReportId = entity.getPersonCompletingReportId();
        if (!Utils.isEmpty(personCompletingReportId)) {
            try {
                foreignKeys.setPersonCompletingReportId(employeeIdResolver.getId(personCompletingReportId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(EmployeeData.TABLE_NAME, personCompletingReportId));
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

        return new FKResolveResult<ResIncidentForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return resIncidentDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}
	
	private Updatable createUpdatable(ResIncidentData sourceEntity, ResIncidentForeignKeys foreignKeys) {
		ResIncident.Updatable updatable = new ResIncident.Updatable();
		
		updatable.setUnitNumber(sourceEntity.getUnitNumber());
		updatable.setTypeOfIncident(sourceEntity.getTypeOfIncident());
		updatable.setSentinelEventYn(sourceEntity.getSentinelEventYn());
		updatable.setLocationOfIncidentGeneral(sourceEntity.getLocationOfIncidentGeneral());
		updatable.setLocationOfIncidentSpecific(sourceEntity.getLocationOfIncidentSpecific());
		updatable.setWitnessYn(sourceEntity.getWitnessYn());
		updatable.setInjuiresYn(sourceEntity.getInjuiresYn());
		updatable.setNotifyEmergSrvsYn(sourceEntity.getNotifyEmergSrvsYn());
		updatable.setNotifyEmergSrvsTime(sourceEntity.getNotifyEmergSrvsTime());
		updatable.setNotifyEmergSrvsArrivedAtTime(sourceEntity.getNotifyEmergSrvsArrivedAtTime());
		updatable.setReceivedMedicalCareYn(sourceEntity.getReceivedMedicalCareYn());
		updatable.setPersonCompletingReportName(sourceEntity.getPersonCompletingReportName());
		updatable.setPersonCompletingReportSign(sourceEntity.getPersonCompletingReportSign());
		updatable.setContribFactorsEnvironmental(sourceEntity.getContribFactorsEnvironmental());
		updatable.setContribFactorsResident(sourceEntity.getContribFactorsResident());
		updatable.setContribFactorsMedical(sourceEntity.getContribFactorsMedical());
		updatable.setSignHlthSrvsDirName(sourceEntity.getSignHlthSrvsDirName());
		updatable.setSignHlthSrvsDirSigned(sourceEntity.getSignHlthSrvsDirSigned());
		updatable.setSignHlthSrvsDirLoggedYn(sourceEntity.getSignHlthSrvsDirLoggedYn());
		updatable.setSignExecutiveDirectorName(sourceEntity.getSignExecutiveDirectorName());
		updatable.setSignExecutiveDirectorSigned(sourceEntity.getSignExecutiveDirectorSigned());
		
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
		updatable.setPersonCompletingReportId(foreignKeys.getPersonCompletingReportId());
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
			logger.error("Incorrect format of time record in Res_Incident table " + e.getMessage(), e);
		}
        
        return dateCal.getTime();
    }
	
}
