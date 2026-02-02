package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.model.source.EmployeeData;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionOperations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class BaseSyncService<E extends IdentifiableSourceEntity<LegacyId>, LegacyId extends Comparable<LegacyId>, FK>
        extends SyncServiceTemplate<E, LegacyId, FK> {
    @Autowired
    private TransactionOperations transactionOperations;

    @Value("${default.load.batch.size}")
    private int loadBatchSize;

    @Value("${default.idmapping.batch.size}")
    private int idMappingBatchSize;

    @Override
    protected DataSyncConfiguration getConfiguration() {
        return new DataSyncConfiguration(loadBatchSize, idMappingBatchSize);
    }

    protected abstract EntityMetadata provideSourceEntityMetadata();

    protected abstract void doEntitiesInsertion(DatabaseSyncContext syncContext,
                                                List<E> sourceEntities, Map<E, FK> foreignKeysMap);

    protected abstract void doEntitiesUpdate(DatabaseSyncContext syncContext, List<E> sourceEntities,
                                             Map<E, FK> foreignKeysMap, IdMapping<LegacyId> idMapping);

    protected abstract void deleteEntity(DatabaseSyncContext syncContext, String legacyIdString);

    @Override
    protected final String getSourceTableName() {
        return provideSourceEntityMetadata().getTableName();
    }

    @Override
    protected final List<EntitySyncError<E, LegacyId>> insertEntities(final DatabaseSyncContext syncContext, final List<E> sourceEntities,
                                            final Map<E, FK> foreignKeysMap) {
        transactionOperations.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                doEntitiesInsertion(syncContext, sourceEntities, foreignKeysMap);
                /*List<DataLogRecord> records = createLogRecords(syncContext.getDatabase(), sourceEntities,
                        SourceEntityStatus.NEW);
                dataLogDao.insert(records);*/
            }
        });
        return Collections.emptyList(); //Currently "entities sync errors" feature isn't used
    }

    @Override
    protected final List<EntitySyncError<E, LegacyId>> updateEntities(final DatabaseSyncContext syncContext, final List<E> sourceEntities,
                                                final Map<E, FK> foreignKeysMap, final IdMapping<LegacyId> idMapping) {
        for (E sourceEntity : sourceEntities) {
            final List<E> singleEntityList = new ArrayList<>();
            singleEntityList.add(sourceEntity);
            transactionOperations.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    doEntitiesUpdate(syncContext, singleEntityList, foreignKeysMap, idMapping);
                }
            });
        }
        return Collections.emptyList(); //Currently "entities sync errors" feature isn't used
    }

    @Override
    public final DeletionRelatedOperations getDeletionRelatedOperations() {
        return new DeletionRelatedOperations() {
            @Override
            public EntityMetadata provideSourceEntityMetadata() {
                return BaseSyncService.this.provideSourceEntityMetadata();
            }

            @Override
            public void onDeletedEntityFound(final DatabaseSyncContext syncContext,
                                             final String legacyIdString) {
                transactionOperations.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        deleteEntity(syncContext, legacyIdString);

                        /*DataLogRecord record = new DataLogRecord();
                        record.setDatabaseId(syncContext.getDatabaseId());
                        record.setDate(new Date());
                        record.setSourceObject(provideSourceEntityMetadata().getTableName() + "#" + legacyIdString);
                        record.setSourceObjectStatusId(SourceEntityStatus.DELETED.getId());

                        dataLogDao.insert(Collections.singletonList(record));*/
                    }
                });
            }
        };
    }

    /*private List<DataLogRecord> createLogRecords(DatabaseInfo database, List<? extends SourceEntity> entities,
                                                 SourceEntityStatus status) {
        Date date = new Date();
        List<DataLogRecord> records = new ArrayList<DataLogRecord>();
        for (SourceEntity sourceEntity : entities) {
            DataLogRecord record = new DataLogRecord();
            record.setDatabaseId(database.getId());
            record.setDate(date);
            record.setSourceObject(sourceEntity.toString());
            record.setSourceObjectStatusId(status.getId());

            records.add(record);
        }
        return records;
    }*/
}
