package com.scnsoft.eldermark.beans.reports.model.sdoh;

import java.util.List;

public class SDoHReport {

    private String submitterName;
    private List<SDoHRow> rows;

    public List<SDoHRow> getRows() {
        return rows;
    }

    public void setRows(List<SDoHRow> rows) {
        this.rows = rows;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public void setSubmitterName(String submitterName) {
        this.submitterName = submitterName;
    }
}
