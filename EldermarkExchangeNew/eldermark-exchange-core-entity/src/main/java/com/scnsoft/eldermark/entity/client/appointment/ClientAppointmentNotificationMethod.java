package com.scnsoft.eldermark.entity.client.appointment;

public enum ClientAppointmentNotificationMethod {
    EMAIL("Email"),
    SMS("SMS"),
    PHONE("Phone");

    private String displayName;

    ClientAppointmentNotificationMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
