package com.scnsoft.eldermark.entity.inbound.therap.summary.event;

import com.fasterxml.jackson.annotation.JsonView;
import com.scnsoft.eldermark.entity.inbound.summary.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapEntityRecordProcessingSummary;

import java.util.ArrayList;
import java.util.List;

public class TherapEventRecordProcessingSummary extends TherapEntityRecordProcessingSummary {

    private String idfFormId;
    private String gerFormId;
    private int eventsCreated;

    @JsonView(ProcessingSummary.LocalView.class)
    private List<Long> eventsCreatedIds = new ArrayList<>();

    public String getIdfFormId() {
        return idfFormId;
    }

    public void setIdfFormId(String idfFormId) {
        this.idfFormId = idfFormId;
    }

    public String getGerFormId() {
        return gerFormId;
    }

    public void setGerFormId(String gerFormId) {
        this.gerFormId = gerFormId;
    }

    public int getEventsCreated() {
        return eventsCreated;
    }

    public void setEventsCreated(int eventsCreated) {
        this.eventsCreated = eventsCreated;
    }

    public List<Long> getEventsCreatedIds() {
        return eventsCreatedIds;
    }

    @Override
    protected boolean shouldSetOkStatus() {
        return eventsCreated != 0;
    }

    @Override
    protected String buildWarnMessage() {
        return "Event record is valid, but system didn't create any entries";
    }
}
