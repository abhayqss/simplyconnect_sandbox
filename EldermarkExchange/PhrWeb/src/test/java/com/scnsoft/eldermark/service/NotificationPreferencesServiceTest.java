package com.scnsoft.eldermark.service;

import com.google.common.collect.Lists;
import com.scnsoft.eldermark.dao.carecoordination.*;
import com.scnsoft.eldermark.dao.phr.UserMobileNotificationPreferencesDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.UserMobileNotificationPreferences;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.shared.web.entity.EventTypeDto;
import com.scnsoft.eldermark.shared.web.entity.EventTypeGroupDto;
import com.scnsoft.eldermark.web.entity.NotificationSettingsDto;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 6/16/2017.
 */
public class NotificationPreferencesServiceTest extends BaseServiceTest {

    @Mock
    private UserMobileNotificationPreferencesDao userMobileNotificationPreferencesDao;
    @Mock
    private EventTypeCareTeamRoleXrefDao eventTypeCareTeamRoleXrefDao;
    @Mock
    private EventTypeService eventTypeService;
    @Mock
    private CareReceiverService careReceiverService;
    @Mock
    private CareTeamMemberNotificationPreferencesDao careTeamMemberNotificationPreferencesDao;

    @InjectMocks
    private NotificationPreferencesService notificationPreferencesService;

    private final String[] EVENT_TYPE_CODES = {"SI", "ME"};
    private final String[] EVENT_TYPE_DESCRIPTIONS = {"Serious injury", "Medical emergency"};

    @Test
    public void testSetNotificationSettings() {
        // Expected objects
        final List<EventType> eventTypes = prepareEventTypes();

        final Map<NotificationType, Boolean> notificationChannels = new HashMap<>();
        notificationChannels.put(NotificationType.EMAIL, Boolean.FALSE);
        notificationChannels.put(NotificationType.SMS, Boolean.TRUE);
        notificationChannels.put(NotificationType.PUSH_NOTIFICATION, Boolean.TRUE);
        notificationChannels.put(NotificationType.SECURITY_MESSAGE, Boolean.FALSE);
        final Set<String> enabledEventTypes = new HashSet<>(Arrays.asList(EVENT_TYPE_CODES));
        final List<EventTypeDto> eventTypeDtos = prepareEventTypeDtos(enabledEventTypes);
        final NotificationSettingsDto notificationSettingsDto = new NotificationSettingsDto();
        notificationSettingsDto.setEventTypes(eventTypeDtos);
        notificationSettingsDto.setNotificationChannels(notificationChannels);

        final List<UserMobileNotificationPreferences> expectedNotificationPreferences = new ArrayList<>();
        UserMobileNotificationPreferences userMobileNotificationPreferences = new UserMobileNotificationPreferences();
        userMobileNotificationPreferences.setUserId(userId);
        userMobileNotificationPreferences.setEventType(eventTypes.get(0));
        userMobileNotificationPreferences.setResponsibility(Responsibility.I);
        userMobileNotificationPreferences.setNotificationType(NotificationType.PUSH_NOTIFICATION);
        expectedNotificationPreferences.add(userMobileNotificationPreferences);
        userMobileNotificationPreferences = new UserMobileNotificationPreferences();
        userMobileNotificationPreferences.setUserId(userId);
        userMobileNotificationPreferences.setEventType(eventTypes.get(1));
        userMobileNotificationPreferences.setResponsibility(Responsibility.I);
        userMobileNotificationPreferences.setNotificationType(NotificationType.PUSH_NOTIFICATION);
        expectedNotificationPreferences.add(userMobileNotificationPreferences);
        userMobileNotificationPreferences = new UserMobileNotificationPreferences();
        userMobileNotificationPreferences.setUserId(userId);
        userMobileNotificationPreferences.setEventType(eventTypes.get(0));
        userMobileNotificationPreferences.setResponsibility(Responsibility.I);
        userMobileNotificationPreferences.setNotificationType(NotificationType.SMS);
        expectedNotificationPreferences.add(userMobileNotificationPreferences);
        userMobileNotificationPreferences = new UserMobileNotificationPreferences();
        userMobileNotificationPreferences.setUserId(userId);
        userMobileNotificationPreferences.setEventType(eventTypes.get(1));
        userMobileNotificationPreferences.setResponsibility(Responsibility.I);
        userMobileNotificationPreferences.setNotificationType(NotificationType.SMS);
        expectedNotificationPreferences.add(userMobileNotificationPreferences);

        // Mockito expectations
        for (EventType eventType : eventTypes) {
            when(eventTypeService.getById(eventType.getId())).thenReturn(eventType);
        }
        when(eventTypeService.getSortedEventTypes(enabledEventTypes)).thenReturn(eventTypeDtos);
        when(userMobileNotificationPreferencesDao.save(anyCollectionOf(UserMobileNotificationPreferences.class))).then(new Answer<List<UserMobileNotificationPreferences>>() {
            @Override
            public List<UserMobileNotificationPreferences> answer(InvocationOnMock invocation) {
                final Iterable<UserMobileNotificationPreferences> firstArgument = invocation.getArgumentAt(0, Iterable.class);
                return Lists.newArrayList(firstArgument);
            }
        });

        // Execute the method being tested
        NotificationSettingsDto result = notificationPreferencesService.setNotificationSettings(userId, notificationSettingsDto);

        // Validation
        assertThat(result.getEventTypes(), contains(eventTypeDtos.toArray()));
        assertThat(result.getNotificationChannels().entrySet(), everyItem(isIn(notificationChannels.entrySet())));
        assertThat(notificationChannels.entrySet(), everyItem(isIn(result.getNotificationChannels().entrySet())));

        verify(userMobileNotificationPreferencesDao).deleteByUserId(userId);
        verify(userMobileNotificationPreferencesDao).save(anyCollection());
        verify(authentication, atLeastOnce()).getDetails();
    }

