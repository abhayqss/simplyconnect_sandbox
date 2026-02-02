package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;

public class ClientIntakesReport extends Report {

    private List<ClientIntakesReportRow> intakeWithinDatesRows;

   private List<ClientIntakesReportRow> allRows;

    public List<ClientIntakesReportRow> getIntakeWithinDatesRows() {
        return intakeWithinDatesRows;
    }

    public void setIntakeWithinDatesRows(List<ClientIntakesReportRow> intakeWithinDatesRows) {
        this.intakeWithinDatesRows = intakeWithinDatesRows;
    }

    public List<ClientIntakesReportRow> getAllRows() {
        return allRows;
    }

    public void setAllRows(List<ClientIntakesReportRow> allRows) {
        this.allRows = allRows;
    }
}
