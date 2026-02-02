package com.scnsoft.eldermark.entity.event;

public enum AppointmentNotificationType {
    STAFF_UPCOMING_APPOINTMENT(true),
    CLIENT_UPCOMING_APPOINTMENT(false),
    CLIENT_UPDATED_EVENT(false),
    CLIENT_CANCELED_EVENT(false),
    CLIENT_REMINDER(false),
    CLIENT_COMPLETED_EVENT(false);

    private final boolean isStaffNotification;

    AppointmentNotificationType(boolean isStaffNotification) {
        this.isStaffNotification = isStaffNotification;
    }

    public boolean isStaffNotification() {
        return isStaffNotification;
    }

    public boolean isClientNotification() {
        return !isStaffNotification;
    }
}
