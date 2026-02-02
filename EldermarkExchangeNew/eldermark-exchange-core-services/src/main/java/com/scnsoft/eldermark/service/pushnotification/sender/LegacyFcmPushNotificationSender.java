package com.scnsoft.eldermark.service.pushnotification.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.dto.notification.PushNotificationVO;
import com.scnsoft.eldermark.dto.notification.fcm.FcmResult;
import com.scnsoft.eldermark.dto.notification.fcm.FcmSuccessMessageDto;
import com.scnsoft.eldermark.entity.PushNotificationRegistration;
import com.scnsoft.eldermark.service.pushnotification.PushNotificationServiceImpl;
import com.scnsoft.eldermark.service.pushnotification.SendPushNotificationResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import us.raudi.pushraven.FcmResponse;
import us.raudi.pushraven.Notification;
import us.raudi.pushraven.Pushraven;

import java.io.IOException;
import java.util.*;

/**
 * Responsible for sending FCM push notifications through legacy HTTP Api
 *
 * Note that Pushraven is singleton, so only one application is supported.
 *
 * Not in use anymore, left just in case of issued during migration from legacy API to new HTTP v1 API.
 *
 * todo remove in future
 */
@Transactional
public class LegacyFcmPushNotificationSender extends BasePushNotificationSender {
    private static final Logger logger = LoggerFactory.getLogger(PushNotificationServiceImpl.class);
    private static final List<String> INVALID_TOKEN_RESPONSE_ERRORS = Arrays.asList("NotRegistered", "InvalidRegistration");

    @Autowired
    private final ObjectMapper objectMapper;

    private final PushNotificationRegistration.Application appName;

    public LegacyFcmPushNotificationSender(ObjectMapper objectMapper, String fcmServerKey, PushNotificationRegistration.Application appName) {
        this.objectMapper = objectMapper;
        this.appName = appName;

        Pushraven.setKey(fcmServerKey);
    }

    @Override
    public SendPushNotificationResult sendAndWait(PushNotificationVO pushNotificationVO) {
        var result = new SendPushNotificationResult();

        var tokens = getTokens(pushNotificationVO, PushNotificationRegistration.ServiceProvider.FCM);
        if (CollectionUtils.isEmpty(tokens)) {
            return result;
        }

        var payload = new HashMap<String, Object>();
        pushNotificationVO.getPayload().forEach(payload::put);
        Notification raven = new Notification()
                .title(pushNotificationVO.getTitle())
                .text(pushNotificationVO.getBody())
//                 .tag(pushNotificationVO.getTag()) //todo uncomment?
                .data(payload)
                .priority(pushNotificationVO.getAndroidSettings().getPriority() == PushNotificationVO.AndroidSettings.Priority.HIGH ? 10 : 5);

//        if (pushNotificationVO.getNotification() != null) {
//            raven = raven
//                    // .tag(pushNotificationVO.getTag())
//                    .notification(pushNotificationVO.getNotification())
//                    .sound(pushNotificationVO.getAndroidSettings().getNotificationSound());
//        }

        boolean multicast = tokens.size() > 1;
        if (multicast) {
            raven.addAllMulticasts(tokens);
        } else {
            raven.to(tokens.get(0));
        }

        logger.debug("Let's push a raven: {}", raven.toJSON());

        final FcmResponse response = Pushraven.push(raven);
        if (!HttpStatus.valueOf(response.getResponseCode()).is2xxSuccessful()) {
            logger.warn(response.toString());
            return result;
        } else {
            var responseMessageDto = readResponse(response);
            if (responseMessageDto == null) {
                return result;
            } else if (responseMessageDto.getFailure() == 0) {
                result.setDeliveredCount(responseMessageDto.getSuccess());
                return result;
            }

            var badTokens = findBadTokens(tokens, responseMessageDto);
            if (CollectionUtils.isNotEmpty(badTokens)) {
                logger.info("Evicting bad tokens. FCM success response: {}", response.getSuccessResponseMessage());
                evictTokens(badTokens, PushNotificationRegistration.ServiceProvider.FCM);
            }

            result.setDeliveredCount(responseMessageDto.getSuccess());
            return result;
        }
    }

    private FcmSuccessMessageDto readResponse(FcmResponse response) {
        var json = response.getSuccessResponseMessage();
        try {
            return objectMapper.readerFor(FcmSuccessMessageDto.class).readValue(json);
        } catch (IOException e) {
            logger.warn("Error during parsing FCM Response {}", json, e);
            return null;
        }
    }

    private List<String> findBadTokens(List<String> tokens, FcmSuccessMessageDto messageDto) {
        var badTokens = new ArrayList<String>();
        final Iterator<String> tokenIterator = tokens.iterator();
        final Iterator<FcmResult> resultIterator = messageDto.getResults().iterator();
        while (tokenIterator.hasNext() && resultIterator.hasNext()) {
            final FcmResult result = resultIterator.next();
            final String token = tokenIterator.next();
            if (INVALID_TOKEN_RESPONSE_ERRORS.contains(StringUtils.defaultString(result.getError()))) {
                badTokens.add(token);
            }
        }
        return badTokens;
    }
}