    @Test
    public void testSetNotificationSettingsMinimal() {
        // Expected objects
        final List<EventType> eventTypes = prepareEventTypes();

        final List<EventTypeDto> eventTypeDtos = prepareEventTypeDtos(null);
        final NotificationSettingsDto notificationSettingsDto = new NotificationSettingsDto();
        notificationSettingsDto.setEventTypes(new ArrayList<EventTypeDto>());
        notificationSettingsDto.setNotificationChannels(new HashMap<NotificationType, Boolean>());

        final Map<NotificationType, Boolean> expectedNotificationChannels = new TreeMap<>(new Comparator<NotificationType>() {
            @Override
            public int compare(NotificationType nt1, NotificationType nt2) {
                return String.valueOf(nt1).compareTo(String.valueOf(nt2));
            }
        });
        expectedNotificationChannels.put(NotificationType.EMAIL, Boolean.FALSE);
        expectedNotificationChannels.put(NotificationType.PUSH_NOTIFICATION, Boolean.FALSE);
        expectedNotificationChannels.put(NotificationType.SECURITY_MESSAGE, Boolean.FALSE);
        expectedNotificationChannels.put(NotificationType.SMS, Boolean.FALSE);

        // Mockito expectations
        for (EventType eventType : eventTypes) {
            when(eventTypeService.getById(eventType.getId())).thenReturn(eventType);
        }
        when(eventTypeService.getSortedEventTypes(new HashSet<String>())).thenReturn(eventTypeDtos);
        when(userMobileNotificationPreferencesDao.save(anyCollectionOf(UserMobileNotificationPreferences.class))).then(new Answer<List<UserMobileNotificationPreferences>>() {
            @Override
            public List<UserMobileNotificationPreferences> answer(InvocationOnMock invocation) {
                final Iterable<UserMobileNotificationPreferences> firstArgument = invocation.getArgumentAt(0, Iterable.class);
                return Lists.newArrayList(firstArgument);
            }
        });

        // Execute the method being tested
        NotificationSettingsDto result = notificationPreferencesService.setNotificationSettings(userId, notificationSettingsDto);

        // Validation
        assertThat(result.getEventTypes(), contains(eventTypeDtos.toArray()));
        assertThat(result.getNotificationChannels().entrySet(), everyItem(isIn(expectedNotificationChannels.entrySet())));
        assertThat(expectedNotificationChannels.entrySet(), everyItem(isIn(result.getNotificationChannels().entrySet())));

        verify(userMobileNotificationPreferencesDao).deleteByUserId(userId);
        verify(authentication, atLeastOnce()).getDetails();
    }

