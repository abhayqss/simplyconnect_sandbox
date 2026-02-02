package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.ResMedDupDao;
import com.scnsoft.eldermark.exchange.fk.ResMedDupForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.*;
import com.scnsoft.eldermark.exchange.model.target.ResMedDup;
import com.scnsoft.eldermark.exchange.model.target.ResMedDup.Updatable;
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
import java.util.List;
import java.util.Map;

@Service
public class ResMedDupSyncService extends StandardSyncService<ResMedDupData, Long, ResMedDupForeignKeys> {

	@Autowired
	@Qualifier("resMedDupSourceDao")
	private StandardSourceDao<ResMedDupData, Long> sourceDao;

	@Autowired
	private ResMedDupDao resMedDupDao;

	@Override
	public List<Class<? extends SyncService>> dependsOn() {
		List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
		dependencies.add(EmployeeSyncService.class);
		dependencies.add(CompanySyncService.class);
		dependencies.add(ResidentSyncService.class);
		dependencies.add(MedicationSyncService.class);
		return dependencies;
	}

	@Override
	public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
	}

	@Override
	protected StandardSourceDao<ResMedDupData, Long> getSourceDao() {
		return sourceDao;
	}

	@Override
	protected EntityMetadata provideSourceEntityMetadata() {
		return new EntityMetadata(ResMedDupData.TABLE_NAME, ResMedDupData.ID_COLUMN, ResMedDupData.class);
	}

	@Override
	protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<ResMedDupData> sourceEntities,
			Map<ResMedDupData, ResMedDupForeignKeys> foreignKeysMap) {
		List<ResMedDup> resMedDups = new ArrayList<ResMedDup>();
		for (ResMedDupData sourceEntity : sourceEntities) {
			ResMedDupForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			ResMedDup resMedDup = new ResMedDup();
			resMedDup.setLegacyId(sourceEntity.getId());
			resMedDup.setDatabaseId(syncContext.getDatabaseId());
			resMedDup.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

			resMedDups.add(resMedDup);
		}
		resMedDupDao.insert(resMedDups);
	}

	@Override
	protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<ResMedDupData> sourceEntities,
			Map<ResMedDupData, ResMedDupForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
		for (ResMedDupData sourceEntity : sourceEntities) {
			long legacyId = sourceEntity.getId();
			long id = idMapping.getNewIdOrThrowException(legacyId);
			ResMedDupForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

			ResMedDup.Updatable update = createUpdatable(sourceEntity, foreignKeys);
			resMedDupDao.update(update, id);
		}
	}

	@Override
	protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
		resMedDupDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
	}

	@Override
	protected FKResolveResult<ResMedDupForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext, ResMedDupData entity) {
		DatabaseInfo database = syncContext.getDatabase();

        EmployeeIdResolver employeeIdResolver = syncContext.getSharedObject(EmployeeIdResolver.class);
        MedicationIdResolver medicationIdResolver = syncContext.getSharedObject(MedicationIdResolver.class);
        ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);

        ResMedDupForeignKeys foreignKeys = new ResMedDupForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(ResMedDupData.TABLE_NAME, entity.getId());
        
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

        String notADupEmployeeLegacyId = entity.getNotADupEmployee();
        if (!Utils.isEmpty(notADupEmployeeLegacyId)) {
            try {
                foreignKeys.setNotADupEmployeeId(employeeIdResolver.getId(notADupEmployeeLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(EmployeeData.TABLE_NAME, notADupEmployeeLegacyId));
            }
        }
        
        Long resMedLegacyId1 = entity.getResMedId1();
        if (!Utils.isNullOrZero(resMedLegacyId1)) {
            try {
                foreignKeys.setResMedId1(medicationIdResolver.getId(resMedLegacyId1, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResMedicationData.TABLE_NAME, resMedLegacyId1));
            }
        }
        
        Long resMedLegacyId2 = entity.getResMedId2();
        if (!Utils.isNullOrZero(resMedLegacyId2)) {
            try {
                foreignKeys.setResMedId2(medicationIdResolver.getId(resMedLegacyId2, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResMedicationData.TABLE_NAME, resMedLegacyId1));
            }
        }

        return new FKResolveResult<ResMedDupForeignKeys>(foreignKeys, errors);
	}

	@Override
	protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
		return resMedDupDao.getIdMapping(syncContext.getDatabase(), legacyIds);
	}

	private Updatable createUpdatable(ResMedDupData sourceEntity, ResMedDupForeignKeys foreignKeys) {
		ResMedDup.Updatable updatable = new ResMedDup.Updatable();

		java.sql.Date createDate = sourceEntity.getCreateDate();
		java.sql.Time createTime = sourceEntity.getCreateTime();
		if (createDate != null) {
			if (createTime != null) {
				updatable.setCreateDate(Utils.mergeDateTime(createDate, createTime));
			} else {
				updatable.setCreateDate(createDate);
			}
		}

		java.sql.Date notADupDate = sourceEntity.getNotADupDate();
		java.sql.Time notADupTime = sourceEntity.getNotADupTime();
		if (notADupDate != null) {
			if (notADupTime != null) {
				updatable.setNotADupDate(Utils.mergeDateTime(notADupDate, notADupTime));
			} else {
				updatable.setNotADupDate(notADupDate);
			}
		}

		updatable.setWaitingForReview(sourceEntity.getWaitingForReview());

		updatable.setResidentId(foreignKeys.getResidentId());
		updatable.setOrganizationId(foreignKeys.getFacilityOrganizationId());
		updatable.setNotADupEmployeeId(foreignKeys.getNotADupEmployeeId());
		updatable.setResMedId1(foreignKeys.getResMedId1());
		updatable.setResMedId2(foreignKeys.getResMedId2());
		return updatable;
	}

}
