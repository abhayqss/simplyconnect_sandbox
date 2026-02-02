package com.scnsoft.eldermark.framework.exceptions;

public class UnknownColumnException extends RuntimeException {
    private final String columnName;

    public UnknownColumnException(String columnName) {
        super("Unknown column name: " + columnName);
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }
}
