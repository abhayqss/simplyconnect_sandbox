package com.scnsoft.eldermark.framework;

import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.context.TablePortionSyncContext;
import com.scnsoft.eldermark.framework.context.TableSyncContext;
import com.scnsoft.eldermark.framework.dao.source.filters.MaxIdFilter;
import com.scnsoft.eldermark.framework.dao.source.filters.SourceEntitiesFilter;
import com.scnsoft.eldermark.framework.fk.FKResolveError;
import com.scnsoft.eldermark.framework.fk.FKResolveResult;
import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public abstract class SyncServiceTemplate<E extends IdentifiableSourceEntity<LegacyId>,
        LegacyId extends Comparable<LegacyId>, FK> implements SyncService {
    private static final Logger logger = LoggerFactory.getLogger(SyncServiceTemplate.class);

    @Autowired
    private DataSyncListenersManager listenersManager;

    protected abstract DataSyncConfiguration getConfiguration();

    protected abstract String getSourceTableName();

    protected abstract FKResolveResult<FK> resolveForeignKeys(DatabaseSyncContext syncContext, E entity);

    protected abstract List<EntitySyncError<E, LegacyId>> insertEntities(DatabaseSyncContext syncContext,
                                                                         List<E> sourceEntities,
                                                                         Map<E, FK> foreignKeysMap);

    protected abstract List<EntitySyncError<E, LegacyId>> updateEntities(DatabaseSyncContext syncContext,
                                                                         List<E> sourceEntities,
                                                                         Map<E, FK> foreignKeysMap,
                                                                         IdMapping<LegacyId> idMapping);

    protected abstract IdMapping<LegacyId> getIdMapping(DatabaseSyncContext syncContext,
                                                        List<LegacyId> legacyIds);

    protected abstract List<E> getSourceEntities(DatabaseSyncContext syncContext,
                                                 SourceEntitiesFilter<LegacyId> filter);

    protected abstract LegacyId getSourceEntitiesMaxId(DatabaseSyncContext syncContext,
                                                       MaxIdFilter<LegacyId> filter);

    @Override
    public final void syncNewAndUpdated(DatabaseSyncContext syncContext, PerformanceStatisticsHolder performanceStatisticsHolder) {
        TableSyncContext tableSyncContext = new TableSyncContext(syncContext, getSourceTableName());

        int loadBatchSize = getConfiguration().getLoadBatchSize();

        if (logger.isInfoEnabled()) {
            logger.info(createLogPrefix(tableSyncContext) + "started synchronization");
        }
        long startTime = System.currentTimeMillis();

        LegacyId idLowerBoundExclusive = null;
        LegacyId idUpperBoundInclusive = getUpperBoundInclusive(tableSyncContext, idLowerBoundExclusive, loadBatchSize,
                performanceStatisticsHolder);
        while (idUpperBoundInclusive != null) {
            TablePortionSyncContext<LegacyId> tablePortionContext = new TablePortionSyncContext<LegacyId>(tableSyncContext,
                    idLowerBoundExclusive, idUpperBoundInclusive);
            try {
                List<E> sourceEntities = loadData(tablePortionContext, performanceStatisticsHolder);

                ResolveForeignKeysResult<E, FK> result = resolveForeignKeys(tablePortionContext, sourceEntities,
                        performanceStatisticsHolder);
                reportFKResolveIssues(result.getErrors(), tableSyncContext);

                List<EntitySyncError<E, LegacyId>> syncErrors = applyToTargetDatabase(tablePortionContext, result.getValidEntities(),
                        result.getForeignKeysMap(), performanceStatisticsHolder);

                idLowerBoundExclusive = idUpperBoundInclusive;
                idUpperBoundInclusive = getUpperBoundInclusive(tableSyncContext, idLowerBoundExclusive, loadBatchSize,
                        performanceStatisticsHolder);
            } catch (RuntimeException e) {
                //Typically exceptions not related to data integrity of certain records indicate more serious issues,
                //so we should better interrupt data sync to avoid creation of too many records in data sync log
                reportException(tablePortionContext, e);
                throw e;
            }
        }

        long executionTime = System.currentTimeMillis() - startTime;
        if (logger.isInfoEnabled()) {
            logger.info(createLogPrefix(tableSyncContext) + "synchronization has been completed in {} ms", executionTime);
        }
    }

    private void reportFKResolveIssues(List<FKResolveError> errors, TableSyncContext tableSyncContext) {
        List<ErrorMessage> messages = new ArrayList<ErrorMessage>();
        for (FKResolveError error : errors) {
            ErrorMessage message = new ErrorMessage();
            message.setDate(new Date());
            message.setDatabase(tableSyncContext.getDatabase());
            message.setTableName(tableSyncContext.getTableName());
            message.setText(error.getDescription());

            messages.add(message);
        }

        listenersManager.notifyAboutErrors(messages);
    }

    private void reportException(TablePortionSyncContext context, Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Failed to synchronize data for batch (");
        if (context.getIdLowerBoundExclusive() != null) {
            sb.append(context.getIdLowerBoundExclusive().toString());
        }
        sb.append("; ").append(context.getIdUpperBoundInclusive().toString()).append("]");

        final ErrorMessage message = new ErrorMessage();
        message.setDatabase(context.getDatabase());
        message.setTableName(context.getTableName());
        message.setText(sb.toString());
        message.setStackTrace(Utils.getStacktraceAsString(e));
        message.setDate(new Date());

        listenersManager.notifyAboutErrors(Collections.singletonList(message));
    }

    private LegacyId getUpperBoundInclusive(TableSyncContext tableSyncContext, LegacyId idLowerBoundExclusive,
                                            int limit, PerformanceStatisticsHolder performanceStatisticsHolder) {
        if (logger.isInfoEnabled()) {
            logger.info(createLogPrefix(tableSyncContext) + "loading id upper bound...");
        }

        long startTime = System.currentTimeMillis();
        MaxIdFilter.Builder<LegacyId> builder = new MaxIdFilter.Builder<LegacyId>();
        builder.setIdLowerBoundExclusive(idLowerBoundExclusive).setOrderById(true).setLimit(limit);

        builder.setCurrentSyncEpoch(tableSyncContext.getDatabase().getCurrentSyncTime());
        builder.setLastSyncEpoch(tableSyncContext.getDatabase().getLastSyncedTime());

        MaxIdFilter<LegacyId> maxIdFilter = builder.build();
        LegacyId maxId = getSourceEntitiesMaxId(tableSyncContext.getDatabaseSyncContext(), maxIdFilter);
        long executionTime = System.currentTimeMillis() - startTime;

        performanceStatisticsHolder.registerExecutionTime(tableSyncContext.getTableName(),
                DataSyncStep.LOAD_ID_UPPER_BOUND, executionTime);
        if (logger.isInfoEnabled()) {
            logger.info(createLogPrefix(tableSyncContext) + "loaded id upper bound {} in {} ms", maxId, executionTime);
        }
        return maxId;
    }

    private List<E> loadData(TablePortionSyncContext<LegacyId> dataPortionContext, PerformanceStatisticsHolder performanceStatisticsHolder) {
        if (logger.isInfoEnabled()) {
            logger.info(createLogPrefix(dataPortionContext) + "loading entities...");
        }

        long startTime = System.currentTimeMillis();

        SourceEntitiesFilter.Builder<LegacyId> builder = new SourceEntitiesFilter.Builder<LegacyId>();
        builder.setIdLowerBoundExclusive(dataPortionContext.getIdLowerBoundExclusive())
                .setIdUpperBoundInclusive(dataPortionContext.getIdUpperBoundInclusive());
        builder.setCurrentSyncEpoch(dataPortionContext.getDatabase().getCurrentSyncTime());
        builder.setLastSyncEpoch(dataPortionContext.getDatabase().getLastSyncedTime());


        SourceEntitiesFilter<LegacyId> filter = builder.build();

        List<E> sourceEntities = getSourceEntities(dataPortionContext.getDatabaseSyncContext(), filter);
        long executionTime = System.currentTimeMillis() - startTime;

        if (logger.isInfoEnabled()) {
            logger.info(createLogPrefix(dataPortionContext) + "loaded entities in {} ms", executionTime);
        }
        performanceStatisticsHolder.registerExecutionTime(dataPortionContext.getTableName(),
                DataSyncStep.LOAD_SOURCE_DATA, executionTime);
        return sourceEntities;
    }

    private ResolveForeignKeysResult<E, FK> resolveForeignKeys(TablePortionSyncContext<LegacyId> context,
                                                               List<E> sourceEntities,
                                                               PerformanceStatisticsHolder performanceStatisticsHolder) {
        if (logger.isInfoEnabled()) {
            logger.info(createLogPrefix(context) + "resolving foreign keys...");
        }

        long startTime = System.currentTimeMillis();

        List<E> validEntities = new ArrayList<E>();
        List<E> invalidEntities = new ArrayList<E>();
        Map<E, FK> foreignKeysMap = new HashMap<E, FK>();
        final List<FKResolveError> errors = new ArrayList<FKResolveError>();

        for (E sourceEntity : sourceEntities) {
            FKResolveResult<FK> result = resolveForeignKeys(context.getDatabaseSyncContext(), sourceEntity);

            if (result == null) {
                validEntities.add(sourceEntity);
            } else if (result.isResolved()) {
                if (!foreignKeysMap.containsKey(sourceEntity)) {
                    foreignKeysMap.put(sourceEntity, result.getForeignKeys());
                    validEntities.add(sourceEntity);
                } else {
                    FKResolveError error = new FKResolveError("Record \"" + context.getTableName() + "#" + sourceEntity.getId().toString()
                            + "\" is duplicated in source database (multiple identical ids were found)");
                    errors.add(error);
                    invalidEntities.add(sourceEntity);
                }
            } else {
                errors.addAll(result.getErrors());
                invalidEntities.add(sourceEntity);
            }
        }

        ResolveForeignKeysResult<E, FK> foreignKeysResult = new ResolveForeignKeysResult<E, FK>();
        foreignKeysResult.setErrors(errors);
        foreignKeysResult.setForeignKeysMap(foreignKeysMap);
        foreignKeysResult.setValidEntities(validEntities);
        foreignKeysResult.setInvalidEntities(invalidEntities);

        long executionTime = System.currentTimeMillis() - startTime;
        if (logger.isInfoEnabled()) {
            logger.info(createLogPrefix(context) + "resolved foreign keys in {} ms", executionTime);
        }
        performanceStatisticsHolder.registerExecutionTime(context.getTableName(), DataSyncStep.RESOLVE_FOREIGN_KEYS,
                executionTime);

        return foreignKeysResult;
    }

    private List<EntitySyncError<E, LegacyId>> applyToTargetDatabase(
                                        final TablePortionSyncContext<LegacyId> tablePortionContext,
                                       List<E> validSourceEntities, final Map<E, FK> foreignKeysMap,
                                       PerformanceStatisticsHolder performanceStatisticsHolder) {
        List<EntitySyncError<E, LegacyId>> syncErrors = new ArrayList<EntitySyncError<E, LegacyId>>();

        int idMappingBatchSize = getConfiguration().getIdMappingBatchSize();
        final String entityDisplayName = tablePortionContext.getTableName();
        final DatabaseSyncContext syncContext = tablePortionContext.getDatabaseSyncContext();

        List<LegacyId> legacyIds = Utils.getIds(validSourceEntities);
        if (logger.isInfoEnabled()) {
            logger.info(createLogPrefix(tablePortionContext) + "finding new and existing entities...");
        }

        long idMappingStartTime = System.currentTimeMillis();
        final IdMapping<LegacyId> existingEntitiesIdMapping = new IdMapping<LegacyId>();
        for (List<LegacyId> legacyIdsSublist : Utils.partitionList(legacyIds, idMappingBatchSize)) {
            IdMapping<LegacyId> idMappingPart = getIdMapping(syncContext, legacyIdsSublist);
            existingEntitiesIdMapping.putAll(idMappingPart);
        }

        final List<E> newEntities = new ArrayList<E>();
        final List<E> existingEntities = new ArrayList<E>();
        for (E sourceEntity : validSourceEntities) {
            if (existingEntitiesIdMapping.containsLegacyId(sourceEntity.getId())) {
                existingEntities.add(sourceEntity);
            } else {
                newEntities.add(sourceEntity);
            }
        }
        long idMappingTime = System.currentTimeMillis() - idMappingStartTime;

        if (logger.isInfoEnabled()) {
            logger.info(createLogPrefix(tablePortionContext) + "found new and existing entities in {} ms", idMappingTime);
        }
        performanceStatisticsHolder.registerExecutionTime(entityDisplayName, DataSyncStep.FIND_NEW_AND_EXISTING_ENTITIES,
                idMappingTime);

        if (!newEntities.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info(createLogPrefix(tablePortionContext) + "inserting new entities...");
            }
            long insertStartTime = System.currentTimeMillis();

            List<EntitySyncError<E, LegacyId>> insertionErrors = insertEntities(syncContext, newEntities, foreignKeysMap);
            syncErrors.addAll(insertionErrors);

            long insertTime = System.currentTimeMillis() - insertStartTime;
            if (logger.isInfoEnabled()) {
                logger.info(createLogPrefix(tablePortionContext) + "inserted entities in {} ms", insertTime);
            }
            performanceStatisticsHolder.registerExecutionTime(entityDisplayName, DataSyncStep.INSERT_ENTITIES,
                    insertTime);
        }

        if (!existingEntities.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info(createLogPrefix(tablePortionContext) + "updating existing entities...");
            }
            long updateStartTime = System.currentTimeMillis();

            List<EntitySyncError<E, LegacyId>> updateErrors = updateEntities(syncContext, existingEntities, foreignKeysMap,
                    existingEntitiesIdMapping);
            syncErrors.addAll(updateErrors);

            long updateTime = System.currentTimeMillis() - updateStartTime;
            if (logger.isInfoEnabled()) {
                logger.info(createLogPrefix(tablePortionContext) + "updated existing entities in {} ms", updateTime);
            }
            performanceStatisticsHolder.registerExecutionTime(entityDisplayName, DataSyncStep.UPDATE_ENTITIES,
                    updateTime);
        }

        return syncErrors;
    }

    private String createLogPrefix(TableSyncContext tableSyncContext) {
        StringBuilder sb = new StringBuilder();
        sb.append("{DATABASE '").append(tableSyncContext.getDatabaseUrl()).append("' ENTITY '")
                .append(tableSyncContext.getTableName()).append("'}: ");
        return sb.toString();
    }

    private String createLogPrefix(TablePortionSyncContext<?> dataPortionContext) {
        StringBuilder sb = new StringBuilder();
        sb.append("{DATABASE '").append(dataPortionContext.getDatabaseUrl()).append("' ENTITY '")
                .append(dataPortionContext.getTableName()).append("' BATCH (");
        if (dataPortionContext.getIdLowerBoundExclusive() != null) {
            sb.append(dataPortionContext.getIdLowerBoundExclusive().toString());
        }
        sb.append("; ").append(dataPortionContext.getIdUpperBoundInclusive().toString()).append("]}: ");
        return sb.toString();
    }

    private static class ResolveForeignKeysResult<E extends IdentifiableSourceEntity<?>, FK> {
        private List<E> validEntities;
        private Map<E, FK> foreignKeysMap;

        private List<E> invalidEntities;
        private List<FKResolveError> errors;

        private Map<E, FK> getForeignKeysMap() {
            return foreignKeysMap;
        }

        private void setForeignKeysMap(Map<E, FK> foreignKeysMap) {
            this.foreignKeysMap = foreignKeysMap;
        }

        private List<E> getValidEntities() {
            return validEntities;
        }

        private void setValidEntities(List<E> validEntities) {
            this.validEntities = validEntities;
        }

        private List<E> getInvalidEntities() {
            return invalidEntities;
        }

        private void setInvalidEntities(List<E> invalidEntities) {
            this.invalidEntities = invalidEntities;
        }

        private List<FKResolveError> getErrors() {
            return errors;
        }

        private void setErrors(List<FKResolveError> errors) {
            this.errors = errors;
        }
    }
}
