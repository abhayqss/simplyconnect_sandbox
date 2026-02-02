package com.scnsoft.eldermark;

import java.util.*;

public final class TableInfo {
    private final String tableName;
    private final int tableSize;
    private final List<ColumnInfo> columnsInfo;
    private final Map<String, String> foreignKeysMap;

    public TableInfo(String tableName, int tableSize, List<ColumnInfo> columnsInfo, List<ForeignKey> foreignKeys) {
        this.tableName = tableName;
        this.tableSize = tableSize;
        this.columnsInfo = columnsInfo;

        foreignKeysMap = new HashMap<String, String>();
        for (ForeignKey foreignKey: foreignKeys) {
            foreignKeysMap.put(foreignKey.getReferencingColumnName(), foreignKey.getReferencedTableName());
        }
    }

    public String getTableName() {
        return tableName;
    }

    public int getTableSize() {
        return tableSize;
    }

    public List<String> getDependencyTables() {
        Collection<String> dependencyTables = foreignKeysMap.values();

        Set<String> dependencyTablesWithoutDuplicates = new HashSet<String>(dependencyTables);
        return new ArrayList<String>(dependencyTablesWithoutDuplicates);
    }

    public List<ColumnInfo> getColumnsInfo() {
        return columnsInfo;
    }

    public boolean isForeignKey(String columnName) {
        return foreignKeysMap.containsKey(columnName);
    }

    public String getReferencedTable(String foreignKeyColumnName) {
        String referencedTable = foreignKeysMap.get(foreignKeyColumnName);
        if (referencedTable == null) {
            throw new IllegalArgumentException("'" + foreignKeyColumnName + "' is not a foreign key");
        }
        return referencedTable;
    }

    public boolean hasPrimaryKey() {
        boolean hasPrimaryKey = false;
        for (ColumnInfo columnInfo: columnsInfo) {
            if (Constants.EXPECTED_PRIMARY_KEY_NAME.equalsIgnoreCase(columnInfo.getColumnName())) {
                hasPrimaryKey = true;
                break;
            }
        }
        return hasPrimaryKey;
    }

    public boolean isPrimaryKey(String columnName) {
        return Constants.EXPECTED_PRIMARY_KEY_NAME.equalsIgnoreCase(columnName);
    }
}
