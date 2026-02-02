package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.target.*;
import com.scnsoft.eldermark.exchange.model.target.DeletionHistoryRecord;
import com.scnsoft.eldermark.exchange.model.target.SyncLogRecord;
import com.scnsoft.eldermark.exchange.model.target.SyncStatsLog;
import com.scnsoft.eldermark.framework.DatabaseInfo;
import com.scnsoft.eldermark.framework.model.source.DeletedKeysData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionOperations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class LoggingServiceImpl implements LoggingService {
    @Autowired
    private SyncLogDao syncLogDao;

    @Autowired
    private SyncStatsDao syncStatsDao;

    @Autowired
    private DatabaseDao databaseDao;

    @Autowired
    private DeletionHistoryDao deletionHistoryDao;

    @Autowired
    private TransactionOperations transactionOperations;

    @Override
    public void log(final SyncLogRecord syncLogRecord) {
        transactionOperations.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                syncLogDao.insert(Collections.singletonList(syncLogRecord));
            }
        });
    }

    @Override
    public void log(final List<SyncLogRecord> syncLogRecords) {
        transactionOperations.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                syncLogDao.insert(syncLogRecords);
            }
        });
    }

    @Override
    public void logDeletionHistory(DatabaseInfo database, List<DeletedKeysData> deletedKeysData) {
        /*Date now = new Date();

        final List<DeletionHistoryRecord> records = new ArrayList<DeletionHistoryRecord>();
        for (DeletedKeysData deletedKeys : deletedKeysData) {
            DeletionHistoryRecord record = new DeletionHistoryRecord();
            record.setDatabaseId(database.getId());
            record.setDateTime(deletedKeys.getDateTime());
            record.setSequenceNum(deletedKeys.getSequenceNum());
            record.setTableName(deletedKeys.getTableName());
            record.setKeyName(deletedKeys.getKeyName());
            record.setKeyValue(deletedKeys.getKeyValue());
            record.setRecycleBinRecNum(deletedKeys.getRecycleBinRecNum());
            record.setUuid(deletedKeys.getUuid());
            record.setCreationDate(now);

            records.add(record);
        }

        transactionOperations.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                deletionHistoryDao.insert(records);
            }
        });*/
    }

    @Override
    public void logSyncRevision(final DatabaseInfo database, final Date syncTime) {
        transactionOperations.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                databaseDao.updateLastSyncDate(database.getId(), syncTime);
            }
        });
    }

    @Override
    public Long logSyncObjectStarted(final SyncStatsLog record) {
        return transactionOperations.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus status) {
                return syncStatsDao.insert(record);
            }
        });
    }

    @Override
    public void logSyncObjectCompleted(final SyncStatsLog record) {
        transactionOperations.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                syncStatsDao.update(record);
            }
        });
    }
}
