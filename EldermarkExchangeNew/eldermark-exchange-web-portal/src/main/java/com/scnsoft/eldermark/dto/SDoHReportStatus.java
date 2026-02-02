package com.scnsoft.eldermark.dto;

public enum SDoHReportStatus {
    PENDING_REVIEW("Pending Review"),
    SENT_TO_UHC("Sent to UHC");

    private final String displayName;

    SDoHReportStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
