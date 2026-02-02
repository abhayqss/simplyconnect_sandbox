package com.scnsoft.eldermark.entity.assessment;

public enum AssessmentStatus {
    IN_PROCESS("In process"),
    INACTIVE("Inactive"),
    COMPLETED("Completed"),
    HIDDEN("Hidden");

    private String displayName;

    AssessmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
