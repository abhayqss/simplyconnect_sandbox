package com.scnsoft.eldermark.beans.reports.model.arizona;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.Report;

import java.util.List;

public class ArizonaMatrixReport extends Report {

    private List<ArizonaMatrixReportRow> rows;

    @Override
    public ReportType getReportType() {
        return ReportType.ARIZONA_MATRIX;
    }

    public List<ArizonaMatrixReportRow> getRows() {
        return rows;
    }

    public void setRows(List<ArizonaMatrixReportRow> rows) {
        this.rows = rows;
    }
}