    @Test
    public void testSetDefaultNotificationSettings() {
        // Expected objects
        final List<EventType> eventTypes = prepareEventTypes();

        final Map<NotificationType, Boolean> notificationChannels = new HashMap<>();
        notificationChannels.put(NotificationType.EMAIL, Boolean.TRUE);
        notificationChannels.put(NotificationType.SMS, Boolean.FALSE);
        notificationChannels.put(NotificationType.PUSH_NOTIFICATION, Boolean.TRUE);
        notificationChannels.put(NotificationType.SECURITY_MESSAGE, Boolean.FALSE);
        // by default EventTypeService#getEventTypes() returns all DTOs with `enabled` = null
        final List<EventTypeDto> eventTypeDtos = prepareEventTypeDtos(null);
        eventTypeDtos.remove(2);
        final NotificationSettingsDto notificationSettingsDto = new NotificationSettingsDto();
        notificationSettingsDto.setEventTypes(eventTypeDtos);
        notificationSettingsDto.setNotificationChannels(notificationChannels);

        final List<UserMobileNotificationPreferences> expectedNotificationPreferences = new ArrayList<>();
        UserMobileNotificationPreferences userMobileNotificationPreferences;
        userMobileNotificationPreferences = new UserMobileNotificationPreferences();
        userMobileNotificationPreferences.setUserId(userId);
        userMobileNotificationPreferences.setEventType(eventTypes.get(0));
        userMobileNotificationPreferences.setResponsibility(Responsibility.I);
        userMobileNotificationPreferences.setNotificationType(NotificationType.EMAIL);
        expectedNotificationPreferences.add(userMobileNotificationPreferences);
        userMobileNotificationPreferences = new UserMobileNotificationPreferences();
        userMobileNotificationPreferences.setUserId(userId);
        userMobileNotificationPreferences.setEventType(eventTypes.get(1));
        userMobileNotificationPreferences.setResponsibility(Responsibility.I);
        userMobileNotificationPreferences.setNotificationType(NotificationType.EMAIL);
        expectedNotificationPreferences.add(userMobileNotificationPreferences);
        userMobileNotificationPreferences = new UserMobileNotificationPreferences();
        userMobileNotificationPreferences.setUserId(userId);
        userMobileNotificationPreferences.setEventType(eventTypes.get(0));
        userMobileNotificationPreferences.setResponsibility(Responsibility.I);
        userMobileNotificationPreferences.setNotificationType(NotificationType.PUSH_NOTIFICATION);
        expectedNotificationPreferences.add(userMobileNotificationPreferences);
        userMobileNotificationPreferences = new UserMobileNotificationPreferences();
        userMobileNotificationPreferences.setUserId(userId);
        userMobileNotificationPreferences.setEventType(eventTypes.get(1));
        userMobileNotificationPreferences.setResponsibility(Responsibility.I);
        userMobileNotificationPreferences.setNotificationType(NotificationType.PUSH_NOTIFICATION);
        expectedNotificationPreferences.add(userMobileNotificationPreferences);

        // Mockito expectations
        for (EventType eventType : eventTypes) {
            when(eventTypeService.getById(eventType.getId())).thenReturn(eventType);
        }
        when(userMobileNotificationPreferencesDao.save(anyCollectionOf(UserMobileNotificationPreferences.class))).then(new Answer<List<UserMobileNotificationPreferences>>() {
            @Override
            public List<UserMobileNotificationPreferences> answer(InvocationOnMock invocation) {
                final Iterable<UserMobileNotificationPreferences> firstArgument = invocation.getArgumentAt(0, Iterable.class);
                return Lists.newArrayList(firstArgument);
            }
        });
        when(eventTypeService.getEventTypes()).thenReturn(eventTypeDtos);

        // Execute the method being tested
        final List<UserMobileNotificationPreferences> result = notificationPreferencesService.setDefaultNotificationSettings(userId, false);

        // Validation
        assertThat(result, hasSize(expectedNotificationPreferences.size()));
        assertThat(result.get(0), sameBeanAs(expectedNotificationPreferences.get(0)));
        assertThat(result.get(1), sameBeanAs(expectedNotificationPreferences.get(1)));
        assertThat(result.get(2), sameBeanAs(expectedNotificationPreferences.get(2)));
        assertThat(result.get(3), sameBeanAs(expectedNotificationPreferences.get(3)));

        //verify(userMobileNotificationPreferencesDao).deleteByUserId(userId);
        verify(userMobileNotificationPreferencesDao).save(anyCollection());
    }

