package com.scnsoft.eldermark.beans.reports.model;

import java.time.Instant;
import java.util.List;

public class ServicePlanRow {

    private String communityName;

    private Long clientId;

    private String clientName;

    private String serviceCoordinator;

    private Instant dateCompleted;

    private String servicePlanStatus;

    private long totalNumberOfDomains;

    private long totalNumberOfGoals;

    private long totalNumberOfResources;

    private List<DomainRow> domainRows;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getServiceCoordinator() {
        return serviceCoordinator;
    }

    public void setServiceCoordinator(String serviceCoordinator) {
        this.serviceCoordinator = serviceCoordinator;
    }

    public Instant getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Instant dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public String getServicePlanStatus() {
        return servicePlanStatus;
    }

    public void setServicePlanStatus(String servicePlanStatus) {
        this.servicePlanStatus = servicePlanStatus;
    }

    public long getTotalNumberOfDomains() {
        return totalNumberOfDomains;
    }

    public void setTotalNumberOfDomains(long totalNumberOfDomains) {
        this.totalNumberOfDomains = totalNumberOfDomains;
    }

    public long getTotalNumberOfGoals() {
        return totalNumberOfGoals;
    }

    public void setTotalNumberOfGoals(long totalNumberOfGoals) {
        this.totalNumberOfGoals = totalNumberOfGoals;
    }

    public long getTotalNumberOfResources() {
        return totalNumberOfResources;
    }

    public void setTotalNumberOfResources(long totalNumberOfResources) {
        this.totalNumberOfResources = totalNumberOfResources;
    }

    public List<DomainRow> getDomainRows() {
        return domainRows;
    }

    public void setDomainRows(List<DomainRow> domainRows) {
        this.domainRows = domainRows;
    }
}
