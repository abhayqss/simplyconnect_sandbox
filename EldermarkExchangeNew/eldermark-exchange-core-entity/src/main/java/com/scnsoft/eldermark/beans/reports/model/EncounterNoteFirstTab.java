package com.scnsoft.eldermark.beans.reports.model;

public class EncounterNoteFirstTab {

    private String communityName;

    private String clientNames;

    private String clientIds;

    private Long totalTimeSpent;

    private String serviceCoordinatorName;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getClientNames() {
        return clientNames;
    }

    public void setClientNames(String clientNames) {
        this.clientNames = clientNames;
    }

    public String getClientIds() {
        return clientIds;
    }

    public void setClientIds(String clientIds) {
        this.clientIds = clientIds;
    }

    public Long getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public void setTotalTimeSpent(Long totalTimeSpent) {
        this.totalTimeSpent = totalTimeSpent;
    }

    public String getServiceCoordinatorName() {
        return serviceCoordinatorName;
    }

    public void setServiceCoordinatorName(String serviceCoordinatorName) {
        this.serviceCoordinatorName = serviceCoordinatorName;
    }
}
