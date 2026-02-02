package com.scnsoft.eldermark.entity;

public enum IncidentReportStatus {

    DRAFT("Draft"),
    SUBMITTED("Submitted"),
    DELETED("Deleted");

    private final String displayName;

    IncidentReportStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
