package com.scnsoft.eldermark.entity;

public enum AssessmentStatus {
    IN_PROCESS("In process"),
    INACTIVE("Inactive"),
    COMPLETED("Completed");

    private String displayName;

    AssessmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
