package com.scnsoft.eldermark.exchange.listeners;

import com.scnsoft.eldermark.exchange.services.LoggingService;
import com.scnsoft.eldermark.framework.DataSyncListener;
import com.scnsoft.eldermark.framework.DatabaseInfo;
import com.scnsoft.eldermark.framework.ErrorMessage;
import com.scnsoft.eldermark.framework.SuccessMessage;
import com.scnsoft.eldermark.framework.model.source.DeletedKeysData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("syncRevisionListener")
public class DataSyncRevisionListenerImpl implements DataSyncListener {
    private static Logger logger = LoggerFactory.getLogger(DataSyncRevisionListenerImpl.class);

    @Autowired
    private LoggingService loggingService;

    @Override
    public void onDataSyncSuccess(SuccessMessage message, Long iterationNumber) {
        try {
            loggingService.logSyncRevision(message.getDatabase(), message.getSyncRevision());
        } catch (Exception e) {
            logger.error("Failed to save last synced revision", e);
        }
    }

    @Override
    public void onDataSyncErrors(List<ErrorMessage> messages, Long iterationNumber) {
    }

    @Override
    public void onNewDeletionLogRecords(DatabaseInfo database, List<DeletedKeysData> logRecords) {
    }
}
