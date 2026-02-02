package com.scnsoft.eldermark;

public final class ColumnInfo {
    private final String columnName;
    private final String columnType;

    public ColumnInfo(String columnName, String columnType) {
        this.columnName = columnName;
        this.columnType = columnType;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnType() {
        return columnType;
    }
}
