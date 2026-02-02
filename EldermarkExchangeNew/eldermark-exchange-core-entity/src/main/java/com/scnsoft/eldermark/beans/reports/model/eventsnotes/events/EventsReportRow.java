package com.scnsoft.eldermark.beans.reports.model.eventsnotes.events;

import java.util.List;

public class EventsReportRow {
    private String organizationName;
    private List<EventsReportCommunityRow> communityRows;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(final String organizationName) {
        this.organizationName = organizationName;
    }

    public List<EventsReportCommunityRow> getCommunityRows() {
        return communityRows;
    }

    public void setCommunityRows(final List<EventsReportCommunityRow> communityRows) {
        this.communityRows = communityRows;
    }
}
