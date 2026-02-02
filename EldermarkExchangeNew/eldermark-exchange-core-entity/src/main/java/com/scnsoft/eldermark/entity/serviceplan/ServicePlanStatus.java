package com.scnsoft.eldermark.entity.serviceplan;

public enum ServicePlanStatus {

    IN_DEVELOPMENT("In development"),
    SHARED_WITH_CLIENT("Shared with client");

    private String displayName;

    ServicePlanStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
