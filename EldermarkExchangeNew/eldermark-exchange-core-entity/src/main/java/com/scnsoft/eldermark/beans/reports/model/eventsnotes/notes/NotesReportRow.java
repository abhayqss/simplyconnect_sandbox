package com.scnsoft.eldermark.beans.reports.model.eventsnotes.notes;

import java.util.List;

public class NotesReportRow {
    private String organizationName;
    private List<NotesReportCommunityRow> communityRows;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(final String organizationName) {
        this.organizationName = organizationName;
    }

    public List<NotesReportCommunityRow> getCommunityRows() {
        return communityRows;
    }

    public void setCommunityRows(final List<NotesReportCommunityRow> communityRows) {
        this.communityRows = communityRows;
    }
}
