package com.scnsoft.eldermark.beans.reports.model.ecmtracking;

import com.scnsoft.eldermark.beans.reports.model.Report;

import java.util.List;

public class EcmTrackingReport extends Report {
    private List<EcmTrackingReportRow> rows;

    public List<EcmTrackingReportRow> getRows() {
        return rows;
    }

    public void setRows(List<EcmTrackingReportRow> rows) {
        this.rows = rows;
    }
}