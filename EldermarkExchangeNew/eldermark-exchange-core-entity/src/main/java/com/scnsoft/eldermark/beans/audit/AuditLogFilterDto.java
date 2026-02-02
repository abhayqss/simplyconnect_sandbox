package com.scnsoft.eldermark.beans.audit;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotNull;
import java.time.ZoneId;
import java.util.List;

public class AuditLogFilterDto {

    @NotNull
    private Long organizationId;
    //empty list means all ids
    private List<Long> communityIds;
    //empty list means all ids
    private List<Long> employeeIds;
    //empty list means all ids
    private List<Long> activityIds;
    //empty list means all ids
    private List<Long> clientIds;
    @NotNull
    private Long fromDate;
    @NotNull
    private Long toDate;

    private Boolean includeInactiveCommunities;
    private Boolean includeInactiveEmployees;
    private Boolean includeInactiveClients;

    @JsonIgnore
    private ZoneId zoneId;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(List<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public List<Long> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(List<Long> employeeIds) {
        this.employeeIds = employeeIds;
    }

    public List<Long> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<Long> activityIds) {
        this.activityIds = activityIds;
    }

    public List<Long> getClientIds() {
        return clientIds;
    }

    public void setClientIds(List<Long> clientIds) {
        this.clientIds = clientIds;
    }

    public Long getFromDate() {
        return fromDate;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getToDate() {
        return toDate;
    }

    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }

    public Boolean getIncludeInactiveCommunities() {
        return includeInactiveCommunities;
    }

    public void setIncludeInactiveCommunities(Boolean includeInactiveCommunities) {
        this.includeInactiveCommunities = includeInactiveCommunities;
    }

    public Boolean getIncludeInactiveEmployees() {
        return includeInactiveEmployees;
    }

    public void setIncludeInactiveEmployees(Boolean includeInactiveEmployees) {
        this.includeInactiveEmployees = includeInactiveEmployees;
    }

    public Boolean getIncludeInactiveClients() {
        return includeInactiveClients;
    }

    public void setIncludeInactiveClients(Boolean includeInactiveClients) {
        this.includeInactiveClients = includeInactiveClients;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }
}
