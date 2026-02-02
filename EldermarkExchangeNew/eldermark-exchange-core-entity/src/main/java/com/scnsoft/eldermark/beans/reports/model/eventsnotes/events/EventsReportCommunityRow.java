package com.scnsoft.eldermark.beans.reports.model.eventsnotes.events;

import java.util.List;

public class EventsReportCommunityRow {
    private String communityName;
    private List<EventsReportClientRow> clientRows;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(final String communityName) {
        this.communityName = communityName;
    }

    public List<EventsReportClientRow> getClientRows() {
        return clientRows;
    }

    public void setClientRows(final List<EventsReportClientRow> clientRows) {
        this.clientRows = clientRows;
    }
}
