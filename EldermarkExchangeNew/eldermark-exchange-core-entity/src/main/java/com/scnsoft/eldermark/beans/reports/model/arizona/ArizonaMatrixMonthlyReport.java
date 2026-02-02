package com.scnsoft.eldermark.beans.reports.model.arizona;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.Report;

import java.util.List;

public class ArizonaMatrixMonthlyReport extends Report {

    private List<ArizonaMatrixMonthlyReportRow> missing;
    private List<ArizonaMatrixMonthlyReportRow> upcoming;

    @Override
    public ReportType getReportType() {
        return ReportType.ARIZONA_MATRIX_MONTHLY;
    }

    public List<ArizonaMatrixMonthlyReportRow> getMissing() {
        return missing;
    }

    public void setMissing(List<ArizonaMatrixMonthlyReportRow> missing) {
        this.missing = missing;
    }

    public List<ArizonaMatrixMonthlyReportRow> getUpcoming() {
        return upcoming;
    }

    public void setUpcoming(List<ArizonaMatrixMonthlyReportRow> upcoming) {
        this.upcoming = upcoming;
    }
}
