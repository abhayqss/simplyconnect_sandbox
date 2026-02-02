package com.scnsoft.eldermark.service.pushnotification.sender;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsPushNotification;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.TokenUtil;
import com.scnsoft.eldermark.dto.notification.PushNotificationVO;
import com.scnsoft.eldermark.entity.PushNotificationRegistration;
import com.scnsoft.eldermark.service.pushnotification.SendPushNotificationResult;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


public abstract class BaseApnsPushNotificationSender extends BasePushNotificationSender {
    private static final Logger logger = LoggerFactory.getLogger(BaseApnsPushNotificationSender.class);

    private final ApnsClient apnsClient;

    @Value("#{${apns.topics}}")
    private Map<PushNotificationRegistration.Application, String> apnsTopics;

    protected BaseApnsPushNotificationSender(ApnsClient apnsClient) {
        this.apnsClient = apnsClient;
    }

    protected abstract PushNotificationRegistration.ServiceProvider resolveServiceProvider(PushNotificationVO pushNotificationVO);

    @Override
    public SendPushNotificationResult sendAndWait(PushNotificationVO pushNotificationVO) {
        var result = new SendPushNotificationResult();
        logger.info("Sending push notification id=[{}] to {}...",
                pushNotificationVO.getPayload().get("id"),
                pushNotificationVO.getReceivers()
        );

        var serviceProvider = resolveServiceProvider(pushNotificationVO);

        var tokens = getAppTokensMap(pushNotificationVO, serviceProvider);
        if (tokens.size() == 0) {
            logger.info("No push registration tokens found");
            return result;
        }
        logger.info("Found tokens {}", tokens.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size())));

        var builder = new SimpleApnsPayloadBuilder()
                .setAlertTitle(
                        StringUtils.isNotEmpty(pushNotificationVO.getTitle()) ?
                                pushNotificationVO.getTitle() :
                                pushNotificationVO.getIosSettings().getDefaultTitle()
                )
                .setAlertBody(pushNotificationVO.getBody())
                .setMutableContent(pushNotificationVO.getIosSettings().isMutableContent())
                .setContentAvailable(pushNotificationVO.getIosSettings().isContentAvailable());

        if (MapUtils.isNotEmpty(pushNotificationVO.getPayload())) {
            builder.addCustomProperty("data", pushNotificationVO.getPayload());
        }

        var payload = builder.build();


        try {
            var notificationResults = tokens.entrySet().stream()
                    .flatMap(e -> e.getValue().stream().map(token ->
                            buildPushNotification(token, e.getKey(), pushNotificationVO, payload)
                    ))
                    .map(apnsClient::sendNotification)
                    .collect(Collectors.toList());

            //wait for notifications to complete and process
            var badTokens = new ArrayList<String>();
            notificationResults.stream()
                    .map(CompletableFuture::join)
                    .forEach(response -> {
                        if (response.isAccepted()) {
                            logger.info("Successfully delivered push notification {}", response.getApnsId());
                            result.addDelivered();
                        } else {
                            logger.info("Failed to deliver push notification, {}", response.getRejectionReason());
                            result.getExceptions().add(new RuntimeException(response.getRejectionReason()));
                            response.getTokenInvalidationTimestamp().ifPresent(x -> badTokens.add(response.getPushNotification().getToken()));
                        }
                    });

            if (!badTokens.isEmpty()) {
                evictTokens(badTokens, serviceProvider);
            }
        } catch (Exception e) {
            logger.error("Failed to send push notification to APNS", e);
            result.getExceptions().add(e);
        }
        return result;
    }

    protected String resolveTopic(PushNotificationRegistration.Application application, PushNotificationVO.IosSettings iosSettings) {
        var topic = apnsTopics.get(application);
        if (iosSettings.isVOIP()) {
            return topic + ".voip";
        }
        return topic;
    }

    protected ApnsPushNotification buildPushNotification(String token,
                                                         PushNotificationRegistration.Application app,
                                                         PushNotificationVO pushNotificationVO,
                                                         String payload) {
        return new SimpleApnsPushNotification(
                TokenUtil.sanitizeTokenString(token),
                resolveTopic(app, pushNotificationVO.getIosSettings()),
                payload,
                resolveInvalidationTime(pushNotificationVO.getIosSettings().getExpirationPeriod()));
    }

    private Instant resolveInvalidationTime(Duration expirationPeriod) {
        if (Duration.ZERO.equals(expirationPeriod)) {
            return null;
        } else {
            var periodOrDefault = Optional.ofNullable(expirationPeriod)
                    .orElse(SimpleApnsPushNotification.DEFAULT_EXPIRATION_PERIOD);
            return Instant.now().plus(periodOrDefault);
        }
    }
}
