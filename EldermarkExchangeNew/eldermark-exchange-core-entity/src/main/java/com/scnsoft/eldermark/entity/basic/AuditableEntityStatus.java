package com.scnsoft.eldermark.entity.basic;

public enum AuditableEntityStatus {
    CREATED("Created"),
    UPDATED("Updated"),
    DELETED("Deleted");

    private String displayName;

    AuditableEntityStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
