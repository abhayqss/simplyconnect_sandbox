package com.scnsoft.eldermark;

import java.util.List;
import java.util.Map;

public interface TablesInfoDao {
    void useDatabase(String databaseName);

    List<String> getTables();

    int getTableSize(String tableName);

    List<String> getPrimaryKeyColumns(String tableName);

    long getPrimaryKeySum(String tableName);

    List<ForeignKey> getForeignKeys(String tableName);

    List<ColumnInfo> getColumnsInfo(String tableName);

    void enableIdentityInsertion(String tableName);

    void disableIdentityInsertion(String tableName);

    void copyTable(TableInfo tableInfo, Map<String, TableInfo> tableInfoMap,
                   String sourceDatabase, String targetDatabase, int copyNumber);
}
