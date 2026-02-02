package com.scnsoft.eldermark.framework;


public interface DataSyncStatsListener {
    Long onSyncIterationStarted();
    Long onSyncDatabaseStarted(Long iterationNumber, DatabaseInfo databaseInfo);
    Long onSyncServiceStarted(Long iterationNumber, DatabaseInfo databaseInfo, Class<? extends SyncService> syncServiceClass);

    void onSyncObjectCompleted(Long statsId);
}
