package com.scnsoft.eldermark.beans.audit;

import java.util.List;

public class AuditLogFilter {

    private Long organizationId;
    private List<Long> communityIds;
    private List<Long> employeeIds;
    private List<AuditLogActionWithParams> actions;
    private List<Long> clientIds;
    private Long fromDate;
    private Long toDate;
    private Boolean includeInactiveCommunities;
    private Boolean includeInactiveEmployees;
    private Boolean includeInactiveClients;

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

    public List<AuditLogActionWithParams> getActions() {
        return actions;
    }

    public void setActions(List<AuditLogActionWithParams> actions) {
        this.actions = actions;
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
}
