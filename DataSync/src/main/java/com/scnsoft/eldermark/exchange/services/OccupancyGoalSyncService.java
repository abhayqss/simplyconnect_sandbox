package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.FKResolveErrorFactory;
import com.scnsoft.eldermark.exchange.dao.target.OccupancyGoalDao;
import com.scnsoft.eldermark.exchange.fk.OccupancyGoalForeignKeys;
import com.scnsoft.eldermark.exchange.model.source.CompanyData;
import com.scnsoft.eldermark.exchange.model.source.OccupancyGoalData;
import com.scnsoft.eldermark.exchange.model.target.OccupancyGoal;
import com.scnsoft.eldermark.exchange.resolvers.CompanyIdResolver;
import com.scnsoft.eldermark.framework.EntityMetadata;
import com.scnsoft.eldermark.framework.IdMapping;
import com.scnsoft.eldermark.framework.SyncService;
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
public class OccupancyGoalSyncService extends StandardSyncService<OccupancyGoalData, Long, OccupancyGoalForeignKeys> {
    @Autowired
    @Qualifier("occupancyGoalSourceDao")
    private StandardSourceDao<OccupancyGoalData, Long> sourceDao;

    @Autowired
    private OccupancyGoalDao occupancyGoalDao;

    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        List<Class<? extends SyncService>> dependencies = new ArrayList<Class<? extends SyncService>>();
        dependencies.add(CompanySyncService.class);
        return dependencies;
    }

    @Override
    protected StandardSourceDao<OccupancyGoalData, Long> getSourceDao() {
        return sourceDao;
    }

    @Override
    protected EntityMetadata provideSourceEntityMetadata() {
        return new EntityMetadata(OccupancyGoalData.TABLE_NAME, OccupancyGoalData.UNIQUE_ID,
                OccupancyGoalData.class);
    }

    @Override
    protected IdMapping<Long> getIdMapping(DatabaseSyncContext syncContext, List<Long> legacyIds) {
        return occupancyGoalDao.getIdMapping(syncContext.getDatabase(), legacyIds);
    }

    @Override
    protected FKResolveResult<OccupancyGoalForeignKeys> resolveForeignKeys(DatabaseSyncContext syncContext,
                                                                           OccupancyGoalData sourceEntity) {
        CompanyIdResolver companyIdResolver = syncContext.getSharedObject(CompanyIdResolver.class);

        OccupancyGoalForeignKeys foreignKeys = new OccupancyGoalForeignKeys();
        List<FKResolveError> errors = new ArrayList<FKResolveError>();
        FKResolveErrorFactory errorFactory = new FKResolveErrorFactory(OccupancyGoalData.TABLE_NAME,
                sourceEntity.getId());

        String companyLegacyId = sourceEntity.getFacility();
        if (companyLegacyId != null) {
            try {
                long companyNewId = companyIdResolver.getId(companyLegacyId, syncContext.getDatabase());
                foreignKeys.setOrganizationId(companyNewId);
            } catch (IdMappingException e) {
                errors.add(errorFactory.newInvalidSourceFkError(CompanyData.TABLE_NAME, companyLegacyId));
            }
        }
        return new FKResolveResult<OccupancyGoalForeignKeys>(foreignKeys, errors);
    }

    @Override
    protected void doEntitiesInsertion(DatabaseSyncContext syncContext,
                                       List<OccupancyGoalData> sourceEntities,
                                       Map<OccupancyGoalData, OccupancyGoalForeignKeys> foreignKeysMap) {
        List<OccupancyGoal> occupancyGoals = new ArrayList<OccupancyGoal>();
        for (OccupancyGoalData sourceEntity : sourceEntities) {
            OccupancyGoalForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            OccupancyGoal occupancyGoal = new OccupancyGoal();
            occupancyGoal.setLegacyId(sourceEntity.getId());
            occupancyGoal.setDatabaseId(syncContext.getDatabaseId());
            occupancyGoal.setUpdatable(createUpdatable(sourceEntity, foreignKeys));

            occupancyGoals.add(occupancyGoal);
        }
        occupancyGoalDao.insert(occupancyGoals);
    }

    @Override
    protected void doEntitiesUpdate(DatabaseSyncContext syncContext,
                                    List<OccupancyGoalData> sourceEntities,
                                    Map<OccupancyGoalData, OccupancyGoalForeignKeys> foreignKeysMap,
                                    IdMapping<Long> idMapping) {
        for (OccupancyGoalData sourceEntity : sourceEntities) {
            OccupancyGoalForeignKeys foreignKeys = foreignKeysMap.get(sourceEntity);

            long legacyId = sourceEntity.getId();
            long newId = idMapping.getNewIdOrThrowException(legacyId);

            OccupancyGoal.Updatable updatable = createUpdatable(sourceEntity, foreignKeys);
            occupancyGoalDao.update(updatable, newId);
        }
    }

    @Override
    protected void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString) {
        occupancyGoalDao.delete(syncContext.getDatabase(), Long.valueOf(legacyIdString));
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
    }

    private OccupancyGoal.Updatable createUpdatable(OccupancyGoalData sourceEntity,
                                                    OccupancyGoalForeignKeys foreignKeys) {
        OccupancyGoal.Updatable updatable = new OccupancyGoal.Updatable();
        updatable.setMonth(sourceEntity.getMonth());
        updatable.setHeadCountGoal(sourceEntity.getHeadCountGoal());
        updatable.setUnitsOccupiedGoal(sourceEntity.getUnitsOccupiedGoal());
        updatable.setBudgetedCensus(sourceEntity.getBudgetedCensus());
        updatable.setStartup(sourceEntity.getStartup());
        updatable.setOrganizationId(foreignKeys.getOrganizationId());
        return updatable;
    }
}