    @Test
    public void testGetNotificationSettings() {
        // Expected objects
        final List<EventType> eventTypes = prepareEventTypes();
        final List<UserMobileNotificationPreferences> notificationPreferences = new ArrayList<>();
        UserMobileNotificationPreferences userMobileNotificationPreferences = new UserMobileNotificationPreferences();
        userMobileNotificationPreferences.setUserId(userId);
        userMobileNotificationPreferences.setEventType(eventTypes.get(0));
        userMobileNotificationPreferences.setResponsibility(Responsibility.I);
        userMobileNotificationPreferences.setNotificationType(NotificationType.EMAIL);
        notificationPreferences.add(userMobileNotificationPreferences);
        userMobileNotificationPreferences = new UserMobileNotificationPreferences();
        userMobileNotificationPreferences.setUserId(userId);
        userMobileNotificationPreferences.setEventType(eventTypes.get(1));
        userMobileNotificationPreferences.setResponsibility(Responsibility.I);
        userMobileNotificationPreferences.setNotificationType(NotificationType.EMAIL);
        notificationPreferences.add(userMobileNotificationPreferences);
        userMobileNotificationPreferences = new UserMobileNotificationPreferences();
        userMobileNotificationPreferences.setUserId(userId);
        userMobileNotificationPreferences.setEventType(eventTypes.get(0));
        userMobileNotificationPreferences.setResponsibility(Responsibility.I);
        userMobileNotificationPreferences.setNotificationType(NotificationType.SMS);
        notificationPreferences.add(userMobileNotificationPreferences);
        userMobileNotificationPreferences = new UserMobileNotificationPreferences();
        userMobileNotificationPreferences.setUserId(userId);
        userMobileNotificationPreferences.setEventType(eventTypes.get(1));
        userMobileNotificationPreferences.setResponsibility(Responsibility.I);
        userMobileNotificationPreferences.setNotificationType(NotificationType.SMS);
        notificationPreferences.add(userMobileNotificationPreferences);

        final Map<NotificationType, Boolean> expectedNotificationChannels = new TreeMap<>(new Comparator<NotificationType>() {
            @Override
            public int compare(NotificationType nt1, NotificationType nt2) {
                return String.valueOf(nt1).compareTo(String.valueOf(nt2));
            }
        });
        expectedNotificationChannels.put(NotificationType.EMAIL, Boolean.TRUE);
        expectedNotificationChannels.put(NotificationType.PUSH_NOTIFICATION, Boolean.FALSE);
        expectedNotificationChannels.put(NotificationType.SECURITY_MESSAGE, Boolean.FALSE);
        expectedNotificationChannels.put(NotificationType.SMS, Boolean.TRUE);
        final Set<String> enabledEventTypes = new HashSet<>(Arrays.asList(EVENT_TYPE_CODES));
        final List<EventTypeDto> expectedEventTypes = prepareEventTypeDtos(enabledEventTypes);
        final NotificationSettingsDto expectedNotificationSettingsDto = new NotificationSettingsDto();
        expectedNotificationSettingsDto.setEventTypes(expectedEventTypes);
        expectedNotificationSettingsDto.setNotificationChannels(expectedNotificationChannels);

        // Mockito expectations
        when(userMobileNotificationPreferencesDao.getByUserId(userId)).thenReturn(notificationPreferences);
        when(eventTypeService.getSortedEventTypes(enabledEventTypes)).thenReturn(expectedEventTypes);

        // Execute the method being tested
        NotificationSettingsDto result = notificationPreferencesService.getNotificationSettings(userId);

        // Validation
        // Verify event types content and ordering
        assertThat(result.getEventTypes(), contains(expectedNotificationSettingsDto.getEventTypes().toArray()));
        // Verify notification channels content
        assertThat(result.getNotificationChannels().entrySet(), everyItem(isIn(expectedNotificationSettingsDto.getNotificationChannels().entrySet())));
        assertThat(expectedNotificationSettingsDto.getNotificationChannels().entrySet(), everyItem(isIn(result.getNotificationChannels().entrySet())));
        // Verify notification channels ordering
        assertThat(expectedNotificationSettingsDto.getNotificationChannels().entrySet(), contains(result.getNotificationChannels().entrySet().toArray()));
        verify(authentication, atLeastOnce()).getDetails();
    }

    @Test
    public void testGetPhrNotificationChannels() {
        Map<NotificationType, String> expectedChannels = new HashMap<NotificationType, String>() {{
            put(NotificationType.PUSH_NOTIFICATION, NotificationType.PUSH_NOTIFICATION.getDescription());
            put(NotificationType.SECURITY_MESSAGE, NotificationType.SECURITY_MESSAGE.getDescription());
            put(NotificationType.EMAIL, NotificationType.EMAIL.getDescription());
            put(NotificationType.SMS, NotificationType.SMS.getDescription());
        }};

        // Execute the method being tested
        final Map<NotificationType, String> phrNotificationChannels = NotificationPreferencesService.getPhrNotificationChannels();

        // Validation
        assertThat(phrNotificationChannels.entrySet(), everyItem(isIn(expectedChannels.entrySet())));
        assertThat(expectedChannels.entrySet(), everyItem(isIn(phrNotificationChannels.entrySet())));
    }

    @Test
    public void testCreateDefaultCareTeamMemberNotificationPreferences() {
        // Expected objects
        final Long careTeamRoleId = TestDataGenerator.randomId();
        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setId(careTeamRoleId);
        final CareTeamMember careTeamMember = new ResidentCareTeamMember();
        careTeamMember.setCareTeamRole(careTeamRole);
        final EventType eventType = prepareEventType(0);

        final EventTypeCareTeamRoleXref defaultXref = new EventTypeCareTeamRoleXref();
        defaultXref.setCareTeamRole(careTeamRole);
        defaultXref.setEventType(eventType);
        defaultXref.setResponsibility(Responsibility.R);

        final CareTeamMemberNotificationPreferences expectedPreferences = new CareTeamMemberNotificationPreferences();
        expectedPreferences.setCareTeamMember(careTeamMember);
        expectedPreferences.setEventType(eventType);
        expectedPreferences.setResponsibility(Responsibility.R);
        expectedPreferences.setNotificationType(NotificationType.EMAIL);
        final CareTeamMemberNotificationPreferences expectedPreferences2 = new CareTeamMemberNotificationPreferences();
        expectedPreferences2.setCareTeamMember(careTeamMember);
        expectedPreferences2.setEventType(eventType);
        expectedPreferences2.setResponsibility(Responsibility.R);
        expectedPreferences2.setNotificationType(NotificationType.PUSH_NOTIFICATION);

        // Mockito expectations
        when(eventTypeCareTeamRoleXrefDao.getResponsibilityForRole(careTeamRoleId)).thenReturn(Collections.singletonList(defaultXref));

        // Execute the method being tested
        notificationPreferencesService.createDefaultCareTeamMemberNotificationPreferences(careTeamMember);

        // Validation
        assertThat(careTeamMember.getCareTeamMemberNotificationPreferencesList(), hasSize(2));
        assertThat(careTeamMember.getCareTeamMemberNotificationPreferencesList(), containsInAnyOrder(
                sameBeanAs(expectedPreferences).ignoring(CareTeamMember.class),
                sameBeanAs(expectedPreferences2).ignoring(CareTeamMember.class)));
        verify(eventTypeCareTeamRoleXrefDao).getResponsibilityForRole(careTeamRoleId);
    }

