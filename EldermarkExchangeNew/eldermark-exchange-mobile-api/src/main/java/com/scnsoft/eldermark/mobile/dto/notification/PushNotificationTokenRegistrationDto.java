package com.scnsoft.eldermark.mobile.dto.notification;

import com.scnsoft.eldermark.entity.PushNotificationRegistration;

public class PushNotificationTokenRegistrationDto {

    private String deviceToken;
    private PushNotificationRegistration.ServiceProvider serviceProvider;

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public PushNotificationRegistration.ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(PushNotificationRegistration.ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }
}
