package com.scnsoft.eldermark.entity.lab;

public enum LabResearchOrderStatus {
    SENT_TO_LAB("Sent to lab"),
    PENDING_REVIEW("Pending review"),
    REVIEWED("Reviewed");

    private final String displayName;

    LabResearchOrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
