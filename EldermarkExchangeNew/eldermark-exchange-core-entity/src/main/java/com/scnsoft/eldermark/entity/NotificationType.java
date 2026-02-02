package com.scnsoft.eldermark.entity;

public enum NotificationType {
    SMS("Sms"),
    EMAIL("E-mail"),
    SECURITY_MESSAGE("Secure Message"),
    BLUE_STONE("Bluestone bridge"),
    FAX("Fax"),
    PUSH_NOTIFICATION("Push notification");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name();
    }
}
