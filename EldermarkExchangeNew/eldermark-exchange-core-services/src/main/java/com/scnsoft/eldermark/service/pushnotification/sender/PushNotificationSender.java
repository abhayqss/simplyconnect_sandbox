package com.scnsoft.eldermark.service.pushnotification.sender;

import com.scnsoft.eldermark.dto.notification.PushNotificationVO;
import com.scnsoft.eldermark.service.pushnotification.SendPushNotificationResult;

/**
 * Each sender is responsible for sending push notifications to services it supports (and probably to specific applications)
 * All the implementations should cover all supported service providers and applications
 */
public interface PushNotificationSender {

    SendPushNotificationResult sendAndWait(PushNotificationVO pushNotificationVO);

}
