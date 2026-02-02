package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlanStatus;

public class ServicePlanFilter {

    private String searchText;

    private Long clientId;

    private ServicePlanStatus status;

    private Boolean resourceNamePopulated;

    private Boolean ongoingService;

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public ServicePlanStatus getStatus() {
        return status;
    }

    public void setStatus(ServicePlanStatus status) {
        this.status = status;
    }

    public Boolean getResourceNamePopulated() {
        return resourceNamePopulated;
    }

    public void setResourceNamePopulated(Boolean resourceNamePopulated) {
        this.resourceNamePopulated = resourceNamePopulated;
    }

    public Boolean getOngoingService() {
        return ongoingService;
    }

    public void setOngoingService(Boolean ongoingService) {
        this.ongoingService = ongoingService;
    }
}
