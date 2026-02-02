package com.scnsoft.eldermark.beans.reports.model.eventsnotes.events;

import java.util.List;

public class EventsReportClientRow {
    private String clientName;
    private Long clientId;
    private List<EventsReportSingleEventRow> singleEventRows;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(final String clientName) {
        this.clientName = clientName;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(final Long clientId) {
        this.clientId = clientId;
    }

    public List<EventsReportSingleEventRow> getSingleEventRows() {
        return singleEventRows;
    }

    public void setSingleEventRows(final List<EventsReportSingleEventRow> singleEventRows) {
        this.singleEventRows = singleEventRows;
    }
}
