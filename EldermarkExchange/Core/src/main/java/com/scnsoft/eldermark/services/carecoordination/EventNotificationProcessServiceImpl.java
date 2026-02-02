package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.dao.DatabasesDao;
import com.scnsoft.eldermark.dao.carecoordination.AdtMessageDao;
import com.scnsoft.eldermark.dao.carecoordination.EventNotificationDao;
import com.scnsoft.eldermark.dao.carecoordination.NotificationType;
import com.scnsoft.eldermark.dao.carecoordination.Responsibility;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.PushNotificationRegistration;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.facades.DirectMessagesFacade;
import com.scnsoft.eldermark.facades.carecoordination.PatientFacade;
import com.scnsoft.eldermark.services.PersonService;
import com.scnsoft.eldermark.services.bluestone.BlueStoneService;
import com.scnsoft.eldermark.services.direct.DirectAccountDetails;
import com.scnsoft.eldermark.services.direct.DirectAttachment;
import com.scnsoft.eldermark.services.direct.MailAccountDetailsFactory;
import com.scnsoft.eldermark.services.fax.EventFaxContentGenerator;
import com.scnsoft.eldermark.services.fax.FaxService;
import com.scnsoft.eldermark.services.fax.NoteFaxContentGenerator;
import com.scnsoft.eldermark.services.mail.ExchangeMailService;
import com.scnsoft.eldermark.services.pushnotifications.PushNotificationService;
import com.scnsoft.eldermark.services.pushnotifications.PushNotificationType;
import com.scnsoft.eldermark.services.sms.SmsService;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteDto;
import com.scnsoft.eldermark.shared.carecoordination.service.FaxDto;
import com.scnsoft.eldermark.shared.carecoordination.service.NoteNotificationDto;
import com.scnsoft.eldermark.shared.carecoordination.service.NotificationDto;
import com.scnsoft.eldermark.shared.phr.PushNotificationVO;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;

import static java.lang.String.format;

/**
 * Event notifications processing service
 * <p>
 * Created by pzhurba on 02-Dec-15.
 */
@Service
public class EventNotificationProcessServiceImpl implements EventNotificationProcessService {
    private static final Logger logger = LoggerFactory.getLogger(EventNotificationProcessServiceImpl.class);

    private static final String ADD_NOTE_EVENT_TYPE_CODE = "NOTEADD";
    private static final String EDIT_NOTE_EVENT_TYPE_CODE = "NOTEEDIT";

    @Value("${portal.url}")
    private String portalUrl;

    @Autowired
    private ExchangeMailService mailService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private DirectMessagesFacade directMessagesFacade;
    @Autowired
    private BlueStoneService blueStoneService;
    @Autowired
    private MailAccountDetailsFactory mailAccountDetailsFactory;
    @Autowired
    private EventNotificationDao eventNotificationDao;
    @Autowired
    private FaxService faxService;
    @Autowired
    private PushNotificationService pushNotificationService;

    @Autowired
    private DatabasesDao databasesDao;
    @Autowired
    private AdtMessageDao adtMessageDao;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    PatientFacade patientFacade;

    @Autowired
    private NoteDetailsService noteDetailsService;

    @Autowired
    private NoteFaxContentGenerator noteFaxContentGenerator;

    @Autowired
    private EventFaxContentGenerator eventFaxContentGenerator;

    @Autowired
    private EventService eventService;