    @Test
    public void testGetNotificationSettingsForCareReceiver() {
        // Expected objects
        final List<EventType> eventTypes = prepareEventTypes();
        final EventType eventType3 = new EventType();
        eventType3.setId(2L);
        eventType3.setCode("TE");
        eventType3.setDescription("Test event type");
        eventType3.setEventGroup(eventTypes.get(0).getEventGroup());

        final Long careTeamRoleId = TestDataGenerator.randomId();
        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setId(careTeamRoleId);
        final Long careReceiverId = TestDataGenerator.randomId();
        final ResidentCareTeamMember careTeamMember = new ResidentCareTeamMember();
        careTeamMember.setId(careReceiverId);
        careTeamMember.setCareTeamRole(careTeamRole);

        final List<EventTypeCareTeamRoleXref> defaultXrefs = new ArrayList<>();
        EventTypeCareTeamRoleXref defaultXref = new EventTypeCareTeamRoleXref();
        defaultXref.setCareTeamRole(careTeamRole);
        defaultXref.setEventType(eventTypes.get(0));
        defaultXref.setResponsibility(Responsibility.I);
        defaultXrefs.add(defaultXref);
        defaultXref = new EventTypeCareTeamRoleXref();
        defaultXref.setCareTeamRole(careTeamRole);
        defaultXref.setEventType(eventTypes.get(1));
        defaultXref.setResponsibility(Responsibility.A);
        defaultXrefs.add(defaultXref);
        defaultXref = new EventTypeCareTeamRoleXref();
        defaultXref.setCareTeamRole(careTeamRole);
        defaultXref.setEventType(eventType3);
        defaultXref.setResponsibility(Responsibility.N);
        defaultXrefs.add(defaultXref);

        final List<CareTeamMemberNotificationPreferences> notificationPreferences = new ArrayList<>();
        CareTeamMemberNotificationPreferences np = new CareTeamMemberNotificationPreferences();
        np.setCareTeamMember(careTeamMember);
        np.setEventType(eventTypes.get(0));
        np.setResponsibility(Responsibility.I);
        np.setNotificationType(NotificationType.EMAIL);
        notificationPreferences.add(np);
        np = new CareTeamMemberNotificationPreferences();
        np.setCareTeamMember(careTeamMember);
        np.setEventType(eventTypes.get(1));
        np.setResponsibility(Responsibility.A);
        np.setNotificationType(NotificationType.SMS);
        notificationPreferences.add(np);
        np = new CareTeamMemberNotificationPreferences();
        np.setCareTeamMember(careTeamMember);
        np.setEventType(eventType3);
        np.setResponsibility(Responsibility.N);
        np.setNotificationType(NotificationType.EMAIL);
        notificationPreferences.add(np);
        careTeamMember.setCareTeamMemberNotificationPreferencesList(notificationPreferences);

        final Map<NotificationType, Boolean> expectedNotificationChannels = new TreeMap<>(new Comparator<NotificationType>() {
            @Override
            public int compare(NotificationType nt1, NotificationType nt2) {
                return String.valueOf(nt1).compareTo(String.valueOf(nt2));
            }
        });
        expectedNotificationChannels.put(NotificationType.EMAIL, Boolean.TRUE);
        expectedNotificationChannels.put(NotificationType.PUSH_NOTIFICATION, Boolean.FALSE);
        expectedNotificationChannels.put(NotificationType.SECURITY_MESSAGE, Boolean.FALSE);
        expectedNotificationChannels.put(NotificationType.SMS, Boolean.TRUE);
        final Set<String> enabledEventTypes = new HashSet<>(Arrays.asList(EVENT_TYPE_CODES));
        final List<EventTypeDto> expectedEventTypes = prepareEventTypeDtos(enabledEventTypes);
        expectedEventTypes.get(0).setEditable(Boolean.TRUE);
        expectedEventTypes.get(1).setEditable(Boolean.TRUE);
        expectedEventTypes.get(2).setEditable(Boolean.FALSE);
        final NotificationSettingsDto expectedNotificationSettingsDto = new NotificationSettingsDto();
        expectedNotificationSettingsDto.setEventTypes(expectedEventTypes);
        expectedNotificationSettingsDto.setNotificationChannels(expectedNotificationChannels);

        // Mockito expectations
        when(careReceiverService.getCareReceiverOrThrow(userId, careReceiverId)).thenReturn(careTeamMember);
        when(eventTypeService.getSortedEventTypes(enabledEventTypes)).thenReturn(expectedEventTypes);
        when(eventTypeService.getSortedEventTypes(enabledEventTypes, defaultXrefs)).thenReturn(expectedEventTypes);
        when(eventTypeCareTeamRoleXrefDao.getResponsibilityForRole(careTeamRoleId)).thenReturn(defaultXrefs);

        // Execute the method being tested
        NotificationSettingsDto result = notificationPreferencesService.getNotificationSettings(userId, careReceiverId);

        // Validation
        // Verify event types content and ordering
        assertThat(result.getEventTypes(), contains(expectedNotificationSettingsDto.getEventTypes().toArray()));
        // Verify notification channels content
        assertThat(result.getNotificationChannels().entrySet(), everyItem(isIn(expectedNotificationSettingsDto.getNotificationChannels().entrySet())));
        assertThat(expectedNotificationSettingsDto.getNotificationChannels().entrySet(), everyItem(isIn(result.getNotificationChannels().entrySet())));
        // Verify notification channels ordering
        assertThat(expectedNotificationSettingsDto.getNotificationChannels().entrySet(), contains(result.getNotificationChannels().entrySet().toArray()));
        verify(authentication, atLeastOnce()).getDetails();
    }

