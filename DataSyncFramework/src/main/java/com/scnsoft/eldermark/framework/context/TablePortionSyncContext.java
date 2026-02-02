package com.scnsoft.eldermark.framework.context;

import com.scnsoft.eldermark.framework.DatabaseInfo;
import com.scnsoft.eldermark.framework.Utils;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import org.springframework.jdbc.core.JdbcOperations;

public final class TablePortionSyncContext<ID extends Comparable<ID>> {
    private final TableSyncContext tableSyncContext;
    private final ID idLowerBoundExclusive;
    private final ID idUpperBoundInclusive;

    public TablePortionSyncContext(TableSyncContext tableSyncContext, ID idLowerBoundExclusive, ID idUpperBoundInclusive) {
        Utils.ensureNotNull(tableSyncContext, "tableSyncContext");

        this.tableSyncContext = tableSyncContext;
        this.idLowerBoundExclusive = idLowerBoundExclusive;
        this.idUpperBoundInclusive = idUpperBoundInclusive;
    }

    public long getDatabaseId() {
        return tableSyncContext.getDatabaseId();
    }

    public String getDatabaseUrl() {
        return tableSyncContext.getDatabaseUrl();
    }

    public DatabaseInfo getDatabase() {
        return tableSyncContext.getDatabase();
    }

    public Sql4DOperations getSqlOperations() {
        return tableSyncContext.getSqlOperations();
    }

    public DatabaseSyncContext getDatabaseSyncContext() {
        return tableSyncContext.getDatabaseSyncContext();
    }

    public String getTableName() {
        return tableSyncContext.getTableName();
    }

    public ID getIdLowerBoundExclusive() {
        return idLowerBoundExclusive;
    }

    public ID getIdUpperBoundInclusive() {
        return idUpperBoundInclusive;
    }
}
