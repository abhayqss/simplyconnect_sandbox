package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;

public class HospitalizationsReport extends Report {

    private List<HospitalizationEventRow> eventRows;

    public List<HospitalizationEventRow> getEventRows() {
        return eventRows;
    }

    public void setEventRows(List<HospitalizationEventRow> eventRows) {
        this.eventRows = eventRows;
    }
}
