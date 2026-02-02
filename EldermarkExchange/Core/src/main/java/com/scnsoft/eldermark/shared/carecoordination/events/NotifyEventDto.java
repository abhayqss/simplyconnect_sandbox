package com.scnsoft.eldermark.shared.carecoordination.events;

import java.util.Date;

public class NotifyEventDto {

    private Long submitterId;

    private Long patientId;

    private Date eventDateTime;

    public NotifyEventDto() {}

    public NotifyEventDto(Long submitterId, Long patientId, Date eventDateTime) {
        this.submitterId = submitterId;
        this.patientId = patientId;
        this.eventDateTime = eventDateTime;
    }

    public Long getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(Long submitterId) {
        this.submitterId = submitterId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Date getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(Date eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    @Override
    public String toString() {
        return "NotifyEventDto{" +
                "submitterId=" + submitterId +
                ", patientId=" + patientId +
                ", eventDateTime=" + eventDateTime +
                '}';
    }
}
