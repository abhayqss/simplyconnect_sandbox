package com.scnsoft.eldermark.entity;

public enum LabResearchOrderStatus {
    SENT_TO_LAB("Sent to lab"),
    PENDING_REVIEW("Pending review"),
    REVIEWED("Reviewed");

    private final String value;

    LabResearchOrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
