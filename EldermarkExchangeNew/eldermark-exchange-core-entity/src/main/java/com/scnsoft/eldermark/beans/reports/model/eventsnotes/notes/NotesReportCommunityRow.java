package com.scnsoft.eldermark.beans.reports.model.eventsnotes.notes;

import java.util.List;

public class NotesReportCommunityRow {
    private String communityName;
    private List<NotesReportClientRow> clientRows;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(final String communityName) {
        this.communityName = communityName;
    }

    public List<NotesReportClientRow> getClientRows() {
        return clientRows;
    }

    public void setClientRows(final List<NotesReportClientRow> clientRows) {
        this.clientRows = clientRows;
    }
}
