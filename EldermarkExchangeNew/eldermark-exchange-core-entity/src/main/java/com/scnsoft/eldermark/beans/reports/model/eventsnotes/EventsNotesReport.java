package com.scnsoft.eldermark.beans.reports.model.eventsnotes;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.reports.model.Report;
import com.scnsoft.eldermark.beans.reports.model.eventsnotes.events.EventsReportRow;
import com.scnsoft.eldermark.beans.reports.model.eventsnotes.notes.NotesReportRow;

import java.util.List;

public class EventsNotesReport extends Report {

    private List<EventsReportRow> eventRows;
    private List<NotesReportRow> notesRows;

    @Override
    public ReportType getReportType() {
        return ReportType.EVENTS_NOTES;
    }

    public List<EventsReportRow> getEventRows() {
        return eventRows;
    }

    public void setEventRows(final List<EventsReportRow> eventRows) {
        this.eventRows = eventRows;
    }

    public List<NotesReportRow> getNotesRows() {
        return notesRows;
    }

    public void setNotesRows(final List<NotesReportRow> notesRows) {
        this.notesRows = notesRows;
    }
}
