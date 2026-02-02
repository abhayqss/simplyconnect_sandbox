package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.EventReadStatusDao;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.carecoordination.EventDao;
import com.scnsoft.eldermark.dao.carecoordination.EventNotificationDao;
import com.scnsoft.eldermark.dao.carecoordination.NotificationType;
import com.scnsoft.eldermark.dao.carecoordination.ResidentCareTeamMemberDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.phr.AccountType;
import com.scnsoft.eldermark.entity.phr.EventReadStatus;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.dao.projections.EventAndReadCount;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.services.carecoordination.CareTeamRoleService;
import com.scnsoft.eldermark.services.carecoordination.EventService;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.services.phr.AccessRightsService;
import com.scnsoft.eldermark.shared.carecoordination.CareTeamRoleDto;
import com.scnsoft.eldermark.shared.carecoordination.EmployeeDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.PatientDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventFilterDto;
import com.scnsoft.eldermark.web.entity.*;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dozer.DozerBeanMapper;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author phomal
 * Created on 5/2/2017
 */
@Service
@Transactional
public class EventsService extends BasePhrService {

    Logger logger = Logger.getLogger(EventsService.class.getName());

    @Autowired
    EventService eventService;

    @Autowired
    ResidentDao residentDao;

    @Autowired
    MPIService mpiService;

    @Autowired
    EventDao eventDao;

    @Autowired
    EventNotificationDao eventNotificationDao;

    @Autowired
    EventReadStatusDao eventReadStatusDao;

    @Autowired
    ResidentCareTeamMemberDao residentCareTeamMemberDao;

    @Autowired
    AccessRightsService accessRightsService;

    @Autowired
    private CareTeamService careTeamService;

    @Autowired
    private CareTeamRoleService careTeamRoleService;

    @Autowired
    private CareTeamSecurityUtils careTeamSecurityUtils;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private DozerBeanMapper dozer;

