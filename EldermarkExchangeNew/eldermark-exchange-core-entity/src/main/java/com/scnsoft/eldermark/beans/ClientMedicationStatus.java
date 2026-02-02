package com.scnsoft.eldermark.beans;

public enum ClientMedicationStatus {
    ACTIVE("Active"),
    COMPLETED("Inactive"),
    UNKNOWN("Unknown");

    private final String title;

    ClientMedicationStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getCode() {
        return name().toLowerCase();
    }
}
