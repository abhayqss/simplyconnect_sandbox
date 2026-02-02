package com.scnsoft.eldermark.exchange.listeners;

import com.scnsoft.eldermark.exchange.dao.target.DatabaseDao;
import com.scnsoft.eldermark.exchange.model.target.SyncLogRecord;
import com.scnsoft.eldermark.exchange.model.target.SyncLogRecordType;
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

import java.util.ArrayList;
import java.util.List;

@Component("syncLogListener")
public class DataSyncListenerImpl implements DataSyncListener {
    private static Logger logger = LoggerFactory.getLogger(DataSyncListenerImpl.class);

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private DatabaseDao databaseDao;

    @Override
    public void onDataSyncSuccess(SuccessMessage message, Long iterationNumber) {
        SyncLogRecord record = new SyncLogRecord();
        record.setDate(message.getDate());
        record.setDescription(message.getText());
        record.setDatabaseId(message.getDatabase().getId());
        record.setTypeId(SyncLogRecordType.INFO.getId());
        record.setIterationNumber(iterationNumber);

        try {
            loggingService.log(record);
            databaseDao.setDatabaseLastSyncedTime(message.getDatabase().getUrl(), message.getDatabase().getCurrentSyncTime());
        } catch (Exception e) {
        }
    }

    @Override
    public void onDataSyncErrors(List<ErrorMessage> messages, Long iterationNumber) {
        List<SyncLogRecord> records = new ArrayList<SyncLogRecord>();
        for (ErrorMessage message : messages) {
            SyncLogRecord record = new SyncLogRecord();
            record.setDate(message.getDate());
            record.setDescription(message.getText());
            record.setDatabaseId(message.getDatabase().getId());
            record.setTypeId(SyncLogRecordType.ERROR.getId());
            record.setTableName(message.getTableName());
            record.setStackTrace(message.getStackTrace());
            record.setIterationNumber(iterationNumber);

            records.add(record);
        }

        try {
            loggingService.log(records);
        } catch (Exception e) {
            logger.error("Failed to save data sync error log records");
        }
    }

    @Override
    public void onNewDeletionLogRecords(DatabaseInfo database, List<DeletedKeysData> logRecords) {
        try {
            loggingService.logDeletionHistory(database, logRecords);
        } catch (Exception e) {
            logger.error("Failed to save deletion log records");
        }
    }
}
