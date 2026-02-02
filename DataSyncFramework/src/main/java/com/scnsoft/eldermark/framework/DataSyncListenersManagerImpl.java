package com.scnsoft.eldermark.framework;

import com.scnsoft.eldermark.framework.model.source.DeletedKeysData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataSyncListenersManagerImpl implements DataSyncListenersManager {
    private List<DataSyncListener> listeners = new ArrayList<DataSyncListener>();

    private DataSyncStatsListener syncStatsListener;

    private Long statsIterationNumber;
    private Long statsDatabaseId;
    private Long statsSyncServiceId;

    @Override
    public void notifyAboutSuccess(SuccessMessage message) {
        for (DataSyncListener listener : listeners) {
            listener.onDataSyncSuccess(message, statsIterationNumber);
        }
    }

    @Override
    public void notifyAboutErrors(List<ErrorMessage> messages) {
        for (DataSyncListener listener : listeners) {
            listener.onDataSyncErrors(messages, statsIterationNumber);
        }
    }

    @Override
    public void notifyAboutNewDeletionLogRecords(DatabaseInfo database, List<DeletedKeysData> logRecords) {
        for (DataSyncListener listener : listeners) {
            listener.onNewDeletionLogRecords(database, logRecords);
        }
    }

    @Override
    public void notifyAboutIterationStarted() {
        if (syncStatsListener != null)
            statsIterationNumber = syncStatsListener.onSyncIterationStarted();
    }

    @Override
    public void notifyAboutIterationCompleted() {
        if (syncStatsListener != null)
            syncStatsListener.onSyncObjectCompleted(statsIterationNumber);
    }

    public void notifyAboutDatabaseStarted(DatabaseInfo database) {
        if (syncStatsListener != null)
            statsDatabaseId = syncStatsListener.onSyncDatabaseStarted(statsIterationNumber, database);
    }

    public void notifyAboutDatabaseCompleted() {
        if (syncStatsListener != null)
            syncStatsListener.onSyncObjectCompleted(statsDatabaseId);
    }

    public void notifyAboutSyncServiceStarted(DatabaseInfo database, Class<? extends SyncService> syncServiceClass) {
        if (syncStatsListener != null)
            statsSyncServiceId = syncStatsListener.onSyncServiceStarted(statsIterationNumber, database, syncServiceClass);
    }

    public void notifyAboutSyncServiceCompleted() {
        if (syncStatsListener != null)
            syncStatsListener.onSyncObjectCompleted(statsSyncServiceId);
    }

    @Override
    public void registerIterationListener(DataSyncStatsListener listener) {
        this.syncStatsListener = listener;
    }

    @Override
    public void register(DataSyncListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregister(DataSyncListener listener) {
        listeners.remove(listener);
    }
}