    @Test
    public void testSetNotificationSettingsForCareReceiver() {
        // Expected objects
        final List<EventType> eventTypes = prepareEventTypes();
        final EventType eventType3 = new EventType();
        eventType3.setId(2L);
        eventType3.setCode("TE");
        eventType3.setDescription("Test event type");
        eventType3.setEventGroup(eventTypes.get(0).getEventGroup());
        eventTypes.add(eventType3);

        final String email = TestDataGenerator.randomEmail();
        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();

        final Employee employee = createEmployee(email, firstName, lastName, null);
        final Long careTeamRoleId = TestDataGenerator.randomId();
        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setId(careTeamRoleId);
        final Long careReceiverId = TestDataGenerator.randomId();
        final ResidentCareTeamMember careTeamMember = new ResidentCareTeamMember();
        careTeamMember.setId(careReceiverId);
        careTeamMember.setCareTeamRole(careTeamRole);
        careTeamMember.setEmployee(employee);

        final List<EventTypeCareTeamRoleXref> defaultXrefs = new ArrayList<>();
        EventTypeCareTeamRoleXref defaultXref = new EventTypeCareTeamRoleXref();
        defaultXref.setCareTeamRole(careTeamRole);
        defaultXref.setEventType(eventTypes.get(0));
        defaultXref.setResponsibility(Responsibility.I);
        defaultXrefs.add(defaultXref);
        defaultXref = new EventTypeCareTeamRoleXref();
        defaultXref.setCareTeamRole(careTeamRole);
        defaultXref.setEventType(eventTypes.get(1));
        defaultXref.setResponsibility(Responsibility.A);
        defaultXrefs.add(defaultXref);
        defaultXref = new EventTypeCareTeamRoleXref();
        defaultXref.setCareTeamRole(careTeamRole);
        defaultXref.setEventType(eventTypes.get(2));
        defaultXref.setResponsibility(Responsibility.N);
        defaultXrefs.add(defaultXref);

        final List<CareTeamMemberNotificationPreferences> notificationPreferences = new ArrayList<>();
        CareTeamMemberNotificationPreferences np = new CareTeamMemberNotificationPreferences();
        np.setCareTeamMember(careTeamMember);
        np.setEventType(eventTypes.get(0));
        np.setResponsibility(Responsibility.I);
        np.setNotificationType(NotificationType.EMAIL);
        notificationPreferences.add(np);
        np = new CareTeamMemberNotificationPreferences();
        np.setCareTeamMember(careTeamMember);
        np.setEventType(eventTypes.get(1));
        np.setResponsibility(Responsibility.A);
        np.setNotificationType(NotificationType.SMS);
        notificationPreferences.add(np);
        np = new CareTeamMemberNotificationPreferences();
        np.setCareTeamMember(careTeamMember);
        np.setEventType(eventTypes.get(2));
        np.setResponsibility(Responsibility.N);
        np.setNotificationType(NotificationType.EMAIL);
        notificationPreferences.add(np);
        careTeamMember.setCareTeamMemberNotificationPreferencesList(notificationPreferences);

        final Map<NotificationType, Boolean> newNotificationChannels = new TreeMap<>(new Comparator<NotificationType>() {
            @Override
            public int compare(NotificationType nt1, NotificationType nt2) {
                return String.valueOf(nt1).compareTo(String.valueOf(nt2));
            }
        });
        newNotificationChannels.put(NotificationType.EMAIL, Boolean.FALSE);
        newNotificationChannels.put(NotificationType.PUSH_NOTIFICATION, Boolean.TRUE);
        newNotificationChannels.put(NotificationType.SECURITY_MESSAGE, Boolean.FALSE);
        newNotificationChannels.put(NotificationType.SMS, Boolean.FALSE);
        final Set<String> enabledEventTypes = new HashSet<>(Arrays.asList(EVENT_TYPE_CODES));
        final List<EventTypeDto> expectedEventTypes = prepareEventTypeDtos(enabledEventTypes);
        expectedEventTypes.get(0).setEditable(Boolean.TRUE);
        expectedEventTypes.get(1).setEditable(Boolean.TRUE);
        expectedEventTypes.get(2).setEditable(Boolean.FALSE);
        final NotificationSettingsDto newNotificationSettingsDto = new NotificationSettingsDto();
        newNotificationSettingsDto.setEventTypes(expectedEventTypes);
        newNotificationSettingsDto.setNotificationChannels(newNotificationChannels);

        // Mockito expectations
        when(careReceiverService.getCareReceiverOrThrow(userId, careReceiverId)).thenReturn(careTeamMember);
        when(eventTypeService.getSortedEventTypes(enabledEventTypes)).thenReturn(expectedEventTypes);
        when(eventTypeService.getSortedEventTypes(enabledEventTypes, defaultXrefs)).thenReturn(expectedEventTypes);
        when(eventTypeCareTeamRoleXrefDao.getResponsibilityForRole(careTeamRoleId)).thenReturn(defaultXrefs);
        for (EventType eventType : eventTypes) {
            when(eventTypeService.getById(eventType.getId())).thenReturn(eventType);
        }

        // Execute the method being tested
        NotificationSettingsDto result = notificationPreferencesService.setNotificationSettings(userId, careReceiverId, newNotificationSettingsDto);

        // Validation
        // Verify event types content and ordering
        assertThat(result.getEventTypes(), contains(newNotificationSettingsDto.getEventTypes().toArray()));
        // Verify notification channels content
        assertThat(result.getNotificationChannels().entrySet(), everyItem(isIn(newNotificationSettingsDto.getNotificationChannels().entrySet())));
        assertThat(newNotificationSettingsDto.getNotificationChannels().entrySet(), everyItem(isIn(result.getNotificationChannels().entrySet())));
        // Verify notification channels ordering
        assertThat(newNotificationSettingsDto.getNotificationChannels().entrySet(), contains(result.getNotificationChannels().entrySet().toArray()));

        verify(careTeamMemberNotificationPreferencesDao).deleteNotificationPreferences(careReceiverId);
        verify(careTeamMemberNotificationPreferencesDao, times(1)).save(Matchers.anyCollectionOf(CareTeamMemberNotificationPreferences.class));
        verify(careTeamMemberNotificationPreferencesDao).flush();
        verify(authentication, atLeastOnce()).getDetails();
    }

