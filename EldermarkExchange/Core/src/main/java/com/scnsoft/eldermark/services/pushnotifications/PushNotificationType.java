package com.scnsoft.eldermark.services.pushnotifications;

/**
 * @author phomal
 * Created on 8/9/2017.
 */
public enum PushNotificationType {
    DEBUG(0),
    NEW_EVENT(1),
    RESULT_OF_PROVIDERS_INVITATION(2),
    INVITATION_TO_CT_FRIEND_FAMILY(3),
    INVITATION_TO_CT_MEDICAL_STAFF(4),
    NOTIFY_ALERT(5),   
    CHAT_NOTIFICATION(6);

    private final int notificationId;

    PushNotificationType(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getNotificationId() {
        return notificationId;
    }
}
