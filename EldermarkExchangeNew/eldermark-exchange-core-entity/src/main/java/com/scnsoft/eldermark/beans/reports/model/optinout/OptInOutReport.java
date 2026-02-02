package com.scnsoft.eldermark.beans.reports.model.optinout;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.Report;

import java.util.List;

public class OptInOutReport extends Report {
    private List<OptInOutReportRow> rows;

    public List<OptInOutReportRow> getRows() {
        return rows;
    }

    public void setRows(List<OptInOutReportRow> rows) {
        this.rows = rows;
    }

    @Override
    public ReportType getReportType() {
        return ReportType.OPT_IN_OUT;
    }
}
