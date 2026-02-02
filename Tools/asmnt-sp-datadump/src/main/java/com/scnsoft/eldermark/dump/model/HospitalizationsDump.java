package com.scnsoft.eldermark.dump.model;

import java.util.List;

public class HospitalizationsDump extends Dump {

    private List<HospitalizationEventRow> eventRows;

    public List<HospitalizationEventRow> getEventRows() {
        return eventRows;
    }

    public void setEventRows(List<HospitalizationEventRow> eventRows) {
        this.eventRows = eventRows;
    }

    @Override
    public DumpType getDumpType() {
        return DumpType.HOSPITALIZATIONS;
    }
}
