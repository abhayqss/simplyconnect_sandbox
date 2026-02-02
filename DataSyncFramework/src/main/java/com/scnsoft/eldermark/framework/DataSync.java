package com.scnsoft.eldermark.framework;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DataSync {
    void setDatabasesWithSyncServices(List<Pair<List<DatabaseInfo>, Set<SyncService>>> databasesWithSyncServices);

    void setStatsListener(DataSyncStatsListener listener);

    void addListener(DataSyncListener listener);
    void removeListener(DataSyncListener listener);

    void setTargetOrganizationsIdMapping(Map<Long, DatabaseIdWithId> organizationsIdMapping);

    void run();
}
