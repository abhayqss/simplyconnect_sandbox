package com.scnsoft.eldermark.exchange;

import com.scnsoft.eldermark.framework.dao.target.DeletedRecordsLogTableConfiguration;
import com.scnsoft.eldermark.framework.dao.target.TargetDaoConfiguration;

public class Constants {
    public static final String SYNC_STATUS_COLUMN = "lastmod_stamp";

    public static final String OLD_SYNC_STATUS_COLUMN = "exchange_sync_status";

    public static final String SYNC_QUALIFIER = "sync_qualifier";

    public static final int DEFAULT_INSERT_BATCH_SIZE = 100;

    public static final int MSSQL_INSERT_PARAM_LIMIT = 2100;

    public static final TargetDaoConfiguration TARGET_DAO_CONFIGURATION = new TargetDaoConfiguration.Builder()
            .setIdColumnName("id").setLegacyIdColumnName("legacy_id")
            .setLegacyTableColumnName("legacy_table").setDatabaseIdColumnName("database_id")
            .setSyncQualifierColumnName(SYNC_QUALIFIER).setDeletedRecordsLogTableConfiguration(new DeletedRecordsLogTableConfiguration.Builder().
                    setTableName("DataSyncDeletedDataLog").setDeletedDateColumnName("deleted_date").setSourceTableColumnName("source_table_name").
                    setTargetTableColumnName("target_table_name").setDatabaseIdColumnName("database_id").setLegacyIdColumnName("legacy_id").
                    setSourceObjectColumnName("deleted_record").build()).build();

    public static final String CARRIAGE_RETURN_SEPARATOR = "\\r+";
    public static final String COMMA_SEPARATOR = "[,\\s]+";
    public static final String TAB_SEPARATOR = "\\t+";

    public static final String DATE_FORMAT = "MM/dd/yyyy";

}
