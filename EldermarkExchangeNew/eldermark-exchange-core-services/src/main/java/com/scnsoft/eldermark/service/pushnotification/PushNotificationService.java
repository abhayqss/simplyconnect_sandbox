package com.scnsoft.eldermark.service.pushnotification;

import com.scnsoft.eldermark.dto.notification.PushNotificationVO;

import java.util.concurrent.Future;


public interface PushNotificationService {

    Future<SendPushNotificationResult> send(PushNotificationVO pushNotificationVO);

    SendPushNotificationResult sendAndWait(PushNotificationVO pushNotificationVO);
}
