package com.scnsoft.eldermark.beans.reports.model.cstracking;

import com.scnsoft.eldermark.beans.reports.model.Report;

import java.util.List;

public class CsTrackingReport extends Report {
    private List<CsTrackingReportRow> rows;

    public List<CsTrackingReportRow> getRows() {
        return rows;
    }

    public void setRows(List<CsTrackingReportRow> rows) {
        this.rows = rows;
    }
}