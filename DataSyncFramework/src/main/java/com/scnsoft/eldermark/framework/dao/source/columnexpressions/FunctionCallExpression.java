package com.scnsoft.eldermark.framework.dao.source.columnexpressions;

public class FunctionCallExpression implements SelectExpression {
    private final String functionCall;

    public FunctionCallExpression(String functionCall) {
        this.functionCall = functionCall;
    }

    @Override
    public String getValue() {
        return functionCall;
    }

    @Override
    public boolean isEscapingNeeded() {
        return false;
    }
}
