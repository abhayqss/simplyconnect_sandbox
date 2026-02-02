package com.scnsoft.eldermark.entity.client.appointment;

public enum ClientAppointmentStatus {

    PLANNED("Planned"),
    RESCHEDULED("Rescheduled"),
    TRIAGED("Triaged"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    ENTERED_IN_ERROR("Entered in Error");

    private String displayName;

    ClientAppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
