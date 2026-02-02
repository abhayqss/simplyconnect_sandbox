package com.scnsoft.eldermark.service.notification.sender;

import com.scnsoft.eldermark.dao.NoteDao;
import com.scnsoft.eldermark.dto.notification.PushNotificationVO;
import com.scnsoft.eldermark.entity.EventNotification;
import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.entity.PushNotificationRegistration;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.service.pushnotification.PushNotificationFactory;
import com.scnsoft.eldermark.service.pushnotification.PushNotificationService;
import com.scnsoft.eldermark.service.pushnotification.PushNotificationType;
import com.scnsoft.eldermark.util.ClientUtils;
import com.scnsoft.eldermark.util.EventNotificationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;

@Service
@Transactional
public class PushEventNotificationSender extends BaseEventNotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(PushEventNotificationSender.class);

    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private NoteDao noteDao;

    @Override
    protected boolean send(EventNotification eventNotification) {
        if (EventNotificationUtils.isLabReviewedNotification(eventNotification.getEvent())) {
            //todo lab notifications
            logger.info("Attempt to send Lab push notification. Not implemented yet - notification won't be sent");
            return false;
        }
        if (EventNotificationUtils.isMAPNotification(eventNotification.getEvent().getEventType())) {
            //todo MAP notifications
            logger.info("Attempt to send MAP push notification. Not implemented yet - notification won't be sent");
            return false;
        }
        var pushNotificationVO = createPushNotificationVO(eventNotification);
        return !pushNotificationService.sendAndWait(pushNotificationVO).hasExceptions();
    }

    private PushNotificationVO createPushNotificationVO(final EventNotification eventNotification) {
        var destinationMobileUserId = Long.valueOf(eventNotification.getDestination());
        var event = eventNotification.getEvent();

        final PushNotificationVO result = PushNotificationFactory
                .builder(PushNotificationType.NEW_EVENT)
                .receiver(PushNotificationRegistration.Application.PHR, destinationMobileUserId)
                .build();

        result.setTitle(getTitle(event));
        result.setBody(eventNotification.getContent());

        var payload = result.getPayload();
        payload.put("eventId", event.getId().toString());
        payload.put("eventType", event.getEventType().getDescription());
        payload.put("eventDate", String.valueOf(event.getEventDateTime().toEpochMilli()));

        if (EventNotificationUtils.isNoteNotification(event.getEventType())) {
            var note = noteDao.findById(EventNotificationUtils.extractNotificationNoteId(event)).orElseThrow();
            payload.put("noteId", note.getId().toString());
            payload.put("noteType", note.getType().name());
        }

        return result;
    }

    private String getTitle(Event event) {
        var forPatient = ClientUtils.getInitials(event.getClient(), " ");
        forPatient = StringUtils.isNotEmpty(forPatient) ? " for " + forPatient : forPatient;

        if (EventNotificationUtils.isNoteAdd(event.getEventType())) {
            return "Note has been added" + forPatient;
        }
        if (EventNotificationUtils.isNoteEdit(event.getEventType())) {
            return "Note has been updated" + forPatient;
        }

        String eventGroup = event.getEventType().getEventGroup().getName();
        return format("A new %s event has been logged%s", eventGroup, forPatient);
    }

    @Override
    public NotificationType supportedNotificationType() {
        return NotificationType.PUSH_NOTIFICATION;
    }
}
