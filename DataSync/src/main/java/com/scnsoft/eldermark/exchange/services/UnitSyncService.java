package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.UnitDao;
import com.scnsoft.eldermark.exchange.fk.UnitForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.UnitData;
import com.scnsoft.eldermark.exchange.model.source.UnitStationData;
import com.scnsoft.eldermark.exchange.model.source.UnitTypeData;
import com.scnsoft.eldermark.exchange.model.target.Unit;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.UnitIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.UnitStationIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.UnitTypeIdResolver;
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
public class UnitSyncService extends StandardSyncService<UnitData, Long, UnitForeignKeys> {
    @Autowired
    @Qualifier("unitSourceDao")
    private StandardSourceDao<UnitData, Long> sourceDao;

    @Autowired
    private UnitDao unitDao;

    @Value("${units.idmapping.cache.size}")
    private int idMappingSizeLimit;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(CompanySyncService.class);
        dependencies.add(UnitTypeSyncService.class);
        dependencies.add(UnitStationSyncService.class);
        return dependencies;
    }

    @Override
    protected StandardSourceDao<UnitData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(UnitData.TABLE_NAME, UnitData.UNIT_ID, UnitData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return unitDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<UnitForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
                                                                  UnitData sourceEntity) {
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);
        UnitTypeIdResolver unitTypeIdResolver = syncContext.getSharedObject(UnitTypeIdResolver.class);
        UnitStationIdResolver unitStationIdResolver = syncContext.getSharedObject(UnitStationIdResolver.class);

        DatabaseInfo database = syncContext.getDatabase();

        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(UnitData.TABLE_NAME, sourceEntity.getId());
        UnitForeignKeys foreignKeys = new UnitForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();

        String facility = sourceEntity.getFacility();
        if (!Utils.isEmpty(facility)) {
            try {
                long companyId = companyIdResolver.getId(facility, database);
                foreignKeys.setOrganizationId(companyId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, facility));
            }
        }

        String privateCurrentLegacyId = sourceEntity.getUnitTypePrivateCurrent();
        if (!Utils.isEmpty(privateCurrentLegacyId)) {
            try {
                long privateCurrentId = unitTypeIdResolver.getId(privateCurrentLegacyId, database);
                foreignKeys.setUnitTypePrivateCurrentId(privateCurrentId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(UnitTypeData.TABLE_NAME, privateCurrentLegacyId));
            }
        }

        String semiPrivateALegacyId = sourceEntity.getUnitTypeSemiPrivateACrnt();
        if (!Utils.isEmpty(semiPrivateALegacyId)) {
            try {
                long semiPrivateAId = unitTypeIdResolver.getId(semiPrivateALegacyId, database);
                foreignKeys.setUnitTypeSemiPrivateACrntId(semiPrivateAId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(UnitTypeData.TABLE_NAME, semiPrivateALegacyId));
            }
        }

        String semiPrivateBLegacyId = sourceEntity.getUnitTypeSemiPrivateBCrnt();
        if (!Utils.isEmpty(semiPrivateBLegacyId)) {
            try {
                long semiPrivateBId = unitTypeIdResolver.getId(semiPrivateBLegacyId, database);
                foreignKeys.setUnitTypeSemiPrivateBCrntId(semiPrivateBId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(UnitTypeData.TABLE_NAME, semiPrivateBLegacyId));
            }
        }
        
        Long unitStationLegacyId = sourceEntity.getStationId();
        if (!Utils.isNullOrZero(unitStationLegacyId)) {
            try {
                foreignKeys.setUnitStationId(unitStationIdResolver.getId(unitStationLegacyId, database));
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(UnitStationData.TABLE_NAME, unitStationLegacyId));
            }
        }
        
        return new FKResolveResult<UnitForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<UnitData> sourceEntities,
                                       Map<UnitData, UnitForeignKeys> foreignKeysMap) {
        List<Unit> units = new ArrayList<Unit>();
        for (UnitData sourceEntity : sourceEntities) {
            UnitForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            Unit unit = new Unit();
            unit.setLegacyId(sourceEntity.getId());
            unit.setDatabaseId(syncContext.getDatabaseId());
            unit.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

            units.add(unit);
        }

        unitDao.insert(units);
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<UnitData> sourceEntities,
                                    Map<UnitData, UnitForeignKeys> foreignKeysMap, IdMapping<Long> idMapping) {
        for (UnitData sourceEntity : sourceEntities) {
            UnitForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);
            Unit.Updatable updatable = createUpdatable(sourceEntity, foreignKeys);
            long unitId = idMapping.getNewIdOrThrowException(sourceEntity.getId());
            unitDao.update(updatable, unitId);
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        unitDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
        final IdMapping<Long> idMapping = unitDao.getIdMapping(context.getDatabase(), idMappingSizeLimit);
        context.putSharedObject(UnitIdResolver.class, new UnitIdResolver() {
            @Override
            public long getId(long legacyId, DatabaseInfo database) {
                Long newId = idMapping.getNewId(legacyId);
                if (newId == null) {
                    newId = unitDao.getId(database, legacyId);
                }
                return newId;
            }
        });
    }

    private Unit.Updatable createUpdatable(UnitData sourceEntity, UnitForeignKeys foreignKeys) {
        Unit.Updatable updatable = new Unit.Updatable();
        updatable.setUnitNumber(sourceEntity.getUnitNumber());
        updatable.setCurrentInMaintenance(sourceEntity.getCurrentInMaintenance());
        updatable.setCurrentDivisionStatus(sourceEntity.getCurrentDivisionStatus());
        updatable.setCurrentProductType(sourceEntity.getCurrentProductType());
        updatable.setCurrentOutOfService(sourceEntity.getCurrentOutOfService());
        updatable.setCurrentModel(sourceEntity.getCurrentModel());
        updatable.setOrganizationId(foreignKeys.getOrganizationId());
        updatable.setUnitTypePrivateCurrentId(foreignKeys.getUnitTypePrivateCurrentId());
        updatable.setUnitTypeSemiPrivateACrntId(foreignKeys.getUnitTypeSemiPrivateACrntId());
        updatable.setUnitTypeSemiPrivateBCrntId(foreignKeys.getUnitTypeSemiPrivateBCrntId());
        updatable.setUnitStationId(foreignKeys.getUnitStationId());
        return updatable;
    }
}
