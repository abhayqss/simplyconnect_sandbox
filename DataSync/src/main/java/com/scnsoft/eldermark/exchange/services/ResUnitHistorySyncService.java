package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.target.ResUnitHistoryDao;
import com.scnsoft.eldermark.exchange.fk.ResUnitHistoryForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.*;
import com.scnsoft.eldermark.exchange.model.target.ResUnitHistory;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.ResAdmittanceHistoryIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.ResidentIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.UnitIdResolver;
import com.scnsoft.eldermark.exchange.services.residents.ResidentSyncService;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.StandardSourceDao;
import com.scnsoft.eldermark.framework.exceptions.IdMappingException;
import com.scnsoft.eldermark.framework.fk.FKResolveError;
import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ResUnitHistorySyncService extends StandardSyncService<ResUnitHistoryData, Long, ResUnitHistoryForeignKeys> {
    @Autowired
    @Qualifier("resUnitHistorySourceDao")
    private StandardSourceDao<ResUnitHistoryData, Long> sourceDao;

    @Autowired
    private ResUnitHistoryDao resUnitHistoryDao;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(CompanySyncService.class);
        dependencies.add(UnitSyncService.class);
        dependencies.add(ResAdmittanceHistorySyncService.class);
        dependencies.add(ResidentSyncService.class);
        return dependencies;
    }

    @Override
    protected StandardSourceDao<ResUnitHistoryData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(ResUnitHistoryData.TABLE_NAME, ResUnitHistoryData.UNIQUE_ID,
                ResUnitHistoryData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return resUnitHistoryDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<ResUnitHistoryForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
                                                                            ResUnitHistoryData sourceEntity) {
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);
        UnitIdResolver unitIdResolver = syncContext.getSharedObject(UnitIdResolver.class);
        ResAdmittanceHistoryIdResolver resAdmittanceHistoryIdResolver =
                syncContext.getSharedObject(ResAdmittanceHistoryIdResolver.class);
        ResidentIdResolver residentIdResolver = syncContext.getSharedObject(ResidentIdResolver.class);

        DatabaseInfo database = syncContext.getDatabase();

        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(ResUnitHistoryData.TABLE_NAME,
                sourceEntity.getId());
        ResUnitHistoryForeignKeys foreignKeys = new ResUnitHistoryForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();

        String facility = sourceEntity.getFacility();
        if (!Utils.isEmpty(facility)) {
            try {
                foreignKeys.setOrganizationId(companyIdResolver.getId(facility, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facility));
            }
        }

        Long unitLegacyId = sourceEntity.getUnitId();
        if (!Utils.isNullOrZero(unitLegacyId)) {
            try {
                foreignKeys.setUnitId(unitIdResolver.getId(unitLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(UnitData.TABLE_NAME, unitLegacyId));
            }
        }

        Long resAdmitLegacyId = sourceEntity.getResAdmitId();
        if (!Utils.isNullOrZero(resAdmitLegacyId)) {
            try {
                foreignKeys.setResAdmittanceHistoryId(resAdmittanceHistoryIdResolver.getId(resAdmitLegacyId,
                        database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResAdmittanceHistoryData.TABLE_NAME, resAdmitLegacyId));
            }
        }

        Long residentLegacyId = sourceEntity.getResNumber();
        if (!Utils.isNullOrZero(residentLegacyId)) {
            try {
                foreignKeys.setResidentId(residentIdResolver.getId(residentLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(ResidentData.TABLE_NAME, residentLegacyId));
            }
        }
        return new FKResolveResult<ResUnitHistoryForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<ResUnitHistoryData> sourceEntities,
                                       Map<ResUnitHistoryData, ResUnitHistoryForeignKeys> foreignKeysMap) {
        List<ResUnitHistory> resUnitHistoryList = new ArrayList<ResUnitHistory>();
        for (ResUnitHistoryData sourceEntity : sourceEntities) {
            ResUnitHistoryForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            ResUnitHistory resUnitHistory = new ResUnitHistory();
            resUnitHistory.setDatabaseId(syncContext.getDatabaseId());
            resUnitHistory.setLegacyId(sourceEntity.getId());
            resUnitHistory.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

            resUnitHistoryList.add(resUnitHistory);
        }
        resUnitHistoryDao.insert(resUnitHistoryList);
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<ResUnitHistoryData> sourceEntities,
                                    Map<ResUnitHistoryData, ResUnitHistoryForeignKeys> foreignKeysMap,
                                    IdMapping<Long> idMapping) {
        for (ResUnitHistoryData sourceEntity : sourceEntities) {
            long id = idMapping.getNewIdOrThrowException(sourceEntity.getId());
            ResUnitHistoryForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            resUnitHistoryDao.update(createUpdatable(sourceEntity, foreignKeys), id);
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        resUnitHistoryDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
    }

    private ResUnitHistory.Updatable createUpdatable(ResUnitHistoryData sourceEntity,
                                                     ResUnitHistoryForeignKeys foreignKeys) {
        ResUnitHistory.Updatable updatable = new ResUnitHistory.Updatable();
        updatable.setUnitNumber(sourceEntity.getUnitNumber());
        updatable.setMoveIn(sourceEntity.getMoveIn());
        updatable.setMoveOut(sourceEntity.getMoveOut());
        updatable.setSecondOccupant(sourceEntity.getSecondOccupant());
        updatable.setMoveInIsTransfer(sourceEntity.getMoveInIsTransfer());
        updatable.setMoveOutIsTransfer(sourceEntity.getMoveOutIsTransfer());
        updatable.setNoticeGiven(sourceEntity.getNoticeGiven());
        updatable.setOrganizationId(foreignKeys.getOrganizationId());
        updatable.setUnitId(foreignKeys.getUnitId());
        updatable.setResAdmittanceHistoryId(foreignKeys.getResAdmittanceHistoryId());
        updatable.setResidentId(foreignKeys.getResidentId());
        return updatable;
    }
}
