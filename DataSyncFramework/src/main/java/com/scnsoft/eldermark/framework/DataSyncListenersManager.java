package com.scnsoft.eldermark.framework;

import com.scnsoft.eldermark.framework.model.source.DeletedKeysData;

import java.util.List;

public interface DataSyncListenersManager {
    void register(DataSyncListener listener);
    void unregister(DataSyncListener listener);
    void registerIterationListener(DataSyncStatsListener listener);

    // Data Logging
    void notifyAboutSuccess(SuccessMessage message);
    void notifyAboutErrors(List<ErrorMessage> messages);
    void notifyAboutNewDeletionLogRecords(DatabaseInfo database, List<DeletedKeysData> logRecords);

    // Stats logging
    void notifyAboutIterationStarted();
    void notifyAboutIterationCompleted();
    void notifyAboutDatabaseStarted(DatabaseInfo database);
    void notifyAboutDatabaseCompleted();
    void notifyAboutSyncServiceStarted(DatabaseInfo database, Class<? extends SyncService> syncServiceClass);
    void notifyAboutSyncServiceCompleted();
}