    @Async
    @Transactional
    public void processNotification(final EventNotification notification, EventDto eventDto) {
        logger.debug("CurrentThread : " + Thread.currentThread().getId());

        Database eventDatabase = databasesDao.getDatabaseById(notification.getEvent().getResident().getDatabaseId());
        Boolean copyEventNotificationsForPatients = eventDatabase.getCopyEventNotificationsForPatients();
        Boolean sendToContact = true;
        if (Responsibility.V.equals(notification.getResponsibility())) {
            sendToContact = false;
        }
        try {
            if (sendToContact) {
                Future<Boolean> result = new AsyncResult<Boolean>(false);
                switch (notification.getNotificationType()) {
                    case BLUE_STONE: {
                        result = blueStoneService.sendBlueStoneNotification(notification.getContent());
                        break;
                    }
                    case SECURITY_MESSAGE: {
                        try {
                            DirectAccountDetails companyAccount = mailAccountDetailsFactory.createRootAccountDetails(notification.getEmployee());
                            if (ADD_NOTE_EVENT_TYPE_CODE.equals(notification.getEvent().getEventType().getCode())) {
                                final NoteNotificationDto noteNotificationDto = createNoteNotificationDto(notification);
                                noteNotificationDto.setAction("added to");
                                directMessagesFacade.sendMessage(notification.getDestination(),
                                        "A note has been added to the Simply Connect platform " + patientInitials(notification.getEvent().getResident(), notification.getNotificationType()),
                                        createDirectMessageNoteBody(notification, noteNotificationDto),
                                        new ArrayList<DirectAttachment>(),
                                        companyAccount);
                            } else if (EDIT_NOTE_EVENT_TYPE_CODE.equals(notification.getEvent().getEventType().getCode())) {
                                final NoteNotificationDto noteNotificationDto = createNoteNotificationDto(notification);
                                noteNotificationDto.setAction("updated in");
                                directMessagesFacade.sendMessage(notification.getDestination(),
                                        "A note has been updated in the Simply Connect platform " + patientInitials(notification.getEvent().getResident(), notification.getNotificationType()),
                                        createDirectMessageNoteBody(notification, noteNotificationDto),
                                        new ArrayList<DirectAttachment>(), companyAccount);

                            } else {
                                String initials =  patientInitials(notification.getEvent().getResident() , notification.getNotificationType());
                                String eventGroup = notification.getEvent().getEventType().getEventGroup().getName();
                                String subject = format("A new %s event has been logged to the Simply Connect platform %s", eventGroup, initials);
                                directMessagesFacade.sendMessage(notification.getDestination(), subject, createDirectMessageBody(notification), new ArrayList<DirectAttachment>(), companyAccount);
                            }
                            result = new AsyncResult<Boolean>(true);

                            break;
                        } catch (Exception e) {
                            logger.error("Error sending security message", e);
                        }
                        break;
                    }
                    case PUSH_NOTIFICATION: {
                        result = pushNotificationService.send(createPushNotificationVO(notification, eventDto));
                        break;
                    }
                    case FAX: {
                        if (ADD_NOTE_EVENT_TYPE_CODE.equals(notification.getEvent().getEventType().getCode()) ||
                                EDIT_NOTE_EVENT_TYPE_CODE.equals(notification.getEvent().getEventType().getCode())) {
                            result = faxService.sendFax(createFaxDto(notification), fetchNoteDetails(notification), noteFaxContentGenerator);
                        } else {
                            result = faxService.sendFax(createFaxDto(notification), eventDto, eventFaxContentGenerator);
                        }
                        break;
                    }
                    case SMS: {
                        result = smsService.sendSmsNotification(notification.getDestination(), notification.getContent());
                        break;
                    }
                    case EMAIL: {
                        // TODO use another email notification template for patient
                        String initials = patientInitials(notification.getEvent().getResident(), notification.getNotificationType());

                        if (ADD_NOTE_EVENT_TYPE_CODE.equals(notification.getEvent().getEventType().getCode())) {
                            final NoteNotificationDto noteNotificationDto = createNoteNotificationDto(notification);
                            noteNotificationDto.setInitials(initials);
                            noteNotificationDto.setAction("added to");
                            result = mailService.sendNoteNotification(noteNotificationDto, initials);
                        } else if (EDIT_NOTE_EVENT_TYPE_CODE.equals(notification.getEvent().getEventType().getCode())) {
                            final NoteNotificationDto noteNotificationDto = createNoteNotificationDto(notification);
                            noteNotificationDto.setInitials(initials);
                            noteNotificationDto.setAction("updated in");
                            result = mailService.sendNoteNotification(noteNotificationDto, initials);
                        } else {
                            NotificationDto notificationDto = createNotificationDto(notification);
                            notificationDto.setInitials(initials);
                            notificationDto.setEventGroup(notification.getEvent().getEventType().getEventGroup().getName());
                            result = mailService.sendNotification(notificationDto, initials);
                        }
                        break;
                    }
                }

                while (!result.isDone()) {
                    Thread.sleep(1000);
                }
                if (result.get() != null && result.get()) {
                    synchronized (this) {
                        eventNotificationDao.updateDelivered(notification.getId());
                        eventNotificationDao.flush();
                    }
                }
            }
            if (BooleanUtils.isTrue(copyEventNotificationsForPatients) && eventDatabase.getAddressAndContacts() != null) {
                copyEventNotificationToOrganization(notification, eventDatabase.getAddressAndContacts());
            }

        } catch (Exception e) {
            logger.error("Error processing notification ", e);
        }
    }

