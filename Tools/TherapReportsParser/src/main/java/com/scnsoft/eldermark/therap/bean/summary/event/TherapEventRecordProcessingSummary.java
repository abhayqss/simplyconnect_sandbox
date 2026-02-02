package com.scnsoft.eldermark.therap.bean.summary.event;

import com.scnsoft.eldermark.therap.bean.summary.TherapEntityRecordProcessingSummary;

public class TherapEventRecordProcessingSummary extends TherapEntityRecordProcessingSummary {

    private String idfFormId;
    private String gerFormId;
    private int eventsCreated;

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

//    @Override
//    protected boolean shouldSetOkStatus() {
//        return eventsCreated != 0;
//    }
//
//    @Override
//    protected String buildWarnMessage() {
//        return "Event record is valid, but system didn't create any entries";
//    }
}
