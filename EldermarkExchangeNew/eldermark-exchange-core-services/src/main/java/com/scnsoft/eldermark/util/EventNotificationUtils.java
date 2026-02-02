package com.scnsoft.eldermark.util;

import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventType;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public final class EventNotificationUtils {
    private static final String NOTE_EDIT = "NOTEEDIT";
    private static final String NOTE_ADD = "NOTEADD";
    public static final String LAB_REVIEWED = "COVID19";
    public static final String MAP_CREATED = "MAP_CREATED";
    public static final String DOCUMENT_SIGNED = "DS";
    public static final String APPOINTMENT_CREATED = "NEWAP";
    public static final String APPOINTMENT_UPDATED = "UPDAP";
    public static final String APPOINTMENT_CANCELED = "CANAP";
    public static final String APPOINTMENT_COMPLETED = "COMAP";

    private static final List<String> NOTE_TYPES = Arrays.asList(NOTE_ADD, NOTE_EDIT);

    private EventNotificationUtils() {
    }

    public static boolean isNoteNotification(EventType eventType) {
        return NOTE_TYPES.contains(eventType.getCode());
    }

    public static boolean isLabReviewedNotification(Event event) {
        return LAB_REVIEWED.equals(event.getEventType().getCode())
                && event.getLabResearchOrder() != null
                && !event.isManual();
    }

    public static boolean isNoteAdd(EventType eventType) {
        return NOTE_ADD.equals(eventType.getCode());
    }

    public static boolean isNoteEdit(EventType eventType) {
        return NOTE_EDIT.equals(eventType.getCode());
    }

    public static String getNoteNotificationAction(EventType eventType) {
        if (EventNotificationUtils.isNoteAdd(eventType)) {
            return "added to";
        }

        if (EventNotificationUtils.isNoteEdit(eventType)) {
            return "updated in";
        }
        return StringUtils.EMPTY;
    }

    public static Long extractNotificationNoteId(Event event) {
        return Long.valueOf(event.getSituation());
    }

    public static boolean isMAPNotification(EventType eventType) {
        return MAP_CREATED.equals(eventType.getCode());
    }
}
