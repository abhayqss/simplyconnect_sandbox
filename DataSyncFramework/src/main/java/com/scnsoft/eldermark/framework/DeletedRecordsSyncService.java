package com.scnsoft.eldermark.framework;

import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;

import java.util.List;

public interface DeletedRecordsSyncService {
    void sync(DatabaseSyncContext context, List<SyncService> syncServices,
              PerformanceStatisticsHolder performanceStatisticsHolder);
}
