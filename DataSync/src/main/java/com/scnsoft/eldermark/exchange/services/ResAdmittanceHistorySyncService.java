package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.MappingUtils;
import com.scnsoft.eldermark.exchange.dao.target.ResAdmittanceHistoryDao;
import com.scnsoft.eldermark.exchange.fk.ResAdmittanceHistoryForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.*;
import com.scnsoft.eldermark.exchange.model.target.ResidentAdmittanceHistory;
import com.scnsoft.eldermark.exchange.resolvers.*;
import com.scnsoft.eldermark.exchange.services.employees.EmployeeSyncService;
import com.scnsoft.eldermark.exchange.services.residents.ResidentSyncService;
import com.scnsoft.eldermark.exchange.services.residents.ResidentUpdateQueueService;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.exceptions.IdMappingException;
import com.scnsoft.eldermark.framework.fk.FKResolveError;
import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class ResAdmittanceHistorySyncService extends
        StandardSyncService<ResAdmittanceHistoryData, Long, ResAdmittanceHistoryForeignKeys> {
    @Autowired
    @Qualifier("resAdmittanceHistorySourceDao")
    private StandardSourceDao<ResAdmittanceHistoryData, Long> sourceDao;

    @Autowired
    private ResAdmittanceHistoryDao targetDao;

    @Autowired
    private ResidentUpdateQueueService residentUpdateQueueService;

    @Value("${resadmittancehistory.idmapping.cache.size}")
    private int idMappingSizeLimit;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(EmployeeSyncService.class);
        dependencies.add(ResidentSyncService.class);
        dependencies.add(CompanySyncService.class);
        dependencies.add(LivingStatusSyncService.class);
        return dependencies;
    }

    @Override
    protected StandardSourceDao<ResAdmittanceHistoryData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(ResAdmittanceHistoryData.TABLE_NAME, ResAdmittanceHistoryData.UNIQUE_ID,
                ResAdmittanceHistoryData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return targetDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<ResAdmittanceHistoryForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
                                                                                  ResAdmittanceHistoryData sourceEntity) {
        EmployeeIdResolver employeeIdResolver = syncContext.getSharedObject(EmployeeIdResolver.class);
        ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);
        LivingStatusIdResolver livingStatusIdResolver = syncContext.getSharedObject(LivingStatusIdResolver.class);

        DatabaseInfo database = syncContext.getDatabase();

        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(ResAdmittanceHistoryData.TABLE_NAME,
                sourceEntity.getId());
        ResAdmittanceHistoryForeignKeys foreignKeys = new ResAdmittanceHistoryForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();

        String employeeLegacyId = sourceEntity.getSalesRepEmployeeId();
        if (!Utils.isEmpty(employeeLegacyId)) {
            try {
                foreignKeys.setSalesRepEmployeeId(employeeIdResolver.getId(employeeLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(EmployeeData.TABLE_NAME, employeeLegacyId));
            }
        }

        Long residentLegacyId = sourceEntity.getResNumber();
        if (!Utils.isNullOrZero(residentLegacyId)) {
            try {
                long residentId = residentIdResolver.getId(residentLegacyId, database);
                foreignKeys.setResidentId(residentId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResidentData.TABLE_NAME, residentLegacyId));
            }
        }

        String facilityLegacyId = sourceEntity.getFacility();
        if (!Utils.isEmpty(facilityLegacyId)) {
            try {
                long facilityId = companyIdResolver.getId(facilityLegacyId, database);
                foreignKeys.setFacilityId(facilityId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facilityLegacyId));
            }
        }

        Long livingStatusLegacyId = sourceEntity.getPrevLivingStatus();
        if (!Utils.isNullOrZero(livingStatusLegacyId)) {
            try {
                long livingStatusId = livingStatusIdResolver.getId(livingStatusLegacyId, database);
                foreignKeys.setLivingStatusId(livingStatusId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(LivingStatusData.TABLE_NAME, livingStatusLegacyId));
            }
        }

        return new FKResolveResult<ResAdmittanceHistoryForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<ResAdmittanceHistoryData> sourceEntities,
                                       Map<ResAdmittanceHistoryData, ResAdmittanceHistoryForeignKeys> foreignKeysMap) {
        List<ResidentAdmittanceHistory> residentAdmittanceHistoryList = new ArrayList<ResidentAdmittanceHistory>();
        Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
        DatabaseInfo database = syncContext.getDatabase();
        Set<Long> mappedConsanaResidentIds = new HashSet<>();
        for (ResAdmittanceHistoryData sourceEntity : sourceEntities) {
            ResAdmittanceHistoryForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            ResidentAdmittanceHistory residentAdmittanceHistory = new ResidentAdmittanceHistory();
            residentAdmittanceHistory.setLegacyId(sourceEntity.getId());
            residentAdmittanceHistory.setDatabaseId(syncContext.getDatabaseId());
            residentAdmittanceHistory.setUpdatable(createUpdatable(sourceEntity, foreignKeys));
            residentAdmittanceHistoryList.add(residentAdmittanceHistory);

            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                ResidentAdmittanceHistory mappedResidentAdmittanceHistory = new ResidentAdmittanceHistory();
                mappedResidentAdmittanceHistory.setLegacyId(sourceEntity.getId());
                mappedResidentAdmittanceHistory.setDatabaseId(databaseIdWithMappedResidentId.getDatabaseId());
                mappedResidentAdmittanceHistory.setUpdatable(createMappedUpdatable(sourceEntity, foreignKeys, databaseIdWithMappedResidentId.getId()));
                if (!StringUtils.isEmpty(databaseIdWithMappedResidentId.getConsanaXOwningId())) {
                    mappedConsanaResidentIds.add(databaseIdWithMappedResidentId.getId());
                }
                residentAdmittanceHistoryList.add(mappedResidentAdmittanceHistory);
            }
        }
        targetDao.insert(residentAdmittanceHistoryList);
        if (!StringUtils.isEmpty(database.getConsanaXOwningId())) {
            residentUpdateQueueService.insert(foreignKeysMap, sourceEntities, "RESIDENT");
        }
        if (!CollectionUtils.isEmpty(mappedConsanaResidentIds)) {
            residentUpdateQueueService.insert(mappedConsanaResidentIds, "RESIDENT");
        }
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<ResAdmittanceHistoryData> sourceEntities,
                                    Map<ResAdmittanceHistoryData, ResAdmittanceHistoryForeignKeys> foreignKeysMap,
                                    IdMapping<Long> idMapping) {
        DatabaseInfo database = syncContext.getDatabase();
        Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
        Set<Long> mappedConsanaResidentIds = new HashSet<>();
        for (ResAdmittanceHistoryData sourceEntity : sourceEntities) {
            ResAdmittanceHistoryForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);
            long id = idMapping.getNewIdOrThrowException(sourceEntity.getId());

            if (MappingUtils.hasMappingForResident(foreignKeys.getResidentId(), mappedResidentIds)) {
                DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(foreignKeys.getResidentId());
                Long mappedNewId = targetDao.getNewId(databaseIdWithMappedResidentId.getDatabaseId(), sourceEntity.getId(), databaseIdWithMappedResidentId.getId());
                if (!StringUtils.isEmpty(databaseIdWithMappedResidentId.getConsanaXOwningId())) {
                    mappedConsanaResidentIds.add(databaseIdWithMappedResidentId.getId());
                }
                targetDao.update(createMappedUpdatable(sourceEntity, foreignKeys, databaseIdWithMappedResidentId.getId()), mappedNewId);
            }

            targetDao.update(createUpdatable(sourceEntity, foreignKeys), id);
        }
        if (!StringUtils.isEmpty(database.getConsanaXOwningId())) {
            residentUpdateQueueService.insert(foreignKeysMap, sourceEntities, "RESIDENT");
        }

        if (!CollectionUtils.isEmpty(mappedConsanaResidentIds)) {
            residentUpdateQueueService.insert(mappedConsanaResidentIds, "RESIDENT");
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        long legacyId = Long.valueOf(legacyIdString);
        DatabaseInfo database = syncContext.getDatabase();
        Map<Long, DatabaseIdWithId> mappedResidentIds = syncContext.getMappedResidentIds();
        Long residentId = targetDao.getResidentId(database, legacyId);
        if (MappingUtils.hasMappingForResident(residentId, mappedResidentIds)) {
            DatabaseIdWithId databaseIdWithMappedResidentId = mappedResidentIds.get(residentId);
            int mappedDeletedCount = targetDao.delete(new IdAwareImpl(databaseIdWithMappedResidentId.getDatabaseId()), legacyId, databaseIdWithMappedResidentId.getId());
            if(!StringUtils.isEmpty(databaseIdWithMappedResidentId.getConsanaXOwningId()) && databaseIdWithMappedResidentId.getId() != null &&
                    mappedDeletedCount  >= 1) {
                residentUpdateQueueService.insert(databaseIdWithMappedResidentId.getId(), "RESIDENT");
            }
        }

        int deletedCount = targetDao.delete(syncContext.getDatabase(), legacyId);
        if(!StringUtils.isEmpty(database.getConsanaXOwningId()) && residentId != null &&
                deletedCount  >= 1) {
            residentUpdateQueueService.insert(residentId, "RESIDENT");
        }
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
        final IdMapping<Long> idMapping = targetDao.getIdMapping(context.getDatabase(), idMappingSizeLimit);
        context.putSharedObject(ResAdmittanceHistoryIdResolver.class, new ResAdmittanceHistoryIdResolver() {
            @Override
            public long getId(long legacyId, DatabaseInfo database) {
                Long newId = idMapping.getNewId(legacyId);
                if (newId == null) {
                    newId = targetDao.getId(database, legacyId);
                }
                return newId;
            }
        });
    }

    private ResidentAdmittanceHistory.Updatable createUpdatable(ResAdmittanceHistoryData sourceEntity,
                                                                      ResAdmittanceHistoryForeignKeys foreignKeys) {
        return createUpdatable(sourceEntity, foreignKeys, null);
    }

    private ResidentAdmittanceHistory.Updatable createMappedUpdatable(ResAdmittanceHistoryData sourceEntity,
                                                                ResAdmittanceHistoryForeignKeys foreignKeys,
                                                                Long residentId) {
        return createUpdatable(sourceEntity, foreignKeys, residentId);
    }

    private ResidentAdmittanceHistory.Updatable createUpdatable(ResAdmittanceHistoryData sourceEntity,
                                                                ResAdmittanceHistoryForeignKeys foreignKeys,
                                                                Long residentId) {
        ResidentAdmittanceHistory.Updatable updatable = new ResidentAdmittanceHistory.Updatable();
        java.sql.Date admitDate = sourceEntity.getAdmitDate();
        java.sql.Time admitTime = sourceEntity.getAdmitTime();
        if (admitDate != null) {
            if (admitTime != null) {
                updatable.setAdmitDate(Utils.mergeDateTime(admitDate, admitTime));
            } else {
                updatable.setAdmitDate(admitDate);
            }
        }

		java.sql.Date dischargeDate = sourceEntity.getDischargeDate();
		java.sql.Time dischargeTime = sourceEntity.getDischargeTime();
		if (dischargeDate != null) {
			if (dischargeTime != null) {
				updatable.setDischargeDate(Utils.mergeDateTime(dischargeDate, dischargeTime));
			} else {
				updatable.setDischargeDate(dischargeDate);
			}
		}
        
		java.sql.Date archiveDate = sourceEntity.getArchiveDate();
		if (archiveDate != null) {
			if (archiveDate != null) {
				updatable.setArchiveDate(archiveDate);
			} else {
				updatable.setArchiveDate(archiveDate);
			}
		}
        
        updatable.setDepositDate(sourceEntity.getDepositDate());
        updatable.setRentalAgreementDate(sourceEntity.getRentalAgreementDate());
        updatable.setReservedFromDate(sourceEntity.getReservedFromDate());
        updatable.setReservedToDate(sourceEntity.getReservedToDate());
        updatable.setAdmitSequence(sourceEntity.getAdmitSequence());
        updatable.setAdmitFacilitySequence(sourceEntity.getAdmitFacilitySequence());
        updatable.setUnitNumber(sourceEntity.getUnitNumber());
        updatable.setSalesRepEmployeeId(foreignKeys.getSalesRepEmployeeId());
        updatable.setResidentId(residentId != null ? residentId : foreignKeys.getResidentId());
        updatable.setOrganizationId(foreignKeys.getFacilityId());
        updatable.setCountyAdmittedFrom(sourceEntity.getCountyAdmittedFrom());
        updatable.setLivingStatusId(foreignKeys.getLivingStatusId());
        return updatable;
    }
}
