package com.scnsoft.eldermark.entity.document.ccd;

public enum ClientAllergyStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    COMPLETED("Completed"),
    RESOLVED("Resolved"),
    UNKNOWN("Unknown"),;

    private final String displayName;

    ClientAllergyStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
