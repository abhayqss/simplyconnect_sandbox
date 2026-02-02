package com.scnsoft.eldermark.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.event.EventType_;
import com.scnsoft.eldermark.entity.event.Event_;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport_;
import org.springframework.data.domain.Sort;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidentReportListItemDto {

    private Long id;
    private Long clientId;
    private Long eventId;
    @EntitySort.List(
            {
                    @EntitySort(IncidentReport_.FIRST_NAME),
                    @EntitySort(IncidentReport_.LAST_NAME)
            }
    )
    private String clientName;
    @EntitySort(joined = {IncidentReport_.EVENT, Event_.EVENT_TYPE, EventType_.DESCRIPTION})
    private String eventType;
    @EntitySort(IncidentReport_.STATUS)
    private String statusName;
    private String statusTitle;
    @DefaultSort(direction = Sort.Direction.DESC)
    @EntitySort(IncidentReport_.INCIDENT_DATETIME)
    private Long incidentDate;
    private Long clientAvatarId;
    private boolean canDelete;
    private Boolean clientActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public Long getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(Long incidentDate) {
        this.incidentDate = incidentDate;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public Long getClientAvatarId() {
        return clientAvatarId;
    }

    public void setClientAvatarId(Long clientAvatarId) {
        this.clientAvatarId = clientAvatarId;
    }

    public boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public Boolean getClientActive() {
        return clientActive;
    }

    public void setClientActive(Boolean clientActive) {
        this.clientActive = clientActive;
    }
}
