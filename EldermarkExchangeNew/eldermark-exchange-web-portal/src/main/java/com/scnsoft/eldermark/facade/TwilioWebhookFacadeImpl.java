package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.twilio.user.IdentityListItemDto;
import com.scnsoft.eldermark.dao.TwilioConversationDao;
import com.scnsoft.eldermark.dao.TwilioParticipantReadMessageStatusDao;
import com.scnsoft.eldermark.dto.twilio.TwilioConversationWebhookDto;
import com.scnsoft.eldermark.dto.twilio.TwilioConversationWebhookEventType;
import com.scnsoft.eldermark.dto.twilio.TwilioRoomWebhookDto;
import com.scnsoft.eldermark.dto.twilio.TwilioRoomWebhookEventType;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.service.twilio.ConversationUtils;
import com.scnsoft.eldermark.service.twilio.TwilioUserService;
import com.scnsoft.eldermark.service.twilio.VideoCallWebhookService;
import com.scnsoft.eldermark.service.twilio.notification.ConversationNotificationService;
import com.scnsoft.eldermark.util.StreamUtils;
import com.twilio.rest.conversations.v1.service.conversation.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TwilioWebhookFacadeImpl implements TwilioWebhookFacade {

    private static final Logger logger = LoggerFactory.getLogger(TwilioWebhookFacadeImpl.class);

    private static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Autowired
    private ChatService twilioChatService;

    @Autowired
    private TwilioUserService twilioUserService;

    @Autowired
    private ConversationNotificationService conversationNotificationService;

    @Autowired
    private VideoCallWebhookService videoCallWebhookService;

    @Autowired
    private TwilioParticipantReadMessageStatusDao twilioParticipantReadMessageStatusDao;

    @Autowired
    private TwilioConversationDao twilioConversationDao;

    @Override
    @Transactional
    public void processConversationWebhook(TwilioConversationWebhookDto conversationWebhookDto) {
        logger.info("Processing Twilio conversation webhook {}", conversationWebhookDto);

        var eventType = TwilioConversationWebhookEventType.fromValue(conversationWebhookDto.getEventType());
        if (eventType.isEmpty()) {
            return;
        }
        if (eventType.get() == TwilioConversationWebhookEventType.MESSAGE_ADDED) {
            twilioConversationDao.updateLastMessage(
                    conversationWebhookDto.getConversationSid(),
                    conversationWebhookDto.getIndex(),
                    Instant.from(DATE_TIME_FORMATTER.parse(conversationWebhookDto.getDateCreated()))
            );
            var participantIdentities = StreamUtils.stream(twilioChatService.getChatParticipants(conversationWebhookDto.getConversationSid()))
                    .map(Participant::getIdentity)
                    .filter(identity -> !identity.equals(conversationWebhookDto.getAuthor()))
                    .collect(Collectors.toSet());

            participantIdentities = twilioUserService.findDtoByIdentities(participantIdentities).stream()
                    .filter(IdentityListItemDto::getCanChat)
                    .map(IdentityListItemDto::getIdentity)
                    .collect(Collectors.toSet());

            conversationNotificationService.sendNewMessageNotifications(participantIdentities,
                    conversationWebhookDto.getConversationSid(),
                    conversationWebhookDto.getMessageSid(),
                    conversationWebhookDto.getMedia(),
                    conversationWebhookDto.getAttributes());
        } else if (eventType.get() == TwilioConversationWebhookEventType.PARTICIPANT_UPDATED) {
            twilioParticipantReadMessageStatusDao.upsertLastReadMessage(
                    conversationWebhookDto.getParticipantSid(),
                    Optional.ofNullable(conversationWebhookDto.getLastReadMessageIndex()).orElse(-1),
                    conversationWebhookDto.getConversationSid(),
                    ConversationUtils.employeeIdFromIdentity(conversationWebhookDto.getIdentity())
            );
        } else {
            logger.warn("Unprocessed Twilio conversation webhook event {}", conversationWebhookDto);
        }

        logger.info("Processed Twilio conversation webhook {}", conversationWebhookDto);
    }

    @Override
    public void processRoomWebhook(TwilioRoomWebhookDto roomWebhookDto) {
        var eventType = TwilioRoomWebhookEventType.fromValue(roomWebhookDto.getStatusCallbackEvent());
        if (eventType.isEmpty()) {
            return;
        }

        logger.info("Processing Twilio room webhook {}", roomWebhookDto);

        switch (eventType.get()) {
            case ROOM_ENDED:
                videoCallWebhookService.roomEnded(roomWebhookDto.getRoomSid(), roomWebhookDto.getTimestamp());
                break;
            case PARTICIPANT_CONNECTED:
                videoCallWebhookService.connectedToRoom(roomWebhookDto.getRoomSid(), roomWebhookDto.getParticipantSid(),
                        roomWebhookDto.getParticipantIdentity(), roomWebhookDto.getTimestamp());
                break;
            case PARTICIPANT_DISCONNECTED:
                videoCallWebhookService.disconnectedFromRoom(roomWebhookDto.getRoomSid(), roomWebhookDto.getParticipantSid(),
                        roomWebhookDto.getTimestamp());
                break;
            default:
                logger.warn("Unprocessed twilio room webhook event {}", roomWebhookDto);
        }

        logger.info("Processed Twilio room webhook {}", roomWebhookDto);
    }
}
