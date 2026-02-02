package com.scnsoft.eldermark.dump.model;

import java.util.List;

public class ERVisitsDump extends Dump {

    private List<ERVisitsRow> eventRows;

    public List<ERVisitsRow> getEventRows() {
        return eventRows;
    }

    public void setEventRows(List<ERVisitsRow> eventRows) {
        this.eventRows = eventRows;
    }

    @Override
    public DumpType getDumpType() {
        return DumpType.ER_VISITS;
    }
}
