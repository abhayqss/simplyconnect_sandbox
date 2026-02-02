package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;

public class ClientServicePlanRow {

    private String communityName;

    private Long clientId;

    private String clientName;

    private long totalNumberOfServices;

    private String coordinatorName;

    private List<ClientDomainRow> domainRows;

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

    public long getTotalNumberOfServices() {
        return totalNumberOfServices;
    }

    public void setTotalNumberOfServices(long totalNumberOfServices) {
        this.totalNumberOfServices = totalNumberOfServices;
    }

    public String getCoordinatorName() {
        return coordinatorName;
    }

    public void setCoordinatorName(String coordinatorName) {
        this.coordinatorName = coordinatorName;
    }

    public List<ClientDomainRow> getDomainRows() {
        return domainRows;
    }

    public void setDomainRows(List<ClientDomainRow> domainRows) {
        this.domainRows = domainRows;
    }
}
