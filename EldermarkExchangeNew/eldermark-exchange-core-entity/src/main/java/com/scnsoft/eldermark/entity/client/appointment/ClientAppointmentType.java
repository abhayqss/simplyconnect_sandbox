package com.scnsoft.eldermark.entity.client.appointment;

public enum ClientAppointmentType {

    CHECK_UP("Check-up"),
    EMERGENCY("Emergency appointment"),
    FOLLOW_UP("A follow up visit"),
    POST_OP("Post op"),
    ROUTINE("Routine appointment"),
    OTHER("Other");

    private String displayName;

    ClientAppointmentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
