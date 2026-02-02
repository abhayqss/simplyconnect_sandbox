package com.scnsoft.eldermark.entity.client.appointment;

public enum ClientAppointmentReminder {

    NEVER("Never"),
    AT_EVENT("At time of event"),
    MIN_15_BEFORE("15 minutes before"),
    MIN_30_BEFORE("30 minutes before"),
    HOUR_1_BEFORE("1 hour before"),
    HOUR_2_BEFORE("2 hours before"),
    DAY_1_BEFORE("1 day before"),
    WEEK_1_BEFORE("1 week before");

    private String displayName;

    ClientAppointmentReminder(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
