package com.scnsoft.eldermark.dao.intersector;

public class ViewableEventStatementInspector extends BaseViewableEventStatementInspector {

    public static final String NOT_VIEWABLE_EVENT_TYPE_FAKE_FUNCTION = "NOT_VIEWABLE_EVENT_TYPE_FAKE";
    private static final String NOT_VIEWABLE_FUNCTION = "dbo.EventNotViewable";

    public ViewableEventStatementInspector() {
        super(NOT_VIEWABLE_FUNCTION, NOT_VIEWABLE_EVENT_TYPE_FAKE_FUNCTION);
    }

    @Override
    protected int getFakeFunctionArgsCount() {
        return 3;
    }

    @Override
    protected String getEmployeesCount(String[] args) {
        return "1";
    }
}
