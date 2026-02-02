package com.scnsoft.eldermark.shared.carecoordination.events;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Null;
import java.util.Date;

/**
 * Created by pzhurba on 09-Oct-15.
 */
public class EventFilterDto {
    private Long eventTypeId;
    private Long eventGroupId;
    private Long patientId;
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Null
    private Date dateFrom;
    @Null
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private Date dateTo;
    
    private Boolean irRelatedEvent;


    public EventFilterDto() {}

    public EventFilterDto(Long eventTypeId, Long eventGroupId, Long patientId, Date dateFrom, Date dateTo, Boolean irRelatedEvent) {
        this.eventTypeId = eventTypeId;
        this.eventGroupId = eventGroupId;
        this.patientId = patientId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.irRelatedEvent=irRelatedEvent;
    }

    public Long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Long getEventGroupId() {
        return eventGroupId;
    }

    public void setEventGroupId(Long eventGroupId) {
        this.eventGroupId = eventGroupId;
    }

    public Boolean getIrRelatedEvent() {
        return irRelatedEvent;
    }

    public void setIrRelatedEvent(Boolean irRelatedEvent) {
        this.irRelatedEvent = irRelatedEvent;
    }
}
