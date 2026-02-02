package com.scnsoft.exchange.audit.model.filters;

public class MpiReportFilter extends ReportFilterDto {
    public static String ALL_STATES = "all";

    public String getState() {
        if (ALL_STATES.equals(state))
            return null;

        return state;
    }
}