    @Transactional(readOnly = true)
    public List<EventListItemDto> getEvents(Long userId, EventFilterDto eventFilter, AccountType.Type accountType, Pageable pageable) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.EVENT_NOTIFICATIONS);

        User user = careTeamSecurityUtils.getCurrentUser();
        eventFilter = fixFilter(eventFilter, user.getTimeZoneOffset());

        List<Long> residentIds = getResidentIdsForEvents(userId, accountType);

        List<EventListItemDbo> events = eventDao.getEvents(eventFilter, residentIds, pageable);
        Map<Long, Long> eventReadCount = getEventReadStatusMap(events);

        return transformListItems(events, eventReadCount);
    }

    @Transactional(readOnly = true)
    public Date getEventsMinimumDate(Long userId, AccountType.Type accountType) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.EVENT_NOTIFICATIONS);

        List<Long> residentIds = getResidentIdsForEvents(userId, accountType);

        return eventService.getEventsMinimumDate(residentIds);
    }

    @Transactional(readOnly = true)
    public Long countEvents(Long userId, EventFilterDto eventFilter, AccountType.Type accountType) {
        User user = careTeamSecurityUtils.getCurrentUser();
        eventFilter = fixFilter(eventFilter, user.getTimeZoneOffset());

        List<Long> residentIds = getResidentIdsForEvents(userId, accountType);
        return eventDao.getEventsCountForEmployee(eventFilter, residentIds);
    }

    private Map<Long, Long> getEventReadStatusMap(List<EventListItemDbo> events) {
        if (CollectionUtils.isEmpty(events)) {
            return Collections.emptyMap();
        }

        Map<Long, Long> eventReadCount = new HashMap<>();
        Long currentUserId = PhrSecurityUtils.getCurrentUserId();
        List<Long> eventIds = new ArrayList<>();
        CollectionUtils.collect(events, new BeanToPropertyValueTransformer("eventId"), eventIds);
        List<EventAndReadCount> eventReadCounts = eventReadStatusDao.getCountByUserIdAndEventIds(currentUserId, eventIds);
        for (EventAndReadCount item : eventReadCounts) {
            eventReadCount.put(item.getEventId(), item.getReadCount());
        }

        return eventReadCount;
    }

    private List<Long> getResidentIdsForEvents(Long userId, AccountType.Type accountType) {
        if (AccountType.Type.PROVIDER.equals(accountType) && PhrSecurityUtils.checkAccessToUserInfo(userId)) {
            return getCareTeamReceiversAndTheirMergesForCurrentUser();
        } else {
            return new ArrayList<>(getResidentIdsOrThrow(userId));
        }
    }

    public void createEvent(Long userId, EventCreateDto eventCreateDto) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.EVENT_NOTIFICATIONS);

        EventDto eventDto = convert(eventCreateDto);

        Long residentId = getResidentIdOrThrow(userId);
        eventDto.setPatient(new PatientDto());
        eventDto.getPatient().setId(residentId);

        EmployeeDto employeeDto;
        final User currentUser = careTeamSecurityUtils.getCurrentUser();
        if (PhrSecurityUtils.checkAccessToUserInfo(userId)) {
            // self-create event
            final Resident resident = residentDao.get(residentId);
            employeeDto = convert(resident);
        } else {
            final Employee employee = getEmployeeOrThrow(currentUser);
            employeeDto = convert(employee);
        }
        eventDto.setEmployee(employeeDto);

        eventService.processManualEvent(eventDto);
    }

    private EmployeeDto convert(Employee employee) {
        EmployeeDto dto = dozer.map(employee, EmployeeDto.class);
        dto.setRoleId(employee.getCareTeamRole().getId());

        return dto;
    }

    private EmployeeDto convert(Resident resident) {
        EmployeeDto dto = dozer.map(resident, EmployeeDto.class);
        CareTeamRoleDto role = careTeamRoleService.get(CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES);
        dto.setRoleId(role.getId());

        return dto;
    }

    private EventDto convert(EventCreateDto eventCreateDto) {
        EventDto dto = dozer.map(eventCreateDto, EventDto.class);

        HospitalEditDto treatingHospital = eventCreateDto.getTreatingHospital();
        if (treatingHospital != null && treatingHospital.getAddress() != null) {
            KeyValueDto stateDto = treatingHospital.getAddress().getState();
            dto.getTreatingHospital().getAddress().setState(stateDto);
        }

        NameWithAddressEditDto responsible = eventCreateDto.getResponsible();
        if (responsible != null && responsible.getAddress() != null) {
            KeyValueDto stateDto = responsible.getAddress().getState();
            dto.getResponsible().getAddress().setState(stateDto);
        }

        NameWithAddressEditDto treatingPhysician = eventCreateDto.getTreatingPhysician();
        if (treatingPhysician != null && treatingPhysician.getAddress() != null) {
            KeyValueDto stateDto = treatingPhysician.getAddress().getState();
            dto.getTreatingPhysician().getAddress().setState(stateDto);
        }

        return dto;
    }

    public EventDto getEvent(Long userId, Long eventId) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.EVENT_NOTIFICATIONS);

        EventDto eventDetails;
        try {
            eventDetails = eventService.getEventDetailsWithoutNotes(eventId);
        } catch (NullPointerException exc) {
            throw new PhrException(PhrExceptionType.EVENT_NOT_FOUND);
        }

        validateAssociation(userId, eventDetails);

        Long currentUserId = PhrSecurityUtils.getCurrentUserId();
        if (!eventReadStatusDao.existsByUserIdAndEventId(currentUserId, eventId)) {
            EventReadStatus eventReadStatus = new EventReadStatus();
            eventReadStatus.setEventId(eventId);
            eventReadStatus.setUserId(currentUserId);
            eventReadStatusDao.save(eventReadStatus);
        }

        // erase sensitive data
        eventDetails.getPatient().setHashKey(null);
        String lastFourDigitsOfSsn = StringUtils.right(eventDetails.getPatient().getSsn(), 4);
        eventDetails.getPatient().setSsn(lastFourDigitsOfSsn);

        return eventDetails;
    }

    public Event getAvailableEvent(Long eventId) {
        //todo filter out not viewable events
        if (eventId == null) { // || event is not viewable) {
            return null;
        }
        return eventDao.get(eventId);
    }

    private void validateAssociation(Long userId, EventDto eventDetails) {
        // Let patientId be the ID of a patient from the specified event
        Long patientId = eventDetails.getPatient().getId();
        validateAssociation(userId, patientId);
    }

    void validateAssociation(Long userId, Event event) {
        // Let patientId be the ID of a patient from the specified event
        Long patientId = event.getResident().getId();
        validateAssociation(userId, patientId);
    }

    private void validateAssociation(Long userId, Long eventPatientId) {
        if (!careTeamSecurityUtils.isAssociated(userId, eventPatientId, AccessRight.Code.EVENT_NOTIFICATIONS)){
            throw new PhrException(PhrExceptionType.EVENT_NOT_ASSOCIATED);
        }
    }

    /**
     * Get a set of resident IDs of care receivers, that allowed access to event notifications, from a care team of the current user
     * plus IDs of their merged residents.
     *
     * @return a list of resident IDs
     */
    private List<Long> getCareTeamReceiversAndTheirMergesForCurrentUser() {
        return careTeamSecurityUtils.getCareTeamReceiversAndTheirMergesForCurrentUser(AccessRight.Code.EVENT_NOTIFICATIONS);
    }

    @Transactional(readOnly = true)
    public List<EventNotificationDto> getEventNotifications(Long userId, Long eventId, Pageable pageable) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.EVENT_NOTIFICATIONS);

        Event event = eventDao.get(eventId);
        if (event == null) {
            throw new PhrException(PhrExceptionType.EVENT_NOT_FOUND);
        }
        Long residentId = event.getResident().getId();

        validateAssociation(userId, event);

        List<EventNotificationDto> result = new ArrayList<>();
        for (EventNotification eventNotification : eventNotificationDao.listByEventId(eventId, pageable, Boolean.TRUE)) {
            CareteamMemberDto ctm = null;
            if (eventNotification.getUserPatient() == null && eventNotification.getEmployee() != null) {
                try {
                    ctm = careTeamService.getResidentCareTeamMember(residentId, eventNotification.getEmployee().getId());
                } catch (EmptyResultDataAccessException | NoResultException ignored) {
                    ignored.printStackTrace();
                }
            }
            result.add(transformEventNotification(eventNotification, ctm));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Long getEventNotificationsCount(Long userId, Long eventId) {
        careTeamSecurityUtils.checkAccessToUserInfoOrThrow(userId, AccessRight.Code.EVENT_NOTIFICATIONS);

        return eventNotificationDao.countByEventId(eventId, Boolean.TRUE);
    }

    private static EventFilterDto fixFilter(EventFilterDto eventFilter, Integer timezoneOffset) {
        if (eventFilter == null) {
            eventFilter = new EventFilterDto();
        }
        EventFilterDto newEventFilter = new EventFilterDto();
        newEventFilter.setEventTypeId(eventFilter.getEventTypeId());
        newEventFilter.setEventGroupId(eventFilter.getEventGroupId());
        newEventFilter.setIrRelatedEvent(eventFilter.getIrRelatedEvent());
        timezoneOffset = timezoneOffset != null ? -timezoneOffset : -0;

        final DateTimeZone zone = DateTimeZone.forOffsetMillis((int) TimeUnit.MINUTES.toMillis(timezoneOffset));
        //final DateTimeZone serverZone = DateTimeZone.getDefault();
        if (eventFilter.getDateFrom() != null) {
            MutableDateTime dateTime = new MutableDateTime(eventFilter.getDateFrom(), zone);
            dateTime.setHourOfDay(0);
            dateTime.setMinuteOfHour(0);
            dateTime.setSecondOfMinute(0);
            dateTime.setMillisOfSecond(0);
            //dateTime.setZoneRetainFields(serverZone);
            newEventFilter.setDateFrom(dateTime.toDate());
        }

        if (eventFilter.getDateTo() != null) {
            MutableDateTime dateTime = new MutableDateTime(eventFilter.getDateTo(), zone);
            dateTime.setHourOfDay(23);
            dateTime.setMinuteOfHour(59);
            dateTime.setSecondOfMinute(59);
            dateTime.setMillisOfSecond(999);
            //dateTime.setZoneRetainFields(serverZone);
            newEventFilter.setDateTo(dateTime.toDate());
        }
        
        return newEventFilter;
    }

    private List<EventListItemDto> transformListItems(List<EventListItemDbo> events, Map<Long, Long> eventReadCount) {
        List<EventListItemDto> dtos = new ArrayList<>();
        for (EventListItemDbo event : events) {
            EventListItemDto dto = dozer.map(event, EventListItemDto.class);
            dto.setResidentName(event.getResidentFirstName() + " " + event.getResidentLastName());
            final Long reads = eventReadCount.get(event.getEventId());
            boolean isRead = reads != null && reads > 0;
            dto.setUnread(!isRead);
            dtos.add(dto);
        }

        return dtos;
    }

    private EventNotificationDto transformEventNotification(EventNotification eventNotification, CareteamMemberDto ctm) {
        EventNotificationDto dto = new EventNotificationDto();

        String fullName = eventNotification.getPersonName();
        dto.setContactName(fullName);

        if (ctm != null) {
            dto.setContactId(ctm.getId());
            dto.setEditableContact(careTeamService.canManageAccessRights(ctm.getId()));
            dto.setUserId(ctm.getUserId());
        } else if (eventNotification.getUserPatient() != null) {
            dto.setUserId(eventNotification.getUserPatient().getId());
        }
        if (eventNotification.getCareTeamRole() != null) {
            dto.setCareTeamRole(eventNotification.getCareTeamRole().getName());
        } else {
            CareTeamRoleDto role = careTeamRoleService.get(CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES);
            dto.setCareTeamRole(role.getLabel());
        }
        if (eventNotification.getSentDatetime() != null) {
            dto.setDateTime(eventNotification.getSentDatetime().getTime());
        }
        dto.setDescription(eventNotification.getDescription());
        dto.setDestination(eventNotification.getDestination());
        dto.setNotificationType(eventNotification.getNotificationType());
        if (NotificationType.PUSH_NOTIFICATION.equals(eventNotification.getNotificationType()) && StringUtils.isNotBlank(eventNotification.getDestination())) {
            dto.setDestination(null);
        }
        if (NotificationType.SECURITY_MESSAGE.equals(eventNotification.getNotificationType()) && StringUtils.isBlank(eventNotification.getDestination())) {
            // should never happen
            dto.setDestination("Warning! Secure Messaging account has not been set up yet.");
        }
        switch (eventNotification.getResponsibility()) {
            case R:
                dto.setResponsibility(ResponsibilityEnum.RESPONSIBLE);
                break;
            case A:
                dto.setResponsibility(ResponsibilityEnum.ACCOUNTABLE);
                break;
            case C:
                dto.setResponsibility(ResponsibilityEnum.CONSULTED);
                break;
            case I:
                dto.setResponsibility(ResponsibilityEnum.INFORMED);
                break;
            case V:
                dto.setResponsibility(ResponsibilityEnum.VIEWABLE);
                break;
            case N:
                dto.setResponsibility(ResponsibilityEnum.NOT_VIEWABLE);
                break;
        }

        if (eventNotification.getUserPatient() == null || !PhrSecurityUtils.checkAccessToUserInfo(eventNotification.getUserPatient().getId())) {
            // if the current user is a consumer-target of the event
            String patientFullName = eventNotification.getEvent().getResident().getFullName();
            String eventTypeDescription = eventNotification.getEvent().getEventType().getDescription();
            dto.setDetails(String.format("You received this alert because you are assigned as the responsible party for event types of \"%s\" occur for %s",
                    eventTypeDescription, patientFullName));
        }

        if (eventNotification.getEmployee() != null) {
            dto.setDataSource(DataSourceService.transform(eventNotification.getEmployee().getDatabase(),
                    ctm == null ? null : ctm.getDataSource().getResidentId()));
        }

        return dto;
    }

    public void setDozer(DozerBeanMapper dozer) {
        this.dozer = dozer;
    }

}
