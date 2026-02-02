package com.scnsoft.eldermark.dump.entity.serviceplan;

public enum ServicePlanStatus {

    IN_DEVELOPMENT("In development"),
    SHARED_WITH_CLIENT("Shared with client"),
    IN_DEPLOYMENT("In deployment");

    private String displayName;

    ServicePlanStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
