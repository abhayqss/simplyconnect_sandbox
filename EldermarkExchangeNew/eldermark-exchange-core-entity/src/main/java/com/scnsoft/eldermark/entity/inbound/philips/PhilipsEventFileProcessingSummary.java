package com.scnsoft.eldermark.entity.inbound.philips;


import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;

import java.util.ArrayList;
import java.util.List;

public class PhilipsEventFileProcessingSummary extends ProcessingSummary {
    private String fileName;
    private int totalEvents;
    private int processedEvents;
    private List<PhilipsEventRecordProcessingSummary> eventRecordProcessingSummaries = new ArrayList<>();

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(int totalEvents) {
        this.totalEvents = totalEvents;
    }

    public int getProcessedEvents() {
        return processedEvents;
    }

    public void setProcessedEvents(int processedEvents) {
        this.processedEvents = processedEvents;
    }

    public List<PhilipsEventRecordProcessingSummary> getEventRecordProcessingSummaries() {
        return eventRecordProcessingSummaries;
    }

    public void setEventRecordProcessingSummaries(List<PhilipsEventRecordProcessingSummary> eventRecordProcessingSummaries) {
        this.eventRecordProcessingSummaries = eventRecordProcessingSummaries;
    }

    @Override
    protected boolean shouldSetOkStatus() {
        return eventRecordProcessingSummaries.stream().allMatch(hasOkStatus);
    }

    @Override
    protected String buildWarnMessage() {
        return "Some events from file could not be processed";
    }
}
