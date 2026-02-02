package com.scnsoft.eldermark.framework;

import com.scnsoft.eldermark.framework.model.source.DeletedKeysData;

import java.util.List;

public interface DataSyncListener {
    void onDataSyncSuccess(SuccessMessage message, Long iterationNumber);

    void onDataSyncErrors(List<ErrorMessage> messages, Long iterationNumber);

    void onNewDeletionLogRecords(DatabaseInfo database, List<DeletedKeysData> logRecords);
}
