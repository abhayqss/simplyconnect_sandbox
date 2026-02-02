package com.scnsoft.eldermark.dao.intersector;

public class ViewableEventMultipleEmployeesStatementInspector extends BaseViewableEventStatementInspector {
    public static final String NOT_VIEWABLE_EVENT_TYPE_MULTIPLE_EMPLOYEES_FAKE_FUNCTION = "NOT_VIEWABLE_EVENT_TYPE_MULTIPLE_EMPLOYEES_FAKE";
    private static final String NOT_VIEWABLE_FUNCTION_MULTIPLE_EMPLOYEES = "dbo.EventNotViewableMultipleEmployees";

    public ViewableEventMultipleEmployeesStatementInspector() {
        super(NOT_VIEWABLE_FUNCTION_MULTIPLE_EMPLOYEES, NOT_VIEWABLE_EVENT_TYPE_MULTIPLE_EMPLOYEES_FAKE_FUNCTION);
    }

    @Override
    protected int getFakeFunctionArgsCount() {
        return 4;
    }

    @Override
    protected String getEmployeesCount(String[] args) {
        return args[3];
    }
}
