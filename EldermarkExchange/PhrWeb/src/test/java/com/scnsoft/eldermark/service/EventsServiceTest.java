package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.dao.carecoordination.*;
import com.scnsoft.eldermark.dao.phr.EventReadStatusDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.dao.projections.EventAndReadCount;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.phr.AccountType;
import com.scnsoft.eldermark.entity.phr.EventReadStatus;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.services.carecoordination.CareTeamRoleService;
import com.scnsoft.eldermark.services.carecoordination.EventService;
import com.scnsoft.eldermark.services.merging.MPIService;
import com.scnsoft.eldermark.services.phr.AccessRightsService;
import com.scnsoft.eldermark.shared.carecoordination.CareTeamRoleDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.ManagerDto;
import com.scnsoft.eldermark.shared.carecoordination.PatientDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventDto;
import com.scnsoft.eldermark.shared.carecoordination.events.EventFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.util.MockitoMatchers;
import com.scnsoft.eldermark.web.entity.*;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 6/15/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class EventsServiceTest extends BaseServiceTest {

    @Mock
    private EventService eventService;

    @Mock
    private ResidentCareTeamMemberDao residentCareTeamMemberDao;

    @Mock
    private AccessRightsService accessRightsService;

    @Mock
    private ResidentDao residentDao;

    @Mock
    private MPIService mpiService;

    @Mock
    private EventDao eventDao;

    @Mock
    private EventNotificationDao eventNotificationDao;

    @Mock
    private EventReadStatusDao eventReadStatusDao;

    @Mock
    private CareTeamService careTeamService;

    @Mock
    private CareTeamRoleService careTeamRoleService;

    @Mock
    private UserResidentRecordsDao userResidentRecordsDao;

    @Mock
    private AvatarService avatarService;

    @Mock
    private UserDao userDao;

    @Mock
    private CareTeamSecurityUtils careTeamSecurityUtils;

    @InjectMocks
    private EventsService eventsService;

    // Shared test data
    private final Long eventId = TestDataGenerator.randomId();
    private final Long eventTypeId = TestDataGenerator.randomId();
    private final Date dateFrom = TestDataGenerator.randomDate();
    private final Date now = new Date();

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\nResident ID: %d\nEvent ID: %d\nEvent type ID: %d\nDate from: %s\n\n",
                userId, residentId, eventId, eventTypeId, dateFrom);
    }

    @Before
    public void injectDozer() {
        final DozerBeanMapper dozer = new DozerBeanMapper();
        eventsService.setDozer(dozer);
    }

    private void verifySecurity(Long currentUserId) {
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(currentUserId, AccessRight.Code.EVENT_NOTIFICATIONS);
    }

    private Pair<User, User> setUpMockitoExpectations(Long consumerUserId) {
        return setUpMockitoExpectations(consumerUserId, true);
    }

    private Pair<User, User> setUpMockitoExpectations(Long consumerUserId, boolean isAssociated) {
        final boolean isConsumer = Objects.equals(consumerUserId, userId);
        final User providerUser = super.createProvider(userId);
        final User consumerUser = super.createConsumer(consumerUserId);
        final User currentUser = isConsumer ? consumerUser : providerUser;

        when(userResidentRecordsDao.getActiveResidentIdsByUserId(consumerUserId)).thenReturn(activeResidentIds);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerUserId)).thenReturn(allResidentIds);

        when(careTeamSecurityUtils.isAssociated(currentUser.getId(), residentId, AccessRight.Code.EVENT_NOTIFICATIONS)).thenReturn(isAssociated);

        when(careTeamSecurityUtils.getCurrentUser()).thenReturn(currentUser);
        when(userDao.findOne(userId)).thenReturn(currentUser);
        when(userDao.findOne(consumerUserId)).thenReturn(consumerUser);
        when(residentDao.get(consumerUser.getResidentId())).thenReturn(consumerUser.getResident());

        return new Pair<>(providerUser, consumerUser);
    }

    @Test
    public void testGetEvents() {
        final Long consumerId = userId;

        // Expected objects
        final Long eventGroupId = TestDataGenerator.randomId(6);
        final EventFilterDto eventFilterDto = new EventFilterDto(eventTypeId, null, null, dateFrom, now,null);
        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();
        final EventListItemDbo eventListItemDbo = new EventListItemDbo();
        eventListItemDbo.setEventId(eventId);
        eventListItemDbo.setEventDate(now);
        eventListItemDbo.setEventType("test");
        eventListItemDbo.setEventGroupId(eventGroupId);
        eventListItemDbo.setResidentFirstName(firstName);
        eventListItemDbo.setResidentLastName(lastName);
        final EventListItemDbo eventListItemDbo2 = new EventListItemDbo();
        eventListItemDbo2.setEventId(eventId + 1);
        eventListItemDbo2.setEventDate(now);
        eventListItemDbo2.setEventType("test test");
        eventListItemDbo2.setEventGroupId(eventGroupId);
        eventListItemDbo2.setResidentFirstName(firstName);
        eventListItemDbo2.setResidentLastName(lastName);

        final EventListItemDto expectedEventListItemDto = new EventListItemDto();
        expectedEventListItemDto.setEventId(eventId);
        expectedEventListItemDto.setEventDate(now.getTime());
        expectedEventListItemDto.setEventType("test");
        expectedEventListItemDto.setEventGroupId(eventGroupId);
        expectedEventListItemDto.setResidentName(firstName + " " + lastName);
        expectedEventListItemDto.setUnread(Boolean.FALSE);
        final EventListItemDto expectedEventListItemDto2 = new EventListItemDto();
        expectedEventListItemDto2.setEventId(eventId + 1);
        expectedEventListItemDto2.setEventDate(now.getTime());
        expectedEventListItemDto2.setEventType("test test");
        expectedEventListItemDto2.setEventGroupId(eventGroupId);
        expectedEventListItemDto2.setResidentName(firstName + " " + lastName);
        expectedEventListItemDto2.setUnread(Boolean.TRUE);

        final EventAndReadCount mock = Mockito.mock(EventAndReadCount.class);
        final Pageable pageable = PaginationUtils.buildPageable(100, 0);

        // Mockito expectations
        setUpMockitoExpectations(consumerId);
        when(eventDao.getEvents(any(EventFilterDto.class), eq(activeResidentIds), eq(pageable)))
                .thenAnswer(new Answer<List<EventListItemDbo>>() {
                    @Override
                    public List<EventListItemDbo> answer(InvocationOnMock invocation) {
                        final EventFilterDto filterDto = invocation.getArgumentAt(0, EventFilterDto.class);
                        if (filterDto.getEventTypeId().equals(eventTypeId) && filterDto.getDateTo() != null && filterDto.getDateFrom() != null) {
                            return Arrays.asList(eventListItemDbo, eventListItemDbo2);
                        } else {
                            return Collections.emptyList();
                        }
                    }
                });
        when(eventReadStatusDao.existsByUserIdAndEventId(consumerId, eventId)).thenReturn(Boolean.TRUE);
        when(eventReadStatusDao.existsByUserIdAndEventId(consumerId, eventId + 1)).thenReturn(Boolean.FALSE);
        when(eventReadStatusDao.getCountByUserIdAndEventIds(userId, Arrays.asList(eventId, eventId + 1))).thenReturn(Arrays.asList(mock));
        when(mock.getEventId()).thenReturn(eventId);
        when(mock.getReadCount()).thenReturn(1L);

        // Execute the method being tested
        List<EventListItemDto> result = eventsService.getEvents(consumerId, eventFilterDto, AccountType.Type.CONSUMER, pageable);

        // Validation
        assertThat(result, hasSize(2));
        assertThat(result.get(0), sameBeanAs(expectedEventListItemDto));
        assertThat(result.get(1), sameBeanAs(expectedEventListItemDto2));
        verifySecurity(consumerId);
    }

    @Test
    public void testGetOwnEventsAsProvider() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Expected objects
        final Long eventGroupId = TestDataGenerator.randomId(6);
        final EventFilterDto eventFilterDto = new EventFilterDto(eventTypeId, null, null, dateFrom, now,null);
        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();
        final EventListItemDbo eventListItemDbo = new EventListItemDbo();
        eventListItemDbo.setEventId(eventId);
        eventListItemDbo.setEventDate(now);
        eventListItemDbo.setEventType("test");
        eventListItemDbo.setEventGroupId(eventGroupId);
        eventListItemDbo.setResidentFirstName(firstName);
        eventListItemDbo.setResidentLastName(lastName);
        final EventListItemDbo eventListItemDbo2 = new EventListItemDbo();
        eventListItemDbo2.setEventId(eventId + 1);
        eventListItemDbo2.setEventDate(now);
        eventListItemDbo2.setEventType("test test");
        eventListItemDbo2.setEventGroupId(eventGroupId);
        eventListItemDbo2.setResidentFirstName(firstName);
        eventListItemDbo2.setResidentLastName(lastName);

        final EventListItemDto expectedEventListItemDto = new EventListItemDto();
        expectedEventListItemDto.setEventId(eventId);
        expectedEventListItemDto.setEventDate(now.getTime());
        expectedEventListItemDto.setEventType("test");
        expectedEventListItemDto.setEventGroupId(eventGroupId);
        expectedEventListItemDto.setResidentName(firstName + " " + lastName);
        expectedEventListItemDto.setUnread(Boolean.FALSE);
        final EventListItemDto expectedEventListItemDto2 = new EventListItemDto();
        expectedEventListItemDto2.setEventId(eventId + 1);
        expectedEventListItemDto2.setEventDate(now.getTime());
        expectedEventListItemDto2.setEventType("test test");
        expectedEventListItemDto2.setEventGroupId(eventGroupId);
        expectedEventListItemDto2.setResidentName(firstName + " " + lastName);
        expectedEventListItemDto2.setUnread(Boolean.TRUE);

        final EventAndReadCount mock = Mockito.mock(EventAndReadCount.class);

        // Mockito expectations
        setUpMockitoExpectations(consumerId);
        when(careTeamSecurityUtils.getCareTeamReceiversAndTheirMergesForCurrentUser(AccessRight.Code.EVENT_NOTIFICATIONS)).thenReturn(allResidentIds);
        when(eventDao.getEvents(any(EventFilterDto.class), argThat(MockitoMatchers.sameAsSet(allResidentIds)), isNull(Pageable.class)))
                .thenAnswer(new Answer<List<EventListItemDbo>>() {
                    @Override
                    public List<EventListItemDbo> answer(InvocationOnMock invocation) {
                        final EventFilterDto filterDto = invocation.getArgumentAt(0, EventFilterDto.class);
                        if (filterDto.getEventTypeId().equals(eventTypeId) && filterDto.getDateTo() != null && filterDto.getDateFrom() != null) {
                            return Arrays.asList(eventListItemDbo, eventListItemDbo2);
                        } else {
                            return Collections.emptyList();
                        }
                    }
                });
        when(eventReadStatusDao.existsByUserIdAndEventId(userId, eventId)).thenReturn(Boolean.TRUE);
        when(eventReadStatusDao.existsByUserIdAndEventId(userId, eventId + 1)).thenReturn(Boolean.FALSE);
        when(eventReadStatusDao.getCountByUserIdAndEventIds(userId, Arrays.asList(eventId, eventId + 1))).thenReturn(Arrays.asList(mock));
        when(mock.getEventId()).thenReturn(eventId);
        when(mock.getReadCount()).thenReturn(1L);

        // Execute the method being tested
        List<EventListItemDto> result = eventsService.getEvents(userId, eventFilterDto, AccountType.Type.PROVIDER, null);

        // Validation
        assertThat(result, hasSize(2));
        assertThat(result.get(0), sameBeanAs(expectedEventListItemDto));
        assertThat(result.get(1), sameBeanAs(expectedEventListItemDto2));
        verifySecurity(userId);
    }

    @Test
    public void testGetEventsAsProvider() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Mockito expectations
        final Pair<User, User> users = setUpMockitoExpectations(consumerId);

        // Expected objects
        final Long eventGroupId = TestDataGenerator.randomId(6);
        final EventFilterDto eventFilterDto = null;
        final String firstName = users.getSecond().getFirstName();
        final String lastName = users.getSecond().getLastName();
        final EventListItemDbo eventListItemDbo = new EventListItemDbo();
        eventListItemDbo.setEventId(eventId);
        eventListItemDbo.setEventDate(now);
        eventListItemDbo.setEventType("test");
        eventListItemDbo.setEventGroupId(eventGroupId);
        eventListItemDbo.setResidentFirstName(firstName);
        eventListItemDbo.setResidentLastName(lastName);
        final EventListItemDbo eventListItemDbo2 = new EventListItemDbo();
        eventListItemDbo2.setEventId(eventId - 1);
        eventListItemDbo2.setEventDate(now);
        eventListItemDbo2.setEventType("test test");
        eventListItemDbo2.setEventGroupId(eventGroupId);
        eventListItemDbo2.setResidentFirstName(firstName);
        eventListItemDbo2.setResidentLastName(lastName);

        final EventListItemDto expectedEventListItemDto = new EventListItemDto();
        expectedEventListItemDto.setEventId(eventId);
        expectedEventListItemDto.setEventDate(now.getTime());
        expectedEventListItemDto.setEventType("test");
        expectedEventListItemDto.setEventGroupId(eventGroupId);
        expectedEventListItemDto.setResidentName(firstName + " " + lastName);
        expectedEventListItemDto.setUnread(Boolean.FALSE);
        final EventListItemDto expectedEventListItemDto2 = new EventListItemDto();
        expectedEventListItemDto2.setEventId(eventId - 1);
        expectedEventListItemDto2.setEventDate(now.getTime());
        expectedEventListItemDto2.setEventType("test test");
        expectedEventListItemDto2.setEventGroupId(eventGroupId);
        expectedEventListItemDto2.setResidentName(firstName + " " + lastName);
        expectedEventListItemDto2.setUnread(Boolean.TRUE);

        final EventAndReadCount mock = Mockito.mock(EventAndReadCount.class);

        // Mockito expectations
        when(eventDao.getEvents(any(EventFilterDto.class), eq(allResidentIds), isNull(Pageable.class)))
                .thenReturn(Arrays.asList(eventListItemDbo, eventListItemDbo2));
        when(eventReadStatusDao.existsByUserIdAndEventId(userId, eventId)).thenReturn(Boolean.TRUE);
        when(eventReadStatusDao.existsByUserIdAndEventId(userId, eventId - 1)).thenReturn(Boolean.FALSE);
        when(eventReadStatusDao.getCountByUserIdAndEventIds(userId, Arrays.asList(eventId, eventId - 1))).thenReturn(Arrays.asList(mock));
        when(mock.getEventId()).thenReturn(eventId);
        when(mock.getReadCount()).thenReturn(1L);

        // Execute the method being tested
        List<EventListItemDto> result = eventsService.getEvents(consumerId, eventFilterDto, AccountType.Type.PROVIDER, null);

        // Validation
        assertThat(result, hasSize(2));
        assertThat(result.get(0), sameBeanAs(expectedEventListItemDto));
        assertThat(result.get(1), sameBeanAs(expectedEventListItemDto2));
        verifySecurity(consumerId);
    }

    @Test
    public void testGetEventsMinimumDate() {
        final Long consumerId = userId;

        // Mockito expectations
        setUpMockitoExpectations(consumerId);
        when(eventService.getEventsMinimumDate(activeResidentIds)).thenReturn(dateFrom);

        // Execute the method being tested
        Date date = eventsService.getEventsMinimumDate(consumerId, AccountType.Type.CONSUMER);

        // Validation
        assertThat(date, is(dateFrom));
        verifySecurity(consumerId);
    }

    @Test
    public void testGetEventsMinimumDateAsProvider() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Mockito expectations
        final Pair<User, User> users = setUpMockitoExpectations(consumerId);
        when(eventReadStatusDao.existsByUserIdAndEventId(userId, eventId)).thenReturn(Boolean.FALSE);
        when(careTeamSecurityUtils.getCareTeamReceiversAndTheirMergesForCurrentUser(AccessRight.Code.EVENT_NOTIFICATIONS))
                .thenReturn(Collections.singletonList(residentId));
        when(eventService.getEventsMinimumDate(activeResidentIds)).thenReturn(dateFrom);

        // Execute the method being tested
        Date date = eventsService.getEventsMinimumDate(userId, AccountType.Type.PROVIDER);

        // Validation
        assertThat(date, is(dateFrom));
        verifySecurity(userId);
    }

    @Test(expected = PhrException.class)
    public void testGetEventsMinimumDateAsConsumerNotProviderThrowsNotFoundEmployeeInfo() {
        final Long consumerId = userId;

        // Mockito expectations
        setUpMockitoExpectations(consumerId);
        when(careTeamSecurityUtils.getCareTeamReceiversAndTheirMergesForCurrentUser(AccessRight.Code.EVENT_NOTIFICATIONS)).thenThrow(PhrException.class);

        // Execute the method being tested
        eventsService.getEventsMinimumDate(userId, AccountType.Type.PROVIDER);
    }

    @Test(expected = PhrException.class)
    public void testGetEventsMinimumDateAsProviderNotConsumerThrowsNotFoundPatientInfo() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Expected objects
        AccessRight accessRight = new AccessRight();

        // Mockito expectations
        final Pair<User, User> users = setUpMockitoExpectations(consumerId);
        final Long employeeIdOfProvider = users.getFirst().getEmployeeId();
        when(eventReadStatusDao.existsByUserIdAndEventId(userId, eventId)).thenReturn(Boolean.FALSE);
        when(accessRightsService.getAccessRight(AccessRight.Code.EVENT_NOTIFICATIONS)).thenReturn(accessRight);
        when(residentCareTeamMemberDao.getCareTeamResidentIdsByEmployeeIdWithAccessRightsCheck(Collections.singleton(employeeIdOfProvider), accessRight))
                .thenReturn(Collections.singletonList(residentId));
        when(eventService.getEventsMinimumDate(activeResidentIds)).thenReturn(dateFrom);

        // Execute the method being tested
        eventsService.getEventsMinimumDate(userId, AccountType.Type.CONSUMER);
    }

    @Test
    public void testCountEvents() {
        final Long consumerId = userId;

        // Expected objects
        final EventFilterDto eventFilterDto = null;

        // Mockito expectations
        setUpMockitoExpectations(consumerId);
        when(eventDao.getEventsCountForEmployee(any(EventFilterDto.class), eq(activeResidentIds))).thenReturn(42L);

        // Execute the method being tested
        final Long result = eventsService.countEvents(consumerId, eventFilterDto, AccountType.Type.CONSUMER);

        // Validation
        assertThat(result, equalTo(42L));
    }

    @Test
    public void testCountEventsAsProvider() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Expected objects
        final EventFilterDto eventFilterDto = new EventFilterDto();
        eventFilterDto.setEventTypeId(1L);

        // Mockito expectations
        setUpMockitoExpectations(consumerId);
        when(eventDao.getEventsCountForEmployee(any(EventFilterDto.class), eq(allResidentIds))).thenAnswer(new Answer<Long>() {
            @Override
            public Long answer(InvocationOnMock invocation) {
                final EventFilterDto filterDto = invocation.getArgumentAt(0, EventFilterDto.class);
                if (filterDto.getEventTypeId().equals(1L)) {
                    return 42L;
                } else {
                    return 60L;
                }
            }
        });

        // Execute the method being tested
        final Long result = eventsService.countEvents(consumerId, eventFilterDto, AccountType.Type.PROVIDER);

        // Validation
        assertThat(result, equalTo(42L));
    }

    @Test
    public void testCreateEventAsConsumer() {
        final Long consumerId = userId;

        // Expected objects
        final EventAddressEditDto address = new EventAddressEditDto();
        address.setState(new KeyValueDto());
        final HospitalEditDto treatingHospital = new HospitalEditDto();
        treatingHospital.setIncludeAddress(true);
        treatingHospital.setAddress(address);
        final NameWithAddressEditDto physicianDto = new NameWithAddressEditDto();
        physicianDto.setIncludeAddress(true);
        physicianDto.setAddress(address);
        final NameWithAddressEditDto responsibleDto = new NameWithAddressEditDto();
        responsibleDto.setIncludeAddress(true);
        responsibleDto.setAddress(address);
        final EventCreateDto eventCreateDto = new EventCreateDto();
        eventCreateDto.setIncludeHospital(true);
        eventCreateDto.setIncludeManager(true);
        eventCreateDto.setIncludeResponsible(true);
        eventCreateDto.setIncludeTreatingPhysician(true);
        eventCreateDto.setTreatingHospital(treatingHospital);
        eventCreateDto.setTreatingPhysician(physicianDto);
        eventCreateDto.setResponsible(responsibleDto);
        eventCreateDto.setManager(new ManagerDto());

        final CareTeamRoleDto careTeamRoleDto = new CareTeamRoleDto(TestDataGenerator.randomId(), "label", CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES);

        // Mockito expectations
        setUpMockitoExpectations(consumerId);
        when(careTeamRoleService.get(CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES)).thenReturn(careTeamRoleDto);

        // Execute the method being tested
        eventsService.createEvent(consumerId, new EventCreateDto());

        // Validation
        verify(eventService).processManualEvent(any(EventDto.class));
        verifySecurity(consumerId);
    }

    @Test
    public void testCreateEventAsProvider() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Expected objects
        final EventAddressEditDto address = new EventAddressEditDto();
        address.setState(new KeyValueDto());
        final HospitalEditDto treatingHospital = new HospitalEditDto();
        treatingHospital.setIncludeAddress(true);
        treatingHospital.setAddress(address);
        final NameWithAddressEditDto physicianDto = new NameWithAddressEditDto();
        physicianDto.setIncludeAddress(true);
        physicianDto.setAddress(address);
        final NameWithAddressEditDto responsibleDto = new NameWithAddressEditDto();
        responsibleDto.setIncludeAddress(true);
        responsibleDto.setAddress(address);
        final EventCreateDto eventCreateDto = new EventCreateDto();
        eventCreateDto.setIncludeHospital(true);
        eventCreateDto.setIncludeManager(true);
        eventCreateDto.setIncludeResponsible(true);
        eventCreateDto.setIncludeTreatingPhysician(true);
        eventCreateDto.setTreatingHospital(treatingHospital);
        eventCreateDto.setTreatingPhysician(physicianDto);
        eventCreateDto.setResponsible(responsibleDto);
        eventCreateDto.setManager(new ManagerDto());

        // Mockito expectations
        setUpMockitoExpectations(consumerId);

        // Execute the method being tested
        eventsService.createEvent(consumerId, eventCreateDto);

        // Validation
        verify(eventService).processManualEvent(any(EventDto.class));
        verifySecurity(consumerId);
    }

    @Test
    public void testCreateEventAsProviderMinimal() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Expected objects
        final HospitalEditDto treatingHospital = new HospitalEditDto();
        treatingHospital.setIncludeAddress(false);
        final NameWithAddressEditDto physicianDto = new NameWithAddressEditDto();
        physicianDto.setIncludeAddress(false);
        final NameWithAddressEditDto responsibleDto = new NameWithAddressEditDto();
        responsibleDto.setIncludeAddress(false);
        final EventCreateDto eventCreateDto = new EventCreateDto();
        eventCreateDto.setIncludeHospital(true);
        eventCreateDto.setIncludeManager(false);
        eventCreateDto.setIncludeResponsible(true);
        eventCreateDto.setIncludeTreatingPhysician(true);
        eventCreateDto.setTreatingHospital(treatingHospital);
        eventCreateDto.setTreatingPhysician(physicianDto);
        eventCreateDto.setResponsible(responsibleDto);

        // Mockito expectations
        setUpMockitoExpectations(consumerId);

        // Execute the method being tested
        eventsService.createEvent(consumerId, eventCreateDto);

        // Validation
        verify(eventService).processManualEvent(any(EventDto.class));
        verifySecurity(consumerId);
    }

    @Test
    public void testGetEvent() {
        final Long consumerId = userId;

        // Expected objects
        final PatientDto patientDto = new PatientDto();
        patientDto.setId(residentId);
        final EventDto eventDto = new EventDto();
        eventDto.setPatient(patientDto);

        final EventDto expectedEventDto = new EventDto();
        expectedEventDto.setPatient(patientDto);

        final EventReadStatus expectedEventReadStatus = new EventReadStatus();
        expectedEventReadStatus.setUserId(consumerId);
        expectedEventReadStatus.setEventId(eventId);

        // Mockito expectations
        setUpMockitoExpectations(consumerId);
        when(eventService.getEventDetailsWithoutNotes(eventId)).thenReturn(eventDto);
        when(eventReadStatusDao.existsByUserIdAndEventId(consumerId, eventId)).thenReturn(Boolean.FALSE);

        // Execute the method being tested
        EventDto result = eventsService.getEvent(consumerId, eventId);

        // Validation
        assertThat(result, sameBeanAs(expectedEventDto));

        ArgumentCaptor<EventReadStatus> eventReadStatusCaptor = ArgumentCaptor.forClass(EventReadStatus.class);
        verify(eventReadStatusDao).existsByUserIdAndEventId(consumerId, eventId);
        verify(eventReadStatusDao).save(eventReadStatusCaptor.capture());
        final EventReadStatus status = eventReadStatusCaptor.getValue();
        assertThat(status, sameBeanAs(expectedEventReadStatus));

        verifySecurity(consumerId);
    }

    @Test
    public void testGetEventSecondTime() {
        final Long consumerId = userId;

        // Expected objects
        final PatientDto patientDto = new PatientDto();
        patientDto.setId(residentId);
        final EventDto eventDto = new EventDto();
        eventDto.setPatient(patientDto);

        final EventDto expectedEventDto = new EventDto();
        expectedEventDto.setPatient(patientDto);

        // Mockito expectations
        setUpMockitoExpectations(consumerId);
        when(eventService.getEventDetailsWithoutNotes(eventId)).thenReturn(eventDto);
        when(eventReadStatusDao.existsByUserIdAndEventId(consumerId, eventId)).thenReturn(Boolean.TRUE);

        // Execute the method being tested
        EventDto result = eventsService.getEvent(consumerId, eventId);

        // Validation
        assertThat(result, sameBeanAs(expectedEventDto));
        verify(eventReadStatusDao).existsByUserIdAndEventId(consumerId, eventId);
        verify(eventReadStatusDao, never()).save(any(EventReadStatus.class));
        verifySecurity(consumerId);
    }

    @Test
    public void testGetEventAsProvider() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Expected objects
        final PatientDto patientDto = new PatientDto();
        patientDto.setId(residentId);
        final EventDto eventDto = new EventDto();
        eventDto.setPatient(patientDto);

        final EventDto expectedEventDto = new EventDto();
        expectedEventDto.setPatient(patientDto);

        final EventReadStatus expectedEventReadStatus = new EventReadStatus();
        expectedEventReadStatus.setUserId(userId);
        expectedEventReadStatus.setEventId(eventId);

        // Mockito expectations
        setUpMockitoExpectations(consumerId);
        when(eventService.getEventDetailsWithoutNotes(eventId)).thenReturn(eventDto);
        when(eventReadStatusDao.existsByUserIdAndEventId(userId, eventId)).thenReturn(Boolean.FALSE);

        // Execute the method being tested
        EventDto result = eventsService.getEvent(userId, eventId);

        // Validation
        assertThat(result, sameBeanAs(expectedEventDto));

        ArgumentCaptor<EventReadStatus> eventReadStatusCaptor = ArgumentCaptor.forClass(EventReadStatus.class);
        verify(eventReadStatusDao).existsByUserIdAndEventId(userId, eventId);
        verify(eventReadStatusDao).save(eventReadStatusCaptor.capture());
        final EventReadStatus status = eventReadStatusCaptor.getValue();
        assertThat(status, sameBeanAs(expectedEventReadStatus));

        verify(careTeamSecurityUtils).isAssociated(userId, residentId, AccessRight.Code.EVENT_NOTIFICATIONS);
        verifySecurity(userId);
    }

    @Test
    public void testGetEventAsProvider2() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Expected objects
        final PatientDto patientDto = new PatientDto();
        patientDto.setId(residentId);
        final EventDto eventDto = new EventDto();
        eventDto.setPatient(patientDto);

        final EventDto expectedEventDto = new EventDto();
        expectedEventDto.setPatient(patientDto);

        final EventReadStatus expectedEventReadStatus = new EventReadStatus();
        expectedEventReadStatus.setUserId(userId);
        expectedEventReadStatus.setEventId(eventId);

        // Mockito expectations
        final Pair<User, User> users = setUpMockitoExpectations(consumerId);
        when(eventService.getEventDetailsWithoutNotes(eventId)).thenReturn(eventDto);
        when(eventReadStatusDao.existsByUserIdAndEventId(userId, eventId)).thenReturn(Boolean.FALSE);

        // Execute the method being tested
        EventDto result = eventsService.getEvent(userId, eventId);

        // Validation
        assertThat(result, sameBeanAs(expectedEventDto));

        ArgumentCaptor<EventReadStatus> eventReadStatusCaptor = ArgumentCaptor.forClass(EventReadStatus.class);
        verify(eventReadStatusDao).existsByUserIdAndEventId(userId, eventId);
        verify(eventReadStatusDao).save(eventReadStatusCaptor.capture());
        final EventReadStatus status = eventReadStatusCaptor.getValue();
        assertThat(status, sameBeanAs(expectedEventReadStatus));

        verify(careTeamSecurityUtils).isAssociated(userId, residentId, AccessRight.Code.EVENT_NOTIFICATIONS);
        verifySecurity(userId);
    }

    @Test(expected = PhrException.class)
    public void testGetEventAsProviderThrowsEventNotAssociated() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Expected objects
        final PatientDto patientDto = new PatientDto();
        patientDto.setId(residentId);
        final EventDto eventDto = new EventDto();
        eventDto.setPatient(patientDto);

        // Mockito expectations
        setUpMockitoExpectations(consumerId, false);
        when(eventService.getEventDetailsWithoutNotes(eventId)).thenReturn(eventDto);

        // Execute the method being tested
        eventsService.getEvent(userId, eventId);

        // Validation
        verifySecurity(userId);
    }

    @Test(expected = PhrException.class)
    public void testGetEventThrowsNotFound() {
        final Long consumerId = userId;

        // Mockito expectations
        setUpMockitoExpectations(consumerId);
        when(eventService.getEventDetailsWithoutNotes(eventId)).thenThrow(NullPointerException.class);

        // Execute the method being tested
        eventsService.getEvent(consumerId, eventId);

        // Validation
        verifySecurity(consumerId);
    }

    @Test(expected = PhrException.class)
    public void testGetEventThrowsNotAssociated() {
        final Long consumerId = userId;

        // Expected objects
        final PatientDto patientDto = new PatientDto();
        patientDto.setId(TestDataGenerator.randomIdExceptOf(residentId));
        final EventDto eventDto = new EventDto();
        eventDto.setPatient(patientDto);

        // Mockito expectations
        setUpMockitoExpectations(consumerId);
        when(careTeamSecurityUtils.isAssociated(consumerId, patientDto.getId(), AccessRight.Code.EVENT_NOTIFICATIONS)).thenReturn(false);
        when(eventService.getEventDetailsWithoutNotes(eventId)).thenReturn(eventDto);

        // Execute the method being tested
        eventsService.getEvent(consumerId, eventId);

        // Validation
        verify(careTeamSecurityUtils).isAssociated(consumerId, patientDto.getId(), AccessRight.Code.EVENT_NOTIFICATIONS);
        verifySecurity(consumerId);
    }

    @Test
    public void testGetEventNotifications() {
        final Long consumerId = userId;

        // Expected objects
        final Sort sort = new Sort(Sort.Direction.ASC, "contactName");
        final Pageable pageable = new PageRequest(0, 100, sort);
        final String email = TestDataGenerator.randomEmail();
        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();
        final String fullName = firstName + " " + lastName;
        final CareCoordinationResident ccResident = new CareCoordinationResident();
        ccResident.setId(residentId);
        ccResident.setFirstName(firstName);
        ccResident.setLastName(lastName);
        final EventType eventType = new EventType();
        eventType.setDescription("test event description");
        final Event event = new Event();
        event.setId(eventId);
        event.setResident(ccResident);
        event.setEventType(eventType);
        final User simpleUserMobile = new User();
        simpleUserMobile.setId(consumerId);
        simpleUserMobile.setEmail(email);
        final EventNotification eventNotification = new EventNotification();
        eventNotification.setEvent(event);
        eventNotification.setEmployee(null);
        eventNotification.setResponsibility(Responsibility.I);
        eventNotification.setNotificationType(NotificationType.EMAIL);
        eventNotification.setUserPatient(simpleUserMobile);
        eventNotification.setPersonName(fullName);
        eventNotification.setDescription("description");
        eventNotification.setDestination(email);
        eventNotification.setCareTeamRole(null);
        eventNotification.setSentDatetime(now);
        final String ROLE_LABEL = "Person receiving services";
        final CareTeamRoleDto ROLE_PERSON_RECEIVING_SERVICES = new CareTeamRoleDto(1L, ROLE_LABEL, CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES);

        final EventNotificationDto expectedEventNotificationDto = new EventNotificationDto();
        expectedEventNotificationDto.setResponsibility(ResponsibilityEnum.INFORMED);
        expectedEventNotificationDto.setNotificationType(NotificationType.EMAIL);
        expectedEventNotificationDto.setUserId(userId);
        expectedEventNotificationDto.setEditableContact(null);
        expectedEventNotificationDto.setDescription("description");
        expectedEventNotificationDto.setDestination(email);
        expectedEventNotificationDto.setDateTime(now.getTime());
        expectedEventNotificationDto.setContactName(fullName);
        expectedEventNotificationDto.setContactId(null);
        expectedEventNotificationDto.setCareTeamRole(ROLE_LABEL);
        expectedEventNotificationDto.setDataSource(null);
        // ignore details for consumer

        // Mockito expectations
        setUpMockitoExpectations(consumerId);
        when(eventDao.get(eventId)).thenReturn(event);
        when(eventNotificationDao.listByEventId(eventId, pageable, Boolean.TRUE)).thenReturn(Collections.singletonList(eventNotification));
        when(careTeamRoleService.get(CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES)).thenReturn(ROLE_PERSON_RECEIVING_SERVICES);
        when(avatarService.getPhotoUrl(userId)).thenReturn("url");

        // Execute the method being tested
        List<EventNotificationDto> result = eventsService.getEventNotifications(consumerId, eventId, pageable);

        // Validation
        assertThat(result, hasSize(1));
        assertThat(result.get(0), sameBeanAs(expectedEventNotificationDto));
        verify(careTeamRoleService).get(CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES);
        verifySecurity(consumerId);
    }

    @Test
    public void testGetEventNotificationsAsProvider() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Expected objects
        final Sort sort = new Sort(Sort.Direction.ASC, "contactName");
        final Pageable pageable = new PageRequest(0, 100, sort);
        final String phone = TestDataGenerator.randomPhone();
        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();
        final String patientFullName = firstName + " " + lastName;
        final String contactFullName = TestDataGenerator.randomFullName();
        final CareCoordinationResident ccResident = new CareCoordinationResident();
        ccResident.setId(residentId);
        ccResident.setFirstName(firstName);
        ccResident.setLastName(lastName);
        final String ROLE_LABEL = "Case Manager";
        final CareTeamRole ROLE_CASE_MANAGER = new CareTeamRole();
        ROLE_CASE_MANAGER.setId(1L);
        ROLE_CASE_MANAGER.setDisplayName(ROLE_LABEL + " (wrong)");
        ROLE_CASE_MANAGER.setName(ROLE_LABEL);
        ROLE_CASE_MANAGER.setCode(CareTeamRoleCode.ROLE_CASE_MANAGER);
        final EventType eventType = new EventType();
        eventType.setDescription("test event description");
        final Event event = new Event();
        event.setId(eventId);
        event.setResident(ccResident);
        event.setEventType(eventType);
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setDatabase(createDatabase());
        final EventNotification eventNotification = new EventNotification();
        eventNotification.setEvent(event);
        eventNotification.setEmployee(employee);
        eventNotification.setResponsibility(Responsibility.R);
        eventNotification.setNotificationType(NotificationType.SMS);
        eventNotification.setUserPatient(null);
        eventNotification.setPersonName(contactFullName);
        eventNotification.setDescription("description");
        eventNotification.setDestination(phone);
        eventNotification.setCareTeamRole(ROLE_CASE_MANAGER);
        eventNotification.setSentDatetime(now);
        final Long contactId = TestDataGenerator.randomId();
        final CareteamMemberDto ctm = new CareteamMemberDto();
        ctm.setId(contactId);
        ctm.setUserId(userId);
        ctm.setCareTeamRole(ROLE_LABEL);
        ctm.setEditable(Boolean.FALSE);
        ctm.setDataSource(new DataSourceDto());

        final EventNotificationDto expectedEventNotificationDto = new EventNotificationDto();
        expectedEventNotificationDto.setResponsibility(ResponsibilityEnum.RESPONSIBLE);
        expectedEventNotificationDto.setNotificationType(NotificationType.SMS);
        expectedEventNotificationDto.setUserId(userId);
        expectedEventNotificationDto.setEditableContact(Boolean.TRUE);
        expectedEventNotificationDto.setDescription("description");
        expectedEventNotificationDto.setDestination(phone);
        expectedEventNotificationDto.setDateTime(now.getTime());
        expectedEventNotificationDto.setContactName(contactFullName);
        expectedEventNotificationDto.setContactId(contactId);
        expectedEventNotificationDto.setCareTeamRole(ROLE_LABEL);
        expectedEventNotificationDto.setDetails(String.format(
                "You received this alert because you are assigned as the responsible party for event types of \"%s\" occur for %s",
                "test event description", patientFullName));
        expectedEventNotificationDto.setDataSource(new DataSourceDto());

        // Mockito expectations
        setUpMockitoExpectations(consumerId);
        when(eventDao.get(eventId)).thenReturn(event);
        when(eventNotificationDao.listByEventId(eventId, pageable, Boolean.TRUE)).thenReturn(Collections.singletonList(eventNotification));
        when(careTeamService.getResidentCareTeamMember(residentId, employeeId)).thenReturn(ctm);
        when(careTeamService.canManageAccessRights(contactId)).thenReturn(Boolean.TRUE);

        // Execute the method being tested
        List<EventNotificationDto> result = eventsService.getEventNotifications(userId, eventId, pageable);

        // Validation
        assertThat(result, hasSize(1));
        assertThat(result.get(0), sameBeanAs(expectedEventNotificationDto)
                .ignoring(DataSourceDto.class));
        verify(careTeamService).getResidentCareTeamMember(residentId, employeeId);
        verifySecurity(userId);
    }

    @Test
    public void testGetEventNotificationsAsProvider2() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Expected objects
        final Sort sort = new Sort(Sort.Direction.ASC, "contactName");
        final Pageable pageable = new PageRequest(0, 100, sort);
        final String phone = TestDataGenerator.randomPhone();
        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();
        final String patientFullName = firstName + " " + lastName;
        final String contactFullName = TestDataGenerator.randomFullName();
        final CareCoordinationResident ccResident = new CareCoordinationResident();
        ccResident.setId(residentId);
        ccResident.setFirstName(firstName);
        ccResident.setLastName(lastName);
        final String ROLE_LABEL = "Case Manager";
        final CareTeamRole ROLE_CASE_MANAGER = new CareTeamRole();
        ROLE_CASE_MANAGER.setId(1L);
        ROLE_CASE_MANAGER.setDisplayName(ROLE_LABEL + " (wrong)");
        ROLE_CASE_MANAGER.setName(ROLE_LABEL);
        ROLE_CASE_MANAGER.setCode(CareTeamRoleCode.ROLE_CASE_MANAGER);
        final EventType eventType = new EventType();
        eventType.setDescription("test event description");
        final Event event = new Event();
        event.setId(eventId);
        event.setResident(ccResident);
        event.setEventType(eventType);
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setDatabase(createDatabase());
        final EventNotification eventNotification = new EventNotification();
        eventNotification.setEvent(event);
        eventNotification.setEmployee(employee);
        eventNotification.setResponsibility(Responsibility.C);
        eventNotification.setNotificationType(NotificationType.SECURITY_MESSAGE);
        eventNotification.setUserPatient(null);
        eventNotification.setPersonName(contactFullName);
        eventNotification.setDescription("description");
        eventNotification.setDestination(phone);
        eventNotification.setCareTeamRole(ROLE_CASE_MANAGER);
        eventNotification.setSentDatetime(now);
        final Long contactId = TestDataGenerator.randomId();
        final CareteamMemberDto ctm = new CareteamMemberDto();
        ctm.setId(contactId);
        ctm.setUserId(userId);
        ctm.setCareTeamRole(ROLE_LABEL);
        ctm.setEditable(Boolean.TRUE);
        ctm.setDataSource(new DataSourceDto());

        final AccessRight accessRight = new AccessRight();

        final EventNotificationDto expectedEventNotificationDto = new EventNotificationDto();
        expectedEventNotificationDto.setResponsibility(ResponsibilityEnum.CONSULTED);
        expectedEventNotificationDto.setNotificationType(NotificationType.SECURITY_MESSAGE);
        expectedEventNotificationDto.setUserId(userId);
        expectedEventNotificationDto.setEditableContact(Boolean.TRUE);
        expectedEventNotificationDto.setDescription("description");
        expectedEventNotificationDto.setDestination(phone);
        expectedEventNotificationDto.setDateTime(now.getTime());
        expectedEventNotificationDto.setContactName(contactFullName);
        expectedEventNotificationDto.setContactId(contactId);
        expectedEventNotificationDto.setCareTeamRole(ROLE_LABEL);
        expectedEventNotificationDto.setDetails(String.format(
                "You received this alert because you are assigned as the responsible party for event types of \"%s\" occur for %s",
                "test event description", patientFullName));

        // Mockito expectations
        final Pair<User, User> users = setUpMockitoExpectations(consumerId);
        final Long employeeIdOfProvider = users.getFirst().getEmployeeId();
        when(eventDao.get(eventId)).thenReturn(event);
        when(eventNotificationDao.listByEventId(eventId, pageable, Boolean.TRUE)).thenReturn(Collections.singletonList(eventNotification));
        when(careTeamService.getResidentCareTeamMember(residentId, employeeId)).thenReturn(ctm);
        when(careTeamService.canManageAccessRights(contactId)).thenReturn(Boolean.TRUE);
        when(accessRightsService.getAccessRight(AccessRight.Code.EVENT_NOTIFICATIONS)).thenReturn(accessRight);
        when(residentCareTeamMemberDao.getCareTeamResidentIdsByEmployeeIdWithAccessRightsCheck(Collections.singleton(employeeIdOfProvider), accessRight))
                .thenReturn(Collections.singletonList(residentId));

        // Execute the method being tested
        List<EventNotificationDto> result = eventsService.getEventNotifications(userId, eventId, pageable);

        // Validation
        assertThat(result, hasSize(1));
        assertThat(result.get(0), sameBeanAs(expectedEventNotificationDto)
                .ignoring(DataSourceDto.class));
        verify(careTeamService).getResidentCareTeamMember(residentId, employeeId);
