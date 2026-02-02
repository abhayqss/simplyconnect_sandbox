package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.model.target.SyncLogRecord;
import com.scnsoft.eldermark.exchange.model.target.SyncStatsLog;
import com.scnsoft.eldermark.framework.DatabaseInfo;
import com.scnsoft.eldermark.framework.model.source.DeletedKeysData;

import java.util.Date;
import java.util.List;

public interface LoggingService {
    void log(SyncLogRecord syncLogRecord);

    void log(List<SyncLogRecord> syncLogRecords);

    void logDeletionHistory(DatabaseInfo database, List<DeletedKeysData> deletionHistoryRecords);

    void logSyncRevision(DatabaseInfo database, Date syncTime);

    Long logSyncObjectStarted(SyncStatsLog record);
    void logSyncObjectCompleted(SyncStatsLog record);
}