    public String patientInitials(CareCoordinationResident resident, NotificationType notificationType){
        String initials = " for ";
        Boolean emptyInitials = true;
        if (NotificationType.SECURITY_MESSAGE.equals(notificationType)){
            if (resident.getFirstName() != null && !resident.getFirstName().isEmpty()) {
                initials = initials + resident.getFirstName() + " ";
                emptyInitials = false;
            }
            if (resident.getLastName() != null && !resident.getLastName().isEmpty()) {
                initials = initials + resident.getLastName();
                emptyInitials = false;
            }
        } else {
            if (resident.getFirstName() != null && !resident.getFirstName().isEmpty()) {
                initials = initials + resident.getFirstName().substring(0, 1) + " ";
                emptyInitials = false;
            }
            if (resident.getLastName() != null && !resident.getLastName().isEmpty()) {
                initials = initials + resident.getLastName().substring(0, 1);
                emptyInitials = false;
            }
        }
        if (emptyInitials) {
            return "";
        } else {
            return initials;
        }
    }

    private String createDirectMessageBody(EventNotification notification) {
        final Event event = notification.getEvent();
        AdtMessage adtMessage = null;
        if (event.getAdtMsgId() != null) {
            adtMessage = adtMessageDao.getOne(event.getAdtMsgId());
        }
        EventDto eventDto = eventService.createEventDto(notification.getEvent(),patientFacade.getPatientDto(event.getResident().getId(), false, false), adtMessage);
        final Map<String, Object> model = new HashMap<>();
        model.put("notification", notification);
        model.put("event", eventDto);
        model.put("portalUrl", portalUrl);
        model.put("eventId", event.getId());
        model.put("date", new DateTool());
        model.put("StringUtils", new StringUtils());
        return VelocityEngineUtils.mergeTemplateIntoString(
                velocityEngine, "velocity/newEventSecureMessage.vm", "UTF-8", model);
    }

    private void copyEventNotificationToOrganization(EventNotification notification, SourceDatabaseAddressAndContacts dbAddressAndContacts) throws InterruptedException, java.util.concurrent.ExecutionException {
        String organizationEmail = dbAddressAndContacts.getEmail();
        NotificationDto notificationDto = createNotificationDto(notification);
        notificationDto.setToEmail(organizationEmail);
        Future<Boolean> resultOrgNotification = mailService.sendNotification(notificationDto, patientInitials(notification.getEvent().getResident(), notification.getNotificationType()));
        while (!resultOrgNotification.isDone()) {
            Thread.sleep(1000);
        }
        if (resultOrgNotification.get() != null && resultOrgNotification.get()) {
            logger.info("Notification was copied to organization email. Notification id=" + notification.getId());
        }
    }

    private String createDirectMessageNoteBody(EventNotification notification, NoteNotificationDto noteNotificationDto) throws ParseException {
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("notification", noteNotificationDto);

        model.put("portalUrl", portalUrl);
        model.put("date", new DateTool());
        model.put("StringUtils", new StringUtils());
        model.put("patient", patientFacade.getPatientDto(notification.getEvent().getResident().getId(), false, false));


        model.put("note", fetchNoteDetails(notification));

        model.put("buttonUrl", portalUrl + "?startPage=care-coordination/patients&note=" +
                noteNotificationDto.getNoteId() + "&patient=" + noteNotificationDto.getPatientId());
        model.put("buttonLabel", "Login");

        return VelocityEngineUtils.mergeTemplateIntoString(
                velocityEngine, "velocity/noteSecureMessage.vm", "UTF-8", model);
    }

    private NoteDto fetchNoteDetails(EventNotification notification) throws ParseException {
        JSONParser parser = new org.json.simple.parser.JSONParser();
        JSONObject obj = (JSONObject) parser.parse(notification.getEvent().getSituation());
        return noteDetailsService.getNoteDetails((Long) obj.get("noteId"), true,0);
    }

    private NoteNotificationDto createNoteNotificationDto(final EventNotification eventNotification) throws ParseException {
        final NoteNotificationDto result = new NoteNotificationDto();
        fillNotificationDto(result, eventNotification);

        JSONParser parser = new org.json.simple.parser.JSONParser();
        JSONObject obj = (JSONObject) parser.parse(eventNotification.getEvent().getSituation());
        result.setNoteId((Long) obj.get("noteId"));
        result.setPatientId((Long) obj.get("patientId"));
        return result;
    }

    private NotificationDto createNotificationDto(final EventNotification eventNotification) {
        final NotificationDto result = new NotificationDto();
        fillNotificationDto(result, eventNotification);
        return result;
    }

