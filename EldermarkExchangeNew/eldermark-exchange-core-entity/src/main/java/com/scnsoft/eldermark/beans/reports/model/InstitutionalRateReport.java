package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;

public class InstitutionalRateReport extends Report {

    private List<InstitutionalRateReportRow> rows;

    public List<InstitutionalRateReportRow> getRows() {
        return rows;
    }

    public void setRows(List<InstitutionalRateReportRow> rows) {
        this.rows = rows;
    }
}
