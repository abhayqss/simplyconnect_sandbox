package com.scnsoft.eldermark.service.pushnotification.sender;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import com.scnsoft.eldermark.dto.notification.PushNotificationVO;
import com.scnsoft.eldermark.entity.PushNotificationRegistration;
import com.scnsoft.eldermark.service.pushnotification.PushNotificationServiceImpl;
import com.scnsoft.eldermark.service.pushnotification.SendPushNotificationResult;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Responsible for sending FCM push notifications to specified application
 */
@Transactional
public class FcmPushNotificationSender extends BasePushNotificationSender {
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationServiceImpl.class);
    private static final List<String> INVALID_TOKEN_RESPONSE_ERRORS = Collections.singletonList("UNREGISTERED");

    private final FirebaseMessaging firebaseMessaging;
    private final PushNotificationRegistration.Application appName;

    public FcmPushNotificationSender(FirebaseApp app, PushNotificationRegistration.Application appName) {
        firebaseMessaging = FirebaseMessaging.getInstance(app);
        this.appName = appName;
    }

    @Override
    public SendPushNotificationResult sendAndWait(PushNotificationVO pushNotificationVO) {
        var result = new SendPushNotificationResult();

        var tokens = getTokens(pushNotificationVO, PushNotificationRegistration.ServiceProvider.FCM, appName);
        if (CollectionUtils.isEmpty(tokens)) {
            return result;
        }

        var notification = Notification.builder()
                .setTitle(pushNotificationVO.getTitle())
                .setBody(pushNotificationVO.getBody())
                .build();

        var androidNotification = AndroidNotification.builder()
                .setSound(pushNotificationVO.getAndroidSettings().getNotificationSound())
                .build();

        var androidConfig = com.google.firebase.messaging.AndroidConfig.builder()
                .setNotification(androidNotification)
                .setPriority(AndroidConfig.Priority.valueOf(pushNotificationVO.getAndroidSettings().getPriority().name()))
                .build();

        MulticastMessage raven = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(notification)
                .setAndroidConfig(androidConfig)
                .putAllData(pushNotificationVO.getPayload())
                .build();


        try {
            logger.info("Sending push notifications receivers {}", pushNotificationVO.getReceivers());
            var response = firebaseMessaging.sendMulticast(raven, pushNotificationVO.getAndroidSettings().isDryRun());
            if (response.getFailureCount() > 0) {
                var badTokens = findBadTokens(tokens, response);

                if (CollectionUtils.isNotEmpty(badTokens)) {
                    logger.info("Evicting FCM bad tokens for app {}", appName);
                    evictTokens(badTokens, PushNotificationRegistration.ServiceProvider.FCM);
                }
            }
            result.setDeliveredCount(response.getSuccessCount());
        } catch (Exception e) {
            logger.error("Error during sending push notifications to app {}", appName, e);
            result.getExceptions().add(e);
        }
        return result;
    }

    private List<String> findBadTokens(List<String> tokens, BatchResponse response) {
        var badTokens = new ArrayList<String>();
        var tokenIterator = tokens.iterator();
        var responseIterator = response.getResponses().iterator();

        //tokens and response have the same order
        while (tokenIterator.hasNext() && responseIterator.hasNext()) {
            var sendResponse = responseIterator.next();
            final String token = tokenIterator.next();

            if (!sendResponse.isSuccessful() &&
                    INVALID_TOKEN_RESPONSE_ERRORS.contains(sendResponse.getException().getMessagingErrorCode().name())) {
                badTokens.add(token);
            }
        }
        return badTokens;
    }
}
