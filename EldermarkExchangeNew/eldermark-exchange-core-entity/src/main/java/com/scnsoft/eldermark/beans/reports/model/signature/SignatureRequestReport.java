package com.scnsoft.eldermark.beans.reports.model.signature;

import com.scnsoft.eldermark.beans.reports.model.Report;

import java.util.List;

public class SignatureRequestReport extends Report {
    private List<SignatureRequestReportRow> rows;

    public List<SignatureRequestReportRow> getRows() {
        return rows;
    }

    public void setRows(final List<SignatureRequestReportRow> rows) {
        this.rows = rows;
    }
}
