package com.scnsoft.eldermark.dao.carecoordination;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Notification Type (aka Notification Channel)
 * Created by pzhurba on 28-Sep-15.
 */
public enum NotificationType {
    ALL("All"), SMS("Sms"), EMAIL("E-mail"), SECURITY_MESSAGE("Secure Message"), BLUE_STONE("Bluestone bridge"), FAX("Fax"), PUSH_NOTIFICATION("Push notification");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static List<NotificationType> getAll() {
        List<NotificationType> list = new ArrayList();
        list.add(SMS);
        list.add(EMAIL);
        list.add(SECURITY_MESSAGE);
        list.add(BLUE_STONE);
        list.add(FAX);
        list.add(PUSH_NOTIFICATION);
        return list;
    }

    @Override
    @JsonValue
    public String toString() {
        return name();
    }

    @JsonCreator
    public static NotificationType fromValue(String name) {
        for (NotificationType b : NotificationType.values()) {
            if (b.name().equals(name)) {
                return b;
            }
        }
        throw new RuntimeException("Unknown Notification type (" + name + ")");
    }

}
