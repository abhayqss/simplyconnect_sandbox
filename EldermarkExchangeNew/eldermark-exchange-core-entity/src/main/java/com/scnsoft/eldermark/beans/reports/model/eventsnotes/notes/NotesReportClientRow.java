package com.scnsoft.eldermark.beans.reports.model.eventsnotes.notes;

import java.util.List;

public class NotesReportClientRow {
    private String clientName;
    private Long clientId;
    private List<NotesReportSingleNoteRow> singleNoteRows;

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

    public List<NotesReportSingleNoteRow> getSingleNoteRows() {
        return singleNoteRows;
    }

    public void setSingleNoteRows(final List<NotesReportSingleNoteRow> singleNoteRows) {
        this.singleNoteRows = singleNoteRows;
    }
}
