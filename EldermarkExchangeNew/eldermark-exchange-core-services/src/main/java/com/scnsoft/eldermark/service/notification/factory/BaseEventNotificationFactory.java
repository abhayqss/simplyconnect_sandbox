package com.scnsoft.eldermark.service.notification.factory;

import com.scnsoft.eldermark.dao.EventNotificationDao;
import com.scnsoft.eldermark.dao.NoteDao;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EventNotification;
import com.scnsoft.eldermark.entity.NotificationPreferences;
import com.scnsoft.eldermark.entity.careteam.CareTeamMemberNotificationPreferences;
import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.event.EventType;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.entity.phr.MobileUser;
import com.scnsoft.eldermark.entity.phr.MobileUserNotificationPreferences;
import com.scnsoft.eldermark.util.ClientUtils;
import com.scnsoft.eldermark.util.EventNotificationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
public abstract class BaseEventNotificationFactory implements EventNotificationFactory {
    protected static final String EVENT_NOTIFICATION_CONTENT_TEMPLATE = "A new %s event has been logged to Simply Connect platform %s %s at %s and you are %s for this type of event.";
    protected static final String EVENT_LAB_NOTIFICATION_CONTENT_TEMPLATE = "You have a new lab result(s) %s.";
    protected static final String EVENT_LAB_SMS_NOTIFICATION_CONTENT_TEMPLATE = "You have a new COVID-19 result(s) %s. %s";
    protected static final String MAP_NOTIFICATION_CONTENT_TEMPLATE = "You have a new Medication action plan (MAP) %s.";
    protected static final String SMS_MAP_NOTIFICATION_CONTENT_TEMPLATE = "You have a new Medication action plan (MAP) %s. %s";
    protected static final String NOTE_NOTIFICATION_CONTENT_TEMPLATE = "A note has been %s the Simply Connect platform %s and you are %s for this type of event.";
    protected static final String SMS_NOTE_NOTIFICATION_CONTENT_TEMPLATE = "A note has been %s the Simply Connect platform %s at %s. %s";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventNotificationDao eventNotificationDao;

    @Autowired
    private NoteDao noteDao;

    @Override
    public Optional<EventNotification> createNotification(Event event, NotificationPreferences np) {
        try {

            if (np instanceof CareTeamMemberNotificationPreferences) {
                return createNotification(event, (CareTeamMemberNotificationPreferences) np);
            }

            if (np instanceof MobileUserNotificationPreferences) {
                return createNotification(event, (MobileUserNotificationPreferences) np);
            }

        } catch (RuntimeException e) {
            logger.warn("Couldn't create event notification for event [{}] with type {}", event.getId(), np.getNotificationType(), e);
        }

        return Optional.empty();
    }

    private Optional<EventNotification> createNotification(Event event, CareTeamMemberNotificationPreferences np) {
        var eventNotification = newEventNotification(event, np);

        var careTeamMember = np.getCareTeamMember();

        eventNotification.setCareTeamRole(careTeamMember.getCareTeamRole());
        eventNotification.setDescription(careTeamMember.getDescription());
        eventNotification.setEmployee(careTeamMember.getEmployee());
        eventNotification.setPersonName(careTeamMember.getEmployee().getFullName());
        eventNotification.setDestination(generateDestination(careTeamMember.getEmployee()));

        return Optional.of(eventNotificationDao.save(eventNotification));
    }

    private Optional<EventNotification> createNotification(Event event, MobileUserNotificationPreferences np) {
        var eventNotification = newEventNotification(event, np);

        var mobileUser = np.getMobileUser();

        eventNotification.setEmployee(mobileUser.getEmployee());
        eventNotification.setUserPatient(mobileUser);
        eventNotification.setPersonName(mobileUser.getClientFullNameLegacy());
        eventNotification.setDestination(generateDestination(mobileUser));

        return Optional.of(eventNotificationDao.save(eventNotification));
    }

    private EventNotification newEventNotification(Event event, NotificationPreferences np) {
        var eventNotification = new EventNotification();

        eventNotification.setCreatedDatetime(Instant.now());
        eventNotification.setEvent(event);
        eventNotification.setNotificationType(np.getNotificationType());
        eventNotification.setResponsibility(np.getResponsibility());
        eventNotification.setContent(generateContent(event, np));

        return eventNotification;
    }

