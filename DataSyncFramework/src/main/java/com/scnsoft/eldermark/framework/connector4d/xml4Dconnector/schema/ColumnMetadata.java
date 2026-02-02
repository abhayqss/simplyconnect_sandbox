package com.scnsoft.eldermark.framework.connector4d.xml4Dconnector.schema;


import com.scnsoft.eldermark.framework.connector4d.ColumnType4D;

/**
 * Created by averazub on 5/24/2016.
 */
public class ColumnMetadata {
    private int columnIndex;
    private String columnName;
    private ColumnType4D columnType4D;
    private Class columnType;

    public ColumnMetadata() {
    }

    public ColumnMetadata(int columnIndex, String columnName, ColumnType4D columnType4D, Class columnType) {
        this.columnIndex = columnIndex;
        this.columnName = columnName;
        this.columnType4D = columnType4D;
        this.columnType = columnType;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public ColumnType4D getColumnType4D() {
        return columnType4D;
    }

    public void setColumnType4D(ColumnType4D columnType4D) {
        this.columnType4D = columnType4D;
    }

    public Class getColumnType() {
        return columnType;
    }

    public void setColumnType(Class columnType) {
        this.columnType = columnType;
    }
}
