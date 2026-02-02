package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.target.UnitTypeDao;
import com.scnsoft.eldermark.exchange.fk.UnitTypeForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.UnitTypeData;
import com.scnsoft.eldermark.exchange.model.target.UnitType;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.exchange.resolvers.UnitTypeIdResolver;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UnitTypeSyncService extends StandardSyncService<UnitTypeData, String, UnitTypeForeignKeys> {
    @Autowired
    @Qualifier("unitTypeSourceDao")
    private StandardSourceDao<UnitTypeData, String> sourceDao;

    @Autowired
    private UnitTypeDao unitTypeDao;

    @Value("${unittypes.idmapping.cache.size}")
    private int idMappingSizeLimit;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(CompanySyncService.class);
        return dependencies;
    }

    @Override
    protected StandardSourceDao<UnitTypeData, String> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(UnitTypeData.TABLE_NAME, UnitTypeData.CODE, UnitTypeData.class);
    }

    @Override
    protected IdMapping<String> getIdMapping(DatabaseSyncContext syncContext, List<String> legacyIds) {
        return unitTypeDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<UnitTypeForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
                                                                      UnitTypeData sourceEntity) {
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);

        DatabaseInfo database = syncContext.getDatabase();

        UnitTypeForeignKeys foreignKeys = new UnitTypeForeignKeys();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(UnitTypeData.TABLE_NAME, sourceEntity.getId());
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
        return new FKResolveResult<UnitTypeForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext, List<UnitTypeData> sourceEntities,
                                       Map<UnitTypeData, UnitTypeForeignKeys> foreignKeysMap) {
        List<UnitType> unitTypes = new ArrayList<UnitType>();
        for (UnitTypeData sourceEntity : sourceEntities) {
            UnitTypeForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            UnitType unitType = new UnitType();
            unitType.setLegacyId(sourceEntity.getCode());
            unitType.setDatabaseId(syncContext.getDatabaseId());
            unitType.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

            unitTypes.add(unitType);
        }
        unitTypeDao.insert(unitTypes);
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext, List<UnitTypeData> sourceEntities,
                                    Map<UnitTypeData, UnitTypeForeignKeys> foreignKeysMap, IdMapping<String> idMapping) {
        for (UnitTypeData sourceEntity : sourceEntities) {
            UnitTypeForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);
            String legacyId = sourceEntity.getCode();
            long newId = idMapping.getNewIdOrThrowException(legacyId);

            UnitType.Updatable updatable = createUpdatable(sourceEntity, foreignKeys);
            unitTypeDao.update(updatable, newId);
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        unitTypeDao.delete(syncContext.getDatabase(), legacyIdString);
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
        final IdMapping<String> idMapping = unitTypeDao.getIdMapping(context.getDatabase(), idMappingSizeLimit);
        context.putSharedObject(UnitTypeIdResolver.class, new UnitTypeIdResolver() {
            @Override
            public long getId(String legacyId, DatabaseInfo database) {
                Long newId = idMapping.getNewId(legacyId);
                if (newId == null) {
                    newId = unitTypeDao.getId(database, legacyId);
                }
                return newId;
            }
        });
    }

    private UnitType.Updatable createUpdatable(UnitTypeData sourceEntity, UnitTypeForeignKeys foreignKeys) {
        UnitType.Updatable updatable = new UnitType.Updatable();
        updatable.setDescription(sourceEntity.getDescription());
        updatable.setOutpatient(sourceEntity.getOutpatient());
        updatable.setInactive(sourceEntity.getInactive());
        updatable.setSemiPrivate(sourceEntity.getSemiPrivate());
        updatable.setOrganizationId(foreignKeys.getOrganizationId());
        updatable.setMonthlyRate(sourceEntity.getMonthlyRate());
        updatable.setDailyRate(sourceEntity.getDailyRate());
        return updatable;
    }
}
