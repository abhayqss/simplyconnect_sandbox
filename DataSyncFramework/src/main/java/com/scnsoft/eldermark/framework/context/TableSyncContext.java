package com.scnsoft.eldermark.framework.context;

import com.scnsoft.eldermark.framework.DatabaseInfo;
import com.scnsoft.eldermark.framework.Utils;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import org.springframework.jdbc.core.JdbcOperations;

public final class TableSyncContext {
    private final DatabaseSyncContext databaseSyncContext;
    private final String tableName;

    public TableSyncContext(DatabaseSyncContext databaseSyncContext, String tableName) {
        Utils.ensureNotNull(databaseSyncContext, "databaseSyncContext");
        Utils.ensureNotNull(tableName, "tableName");

        this.databaseSyncContext = databaseSyncContext;
        this.tableName = tableName;
    }

    public DatabaseSyncContext getDatabaseSyncContext() {
        return databaseSyncContext;
    }

    public long getDatabaseId() {
        return databaseSyncContext.getDatabaseId();
    }

    public String getDatabaseUrl() {
        return databaseSyncContext.getDatabaseUrl();
    }

    public DatabaseInfo getDatabase() {
        return databaseSyncContext.getDatabase();
    }

    public Sql4DOperations getSqlOperations() {
        return databaseSyncContext.getSql4dOperations();
    }

    public String getTableName() {
        return tableName;
    }
}
