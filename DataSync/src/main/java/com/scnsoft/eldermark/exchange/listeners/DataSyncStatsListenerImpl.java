package com.scnsoft.eldermark.exchange.listeners;

import com.scnsoft.eldermark.exchange.model.target.SyncStatsLog;
import com.scnsoft.eldermark.exchange.services.LoggingService;
import com.scnsoft.eldermark.framework.DataSyncStatsListener;
import com.scnsoft.eldermark.framework.DatabaseInfo;
import com.scnsoft.eldermark.framework.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DataSyncStatsListenerImpl implements DataSyncStatsListener {
    private static Logger logger = LoggerFactory.getLogger(DataSyncStatsListenerImpl.class);

    @Autowired
    private LoggingService loggingService;

    @Override
    public Long onSyncIterationStarted() {
        SyncStatsLog record = new SyncStatsLog();
        record.setStarted(new Date());
        try {
            return loggingService.logSyncObjectStarted(record);
        } catch (Exception e) {
            logger.error("Failed to log data sync iteration number");
            return null;
        }
    }

    @Override
    public Long onSyncDatabaseStarted(Long iterationNumber, DatabaseInfo databaseInfo) {
        SyncStatsLog record = new SyncStatsLog();
        record.setStarted(new Date());
        record.setDatabaseId(databaseInfo.getId());
        record.setIterationNumber(iterationNumber);
        try {
            return loggingService.logSyncObjectStarted(record);
        } catch (Exception e) {
            logger.error("Failed to log data sync database started date", e);
            return null;
        }
    }

    @Override
    public Long onSyncServiceStarted(Long iterationNumber, DatabaseInfo databaseInfo, Class<? extends SyncService> syncServiceClass) {
        SyncStatsLog record = new SyncStatsLog();
        record.setStarted(new Date());
        record.setDatabaseId(databaseInfo.getId());
        record.setIterationNumber(iterationNumber);
        record.setSyncServiceName(syncServiceClass.getSimpleName());
        try {
            return loggingService.logSyncObjectStarted(record);
        } catch (Exception e) {
            logger.error("Failed to log data sync syncservice started date", e);
            return null;
        }
    }

    @Override
    public void onSyncObjectCompleted(Long id) {
        SyncStatsLog record = new SyncStatsLog();
        record.setId(id);
        record.setCompleted(new Date());
        try {
            loggingService.logSyncObjectCompleted(record);
        } catch (Exception e) {
            logger.error("Failed to log the sync object completed date", e);
        }
    }
}
