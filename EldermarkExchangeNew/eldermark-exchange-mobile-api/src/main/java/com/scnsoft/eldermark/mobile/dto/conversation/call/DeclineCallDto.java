package com.scnsoft.eldermark.mobile.dto.conversation.call;

public class DeclineCallDto extends CallRoomDto {
    private String devicePushNotificationToken;

    public String getDevicePushNotificationToken() {
        return devicePushNotificationToken;
    }

    public void setDevicePushNotificationToken(String devicePushNotificationToken) {
        this.devicePushNotificationToken = devicePushNotificationToken;
    }
}
