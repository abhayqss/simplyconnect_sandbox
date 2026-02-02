package com.scnsoft.eldermark.service.twilio.notification;

import com.scnsoft.eldermark.beans.twilio.chat.MediaMessageCallbackListItem;
import com.scnsoft.eldermark.dao.ConversationNotificationDao;
import com.scnsoft.eldermark.dto.notification.PushNotificationVO;
import com.scnsoft.eldermark.entity.PushNotificationRegistration;
import com.scnsoft.eldermark.entity.video.ConversationNotification;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.service.SmsService;
import com.scnsoft.eldermark.service.UrlService;
import com.scnsoft.eldermark.service.UrlShortenerService;
import com.scnsoft.eldermark.service.pushnotification.PushNotificationFactory;
import com.scnsoft.eldermark.service.pushnotification.PushNotificationService;
import com.scnsoft.eldermark.service.pushnotification.PushNotificationType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class ConversationNotificationSenderImpl implements ConversationNotificationSender {

    @Autowired
    private UrlService urlService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private UrlShortenerService urlShortenerService;

    @Autowired
    private ConversationNotificationDao conversationNotificationDao;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Override
    @Async
    @Transactional
    public CompletableFuture<Boolean> send(ConversationNotificationVO notificationVO) {
        return CompletableFuture.completedFuture(sendAndWait(notificationVO));
    }

    @Override
    @Transactional
    public boolean sendAndWait(ConversationNotificationVO notificationVO) {
        var id = notificationVO.getNotificationId();
        ConversationNotification notification = conversationNotificationDao.findById(id).orElseThrow();
        boolean result = false;
        if (StringUtils.isNotEmpty(notification.getDestination())) {
            switch (notification.getChannel()) {
                case SMS:
                    result = smsService.sendSmsNotificationAndWait(notification.getDestination(), generateSmsMessageText(notification));
                    break;

                case PUSH_NOTIFICATION:
                    //at least one push was delivered (to possibly more that one user's device).
                    //Can be used to identify that user has mobile app installed
                    result = pushNotificationService.sendAndWait(createPushNotification(notification, notificationVO)).getDeliveredCount() > 0;
                    break;

                default:
                    throw new InternalServerException(InternalServerExceptionType.NOT_IMPLEMENTED);
            }
        }
        if (result) {
            notification.setSentDatetime(Instant.now());
        } else {
            notification.setFail(true);
        }
        conversationNotificationDao.save(notification);

        return result;
    }

    private String generateSmsMessageText(ConversationNotification notification) {
        String url;
        if (notification.getTwilioConversationSid() != null) {
            url = urlService.conversationUrl(notification.getTwilioConversationSid());
        } else {
            url = urlService.conversationsUrl();
        }
        switch (notification.getType()) {
            case NEW_MESSAGE:
                return "You have a new message. Login to Simply Connect to see it. " + urlShortenerService.getShortUrl(url);
            case INCOMING_CALL:
                return "You have an incoming video call. Login to Simply Connect to pick it up. " +
                        urlShortenerService.getShortUrl(url);
            default:
                throw new InternalServerException(InternalServerExceptionType.NOT_IMPLEMENTED);
        }
    }

    private PushNotificationVO createPushNotification(ConversationNotification notification, ConversationNotificationVO notificationVO) {
        if (notificationVO.getPreparedPushNotificationVO() != null) {
            return notificationVO.getPreparedPushNotificationVO();
        }

        var employeeId = Long.valueOf(notification.getDestination());
        switch (notification.getType()) {
            case NEW_MESSAGE:
                var vo = PushNotificationFactory.builder(PushNotificationType.NEW_CHAT_MESSAGE)
                        .receiver(PushNotificationRegistration.Application.SCM, employeeId)
                        .build();

                vo.getPayload().put("conversationSid", notification.getTwilioConversationSid());
                vo.getPayload().put("messageSid", notification.getTwilioMessageSid());
                vo.getPayload().put("messageType", resolveType(notificationVO).name());
                return vo;
        }
        throw new InternalServerException(InternalServerExceptionType.NOT_IMPLEMENTED);
    }

    private MessageType resolveType(ConversationNotificationVO conversationNotificationVO) {
        var mediaItems = conversationNotificationVO.getMessageMedia();
        if (CollectionUtils.isEmpty(mediaItems)) {
            return MessageType.TEXT;
        }
        if (BooleanUtils.isTrue(conversationNotificationVO.getIsVoiceMessage()) || (mediaItems.size() == 1 && isVoiceMedia(mediaItems.get(0)))) {
            return MessageType.VOICE;
        }
        return MessageType.MEDIA;
    }

    private boolean isVoiceMedia(MediaMessageCallbackListItem item) {
        return "audio/mp3".equals(item.getContentType()) && "voice-message.mp3".equals(item.getFileName());
    }

    enum MessageType {
        TEXT,
        MEDIA,
        VOICE
    }
}
