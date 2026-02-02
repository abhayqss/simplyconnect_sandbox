package com.scnsoft.eldermark.api.shared.dto.adt;

import java.util.Date;

public class EVNEventTypeSegmentDto implements SegmentDto {
    private String eventTypeCode;
    private Date recordedDateTime;
    private String eventReasonCode;
    private Date eventOccured;

    public String getEventTypeCode() {
        return eventTypeCode;
    }

    public void setEventTypeCode(String eventTypeCode) {
        this.eventTypeCode = eventTypeCode;
    }

    public Date getRecordedDateTime() {
        return recordedDateTime;
    }

    public void setRecordedDateTime(Date recordedDateTime) {
        this.recordedDateTime = recordedDateTime;
    }

    public String getEventReasonCode() {
        return eventReasonCode;
    }

    public void setEventReasonCode(String eventReasonCode) {
        this.eventReasonCode = eventReasonCode;
    }

    public Date getEventOccured() {
        return eventOccured;
    }

    public void setEventOccured(Date eventOccured) {
        this.eventOccured = eventOccured;
    }
}
