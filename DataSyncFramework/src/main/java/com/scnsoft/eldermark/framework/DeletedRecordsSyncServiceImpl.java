package com.scnsoft.eldermark.framework;

import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.dao.source.DeletedKeysSourceDao;
import com.scnsoft.eldermark.framework.dao.source.filters.DeletedKeysReadFilter;
import com.scnsoft.eldermark.framework.model.source.DeletedKeysData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class DeletedRecordsSyncServiceImpl implements DeletedRecordsSyncService {
    private static final Logger logger = LoggerFactory.getLogger(DeletedRecordsSyncServiceImpl.class);
    private static final int LOAD_BATCH_SIZE = 1000;

    @Autowired
    private DeletedKeysSourceDao sourceDao;

    @Autowired
    private DataSyncListenersManager listenersManager;

    @Override
    public void sync(DatabaseSyncContext context, List<SyncService> syncServices,
                     PerformanceStatisticsHolder performanceStatisticsHolder) {
        try {
            logger.info("Synchronizing deleted records...");
            long startTime = System.currentTimeMillis();

            Sql4DOperations sqlOperations = context.getSql4dOperations();

            for (SyncService syncService : syncServices) {
                SyncService.DeletionRelatedOperations deletionRelatedOperations = syncService.getDeletionRelatedOperations();

                if (deletionRelatedOperations != null) {
                    EntityMetadata metadata = deletionRelatedOperations.provideSourceEntityMetadata();
                    if (metadata == null) {
                        throw new NullPointerException("EntityMetadata is null: " + syncService.getClass().getSimpleName());
                    }
                    logger.info("Synchronizing deleted records for '{}' table...", metadata.getTableName());

                    long entitySyncStartTime = System.currentTimeMillis();

                    Long idLowerBoundExclusive = null;
                    List<DeletedKeysData> deletedKeysList;
                    do {
                        DeletedKeysReadFilter filter = new DeletedKeysReadFilter.Builder()
                                .setCurrentSyncEpoch(context.getDatabase().getCurrentSyncTime())
                                .setLastSyncEpoch(context.getDatabase().getLastSyncedTime())
                                .setTableName(metadata.getTableName())
                                .setIdLowerBoundExclusive(idLowerBoundExclusive)
                                .setOrderById(true)
                                .setLimit(LOAD_BATCH_SIZE)
                                .build();
                        deletedKeysList = sourceDao.read(sqlOperations, filter);

                        if (!deletedKeysList.isEmpty()) {
                            idLowerBoundExclusive = getMaxSequenceNumber(deletedKeysList);
                            for (DeletedKeysData deletedKeys : deletedKeysList) {
                                deleteRecord(deletedKeys, context, syncService);
                            }
                        }
                    } while (!deletedKeysList.isEmpty());

                    long entitySyncExecutionTime = System.currentTimeMillis() - entitySyncStartTime;

                    logger.info("Completed synchronization of deleted records for '{}' table in {} ms",
                            metadata.getTableName(),
                            entitySyncExecutionTime);

                    performanceStatisticsHolder.registerExecutionTime(metadata.getTableName(),
                            DataSyncStep.SYNC_DELETED, entitySyncExecutionTime);
                }
            }

            //logger.info("Saving a full history of deleted records (including ignored records) into a history table...");
            //Saves a history of DeletesKeys (including ignored records, for tables not used in the Exchange)
            //CCN-5502 saving full log of deleted records is disabled
            //saveDeletedKeysFullHistory(context);
            //logger.info("Successfully saved a history of deleted records");

            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("Successfully completed synchronization of deleted records in {} ms", executionTime);
        } catch (Exception e) {
            String issueDescription = "Failed to synchronize deleted records";

            ErrorMessage message = new ErrorMessage();
            message.setDatabase(context.getDatabase());
            message.setTableName(null);
            message.setText(issueDescription);
            message.setStackTrace(Utils.getStacktraceAsString(e));
            message.setDate(new Date());

            listenersManager.notifyAboutErrors(Collections.singletonList(message));
            logger.error(issueDescription, e);
        }

    }

    @Deprecated
    private void saveDeletedKeysFullHistory(final DatabaseSyncContext syncContext) {
        Sql4DOperations sqlOperations = syncContext.getSql4dOperations();

        List<DeletedKeysData> deletedKeysList;
        Long idLowerBoundExclusive = null;
        do {
            DeletedKeysReadFilter filter = new DeletedKeysReadFilter.Builder()
                    .setCurrentSyncEpoch(syncContext.getDatabase().getCurrentSyncTime())
                    .setLastSyncEpoch(syncContext.getDatabase().getLastSyncedTime())
                    .setIdLowerBoundExclusive(idLowerBoundExclusive)
                    .setOrderById(true)
                    .setLimit(LOAD_BATCH_SIZE)
                    .build();
            deletedKeysList = sourceDao.read(sqlOperations, filter);

            if (!deletedKeysList.isEmpty()) {
                idLowerBoundExclusive = getMaxSequenceNumber(deletedKeysList);
                listenersManager.notifyAboutNewDeletionLogRecords(syncContext.getDatabase(), deletedKeysList);
            }

        } while (!deletedKeysList.isEmpty());
    }

    private Long getMaxSequenceNumber(List<DeletedKeysData> recordDeletions) {
        Long maxSequenceNumber = null;
        for (DeletedKeysData recordDeletion : recordDeletions) {
            long currentSequenceNumber = recordDeletion.getSequenceNum();
            if (maxSequenceNumber == null || currentSequenceNumber > maxSequenceNumber) {
                maxSequenceNumber = currentSequenceNumber;
            }
        }
        return maxSequenceNumber;
    }


    private void deleteRecord(final DeletedKeysData deletedKeys, final DatabaseSyncContext context,
                              SyncService syncService) {
        try {
            final SyncService.DeletionRelatedOperations deletionRelatedOperations = syncService.getDeletionRelatedOperations();
            EntityMetadata metadata = deletionRelatedOperations.provideSourceEntityMetadata();
            validateRecord(deletedKeys, metadata.getIdColumnName(), metadata.getTableName());
            deletionRelatedOperations.onDeletedEntityFound(context, deletedKeys.getKeyValue());
        } catch (RuntimeException e) {
            String issueDescription = "Failed to delete record #" + deletedKeys.getKeyValue();

            ErrorMessage message = new ErrorMessage();
            message.setDatabase(context.getDatabase());
            message.setTableName(deletedKeys.getTableName());
            message.setText(issueDescription);
            message.setStackTrace(Utils.getStacktraceAsString(e));
            message.setDate(new Date());

            listenersManager.notifyAboutErrors(Collections.singletonList(message));
            logger.error(issueDescription, e);

            throw e;
        }
    }

    private void validateRecord(DeletedKeysData deletedKeys, String expectedKeyName, String tableName) {
        String actualKeyName = deletedKeys.getKeyName();

        //TODO workaround for res_vitals keys issue. Remove, when fixes to names will be applied to 4D
        if ("Res_Vitals".equalsIgnoreCase(tableName) && "Res_Vitals_UUID".equalsIgnoreCase(actualKeyName)) {
            return;
        }

        if (!expectedKeyName.equalsIgnoreCase(actualKeyName)) {
            throw new RuntimeException("Unexpected key name '" + actualKeyName + "': expected '" + expectedKeyName + "'");
        }
    }
}
