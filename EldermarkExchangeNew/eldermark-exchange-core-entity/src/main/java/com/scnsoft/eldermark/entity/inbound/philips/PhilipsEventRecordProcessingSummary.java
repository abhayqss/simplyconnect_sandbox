package com.scnsoft.eldermark.entity.inbound.philips;

import com.fasterxml.jackson.annotation.JsonView;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;

public class PhilipsEventRecordProcessingSummary extends ProcessingSummary {

    @JsonView(ProcessingSummary.LocalView.class)
    private Long eventId;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @Override
    protected boolean shouldSetOkStatus() {
        return eventId != null;
    }

    @Override
    protected String buildWarnMessage() {
        return "Event record is valid, but system didn't create any entries";
    }
}
