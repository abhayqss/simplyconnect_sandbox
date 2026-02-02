package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.dao.phr.MobileUserClientRecordDao;
import com.scnsoft.eldermark.dao.phr.MobileUserDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamMemberNotificationPreferences;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.document.ClientDocument;
import com.scnsoft.eldermark.entity.event.*;
import com.scnsoft.eldermark.entity.note.Note;
import com.scnsoft.eldermark.entity.note.NoteType;
import com.scnsoft.eldermark.entity.phr.MobileUser;
import com.scnsoft.eldermark.entity.phr.MobileUserClientRecord;
import com.scnsoft.eldermark.entity.phr.MobileUserNotificationPreferences;
import com.scnsoft.eldermark.service.notification.factory.EventNotificationFactory;
import com.scnsoft.eldermark.service.notification.sender.EventNotificationSender;
import com.scnsoft.eldermark.service.phr.MobileUserService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.EventNotificationUtils;
import com.scnsoft.eldermark.util.StreamUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class EventNotificationServiceImpl implements EventNotificationService {

    private final Logger logger = LoggerFactory.getLogger(EventNotificationServiceImpl.class);

    private final List<Responsibility> NOT_SENDABLE_RESPONSIBILITIES = Arrays.asList(Responsibility.V, Responsibility.N);

    @Autowired
    private NoteDao noteDao;

    @Autowired
    private EventNotificationDao eventNotificationDao;

    @Autowired
    private GroupedEventNotificationDao groupedEventNotificationDao;

    @Autowired
    private CareTeamMemberService careTeamMemberService;

    @Autowired
    private MobileUserDao mobileUserDao;

    @Autowired
    private MobileUserClientRecordDao mobileUserClientRecordDao;

    @Autowired
    private MobileUserService mobileUserService;

    @Autowired
    private EventDao eventDao;

    @Autowired
    private EventTypeDao eventTypeDao;

    private final Map<NotificationType, EventNotificationSender> notificationSenders;
    private final Map<NotificationType, EventNotificationFactory> notificationFactories;

    @Autowired
    public EventNotificationServiceImpl(List<EventNotificationSender> sendersList, List<EventNotificationFactory> factoryList) {
        notificationSenders = sendersList
                .stream()
                .collect(Collectors.toMap(
                        EventNotificationSender::supportedNotificationType,
                        Function.identity())
                );

        notificationFactories = factoryList
                .stream()
                .collect(Collectors.toMap(
                        EventNotificationFactory::supportedNotificationType,
                        Function.identity())
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GroupedEventNotification> find(Long eventId, Pageable pageable) {
        return groupedEventNotificationDao.findAllByEvent_Id(eventId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventNotificationMessage> find(Long eventId, Long employeeId, Long careTeamRoleId) {
        return eventNotificationDao.findAllByEvent_IdAndEmployee_IdAndCareTeamRole_Id(eventId, employeeId, careTeamRoleId, EventNotificationMessage.class);
    }

    @Override
    public void send(Event savedEvent) {
        var notifications = createNotifications(savedEvent);
        sendNotifications(notifications);
    }

    @Override
    public void send(Note note) {
        var event = createEventForNoteNotification(note);
        send(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Long count(Long eventId) {
        return groupedEventNotificationDao.countAllByEvent_Id(eventId);
    }

    @Override
    public void sendNewMAP(ClientDocument mapDocument) {
        var event = createEventForMapNotification(mapDocument);
        send(event);
    }

    private Event createEventForNoteNotification(Note note) {
        var event = new Event();

        event.setClient(note.getNoteClients().get(0));

        var employee = note.getEmployee();
        var author = new EventAuthor();

        author.setFirstName(employee.getFirstName());
        author.setLastName(employee.getLastName());
        author.setOrganization(employee.getOrganization().getName());
        author.setRole(employee.getCareTeamRole().getName());
        event.setEventAuthor(author);

        event.setEventDateTime(Instant.now());
        event.setSituation(note.getId().toString());
        event.setEventType(getEventType(note));

        event.setManual(false);

        //set event content xml for bluestone if needed

        return eventDao.save(event);
    }

    private EventType getEventType(Note note) {
        switch (note.getAuditableStatus()) {
            case CREATED:
                return eventTypeDao.getByCode("NOTEADD");
            case UPDATED:
                return eventTypeDao.getByCode("NOTEEDIT");
        }
        return null;
    }

    private Event createEventForMapNotification(ClientDocument document) {
        var event = new Event();

        event.setClient(document.getClient());

        var employee = document.getAuthor();
        var author = new EventAuthor();

        author.setFirstName(employee.getFirstName());
        author.setLastName(employee.getLastName());
        author.setOrganization(employee.getOrganization().getName());
        author.setRole(employee.getCareTeamRole().getName());
        event.setEventAuthor(author);

        event.setEventDateTime(Instant.now());
        event.setMapDocumentId(document.getId());
        event.setEventType(eventTypeDao.getByCode(EventNotificationUtils.MAP_CREATED));

        //save document id in case document is deleted from DB and, therefore, mapDocumentId is set to null
        event.setSituation(document.getId().toString());

        event.setManual(false);

        //set event content xml for bluestone if needed

        return eventDao.save(event);
    }

    private List<EventNotification> createNotifications(Event event) {
        logger.info("Create Notifications for event ID: " + event.getId());

        if (!isActiveClient(event)) {
            return Collections.emptyList();
        }

        return Stream.concat(createCareTeamNotifications(event), createMobilePatientsNotifications(event))
                .collect(Collectors.toList());

    }

    private boolean isActiveClient(Event event) {
        if (loadNotificationClients(event).stream().noneMatch(client -> BooleanUtils.isTrue(event.getClient().getActive()))) {
            logger.info("Client is inactive, no notifications will be sent");
            return false;
        }

        return true;
    }

    private Stream<EventNotification> createCareTeamNotifications(Event event) {
        var fullCareTeam = careTeamMemberService.getFullCareTeam(loadNotificationClients(event));

        return fullCareTeam
                .stream()
                .flatMap(careTeamMember -> careTeamMember.getNotificationPreferences().stream())
                .filter(np -> isShouldSendNotification(event, np))
                .filter(StreamUtils.distinctByKey(np -> new Pair<>(np.getCareTeamMember().getEmployee().getId(), np.getNotificationType())))
                .map(np -> createNotification(event, np))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    private Stream<EventNotification> createMobilePatientsNotifications(Event event) {
        var clients = loadNotificationClients(event);
        final List<MobileUserClientRecord> simpleUserClientRecords = mobileUserClientRecordDao.getAllByClientIn(clients);

        var users = simpleUserClientRecords
                .stream()
                .map(MobileUserClientRecord::getMobileUser);

        var usersByClient = mobileUserDao.findAllByClientIn(clients).stream();

        var allDistinctUsers = Stream.concat(users, usersByClient)
                .filter(StreamUtils.distinctByKey(MobileUser::getId));

        return allDistinctUsers
                .filter(mobileUserService::isActiveMobileUser)
                .flatMap(user -> user.getMobileUserNotificationPreferencesList().stream())
                .filter(np -> isShouldSendNotification(event, np))
                .map(np -> createNotification(event, np))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    private boolean isShouldSendNotification(Event event, NotificationPreferences np) {
        if (NOT_SENDABLE_RESPONSIBILITIES.contains(np.getResponsibility())) {
            return false;
        }

        if (!event.getEventType().getId().equals(np.getEventType().getId())) {
            return false;
        }

        if (NotificationType.PUSH_NOTIFICATION.equals(np.getNotificationType())
                && EventNotificationUtils.isNoteNotification(event.getEventType())
                && NoteType.GROUP_NOTE.equals(loadNote(event).getType())) {
            logger.info("Push Notifications for group notes are not supported");
            return false;
        }

        if (np instanceof CareTeamMemberNotificationPreferences) {
            var careTeamMember = ((CareTeamMemberNotificationPreferences) np).getCareTeamMember();

            if (!EmployeeStatus.ACTIVE.equals(careTeamMember.getEmployee().getStatus())) {
                return false;
            }

            if (careTeamMember instanceof ClientCareTeamMember) {
                var clientCtm = (ClientCareTeamMember) careTeamMember;

                if (clientCtm.getAccessRights()
                        .stream()
                        .map(AccessRight::getCode)
                        .noneMatch(AccessRight.Code.EVENT_NOTIFICATIONS::equals)) {
                    return false;
                }
            }

            if (NotificationType.PUSH_NOTIFICATION.equals(np.getNotificationType()) && !mobileUserDao.existsByEmployee(careTeamMember.getEmployee())) {
                return false;
            }

            if (NotificationType.FAX.equals(np.getNotificationType()) && PersonTelecomUtils.findValue(careTeamMember.getEmployee().getPerson(), PersonTelecomCode.FAX).isEmpty()) {
                return false;
            }
        }

        if (np instanceof MobileUserNotificationPreferences) {
            var employee = ((MobileUserNotificationPreferences) np).getMobileUser().getEmployee();

            if (employee != null && !EmployeeStatus.ACTIVE.equals(employee.getStatus())) {
                return false;
            }
        }

        return true;
    }


    private List<Client> loadNotificationClients(Event event) {
        if (EventNotificationUtils.isNoteNotification(event.getEventType())) {
            return loadNote(event).getNoteClients();
        }
        return Collections.singletonList(event.getClient());
    }

    private Note loadNote(Event event) {
        return noteDao.findById(EventNotificationUtils.extractNotificationNoteId(event)).orElseThrow();
    }

    private Optional<EventNotification> createNotification(Event event, NotificationPreferences np) {
        return notificationFactories.get(np.getNotificationType()).createNotification(event, np);
    }

    private void sendNotifications(List<EventNotification> notifications) {
        notifications.forEach(this::sendNotification);
    }

    private void sendNotification(EventNotification notification) {
        logger.info("Available notification senders - [{}] ", Optional.ofNullable(notificationSenders).map(map -> map.keySet().size()).orElse(0));
        notificationSenders.get(notification.getNotificationType()).send(notification.getId());
    }
}