//        verify(residentCareTeamMemberDao).getCareTeamResidentIdsByEmployeeIdWithAccessRightsCheck(Collections.singleton(employeeIdOfProvider), accessRight);
        verifySecurity(userId);
    }

    @Test
    public void testGetEventNotificationsAsProvider3() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Expected objects
        final Sort sort = new Sort(Sort.Direction.ASC, "contactName");
        final Pageable pageable = new PageRequest(0, 100, sort);
        final String phone = TestDataGenerator.randomPhone();
        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();
        final String patientFullName = firstName + " " + lastName;
        final String contactFullName = TestDataGenerator.randomFullName();
        final CareCoordinationResident ccResident = new CareCoordinationResident();
        ccResident.setId(residentId);
        ccResident.setFirstName(firstName);
        ccResident.setLastName(lastName);
        final String ROLE_LABEL = "Case Manager";
        final CareTeamRole ROLE_CASE_MANAGER = new CareTeamRole();
        ROLE_CASE_MANAGER.setId(1L);
        ROLE_CASE_MANAGER.setDisplayName(ROLE_LABEL + " (wrong)");
        ROLE_CASE_MANAGER.setName(ROLE_LABEL);
        ROLE_CASE_MANAGER.setCode(CareTeamRoleCode.ROLE_CASE_MANAGER);
        final EventType eventType = new EventType();
        eventType.setDescription("test event description");
        final Event event = new Event();
        event.setId(eventId);
        event.setResident(ccResident);
        event.setEventType(eventType);
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setDatabase(createDatabase());
        final EventNotification eventNotification = new EventNotification();
        eventNotification.setEvent(event);
        eventNotification.setEmployee(employee);
        eventNotification.setResponsibility(Responsibility.A);
        eventNotification.setNotificationType(NotificationType.SMS);
        eventNotification.setUserPatient(null);
        eventNotification.setPersonName(contactFullName);
        eventNotification.setDescription("description");
        eventNotification.setDestination(phone);
        eventNotification.setCareTeamRole(ROLE_CASE_MANAGER);
        eventNotification.setSentDatetime(now);

        final EventNotificationDto expectedEventNotificationDto = new EventNotificationDto();
        expectedEventNotificationDto.setResponsibility(ResponsibilityEnum.ACCOUNTABLE);
        expectedEventNotificationDto.setNotificationType(NotificationType.SMS);
        expectedEventNotificationDto.setUserId(null);
        expectedEventNotificationDto.setEditableContact(null);
        expectedEventNotificationDto.setDescription("description");
        expectedEventNotificationDto.setDestination(phone);
        expectedEventNotificationDto.setDateTime(now.getTime());
        expectedEventNotificationDto.setContactName(contactFullName);
        expectedEventNotificationDto.setContactId(null);
        expectedEventNotificationDto.setCareTeamRole(ROLE_LABEL);
        expectedEventNotificationDto.setDetails(String.format(
                "You received this alert because you are assigned as the responsible party for event types of \"%s\" occur for %s",
                "test event description", patientFullName));

        // Mockito expectations
        final Pair<User, User> users = setUpMockitoExpectations(consumerId);
        final Long employeeIdOfProvider = users.getFirst().getEmployeeId();
        when(eventDao.get(eventId)).thenReturn(event);
        when(eventNotificationDao.listByEventId(eventId, pageable, Boolean.TRUE)).thenReturn(Collections.singletonList(eventNotification));
        when(careTeamService.getResidentCareTeamMember(residentId, employeeId)).thenThrow(EmptyResultDataAccessException.class);

        // Execute the method being tested
        List<EventNotificationDto> result = eventsService.getEventNotifications(userId, eventId, pageable);

        // Validation
        assertThat(result, hasSize(1));
        assertThat(result.get(0), sameBeanAs(expectedEventNotificationDto)
                .ignoring(DataSourceDto.class));
        verify(careTeamService).getResidentCareTeamMember(residentId, employeeId);
        verify(careTeamSecurityUtils).isAssociated(userId, residentId, AccessRight.Code.EVENT_NOTIFICATIONS);
        verifySecurity(userId);
    }

    @Test(expected = PhrException.class)
    public void testGetEventNotificationsAsProviderThrowsEventNotAssociated() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Expected objects
        final Sort sort = new Sort(Sort.Direction.ASC, "contactName");
        final Pageable pageable = new PageRequest(0, 100, sort);
        final CareCoordinationResident ccResident = new CareCoordinationResident();
        ccResident.setId(residentId);
        final String ROLE_LABEL = "Case Manager";
        final CareTeamRole ROLE_CASE_MANAGER = new CareTeamRole();
        ROLE_CASE_MANAGER.setId(1L);
        ROLE_CASE_MANAGER.setDisplayName(ROLE_LABEL + " (wrong)");
        ROLE_CASE_MANAGER.setName(ROLE_LABEL);
        ROLE_CASE_MANAGER.setCode(CareTeamRoleCode.ROLE_CASE_MANAGER);
        final EventType eventType = new EventType();
        eventType.setDescription("test event description");
        final Event event = new Event();
        event.setId(eventId);
        event.setResident(ccResident);
        event.setEventType(eventType);

        // Mockito expectations
        final Pair<User, User> users = setUpMockitoExpectations(consumerId, false);
        final Long employeeIdOfProvider = users.getFirst().getEmployeeId();
        when(eventDao.get(eventId)).thenReturn(event);

        // Execute the method being tested
        eventsService.getEventNotifications(userId, eventId, pageable);

        // Validation
        verify(careTeamSecurityUtils).isAssociated(userId, employeeIdOfProvider, AccessRight.Code.EVENT_NOTIFICATIONS);
        verifySecurity(userId);
    }

    @Test
    public void testGetEventNotificationsCount() {
        final Long consumerId = userId;

        // Mockito expectations
        setUpMockitoExpectations(consumerId);
        when(eventNotificationDao.countByEventId(eventId, Boolean.TRUE)).thenReturn(42L);

        // Execute the method being tested
        Long result = eventsService.getEventNotificationsCount(consumerId, eventId);

        // Validation
        assertEquals(result, (Long) 42L);
        verify(eventNotificationDao).countByEventId(eventId, Boolean.TRUE);
        verifySecurity(consumerId);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme