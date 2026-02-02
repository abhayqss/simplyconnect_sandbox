package com.scnsoft.eldermark.service.pushnotification;

import com.scnsoft.eldermark.dto.notification.PushNotificationVO;

import java.time.Duration;

/**
 * Defines push notification use case, so that delivery settings are the same for notifications with the same type
 */
public enum PushNotificationType {

    DEBUG(0) {
        @Override
        public PushNotificationVO.AndroidSettings createAndroidSettings() {
            var settings = new PushNotificationVO.AndroidSettings();
            settings.setPriority(PushNotificationVO.AndroidSettings.Priority.HIGH);
            settings.setNotificationSound(PushNotificationConstants.ANDROID_DEFAULT_SOUND);
            return settings;
        }

        @Override
        public PushNotificationVO.IosSettings createIosSettings() {
            var settings = new PushNotificationVO.IosSettings();
            settings.setDefaultTitle("Debug");
            return settings;
        }
    },

    NEW_EVENT(1) {
        @Override
        public PushNotificationVO.AndroidSettings createAndroidSettings() {
            var settings = new PushNotificationVO.AndroidSettings();
            settings.setPriority(PushNotificationVO.AndroidSettings.Priority.HIGH);
//            settings.setNotificationSound(PushNotificationConstants.ANDROID_DEFAULT_SOUND);
            return settings;
        }

        @Override
        public PushNotificationVO.IosSettings createIosSettings() {
            var settings = new PushNotificationVO.IosSettings();
            settings.setDefaultTitle("New event");
            return settings;
        }
    },
    //below are reserved for old portal
//    RESULT_OF_PROVIDERS_INVITATION(2),
//    INVITATION_TO_CT_FRIEND_FAMILY(3),
//    INVITATION_TO_CT_MEDICAL_STAFF(4),
//    CHAT_NOTIFICATION(6);

    NEW_CHAT_MESSAGE(7) {
        @Override
        public PushNotificationVO.AndroidSettings createAndroidSettings() {
            var settings = new PushNotificationVO.AndroidSettings();
            settings.setPriority(PushNotificationVO.AndroidSettings.Priority.HIGH);
//            settings.setNotificationSound(PushNotificationConstants.ANDROID_DEFAULT_SOUND);
            return settings;
        }

        @Override
        public PushNotificationVO.IosSettings createIosSettings() {
            var settings = new PushNotificationVO.IosSettings();
            settings.setDefaultTitle("New message");
            settings.setMutableContent(true);
            return settings;
        }
    },

    SERVICE_MESSAGE(8) {
        @Override
        public PushNotificationVO.AndroidSettings createAndroidSettings() {
            var settings = new PushNotificationVO.AndroidSettings();
            settings.setPriority(PushNotificationVO.AndroidSettings.Priority.HIGH);
//            settings.setNotificationSound(PushNotificationConstants.ANDROID_DEFAULT_SOUND);
            return settings;
        }

        @Override
        public PushNotificationVO.IosSettings createIosSettings() {
            var settings = new PushNotificationVO.IosSettings();
            settings.setDefaultTitle("New service message");
            settings.setVOIP(true);
            settings.setContentAvailable(true);
            settings.setExpirationPeriod(Duration.ZERO);
            return settings;
        }
    },

    NEW_CARE_TEAM_INVITATION(9) {
        @Override
        public PushNotificationVO.AndroidSettings createAndroidSettings() {
            var settings = new PushNotificationVO.AndroidSettings();
            settings.setPriority(PushNotificationVO.AndroidSettings.Priority.HIGH);
            return settings;
        }

        @Override
        public PushNotificationVO.IosSettings createIosSettings() {
            var settings = new PushNotificationVO.IosSettings();
            settings.setDefaultTitle("Invitation to join the Care Team");
            return settings;
        }
    };

    private final int typeId;

    PushNotificationType(int notificationId) {
        this.typeId = notificationId;
    }

    public int getTypeId() {
        return typeId;
    }

    //todo implement below by all types
    public abstract PushNotificationVO.AndroidSettings createAndroidSettings();

    public abstract PushNotificationVO.IosSettings createIosSettings();
}
