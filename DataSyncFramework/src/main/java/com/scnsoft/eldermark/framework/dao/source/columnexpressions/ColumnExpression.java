package com.scnsoft.eldermark.framework.dao.source.columnexpressions;

public class ColumnExpression implements SelectExpression {
    private final String columnName;

    public ColumnExpression(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String getValue() {
        return columnName;
    }

    @Override
    public boolean isEscapingNeeded() {
        return true;
    }
}
