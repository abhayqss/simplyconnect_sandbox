package com.scnsoft.eldermark.beans.reports.model.intune;

import com.scnsoft.eldermark.beans.reports.model.Report;

import java.util.List;

public class InTuneReport extends Report {

    private List<InTuneReportRow> rows;

    private boolean isSingleClientReport;

    public List<InTuneReportRow> getRows() {
        return rows;
    }

    public void setRows(List<InTuneReportRow> rows) {
        this.rows = rows;
    }

    public boolean isSingleClientReport() {
        return isSingleClientReport;
    }

    public void setSingleClientReport(boolean singleClientReport) {
        isSingleClientReport = singleClientReport;
    }
}