    private void fillNotificationDto(NotificationDto result, EventNotification eventNotification) {
        String fullName = getFullName(eventNotification);
        result.setEventId(eventNotification.getEvent().getId());
        result.setUserName(fullName);
        result.setPortalUrl(portalUrl);
        result.setResponsibility(eventNotification.getResponsibility().getDescription());
        result.setToEmail(eventNotification.getDestination());
        result.setDatabaseId(eventNotification.getEvent().getResident().getDatabase().getId());
        result.setEventType(eventNotification.getEvent().getEventType().getDescription());
        result.setCommunityName(eventNotification.getEvent().getResident().getFacility().getName());
    }

    private String getFullName(EventNotification eventNotification) {
        return eventNotification.getPersonName();
    }

    private PushNotificationVO createPushNotificationVO(final EventNotification eventNotification, final EventDto eventDto) {
        final String destinationUserId = eventNotification.getDestination();
        final Long userId = Long.valueOf(destinationUserId);
        final Collection<String> tokens = pushNotificationService.getTokens(userId, PushNotificationRegistration.ServiceProvider.FCM);
        final Event event = eventNotification.getEvent();
        final Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("id", PushNotificationType.NEW_EVENT.getNotificationId());
        payload.put("userId", userId);
        payload.put("eventId", event.getId());
        payload.put("eventType", event.getEventType().getDescription());
        payload.put("eventDate", event.getEventDatetime().getTime());
        if (ADD_NOTE_EVENT_TYPE_CODE.equals(event.getEventType().getCode())
                || EDIT_NOTE_EVENT_TYPE_CODE.equals(event.getEventType().getCode())) {
            try {
                payload.put("noteId", ((JSONObject) new JSONParser().parse(event.getSituation())).get("noteId"));
                payload.put("noteType", ((JSONObject) new JSONParser().parse(event.getSituation())).get("noteType"));
            } catch (ParseException e) {
                logger.info(ExceptionUtils.getStackTrace(e));
            }

        }
        final PushNotificationVO result = new PushNotificationVO();
        result.setTokens(new ArrayList<>(tokens));
        if (ADD_NOTE_EVENT_TYPE_CODE.equals(eventNotification.getEvent().getEventType().getCode())) {
            result.setTitle("Note has been added" + patientInitials(eventNotification.getEvent().getResident(), NotificationType.PUSH_NOTIFICATION));
        } else if (EDIT_NOTE_EVENT_TYPE_CODE.equals(eventNotification.getEvent().getEventType().getCode())){
            result.setTitle("Note has been updated" + patientInitials(eventNotification.getEvent().getResident(), NotificationType.PUSH_NOTIFICATION));
        } else {
            String eventGroup = eventNotification.getEvent().getEventType().getEventGroup().getName();
            String patientInitials = patientInitials(eventNotification.getEvent().getResident(), NotificationType.PUSH_NOTIFICATION);
            result.setTitle(format("A new %s event has been logged %s", eventGroup, patientInitials));
        }
        result.setText(eventNotification.getContent());
        result.setPayload(payload);
        result.setServiceProvider(PushNotificationRegistration.ServiceProvider.FCM);
        return result;
    }

    private FaxDto createFaxDto(final EventNotification eventNotification) {
        final FaxDto result = new FaxDto();
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
        String fullName = getFullName(eventNotification);
        result.setTo(fullName);
        result.setFax(eventNotification.getDestination());
        if (eventNotification.getEmployee() != null) {
            result.setPhone(PersonService.getPersonTelecomValue(eventNotification.getEmployee().getPerson(), PersonTelecomCode.WP));
        }
        if (ADD_NOTE_EVENT_TYPE_CODE.equals(eventNotification.getEvent().getEventType().getCode())) {
            result.setSubject("A new note has been added to the Simply Connect system");
        } else if (EDIT_NOTE_EVENT_TYPE_CODE.equals(eventNotification.getEvent().getEventType().getCode())) {
            result.setSubject("A note has been updated in the Simply Connect system");
        } else {
            result.setSubject("A new event has been logged to the Simply Connect system");
        }
        result.setFrom("Simply Connect system");
        result.setDate(sdf.format(new Date()));
        result.setResponsibility(eventNotification.getResponsibility().getDescription());
        result.setUrl(portalUrl);

        /*
        if (eventNotification.getEvent().getResident().getFacility().getInterfaxConfiguration() != null) {
            final InterfaxConfiguration interfaxConfiguration = eventNotification.getEvent().getResident().getFacility().getInterfaxConfiguration();
            result.setFaxUserName(interfaxConfiguration.getUsername());
            result.setFaxPassword(interfaxConfiguration.getPassword());
        }
        */

        return result;
    }

}