    @Test(expected = PhrException.class)
    public void testSetNotificationSettingsForCareReceiverThrowsNoSecureEmailForNotification() {
        // Expected objects
        final List<EventType> eventTypes = prepareEventTypes();
        final EventType eventType3 = new EventType();
        eventType3.setId(2L);
        eventType3.setCode("TE");
        eventType3.setDescription("Test event type");
        eventType3.setEventGroup(eventTypes.get(0).getEventGroup());
        eventTypes.add(eventType3);

        final String email = TestDataGenerator.randomEmail();
        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();

        final Employee employee = createEmployee(email, firstName, lastName, null);
        final Long careTeamRoleId = TestDataGenerator.randomId();
        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setId(careTeamRoleId);
        final Long careReceiverId = TestDataGenerator.randomId();
        final ResidentCareTeamMember careTeamMember = new ResidentCareTeamMember();
        careTeamMember.setId(careReceiverId);
        careTeamMember.setCareTeamRole(careTeamRole);
        careTeamMember.setEmployee(employee);

        final List<EventTypeCareTeamRoleXref> defaultXrefs = new ArrayList<>();
        EventTypeCareTeamRoleXref defaultXref = new EventTypeCareTeamRoleXref();
        defaultXref.setCareTeamRole(careTeamRole);
        defaultXref.setEventType(eventTypes.get(0));
        defaultXref.setResponsibility(Responsibility.I);
        defaultXrefs.add(defaultXref);
        defaultXref = new EventTypeCareTeamRoleXref();
        defaultXref.setCareTeamRole(careTeamRole);
        defaultXref.setEventType(eventTypes.get(1));
        defaultXref.setResponsibility(Responsibility.A);
        defaultXrefs.add(defaultXref);
        defaultXref = new EventTypeCareTeamRoleXref();
        defaultXref.setCareTeamRole(careTeamRole);
        defaultXref.setEventType(eventTypes.get(2));
        defaultXref.setResponsibility(Responsibility.N);
        defaultXrefs.add(defaultXref);

        final List<CareTeamMemberNotificationPreferences> notificationPreferences = new ArrayList<>();
        CareTeamMemberNotificationPreferences np = new CareTeamMemberNotificationPreferences();
        np.setCareTeamMember(careTeamMember);
        np.setEventType(eventTypes.get(0));
        np.setResponsibility(Responsibility.I);
        np.setNotificationType(NotificationType.EMAIL);
        notificationPreferences.add(np);
        np = new CareTeamMemberNotificationPreferences();
        np.setCareTeamMember(careTeamMember);
        np.setEventType(eventTypes.get(1));
        np.setResponsibility(Responsibility.A);
        np.setNotificationType(NotificationType.SMS);
        notificationPreferences.add(np);
        np = new CareTeamMemberNotificationPreferences();
        np.setCareTeamMember(careTeamMember);
        np.setEventType(eventTypes.get(2));
        np.setResponsibility(Responsibility.N);
        np.setNotificationType(NotificationType.EMAIL);
        notificationPreferences.add(np);
        careTeamMember.setCareTeamMemberNotificationPreferencesList(notificationPreferences);

        final Map<NotificationType, Boolean> expectedNotificationChannels = new TreeMap<>(new Comparator<NotificationType>() {
            @Override
            public int compare(NotificationType nt1, NotificationType nt2) {
                return String.valueOf(nt1).compareTo(String.valueOf(nt2));
            }
        });
        expectedNotificationChannels.put(NotificationType.EMAIL, Boolean.FALSE);
        expectedNotificationChannels.put(NotificationType.PUSH_NOTIFICATION, Boolean.TRUE);
        expectedNotificationChannels.put(NotificationType.SECURITY_MESSAGE, Boolean.TRUE);
        expectedNotificationChannels.put(NotificationType.SMS, Boolean.FALSE);
        final Set<String> enabledEventTypes = new HashSet<>(Arrays.asList(EVENT_TYPE_CODES));
        final List<EventTypeDto> expectedEventTypes = prepareEventTypeDtos(enabledEventTypes);
        expectedEventTypes.get(0).setEditable(Boolean.TRUE);
        expectedEventTypes.get(1).setEditable(Boolean.TRUE);
        expectedEventTypes.get(2).setEditable(Boolean.FALSE);
        final NotificationSettingsDto newNotificationSettingsDto = new NotificationSettingsDto();
        newNotificationSettingsDto.setEventTypes(expectedEventTypes);
        newNotificationSettingsDto.setNotificationChannels(expectedNotificationChannels);

        // Mockito expectations
        when(careReceiverService.getCareReceiverOrThrow(userId, careReceiverId)).thenReturn(careTeamMember);
        when(eventTypeService.getSortedEventTypes(enabledEventTypes)).thenReturn(expectedEventTypes);
        when(eventTypeService.getSortedEventTypes(enabledEventTypes, defaultXrefs)).thenReturn(expectedEventTypes);
        when(eventTypeCareTeamRoleXrefDao.getResponsibilityForRole(careTeamRoleId)).thenReturn(defaultXrefs);
        for (EventType eventType : eventTypes) {
            when(eventTypeService.getById(eventType.getId())).thenReturn(eventType);
        }

        // Execute the method being tested
        notificationPreferencesService.setNotificationSettings(userId, careReceiverId, newNotificationSettingsDto);
    }