    protected abstract String generateContent(Event event, NotificationPreferences np);

    protected abstract String generateDestination(Employee employee);

    protected abstract String generateDestination(MobileUser mobileUser);

    protected String createNotificationContentUtil(Event event, NotificationPreferences np, String eventTypeStr) {
        if (EventNotificationUtils.isNoteNotification(event.getEventType())) {
            return createNoteNotificationContent(event, np, eventTypeStr);
        }
        if (EventNotificationUtils.isLabReviewedNotification(event)) {
            return createLabNotificationContent(event, np, eventTypeStr);
        }
        if (EventNotificationUtils.isMAPNotification(event.getEventType())) {
            return createMAPNotificationContent(event, np, eventTypeStr);
        }
        return createEventNotificationContent(event, np, eventTypeStr);
    }

    protected String createNoteNotificationContent(Event event, NotificationPreferences np, String eventTypeStr) {
        var action = getNoteAction(event.getEventType());
        var patientName = buildPatientName(event);
        var eventText = getNoteEventText(event, np, eventTypeStr);

        return String.format(getNoteContentTemplate(), action, patientName, eventText);
    }

    protected String createLabNotificationContent(Event event, NotificationPreferences np, String eventTypeStr) {
        var patientName = buildPatientName(event);

        return String.format(getLabEventContentTemplate(), patientName);
    }

    protected String createMAPNotificationContent(Event event, NotificationPreferences np, String eventTypeStr) {
        var patientName = buildPatientName(event);

        return String.format(getMAPContentTemplate(), patientName);
    }

    protected String createEventNotificationContent(Event event, NotificationPreferences np, String eventTypeStr) {
        var responsibility = np.getResponsibility();

        var eventText = getEventText(event, np, eventTypeStr);
        var patientName = buildPatientName(event);
        var communityName = getCommunityName(event);

        return String.format(getEventContentTemplate(), eventTypeStr, eventText, patientName, communityName, responsibility.getDescription());
    }

    protected String getCommunityName(Event event) {
        //community should be the same even for group note
        return loadNotificationClients(event).get(0).getCommunity().getName();
    }

    protected String getNoteAction(EventType eventType) {
        return EventNotificationUtils.getNoteNotificationAction(eventType);
    }

    protected String getEventContentTemplate() {
        return EVENT_NOTIFICATION_CONTENT_TEMPLATE;
    }

    protected String getNoteContentTemplate() {
        return NOTE_NOTIFICATION_CONTENT_TEMPLATE;
    }

    protected String getLabEventContentTemplate() {
        return EVENT_LAB_NOTIFICATION_CONTENT_TEMPLATE;
    }

    protected String getMAPContentTemplate() {
        return MAP_NOTIFICATION_CONTENT_TEMPLATE;
    }

    protected String getEventText(Event event, NotificationPreferences np, String eventTypeStr) {
        return StringUtils.EMPTY;
    }

    protected String getNoteEventText(Event event, NotificationPreferences np, String eventTypeStr) {
        return np.getResponsibility().getDescription();
    }

    protected List<Client> loadNotificationClients(Event event) {
        if (EventNotificationUtils.isNoteNotification(event.getEventType())) {
            return loadNote(event).getNoteClients();
        }
        return Collections.singletonList(event.getClient());
    }

    private Note loadNote(Event event) {
        return noteDao.findById(EventNotificationUtils.extractNotificationNoteId(event)).orElseThrow();
    }

    protected String getPatientName(List<Client> clients) {
        return clients.stream().map(this::fetchClientName)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(", "));
    }

    protected String fetchClientName(Client client) {
        return ClientUtils.getInitials(client, " ");
    }

    protected String buildPatientName(Event event) {
        var clients = loadNotificationClients(event);
        var patientName = getPatientName(clients);

        if (StringUtils.isNotEmpty(patientName)) {
            patientName = "for " + patientName;
        }

        return patientName;
    }
}
