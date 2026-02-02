package com.scnsoft.eldermark.dto.notification;

import com.scnsoft.eldermark.entity.PushNotificationRegistration;
import org.apache.commons.collections4.CollectionUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PushNotificationVO {

    private List<Receiver> receivers;
    private AndroidSettings androidSettings;
    private IosSettings iosSettings;

    private String devicePushNotificationTokenToExclude;


    private String title;
    private String body;
    private final Map<String, String> payload;

    public PushNotificationVO(List<Receiver> appReceiverSettings) {
        if (CollectionUtils.isEmpty(appReceiverSettings)) {
            throw new IllegalArgumentException("appReceiverSettings shouldn't be empty");
        }
        this.receivers = appReceiverSettings;
        this.payload = new HashMap<>();
    }

    public List<Receiver> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<Receiver> receivers) {
        this.receivers = receivers;
    }

    public AndroidSettings getAndroidSettings() {
        return androidSettings;
    }

    public void setAndroidSettings(AndroidSettings androidSettings) {
        this.androidSettings = androidSettings;
    }

    public IosSettings getIosSettings() {
        return iosSettings;
    }

    public void setIosSettings(IosSettings iosSettings) {
        this.iosSettings = iosSettings;
    }


    public String getDevicePushNotificationTokenToExclude() {
        return devicePushNotificationTokenToExclude;
    }

    public void setDevicePushNotificationTokenToExclude(String devicePushNotificationTokenToExclude) {
        this.devicePushNotificationTokenToExclude = devicePushNotificationTokenToExclude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getPayload() {
        return payload;
    }

    public static class IosSettings {
        private boolean isVOIP;
        private String defaultTitle;
        private boolean mutableContent;
        private boolean contentAvailable;
        private Duration expirationPeriod;

        public boolean isVOIP() {
            return isVOIP;
        }

        public void setVOIP(boolean VOIP) {
            isVOIP = VOIP;
        }

        public String getDefaultTitle() {
            return defaultTitle;
        }

        public void setDefaultTitle(String defaultTitle) {
            this.defaultTitle = defaultTitle;
        }

        public boolean isMutableContent() {
            return mutableContent;
        }

        public void setMutableContent(boolean mutableContent) {
            this.mutableContent = mutableContent;
        }

        public boolean isContentAvailable() {
            return contentAvailable;
        }

        public void setContentAvailable(boolean contentAvailable) {
            this.contentAvailable = contentAvailable;
        }

        public Duration getExpirationPeriod() {
            return expirationPeriod;
        }

        public void setExpirationPeriod(Duration expirationPeriod) {
            this.expirationPeriod = expirationPeriod;
        }
    }

    public static class AndroidSettings {
        //todo add more properties
        private Priority priority;
        private String notificationSound;
        private boolean dryRun;

        public Priority getPriority() {
            return priority;
        }

        public void setPriority(Priority priority) {
            this.priority = priority;
        }

        public String getNotificationSound() {
            return notificationSound;
        }

        public void setNotificationSound(String notificationSound) {
            this.notificationSound = notificationSound;
        }

        public boolean isDryRun() {
            return dryRun;
        }

        public void setDryRun(boolean dryRun) {
            this.dryRun = dryRun;
        }

        public enum Priority {
            NORMAL,
            HIGH
        }
    }

    public static class Receiver {
        private PushNotificationRegistration.Application appName;
        private Long userId;

        public Receiver(PushNotificationRegistration.Application appName, Long userId) {
            this.appName = appName;
            this.userId = userId;
        }

        public PushNotificationRegistration.Application getAppName() {
            return appName;
        }

        public void setAppName(PushNotificationRegistration.Application appName) {
            this.appName = appName;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        @Override
        public String toString() {
            return "Receiver{" +
                    "appName=" + appName +
                    ", userId=" + userId +
                    '}';
        }
    }

}

