package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.dao.carecoordination.*;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserMobileNotificationPreferencesDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.entity.phr.UserMobileNotificationPreferences;
import com.scnsoft.eldermark.entity.phr.UserResidentRecord;
import com.scnsoft.eldermark.services.direct.MailAccountDetailsFactory;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.services.phr.EventActivityService;
import com.scnsoft.eldermark.services.phr.UserService;
import com.scnsoft.eldermark.shared.carecoordination.CareTeamRoleDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventNotificationDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author averazub
 * @author mradzivonenka
 * @author Netkachev
 * @author phomal
 * @author pzhurba
 * Created by pzhurba on 28-Sep-15.
 */
@Service
public class EventNotificationServiceImpl implements EventNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(EventNotificationServiceImpl.class);

    @Autowired
    private EventNotificationDao eventNotificationDao;
    @Autowired
    private CareTeamService careTeamService;

    @Autowired
    private CareTeamMemberNotificationPreferencesDao careTeamMemberNotificationPreferencesDao;

    @Autowired
    private UserMobileNotificationPreferencesDao userMobileNotificationPreferencesDao;

    @Autowired
    private MailAccountDetailsFactory mailAccountDetailsFactory;

    @Autowired
    private EventNotificationProcessService eventNotificationProcessService;

    @Autowired
    private CareTeamRoleDao careTeamRoleDao;

    @Autowired
    private CareTeamRoleService careTeamRoleService;

    @Autowired
    private UserResidentRecordsDao userResidentRecordsDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Autowired
    private EventActivityService activityService;

    @Autowired
    private MPIService mpiService;

    @Autowired
    private EventNotificationJpaDao eventNotificationJpaDao;

    @Value("${portal.url}")
    private String portalUrl;

    @Value("${google.api.key}")
    private String googleApiKey;

    @Value("${bitly.token}")
    private String bitlyToken;

    @Value("${bitly.group.id}")
    private String bitlyGroupId;

    @Value("${bitly.shorten.url}")
    private String bitlyUrl;

    private static final String EVENT_NOTIFICATION_CONTENT_TEMPLATE = "A new %s event has been logged to the Simply Connect platform %s %s in %s and you are %s for this type of event.";
    private static final String NOTE_NOTIFICATION_CONTENT_TEMPLATE = "A note has been %s the Simply Connect system %s and you are %s for this type of event.";
    private static final String SMS_NOTE_NOTIFICATION_CONTENT_TEMPLATE = "A note has been %s the Simply Connect system %s %s.";
    private static final String NOTE_EDIT = "NOTEEDIT";
    private static final String NOTE_ADD = "NOTEADD";

    @Override
    public Page<EventNotificationDto> getEventNotifications(final Long eventId, Pageable pageRequest, Boolean isSend) {
        final List<EventNotificationDto> result = new ArrayList<EventNotificationDto>();

        for (EventNotification eventNotification : eventNotificationDao.listByEventId(eventId, pageRequest, isSend)) {
//            result.add(createEventNotificationDto(eventNotification));
            addNotification(eventNotification, result);
        }
        return new PageImpl<EventNotificationDto>(result, pageRequest, eventNotificationDao.countByEventId(eventId, isSend));
    }

    private void addNotification(EventNotification eventNotification, List<EventNotificationDto> result) {
        for (EventNotificationDto notificationDto : result) {
            if (eventNotification.getEmployee() != null && eventNotification.getEmployee().getId().equals(notificationDto.getContactId()) && eventNotification.getCareTeamRole() != null && notificationDto.getCareTeamRole().equals(eventNotification.getCareTeamRole().getName())) {
                notificationDto.setNotificationText(notificationDto.getNotificationText() + ", " + eventNotification.getNotificationType().getDescription());
                notificationDto.setSentToText(notificationDto.getSentToText() + ", " + eventNotification.getNotificationType().getDescription() + " sent" +
                        (eventNotification.getNotificationType().equals(NotificationType.PUSH_NOTIFICATION) ? "" : " to " + eventNotification.getDestination()));
                return;
            }
        }
        result.add(createEventNotificationDto(eventNotification));
    }

    public void createNotifications(Event eventEntity, EventDto eventDetails) {
        List<EventNotification> notifications = createNotificationEntries(eventEntity);
        processNotifications(notifications, eventDetails);
    }

    public void processNotifications(List<EventNotification> notifications, EventDto eventDetails) {
        for (EventNotification eventNotification : notifications) {
            if (eventNotification.getSentDatetime() == null) {
                eventNotificationProcessService.processNotification(eventNotification, eventDetails);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createNotificationsForAdmin(EventNotification employeeNotification) {
        logger.info("Create Admin Notifications for notification : " + employeeNotification.getId());
        try {
            Long databaseId = employeeNotification.getEvent().getResident().getDatabaseId();
            Long employeeId = employeeNotification.getEmployee().getId();
            List<Employee> eventEmployees = careTeamRoleDao.getEmployeesForCompanyByRoleNotEqualToEmployee(databaseId, CareTeamRoleCode.ROLE_CASE_MANAGER, employeeId);
            if (CollectionUtils.isEmpty(eventEmployees)) {
                eventEmployees = careTeamRoleDao.getEmployeesForCompanyByRoleNotEqualToEmployee(databaseId, CareTeamRoleCode.ROLE_CARE_COORDINATOR, employeeId);
            }
            if (CollectionUtils.isEmpty(eventEmployees)) {
                eventEmployees = careTeamRoleDao.getEmployeesForCompanyByRoleNotEqualToEmployee(databaseId, CareTeamRoleCode.ROLE_ADMINISTRATOR, employeeId);
            }
            if (CollectionUtils.isEmpty(eventEmployees)) {
                eventEmployees = careTeamRoleDao.getSuperAdministrators();
            }
            if (CollectionUtils.isNotEmpty(eventEmployees)) {
                for (Employee adminEmployee : eventEmployees) {
                    EventNotification adminNotification = copyNotificationForEmployee(employeeNotification, adminEmployee);
                    eventNotificationDao.create(adminNotification);
                }
                eventNotificationDao.flush();
            }
        } catch (Exception e) {
            logger.error("Error creating notification", e);
        }
    }

    @Transactional//(propagation = Propagation.REQUIRES_NEW)
    public List<EventNotification> createNotificationEntries(Event eventEntity) {
        logger.info("Create Notifications for event ID: " + eventEntity.getId());
        List<EventNotification> eventNotifications = new ArrayList<>();
        try {
            // 1. create notifications for care team members including members form one care team for merged residents
            Long eventResidentId = eventEntity.getResident().getId();
            List<Long> mergedResidentIds = mpiService.listMergedResidents(eventResidentId);
            Set<Long> residentIds = new HashSet<>();
            residentIds.add(eventResidentId);
            if (!CollectionUtils.isEmpty(mergedResidentIds)) {
                residentIds.addAll(mergedResidentIds);
            }
            List<CareTeamMember> careTeamMembers = careTeamService.getCareTeamMembersAvailableToReceiveEventNotificationsForPatient(eventEntity.getResident(), residentIds);
            for (CareTeamMember careTeamMember : careTeamMembers) {
                for (NotificationPreferences np : careTeamMemberNotificationPreferencesDao.getNotificationPreferences(careTeamMember.getId(), eventEntity.getEventType())) {
                    if (Responsibility.N.equals(np.getResponsibility())) {
                        continue;
                    }

                    try {
                        EventNotification eventNotification = null;
                        if (NotificationType.PUSH_NOTIFICATION.equals(np.getNotificationType())) {
                            final List<User> users = userDao.getAllByEmployee(careTeamMember.getEmployee());
                            if (CollectionUtils.isEmpty(users)) {
                                continue;
                            }

                            for (User user : users) {
                                eventNotification = prepareNewEventNotification(eventEntity, careTeamMember, np);
                                eventNotification.setDestination(getDestinationByTypeAndUser(NotificationType.PUSH_NOTIFICATION, user));
                                eventNotification = eventNotificationDao.create(eventNotification);
                                eventNotifications.add(eventNotification);
                            }
                        } else {
                            eventNotification = prepareNewEventNotification(eventEntity, careTeamMember, np);
                            eventNotification.setDestination(getDestinationByTypeAndEmployee(np.getNotificationType(), careTeamMember.getEmployee()));
                            eventNotification = eventNotificationDao.create(eventNotification);
                            eventNotifications.add(eventNotification);
                        }

                        if (eventNotification != null) {
                            activityService.logEventActivity(eventNotification);
                        }
                    } catch (Exception exc) {
                        // do not break the loop if any of the notifications fails, log the problem, and proceed with other notifications
                        logger.error("Error creating notification for care team member", exc);
                    }
                }
            }

            // 2. create notifications for patients
            final List<UserResidentRecord> simpleUserResidentRecords = userResidentRecordsDao.getAllByResidentId(eventEntity.getResident().getId());
            final List<User> usersByResident = userDao.findUsersByResidentId(eventEntity.getResident().getId());
            final Set<User> users = new HashSet<>(usersByResident);
            for (UserResidentRecord userResidentRecord : simpleUserResidentRecords) {
                users.add(userResidentRecord.getUser());
            }

            for (User user : users) {
                if (!userService.isActiveMobileUser(user.getId())) {
                    // if user has never completed registration then he shouldn't receive any notifications
                    continue;
                }

                List<UserMobileNotificationPreferences> notificationPreferences = userMobileNotificationPreferencesDao.getByUserAndEventType(user, eventEntity.getEventType());
                for (NotificationPreferences np : notificationPreferences) {
                    if (Responsibility.N.equals(np.getResponsibility())) {
                        continue;
                    }

                    try {
                        EventNotification eventNotification = new EventNotification();
                        eventNotification.setCareTeamRole(null);
                        eventNotification.setDescription(null);
                        eventNotification.setEmployee(user.getEmployee());
                        eventNotification.setCreatedDatetime(new Date());
                        eventNotification.setEvent(eventEntity);
                        eventNotification.setNotificationType(np.getNotificationType());
                        eventNotification.setResponsibility(np.getResponsibility());
                        eventNotification.setContent(generateContent(eventEntity, np));
                        eventNotification.setUserPatient(user);
                        eventNotification.setPersonName(user.getResidentFullNameLegacy());

                        eventNotification.setDestination(getDestinationByTypeAndUser(np.getNotificationType(), user));

                        eventNotifications.add(eventNotificationDao.create(eventNotification));
                    } catch (Exception exc) {
                        // do not break the loop if any of the notifications fails, log the problem, and proceed with other notifications
                        logger.error("Error creating notification for patient", exc);
                    }
                }
            }

            eventNotificationDao.flush();
        } catch (Exception e) {
            logger.error("Error creating notification", e);
        }
        return eventNotifications;
    }

    private EventNotification prepareNewEventNotification(Event eventEntity, CareTeamMember careTeamMember, NotificationPreferences np) throws ParseException {
        EventNotification eventNotification = new EventNotification();
        eventNotification.setCareTeamRole(careTeamMember.getCareTeamRole());
        eventNotification.setCreatedDatetime(new Date());
        eventNotification.setDescription(careTeamMember.getDescription());
        eventNotification.setEmployee(careTeamMember.getEmployee());
        eventNotification.setEvent(eventEntity);
        eventNotification.setNotificationType(np.getNotificationType());
        eventNotification.setResponsibility(np.getResponsibility());
        eventNotification.setContent(generateContent(eventEntity, np));
        eventNotification.setUserPatient(null);
        eventNotification.setPersonName(careTeamMember.getEmployee().getFullName());
        return eventNotification;
    }

    private String getDestinationByTypeAndEmployee(NotificationType notificationType, Employee employee) {
        String result = "";
        Person person = employee.getPerson();
        switch (notificationType) {
            case EMAIL: {
                for (final PersonTelecom telecom : person.getTelecoms()) {
                    if (telecom.getUseCode().equals(PersonTelecomCode.EMAIL.toString())) {
                        result = telecom.getValue();
                        break;
                    }
                }
                break;
            }
            case SMS: {
                String mobilePhone = "";
                String workPhone = "";

                for (final PersonTelecom telecom : person.getTelecoms()) {
                    if (telecom.getUseCode().equals(PersonTelecomCode.WP.toString())) {
                        workPhone = telecom.getValue();
                    }
                    if (telecom.getUseCode().equals(PersonTelecomCode.MC.toString())) {
                        mobilePhone = telecom.getValue();
                    }
                }

                if (StringUtils.isNotEmpty(mobilePhone)) {
                    result = mobilePhone;
                } else {
                    result = workPhone;
                }

                break;
            }
            case FAX: {
                for (final PersonTelecom telecom : person.getTelecoms()) {
                    if (telecom.getUseCode().equals(PersonTelecomCode.FAX.toString())) {
                        result = telecom.getValue();
                        break;
                    }
                }
                break;
            }
            case SECURITY_MESSAGE: {
                result = mailAccountDetailsFactory.createMailAccountDetails(employee).getSecureEmail();
                break;
            }
            case BLUE_STONE: {
                result = "";
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown Event Notification Type: " + notificationType);
            }
        }
        return result;
    }

    private String getDestinationByTypeAndUser(NotificationType notificationType, User user) {
        switch (notificationType) {
            case EMAIL: {
                return user.getResidentEmailLegacy();
            }
            case SMS: {
                return user.getResidentPhoneLegacy();
            }
            case PUSH_NOTIFICATION: {
                return String.valueOf(user.getId());
            }
            case SECURITY_MESSAGE: {
                if (user.getEmployee() != null) {
                    return mailAccountDetailsFactory.createMailAccountDetails(user.getEmployee()).getSecureEmail();
                }
                return "";
            }
            default: {
                throw new IllegalArgumentException("Unsupported Event Notification Type: " + notificationType.toString());
            }
        }
    }

    private EventNotificationDto createEventNotificationDto(EventNotification eventNotification) {
        final EventNotificationDto eventNotificationDto = new EventNotificationDto();

        eventNotificationDto.setDateTime(eventNotification.getCreatedDatetime());
        eventNotificationDto.setNotificationType(eventNotification.getNotificationType().toString());
        eventNotificationDto.setNotificationText(eventNotification.getNotificationType().getDescription());
        eventNotificationDto.setContactName(eventNotification.getPersonName());
        if (eventNotification.getEmployee() != null) {
            eventNotificationDto.setContactId(eventNotification.getEmployee().getId());
            eventNotificationDto.setOrganization(eventNotification.getEmployee().getDatabase().getName());
        } else {
            if (eventNotification.getUserPatient() != null) {
                eventNotificationDto.setOrganization(eventNotification.getUserPatient().getDatabase().getName());
            }
        }
        if (eventNotification.getCareTeamRole() != null) {
            eventNotificationDto.setCareTeamRole(eventNotification.getCareTeamRole().getName());
        } else {
            CareTeamRoleDto role = careTeamRoleService.get(CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES);
            eventNotificationDto.setCareTeamRole(role.getLabel());
        }
        eventNotificationDto.setDescription(eventNotification.getDescription());
        eventNotificationDto.setResponsibility(eventNotification.getResponsibility().getDescription());
        String eventType = eventNotification.getEvent().getEventType().getDescription();
        String communityName = eventNotification.getEvent().getResident().getFacility().getName();
        String patientInitials = eventNotificationProcessService.patientInitials(eventNotification.getEvent().getResident(), eventNotification.getNotificationType());
        eventNotificationDto.setDetails(String.format(EVENT_NOTIFICATION_CONTENT_TEMPLATE, eventType, "", patientInitials, communityName, eventNotificationDto.getResponsibility()));
//        eventNotificationDto.setDestination(eventNotification.getDestination());
        eventNotificationDto.setSentToText(eventNotificationDto.getNotificationText() + " sent" +
                (eventNotification.getNotificationType().equals(NotificationType.PUSH_NOTIFICATION) ? "" : " to " + eventNotification.getDestination()));

        return eventNotificationDto;
    }

    // TODO
    private String generateContent(Event event, final NotificationPreferences notificationPreferences) throws ParseException {
        switch (notificationPreferences.getNotificationType()) {
            case BLUE_STONE: {
                return event.getEventContent();
            }
            // intentional fall through
            case PUSH_NOTIFICATION:
            case FAX:
            case SECURITY_MESSAGE:
            case EMAIL:
                return createNotification(event, notificationPreferences.getNotificationType(), notificationPreferences.getResponsibility(), event.getEventType().getDescription());
            case SMS: {
                return createNotification(event, notificationPreferences.getNotificationType(), notificationPreferences.getResponsibility(), event.getEventType().getEventGroup().getName());
            }
        }
        return "";
    }

    private String createNotification(Event event, NotificationType notificationType, Responsibility responsibility, String eventParam) throws ParseException {
        String action = "logged to";
        String format_str;
        String eventText = responsibility.getDescription();
        if ((NOTE_ADD.equals(event.getEventType().getCode()) || NOTE_EDIT.equals(event.getEventType().getCode()))) {
            if (NotificationType.SMS.equals(notificationType)) {
                format_str = SMS_NOTE_NOTIFICATION_CONTENT_TEMPLATE;
                JSONParser parser = new org.json.simple.parser.JSONParser();
                JSONObject obj = (JSONObject) parser.parse(event.getSituation());
                eventText = getShortUrl(portalUrl +
                        "?startPage=care-coordination/patients&note=" + obj.get("noteId") + "&patient=" + obj.get("patientId"));
            } else {
                format_str = NOTE_NOTIFICATION_CONTENT_TEMPLATE;
            }
        } else {
            if (NotificationType.SMS.equals(notificationType)) {
                eventText = getShortUrl(portalUrl +
                        "?startPage=care-coordination/events-log&id=" + event.getId() + "&orgId=" + event.getResident().getDatabaseId());
            } else {
                eventText = "";
            }
            format_str = EVENT_NOTIFICATION_CONTENT_TEMPLATE;
            String communityName = event.getResident().getFacility().getName();
            return String.format(format_str, eventParam, eventText, eventNotificationProcessService.patientInitials(event.getResident(), notificationType), communityName, responsibility.getDescription());
        }
        if (NOTE_ADD.equals(event.getEventType().getCode())) {
            action = "added to";
        }
        if (NOTE_EDIT.equals(event.getEventType().getCode())) {
            action = "updated in";
        }
        return String.format(format_str, action, eventNotificationProcessService.patientInitials(event.getResident(), notificationType), eventText);
    }

    private EventNotification copyNotificationForEmployee(EventNotification employeeNotification, Employee newEmployee) {
        EventNotification eventNotification = new EventNotification();
        eventNotification.setCareTeamRole(newEmployee.getCareTeamRole());
        eventNotification.setContent(employeeNotification.getContent());
        eventNotification.setCreatedDatetime(new Date());
        eventNotification.setDescription(employeeNotification.getDescription());
        eventNotification.setDestination(getDestinationByTypeAndEmployee(employeeNotification.getNotificationType(), newEmployee));
        eventNotification.setEmployee(newEmployee);
        eventNotification.setPersonName(newEmployee.getFullName());
        eventNotification.setEvent(employeeNotification.getEvent());
        eventNotification.setNotificationType(employeeNotification.getNotificationType());
        eventNotification.setResponsibility(employeeNotification.getResponsibility());
        return eventNotification;
    }

    private String getShortUrl(String longUrl) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(bitlyUrl);
        try {
            post.addHeader("Content-Type", "application/json");
            post.addHeader("Authorization", bitlyToken);

            JSONObject json = new JSONObject();
            //Bitly doesn't support url's like localhost:8080, so local URL's will not work
            json.put("long_url",longUrl);
            json.put("group_guid",bitlyGroupId);
            StringEntity params = new StringEntity(json.toJSONString());
            params.setContentType("application/json");
            post.setEntity(params);

            logger.info("Sending request to bitly");
            HttpResponse response = httpClient.execute(post);
            if (HttpStatus.valueOf(response.getStatusLine().getStatusCode()).series()  == HttpStatus.Series.SUCCESSFUL) {
                logger.info("Bitly request succeded");
                String responseStr = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                JSONParser parser = new org.json.simple.parser.JSONParser();
                JSONObject obj = (JSONObject) parser.parse(responseStr);
                return (String)obj.get("link");
            }
            return portalUrl;
        } catch (Exception e) {
            logger.error("Error communication with Bitly services", e);
            return portalUrl;
        } finally {
            post.releaseConnection();
        }
    }


}
