package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;

public class Covid19Report extends Report {

    private List<Covid19ReportRow> rows;

    public List<Covid19ReportRow> getRows() {
        return rows;
    }

    public void setRows(List<Covid19ReportRow> rows) {
        this.rows = rows;
    }
}
