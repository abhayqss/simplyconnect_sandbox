package com.scnsoft.eldermark.entity.phr;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

@Entity
@Table(name = "PushNotificationRegistration")
public class PushNotificationRegistration extends BaseEntity {

    public static final String PHR_APP = "PHR";

    /**
     * Push notifications service provider
     */
    public enum ServiceProvider {
        /**
         * Firebase Cloud Messaging
         */
        FCM("FCM"),
        /**
         * Google Cloud Messaging
         * NOTE that on April 10, 2018, Google deprecated GCM. The GCM server and client APIs were removed 
         * on May 29, 2019, and currently any calls to those APIs can be expected to fail.
         * 
         * Use FCM instead.
         * 
         * todo remove this option if there are no GCM tokens in DB
         */
        @Deprecated
        GCM("GCM"),
        /**
         * Apple Push Notifications Service UserNotifications Framework
         * notification types: alert, background
         *
         */
        APNS_UN("APNS"),
        /**
         * Apple Push Notifications Service PushKit Framework
         * notification types: voip, complication, fileprovider
         *
         */
        APNS_PK("APNS_VOIP");

        private final String legacyValue;

        ServiceProvider(String legacyValue) {
            this.legacyValue = legacyValue;
        }

        @Override
        @JsonValue
        public String toString() {
            return legacyValue;
        }

        @JsonCreator
        public static ServiceProvider fromLegacyValue(String text) {
            text = StringUtils.upperCase(text);
            for (ServiceProvider b : ServiceProvider.values()) {
                if (String.valueOf(b.legacyValue).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "service", nullable = false)
    private ServiceProvider serviceProvider;

    @Column(name = "device_token", nullable = false)
    private String deviceToken;

    @Column(name = "app_name", nullable = false)
    private String appName;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String regId) {
        this.deviceToken = regId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}

