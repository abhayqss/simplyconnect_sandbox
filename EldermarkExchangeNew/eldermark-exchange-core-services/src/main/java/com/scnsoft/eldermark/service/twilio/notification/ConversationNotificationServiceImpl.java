package com.scnsoft.eldermark.service.twilio.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.PersonTelecomsAware;
import com.scnsoft.eldermark.beans.twilio.attributes.MessageAttributes;
import com.scnsoft.eldermark.beans.twilio.chat.MediaMessageCallbackListItem;
import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessage;
import com.scnsoft.eldermark.beans.twilio.messages.video.InitiateCallServiceMessage;
import com.scnsoft.eldermark.beans.twilio.messages.video.VideoCallServiceMessage;
import com.scnsoft.eldermark.beans.twilio.user.EmployeeTwilioCommunicationsUser;
import com.scnsoft.eldermark.dao.ConversationNotificationDao;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.EmployeeDisabledConversationNotificationDao;
import com.scnsoft.eldermark.dao.specification.ConversationNotificationSpecificationGenerator;
import com.scnsoft.eldermark.dto.notification.PushNotificationVO;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.PushNotificationRegistration;
import com.scnsoft.eldermark.entity.video.ConversationNotification;
import com.scnsoft.eldermark.entity.video.ConversationNotificationType;
import com.scnsoft.eldermark.entity.video.ConversationNotification_;
import com.scnsoft.eldermark.service.audit.AuditLogService;
import com.scnsoft.eldermark.service.pushnotification.PushNotificationFactory;
import com.scnsoft.eldermark.service.pushnotification.PushNotificationService;
import com.scnsoft.eldermark.service.pushnotification.PushNotificationType;
import com.scnsoft.eldermark.service.twilio.ConversationUtils;
import com.scnsoft.eldermark.service.twilio.TwilioAttributeService;
import com.scnsoft.eldermark.service.twilio.TwilioUserService;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class ConversationNotificationServiceImpl implements ConversationNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(ConversationNotificationServiceImpl.class);
    private static final Sort NOTIFICATIONS_BY_SENT_DATE_DESC = Sort.by(Sort.Direction.DESC, ConversationNotification_.SENT_DATETIME);

    @Autowired
    private ConversationNotificationSender conversationNotificationSender;

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private ConversationNotificationDao conversationNotificationDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private ConversationNotificationSpecificationGenerator conversationNotificationSpecifications;

    @Autowired
    private TwilioUserService userService;

    @Autowired
    private TwilioAttributeService twilioAttributeService;

    @Autowired
    private EmployeeDisabledConversationNotificationDao employeeDisabledConversationNotificationDao;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${conversation.notification.timeout}")
    private Long notificationTimeoutPerConversation;

    @Value("${video.push-notifications.enabled}")
    private Boolean videoPushNotificationsEnabled;

    @Override
    @Async
    public void sendNewMessageNotifications(Set<String> identities, String conversationSid, String messageSid, String mediaJson, String attributesJson) {
        sendNotifications(
                identities,
                new NewMessageEvent(
                        conversationSid,
                        messageSid,
                        mediaJson,
                        twilioAttributeService.parse(attributesJson, MessageAttributes.class)
                )
        );
    }

    @Override
    @Async
    public void sendServiceMessageNotifications(ServiceMessage serviceMessage, String messageSid, Long employeeId,
                                                String devicePushNotificationTokenToExclude) {
        switch (serviceMessage.getType()) {
            case INITIATE_CALL:
                logger.info("Sending service message notifications for INITIATE_CALL for " + employeeId);
                //send init call push and SMS if no app
                sendNotifications(Set.of(ConversationUtils.employeeIdToIdentity(employeeId)),
                        new IncomingCallEvent(
                                (InitiateCallServiceMessage) serviceMessage,
                                messageSid,
                                employeeId,
                                devicePushNotificationTokenToExclude));
                return;

            default: {
                //send service message push notifications
                prepareServiceMessageVO(messageSid, serviceMessage, employeeId, devicePushNotificationTokenToExclude)
                        .ifPresent(pushNotificationService::send);
            }
        }

    }

    private Optional<PushNotificationVO> prepareServiceMessageVO(String messageSid, ServiceMessage serviceMessage,
                                                                 Long employeeId,
                                                                 String devicePushNotificationTokenToExclude) {
        if (!videoPushNotificationsEnabled) {
            return Optional.empty();
        }
        var data = serviceMessage.toPushNotificationData(ConversationUtils.employeeIdToIdentity(employeeId));
        if (data.isEmpty()) {
            return Optional.empty();
        }

        var pushNotificationVO = PushNotificationFactory.builder(PushNotificationType.SERVICE_MESSAGE)
                .receiver(PushNotificationRegistration.Application.SCM, employeeId)
                .excludingDevice(devicePushNotificationTokenToExclude)
                .build();

        pushNotificationVO.getPayload().put("messageSid", messageSid);
        pushNotificationVO.getPayload().putAll(data);

        return Optional.of(pushNotificationVO);
    }

    private void sendNotifications(Set<String> identities, ConversationNotificationEvent event) {

        logger.info("Sending Push notifications for " + event.getType() + " for identities " + String.join(", ", identities));
        var usersToSendAfterPush = sendPushNotifications(identities, event);
        logger.info("Push notifications were sent");
        logger.info("Users to send " + event.getType() + " NonMobileNotifications: " + usersToSendAfterPush.stream().map(user -> user.getId().toString()).collect(Collectors.joining(", ")));
        sendNonMobileAppNotifications(usersToSendAfterPush, event);
    }

    private List<EmployeeTwilioCommunicationsUser> sendPushNotifications(Set<String> identities, ConversationNotificationEvent event) {
        var users = userService.fromIdentities(identities).stream()
                .filter(user -> EmployeeStatus.ACTIVE.equals(user.getStatus()))
                .collect(Collectors.toList());

        switch (event.getType()) {
            case NEW_MESSAGE:
            case INCOMING_CALL:
                var usersWithoutApp = users.stream()
                        .map(user -> {
                            var userWithApp = preparePushNotification(user, event)
                                    .map(n -> toVO(n, event))
                                    .map(conversationNotificationSender::sendAndWait)
                                    .filter(r -> r)
                                    .map(r -> user);
                            return userWithApp.isPresent() ? null : user;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                return usersWithoutApp;
            default:
                return users;
        }
    }

    private Optional<ConversationNotification> preparePushNotification(EmployeeTwilioCommunicationsUser user, ConversationNotificationEvent event) {
        return createConversationNotification(user, user.getId().toString(), event, NotificationType.PUSH_NOTIFICATION);
    }

    private void sendNonMobileAppNotifications(List<EmployeeTwilioCommunicationsUser> users, ConversationNotificationEvent event) {
        //step 2 - send sms notification for users without mobile app
        users.stream()
                .flatMap(user -> prepareNonMobileAppNotifications(user, event))
                .map(notification -> toVO(notification, event))
                .forEach(conversationNotificationSender::send);

        //todo test that executed
    }

    private Stream<ConversationNotification> prepareNonMobileAppNotifications(EmployeeTwilioCommunicationsUser user, ConversationNotificationEvent event) {
        //currently only SMS
        var enabledSms = !employeeDisabledConversationNotificationDao.existsByEmployeeIdAndChannelAndType(user.getId(), NotificationType.SMS, event.getType());
        if (enabledSms && isNonTimedOutSmsNotification(user, event) && !isOnline(user.getTwilioUserSid())) {
            logger.info("Preparing SMS notification for user: " + user.getId());
            return prepareSmsNotification(user, event).stream();
        }
        logger.info("SMS sending is not available for user: " + user.getId());
        return Stream.empty();
    }

    private boolean isOnline(String sidOrIdentity) {
        var twilioUser = userService.fetchFromTwilio(sidOrIdentity);
        return Boolean.TRUE.equals(twilioUser.getIsOnline());
    }

    private boolean isNonTimedOutSmsNotification(EmployeeTwilioCommunicationsUser user, ConversationNotificationEvent event) {
        if (ConversationNotificationType.NEW_MESSAGE == event.getType()) {
            var lastLoginTime = Optional.ofNullable(auditLogService.findLastLoginTime(user.getId())).orElse(Instant.MIN);
            var timeoutEnd = Instant.now().minusMillis(notificationTimeoutPerConversation);

            var byEmployeeAndConversation = conversationNotificationSpecifications.byEmployeeIdAndConversation(user.getId(), event.getConversationSid());
            var nonFailed = conversationNotificationSpecifications.notFailed();
            var createdAfter = conversationNotificationSpecifications.createdAfter(
                    timeoutEnd.isAfter(lastLoginTime) ? timeoutEnd : lastLoginTime
            );
            var smsNotification = conversationNotificationSpecifications.byChannel(NotificationType.SMS);
            var byType = conversationNotificationSpecifications.byType(event.getType());

            var spec = byEmployeeAndConversation.and(nonFailed).and(createdAfter).and(smsNotification).and(byType);

            var latestNotificationWithinTimeout = conversationNotificationDao.findFirst(spec,
                    IdAware.class, NOTIFICATIONS_BY_SENT_DATE_DESC);

            return latestNotificationWithinTimeout.isEmpty();
        }
        return true;
    }

    private Optional<ConversationNotification> prepareSmsNotification(EmployeeTwilioCommunicationsUser user,
                                                                      ConversationNotificationEvent event) {
        return createConversationNotification(user, getPhoneNumber(user.getId()), event, NotificationType.SMS);
    }

    private ConversationNotificationVO toVO(ConversationNotification notification, ConversationNotificationEvent event) {
        var vo = new ConversationNotificationVO();
        vo.setNotificationId(notification.getId());
        event.fillVO(vo);
        return vo;
    }

    private Optional<ConversationNotification> createConversationNotification(EmployeeTwilioCommunicationsUser user, String destination,
                                                                              ConversationNotificationEvent event,
                                                                              NotificationType channel) {
        if (!event.isChanelSupported(channel)) {
            return Optional.empty();
        }
        var entity = new ConversationNotification();
        entity.setDestination(destination);
        entity.setCreatedDatetime(Instant.now());
        entity.setChannel(channel);
        entity.setEmployee(employeeDao.getOne(user.getId()));
        entity.setType(event.getType());
        entity.setTwilioIdentity(userService.toIdentity(user));
        event.fill(entity);
        logger.info("Saving conversation notification for user: " + entity.getEmployee().getId() + " Type: : " + entity.getType());
        return Optional.of(conversationNotificationDao.save(entity));
    }

    private String getPhoneNumber(Long employeeId) {
        var telecoms = employeeDao.findById(employeeId, PersonTelecomsAware.class)
                .map(PersonTelecomsAware::getPersonTelecoms)
                .orElseThrow();

        return PersonTelecomUtils.findValue(telecoms, PersonTelecomCode.MC)
                .or(() -> PersonTelecomUtils.findValue(telecoms, PersonTelecomCode.WP))
                .orElse(StringUtils.EMPTY);
    }

    interface ConversationNotificationEvent {

        ConversationNotificationType getType();

        String getConversationSid();

        void fill(ConversationNotification notification);

        void fillVO(ConversationNotificationVO notificationVO);

        default boolean isChanelSupported(NotificationType type) {
            return true;
        }

        default MessageAttributes getMessageAttributes() {
            return null;
        }
    }

    class NewMessageEvent implements ConversationNotificationEvent {

        private final String conversationSid;
        private final String messageSid;
        private final List<MediaMessageCallbackListItem> messageMedia;
        private final Optional<MessageAttributes> messageAttributes;

        public NewMessageEvent(
                String conversationSid,
                String messageSid,
                String mediaJson,
                Optional<MessageAttributes> messageAttributes
        ) {
            this.conversationSid = conversationSid;
            this.messageSid = messageSid;
            this.messageMedia = parseMedia(mediaJson);
            this.messageAttributes = messageAttributes;
        }

        private List<MediaMessageCallbackListItem> parseMedia(String mediaJson) {
            if (StringUtils.isEmpty(mediaJson)) {
                return Collections.emptyList();
            }
            try {
                return objectMapper.readerFor(objectMapper.getTypeFactory().constructCollectionType(List.class, MediaMessageCallbackListItem.class))
                        .readValue(mediaJson);
            } catch (JsonProcessingException e) {
                logger.info("Couldn't parse media json {}", mediaJson, e);
            }
            return Collections.emptyList();
        }

        @Override
        public ConversationNotificationType getType() {
            return ConversationNotificationType.NEW_MESSAGE;
        }

        @Override
        public String getConversationSid() {
            return this.conversationSid;
        }

        @Override
        public void fill(ConversationNotification notification) {
            notification.setTwilioConversationSid(this.conversationSid);
            notification.setTwilioMessageSid(this.messageSid);
        }

        @Override
        public void fillVO(ConversationNotificationVO notificationVO) {
            notificationVO.setMessageMedia(messageMedia);
            messageAttributes.map(MessageAttributes::getIsVoiceMessage)
                    .ifPresent(notificationVO::setIsVoiceMessage);
        }

        @Override
        public MessageAttributes getMessageAttributes() {
            return messageAttributes.orElse(null);
        }
    }


    abstract class BaseServiceMessageNotificationEvent<T extends ServiceMessage> implements ConversationNotificationEvent {
        protected final T serviceMessage;
        protected final Long employeeId;
        protected final PushNotificationVO preparedPushNotificationVO;


        protected BaseServiceMessageNotificationEvent(T serviceMessage, String serviceMessageSid, Long employeeId,
                                                      String devicePushNotificationTokenToExclude) {
            this.serviceMessage = serviceMessage;
            this.employeeId = employeeId;
            this.preparedPushNotificationVO = prepareServiceMessageVO(
                    serviceMessageSid,
                    serviceMessage,
                    employeeId,
                    devicePushNotificationTokenToExclude)
                    .orElse(null);
        }

        @Override
        public void fillVO(ConversationNotificationVO notificationVO) {
            notificationVO.setPreparedPushNotificationVO(preparedPushNotificationVO);
        }

        @Override
        public boolean isChanelSupported(NotificationType type) {
            switch (type) {
                case PUSH_NOTIFICATION:
                    return preparedPushNotificationVO != null;
            }
            return true;
        }
    }

    abstract class BaseVideoServiceMessageNotificationEvent<T extends VideoCallServiceMessage> extends BaseServiceMessageNotificationEvent<T> {

        protected BaseVideoServiceMessageNotificationEvent(T serviceMessage, String serviceMessageSid, Long employeeId,
                                                           String devicePushNotificationTokenToExclude) {
            super(serviceMessage, serviceMessageSid, employeeId, devicePushNotificationTokenToExclude);
        }

        @Override
        public void fill(ConversationNotification notification) {
            notification.setTwilioRoomSid(serviceMessage.getRoomSid());
        }
    }

    class IncomingCallEvent extends BaseVideoServiceMessageNotificationEvent<InitiateCallServiceMessage> {

        protected IncomingCallEvent(InitiateCallServiceMessage serviceMessage, String serviceMessageSid, Long employeeId,
                                    String devicePushNotificationTokenToExclude) {
            super(serviceMessage, serviceMessageSid, employeeId, devicePushNotificationTokenToExclude);
        }

        @Override
        public ConversationNotificationType getType() {
            return ConversationNotificationType.INCOMING_CALL;
        }

        @Override
        public String getConversationSid() {
            return null;
        }

        @Override
        public void fill(ConversationNotification notification) {
            super.fill(notification);
            notification.setTwilioConversationSid(serviceMessage.getConversationSid());
        }

        @Override
        public boolean isChanelSupported(NotificationType type) {
            switch (type) {
                case PUSH_NOTIFICATION:
                    return preparedPushNotificationVO != null;
                case SMS:
                    return !serviceMessage.getCaller().getEmployeeId().equals(employeeId); // don't send SMS to caller
            }
            return false;
        }
    }

}
