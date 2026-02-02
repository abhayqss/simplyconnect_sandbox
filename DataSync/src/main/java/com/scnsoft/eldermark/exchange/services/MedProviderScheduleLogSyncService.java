package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.MedProviderScheduleLogDao;
import com.scnsoft.eldermark.exchange.fk.MedProviderScheduleLogForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.EmployeeData;
import com.scnsoft.eldermark.exchange.model.source.MedProviderSchedLogData;
import com.scnsoft.eldermark.exchange.model.source.MedProviderScheduleData;
import com.scnsoft.eldermark.exchange.model.target.MedProviderScheduleLog;
import com.scnsoft.eldermark.exchange.model.target.MedProviderScheduleLog.Updatable;
import com.scnsoft.eldermark.exchange.resolvers.EmployeeIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.MedProviderScheduleIdResolver;
import com.scnsoft.eldermark.exchange.services.employees.EmployeeSyncService;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MedProviderScheduleLogSyncService extends StandardSyncService<MedProviderSchedLogData, String, MedProviderScheduleLogForeignKeys> {

	private static final Logger logger = LoggerFactory.getLogger(ResIncidentSyncService.class);
	
	@Autowired
	@Qualifier("medProviderSchedLogSourceDao")
	private StandardSourceDao<MedProviderSchedLogData, String> sourceDao;

	@Autowired
	private MedProviderScheduleLogDao medProviderScheduleLogDao;
	
	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(MedProviderScheduleSyncService.class);
        dependencies.add(EmployeeSyncService.class);
        return dependencies;
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
	}

	@Override
	protected StandardSourceDao<MedProviderSchedLogData, String> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(MedProviderSchedLogData.TABLE_NAME, MedProviderSchedLogData.ID_COLUMN, MedProviderSchedLogData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<MedProviderSchedLogData> sourceEntities,
			Map<MedProviderSchedLogData, MedProviderScheduleLogForeignKeys> foreignKeysMap) {
		List<MedProviderScheduleLog> medProviderScheduleLogs = new ArrayList<MedProviderScheduleLog>();
		for (MedProviderSchedLogData sourceEntity : sourceEntities) {
			MedProviderScheduleLogForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			MedProviderScheduleLog medProviderScheduleLog = new MedProviderScheduleLog();
			medProviderScheduleLog.setLegacyId(sourceEntity.getId());
			medProviderScheduleLog.setDatabaseId(syncContext.getDatabaseId());
			medProviderScheduleLog.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

			medProviderScheduleLogs.add(medProviderScheduleLog);
		}
		medProviderScheduleLogDao.insert(medProviderScheduleLogs);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<MedProviderSchedLogData> sourceEntities,
			Map<MedProviderSchedLogData, MedProviderScheduleLogForeignKeys> foreignKeysMap, IdMapping<String> idMapping) {
		for (MedProviderSchedLogData sourceEntity : sourceEntities) {
			String legacyId = sourceEntity.getId();
			long id = idMapping.getNewIdOrThrowException(legacyId);
			MedProviderScheduleLogForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			MedProviderScheduleLog.Updatable update = createUpdatable(sourceEntity, foreignKeys);
			medProviderScheduleLogDao.update(update, id);
		}
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		medProviderScheduleLogDao.delete(syncContext.getDatabase(), legacyIdString);
	}

	@Override
	protected FKResolveResult<MedProviderScheduleLogForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
			MedProviderSchedLogData entity) {
		DatabaseInfo database = syncContext.getDatabase();

        EmployeeIdResolver employeeIdResolver = syncContext.getSharedObject(EmployeeIdResolver.class);
        MedProviderScheduleIdResolver medProviderScheduleIdResolver = syncContext.getSharedObject(MedProviderScheduleIdResolver.class);

        MedProviderScheduleLogForeignKeys foreignKeys = new MedProviderScheduleLogForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(MedProviderSchedLogData.TABLE_NAME, entity.getId());
        
        Long medProviderScheduleLegacyId = entity.getMedProviderSchedId();
        if (!Utils.isNullOrZero(medProviderScheduleLegacyId)) {
            try {
                foreignKeys.setMedProviderScheduleId(medProviderScheduleIdResolver.getId(medProviderScheduleLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(MedProviderScheduleData.TABLE_NAME, medProviderScheduleLegacyId));
            }
        }
        
        String employeeLegacyId = entity.getEmployeeId();
        if (!Utils.isEmpty(employeeLegacyId)) {
            try {
                foreignKeys.setEmployeeId(employeeIdResolver.getId(employeeLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(EmployeeData.TABLE_NAME, employeeLegacyId));
            }
        }
        
        return new FKResolveResult<MedProviderScheduleLogForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<String> getIdMapping(DatabaseSyncContext syncContext, List<String> legacyIds) {
		return medProviderScheduleLogDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}
	
	private Updatable createUpdatable(MedProviderSchedLogData sourceEntity, MedProviderScheduleLogForeignKeys foreignKeys) {
		MedProviderScheduleLog.Updatable updatable = new MedProviderScheduleLog.Updatable();
		
		updatable.setDescription(sourceEntity.getDescription());
		updatable.setMoreData(sourceEntity.getMoreData());
		updatable.setMoreTag(sourceEntity.getMoreTag());
		updatable.setSequence(sourceEntity.getSequence());
		
		String strDate = sourceEntity.getDateTime();
		if (!Utils.isEmpty(strDate)) {
			updatable.setDateTime(converStringTimestampToDate(strDate, MedProviderSchedLogData.DATE_TIME_FORMAT));
		}

		updatable.setEmployeeId(foreignKeys.getEmployeeId());
		updatable.setMedProviderScheduleId(foreignKeys.getMedProviderScheduleId());
		return updatable;
	}
	
	private java.util.Date converStringTimestampToDate(String strDate, String format) {
		java.util.Date resultDate = null;
        try {
        	SimpleDateFormat sdf = new SimpleDateFormat(format);
        	resultDate = sdf.parse(strDate);
		} catch (ParseException e) {
			logger.error("Incorrect format of date_time record in Med_Provider_Sched_Log table " + e.getMessage(), e);
		}
        
        return resultDate;
    }
}
