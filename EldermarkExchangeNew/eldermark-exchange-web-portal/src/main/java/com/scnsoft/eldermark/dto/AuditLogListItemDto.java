package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.annotations.sort.EntitySort;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.audit.AuditLogFirstClient_;
import com.scnsoft.eldermark.entity.audit.AuditLog_;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import org.springframework.data.domain.Sort;

import java.util.List;

public class AuditLogListItemDto {

    private Long id;

    private String activityName;

    @EntitySort(AuditLog_.ACTION)
    private String activityTitle;

    @EntitySort(joined = {AuditLog_.FIRST_CLIENT, AuditLogFirstClient_.CLIENT_NAME})
    private List<IdentifiedNamedEntityDto> clients;

    @EntitySort.List(
            {
                    @EntitySort(joined = {AuditLog_.EMPLOYEE, Employee_.FIRST_NAME}),
                    @EntitySort(joined = {AuditLog_.EMPLOYEE, Employee_.LAST_NAME})
            }
    )
    private String employeeName;

    @DefaultSort(direction = Sort.Direction.DESC)
    private Long date;

    private List<Long> relatedIds;
    private List<String> relatedAdditionalFields;

    private Long eventId;

    private List<String> notes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public List<IdentifiedNamedEntityDto> getClients() {
        return clients;
    }

    public void setClients(List<IdentifiedNamedEntityDto> clients) {
        this.clients = clients;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public List<Long> getRelatedIds() {
        return relatedIds;
    }

    public void setRelatedIds(List<Long> relatedIds) {
        this.relatedIds = relatedIds;
    }

    public List<String> getRelatedAdditionalFields() {
        return relatedAdditionalFields;
    }

    public void setRelatedAdditionalFields(List<String> relatedAdditionalFields) {
        this.relatedAdditionalFields = relatedAdditionalFields;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }
}