    // Utility methods

    private List<EventType> prepareEventTypes() {
        List<EventType> eventTypes = new ArrayList<>();
        for (int i = EVENT_TYPE_CODES.length - 1; i >= 0; --i) {
            EventType eventType = prepareEventType(i);
            eventTypes.add(eventType);
        }
        return eventTypes;
    }

    private EventType prepareEventType(int i) {
        EventGroup eventGroup = new EventGroup();
        eventGroup.setId((long) i);
        eventGroup.setPriority(i);
        EventType eventType = new EventType();
        eventType.setId((long) i);
        eventType.setCode(EVENT_TYPE_CODES[i]);
        eventType.setDescription(EVENT_TYPE_DESCRIPTIONS[i]);
        eventType.setEventGroup(eventGroup);
        return eventType;
    }

    private List<EventTypeDto> prepareEventTypeDtos(Set<String> enabledEventTypes) {
        List<EventTypeDto> eventTypes = new ArrayList<>();
        for (int i = EVENT_TYPE_CODES.length - 1; i >= 0; --i) {
            EventTypeDto eventType = prepareEventTypeDto(i, enabledEventTypes);
            eventTypes.add(eventType);
        }

        final EventTypeDto disabledEventTypeDto = new EventTypeDto();
        disabledEventTypeDto.setId(2L);
        disabledEventTypeDto.setEnabled(Boolean.FALSE);
        disabledEventTypeDto.setCode("TE");
        disabledEventTypeDto.setDescription("Test event type");
        eventTypes.add(disabledEventTypeDto);

        return eventTypes;
    }

    private EventTypeDto prepareEventTypeDto(int i, Set<String> enabledEventTypes) {
        EventTypeGroupDto eventTypeGroupDto = new EventTypeGroupDto();
        eventTypeGroupDto.setId((long) i);
        eventTypeGroupDto.setPriority(i);
        EventTypeDto eventType = new EventTypeDto();
        eventType.setId((long) i);
        eventType.setCode(EVENT_TYPE_CODES[i]);
        eventType.setDescription(EVENT_TYPE_DESCRIPTIONS[i]);
        eventType.setEventGroup(eventTypeGroupDto);
        if (enabledEventTypes != null) {
            eventType.setEnabled(enabledEventTypes.contains(EVENT_TYPE_CODES[i]));
        }
        return eventType;
    }

}
