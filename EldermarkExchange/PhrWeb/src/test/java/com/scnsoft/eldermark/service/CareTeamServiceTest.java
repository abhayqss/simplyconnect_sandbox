package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.EventReadStatusDao;
import com.scnsoft.eldermark.dao.carecoordination.CareTeamRoleDao;
import com.scnsoft.eldermark.dao.carecoordination.ResidentCareTeamMemberDao;
import com.scnsoft.eldermark.dao.carecoordination.Responsibility;
import com.scnsoft.eldermark.dao.phr.CareTeamRelationDao;
import com.scnsoft.eldermark.dao.phr.CareTeamRelationshipDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.*;
import com.scnsoft.eldermark.services.consana.ResidentUpdateQueueProducer;
import com.scnsoft.eldermark.services.consana.model.ResidentUpdateType;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.service.internal.EmployeeSupplierFactory;
import com.scnsoft.eldermark.service.internal.MockEmployeeSupplier;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.services.phr.AccessRightsService;
import com.scnsoft.eldermark.shared.ccd.AddressDto;
import com.scnsoft.eldermark.shared.ccd.NameDto;
import com.scnsoft.eldermark.shared.ccd.PersonDto;
import com.scnsoft.eldermark.shared.ccd.TelecomDto;
import com.scnsoft.eldermark.shared.phr.utils.Normalizer;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.util.MockitoAnswers;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.*;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 6/19/2017.
 */
public class CareTeamServiceTest extends BaseServiceTest {

    @Mock
    private UserDao userDao;
    @Mock
    private UserResidentRecordsDao userResidentRecordsDao;
    @Mock
    private UserRegistrationApplicationService userRegistrationApplicationService;
    @Mock
    private ProfileService profileService;
    @Mock
    private AccessRightsService accessRightsService;
    @Mock
    private ResidentCareTeamMemberDao residentCareTeamMemberDao;
    //@Mock
    //private EmployeeDao employeeDao;
    @Mock
    private CareTeamRoleDao careTeamRoleDao;
    @Mock
    private CareTeamRelationDao careTeamRelationDao;
    @Mock
    private CareTeamRelationshipDao careTeamRelationshipDao;
    @Mock
    private EventReadStatusDao eventReadStatusDao;
    @Mock
    private PhysiciansService physiciansService;
    @Mock
    private ActivityService activityService;
    @Mock
    private ContactService contactService;
    @Mock
    private PhrResidentService phrResidentService;
    @Mock
    private AvatarService avatarService;
    @Mock
    private PrivilegesService privilegesService;
    @Mock
    private NotificationPreferencesService notificationPreferencesService;
    @Mock
    private NotificationsFacade notificationsFacade;
    @Mock
    private DatabasesService databasesService;
    @Mock
    private CareTeamSecurityUtils careTeamSecurityUtils;
    @Mock
    private EmployeeSupplierFactory employeeProviderFactory;
    @Mock
    private ResidentUpdateQueueProducer residentUpdateQueueProducer;

    @InjectMocks
    private CareTeamService careTeamService;
    @SuppressWarnings("unused")
    @InjectMocks
    private ActivityService mockActivityService;

    // Shared test data
    private final Long contactId = TestDataGenerator.randomId();
    private final Long providerUserId = TestDataGenerator.randomIdExceptOf(userId);
    private final String ssn = TestDataGenerator.randomValidSsn();
    private final String phone = TestDataGenerator.randomPhone();
    private final String email = TestDataGenerator.randomEmail();
    private final String phoneNormalized = Normalizer.normalizePhone(phone);
    private final String emailNormalized = Normalizer.normalizeEmail(email);
    private final String firstName = TestDataGenerator.randomName();
    private final String lastName = TestDataGenerator.randomName();

    @Before
    public void injectDozer() {
        final DozerBeanMapper dozer = new DozerBeanMapper();
        careTeamService.setDozer(dozer);
    }

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID (current): %d\nCare team member ID (Contact ID): %d\nUser ID (Provider): %d\nSSN (invitee): %s\nPhone (invitee): %s\nEmail (invitee): %s\nFirst name (invitee): %s\nLast name (invitee): %s\n\n",
                userId, contactId, providerUserId, ssn, phone, email, firstName, lastName);
    }

    private User setUpMockitoExpectations(Long consumerUserId) {
        final User user = super.createConsumer(consumerUserId);

        when(userResidentRecordsDao.getActiveResidentIdsByUserId(consumerUserId)).thenReturn(activeResidentIds);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerUserId)).thenReturn(allResidentIds);
        when(userDao.findOne(consumerUserId)).thenReturn(user);
        when(userDao.getOne(consumerUserId)).thenReturn(user);
        when(profileService.isPatient(user)).thenReturn(Boolean.TRUE);
        if (consumerUserId.equals(userId)) {
            when(careTeamSecurityUtils.getCurrentUser()).thenReturn(user);
        }
        when(avatarService.getPhotoUrl(consumerUserId)).thenReturn("url" + consumerUserId);
        when(phrResidentService.findAssociatedResidentInUnaffiliated(consumerUserId)).thenReturn(user.getResident());
        when(phrResidentService.findAssociatedResident(user.getSsn(), user.getPhone(), user.getEmail(), user.getFirstName(), user.getLastName()))
                .thenReturn(user.getResident());

        return user;
    }

    private void whenAskedForEmployeeThenReturn(Employee employee) {
        /*
        if (employee == null) {
            when(employeeDao.getEmployeesByData(email, phone)).thenReturn(Collections.<Employee>emptyList());
            when(employeeDao.getEmployeesByData(null, email, phone, firstName, lastName)).thenReturn(Collections.<Employee>emptyList());
        } else {
            when(employeeDao.getEmployeesByData(email, phone)).thenReturn(Collections.singletonList(employee));
            when(employeeDao.getEmployeesByData(null, email, phone, firstName, lastName)).thenReturn(Collections.singletonList(employee));
        } */

        final MockEmployeeSupplier employeeProvider = new MockEmployeeSupplier(employee);
        when(employeeProviderFactory.getEmployeeSupplier(email, phone, firstName, lastName)).thenReturn(employeeProvider);
        when(employeeProviderFactory.getUnaffiliatedEmployeeSupplier(email, phone, firstName, lastName)).thenReturn(employeeProvider);
        when(employeeProviderFactory.getEmployeeSupplier(anyCollectionOf(Long.class), eq(email), eq(phone), eq(firstName), eq(lastName)))
                .thenReturn(employeeProvider);
    }

    private User setUpMockitoExpectationsAsProvider(Long providerUserId) {
        final User user = super.createProvider(providerUserId);

        when(userDao.findOne(providerUserId)).thenReturn(user);
        when(userDao.getOne(providerUserId)).thenReturn(user);
        when(profileService.isGuardian(user)).thenReturn(Boolean.TRUE);
        if (providerUserId.equals(userId)) {
            when(careTeamSecurityUtils.getCurrentUser()).thenReturn(user);
        }

        return user;
    }

    @Test
    public void testSetCareTeamMemberEmergency() {
        // Expected objects
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setEmergencyContact(Boolean.FALSE);

        // Mockito expectations
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        setUpMockitoExpectations(userId);

        // Execute the method being tested
        Boolean result = careTeamService.setCareTeamMemberEmergency(userId, contactId, true);

        // Validation
        assertEquals(Boolean.TRUE, result);
        assertEquals(Boolean.TRUE, rctm.getEmergencyContact());
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
        verify(privilegesService, never()).canSetCareTeamMemberEmergency();
    }

    @Test
    public void testSetCareTeamMemberEmergencyAsProvider() {
        // Expected objects
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setEmergencyContact(Boolean.FALSE);

        // Mockito expectations
        setUpMockitoExpectationsAsProvider(userId);
        setUpMockitoExpectations(consumerId);
        when(privilegesService.canSetCareTeamMemberEmergency()).thenReturn(Boolean.TRUE);
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);

        // Execute the method being tested
        Boolean result = careTeamService.setCareTeamMemberEmergency(consumerId, contactId, true);

        // Validation
        assertEquals(Boolean.TRUE, result);
        assertEquals(Boolean.TRUE, rctm.getEmergencyContact());
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(consumerId, AccessRight.Code.MY_CT_VISIBILITY);
        verify(privilegesService).canSetCareTeamMemberEmergency();
    }

    @Test(expected = PhrException.class)
    public void testSetCareTeamMemberEmergencyAsProviderThrowsAccessForbidden() {
        // Expected objects
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Mockito expectations
        setUpMockitoExpectationsAsProvider(userId);
        setUpMockitoExpectations(consumerId);
        when(privilegesService.canSetCareTeamMemberEmergency()).thenReturn(Boolean.FALSE);

        // Execute the method being tested
        careTeamService.setCareTeamMemberEmergency(consumerId, contactId, true);

        // Validation
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
        verify(privilegesService).canSetCareTeamMemberEmergency();
    }

    @Test(expected = PhrException.class)
    public void testSetCareTeamMemberEmergencyThrowsNotAssociated() {
        // Expected objects
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident2);
        rctm.setResidentId(residentId2);
        rctm.setEmergencyContact(Boolean.FALSE);

        // Mockito expectations
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        setUpMockitoExpectations(userId);

        // Execute the method being tested
        careTeamService.setCareTeamMemberEmergency(userId, contactId, true);
    }

    @Test
    public void testGetUserCareTeamMembers() {
        // Expected objects
        final User provider = super.createProvider(providerUserId);
        setUpMockitoExpectations(userId);

        final CareTeamRelation careTeamRelation = new CareTeamRelation();
        careTeamRelation.setCode(CareTeamRelation.Relation.FRIEND);
        final CareTeamRelationship careTeamRelationship = new CareTeamRelationship();
        careTeamRelationship.setCode(CareTeamRelationship.Relationship.FRIEND_FAMILY);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setEmergencyContact(Boolean.FALSE);
        rctm.setCreatedByResidentId(residentId);
        rctm.setCareTeamRole(provider.getEmployee().getCareTeamRole());
        rctm.setCareTeamRelation(careTeamRelation);
        rctm.setCareTeamRelationship(careTeamRelationship);
        rctm.setEmployee(provider.getEmployee());

        final CareteamMemberDto expectedDto = new CareteamMemberDto();
        expectedDto.setId(contactId);
        expectedDto.setUserId(providerUserId);
        expectedDto.setPhotoUrl("url" + providerUserId);
        expectedDto.setEditable(Boolean.TRUE);
        expectedDto.setCareTeamRole(BEHAVIORAL_HEALTH);
        expectedDto.setRelation(CareTeamRelation.Relation.FRIEND);
        expectedDto.setRelationship(CareTeamRelationship.Relationship.FRIEND_FAMILY);
        expectedDto.setEmergencyContact(Boolean.FALSE);
        expectedDto.setInvitationStatus(InvitationStatus.ACTIVE);
        final PersonDto person = new PersonDto();
        person.setNames(Collections.<NameDto>singletonList(null));
        person.setAddresses(Collections.<AddressDto>emptyList());
        expectedDto.setPerson(person);
        expectedDto.setDataSource(new DataSourceDto());

        // Mockito expectations
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        when(residentCareTeamMemberDao.getCareTeamMembers(activeResidentIds, null, null))
                .thenReturn(Collections.singletonList(rctm));
        when(userDao.getFirstByEmployee(provider.getEmployee())).thenReturn(provider);
        when(avatarService.getPhotoUrl(providerUserId)).thenReturn("url" + providerUserId);

        // Execute the method being tested
        List<CareteamMemberDto> result = careTeamService.getUserCareTeamMembers(userId);

        // Validation
        assertThat(result, hasSize(1));
        assertThat(result.get(0), sameBeanAs(expectedDto)
                .ignoring(DataSourceDto.class).ignoring(NameDto.class));
    }

    @Test
    public void testGetUserCareTeamMembersBrief() {
        // Expected objects
        final User provider = super.createProvider(providerUserId);
        setUpMockitoExpectations(userId);

        final CareTeamRelation careTeamRelation = new CareTeamRelation();
        careTeamRelation.setCode(CareTeamRelation.Relation.FRIEND);
        final CareTeamRelationship careTeamRelationship = new CareTeamRelationship();
        careTeamRelationship.setCode(CareTeamRelationship.Relationship.FRIEND_FAMILY);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setEmergencyContact(Boolean.FALSE);
        rctm.setCreatedByResidentId(residentId);
        rctm.setCareTeamRole(provider.getEmployee().getCareTeamRole());
        rctm.setCareTeamRelation(careTeamRelation);
        rctm.setCareTeamRelationship(careTeamRelationship);
        rctm.setEmployee(provider.getEmployee());

        final CareteamMemberBriefDto expectedDto = new CareteamMemberBriefDto();
        expectedDto.setId(contactId);
        expectedDto.setUserId(providerUserId);
        expectedDto.setCareTeamRole(BEHAVIORAL_HEALTH);
        expectedDto.setEmergencyContact(Boolean.FALSE);
        expectedDto.setInvitationStatus(InvitationStatus.ACTIVE);
        expectedDto.setFullName(provider.getFullName());
        expectedDto.setDataSource(new DataSourceDto());

        // Mockito expectations
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        when(residentCareTeamMemberDao.getCareTeamMembers(activeResidentIds, null, null))
                .thenReturn(Collections.singletonList(rctm));
        when(userDao.getFirstByEmployee(provider.getEmployee())).thenReturn(provider);

        // Execute the method being tested
        List<CareteamMemberBriefDto> result = careTeamService.getUserCareTeamMembersBrief(userId);

        // Validation
        assertThat(result, hasSize(1));
        assertThat(result.get(0), sameBeanAs(expectedDto)
                .ignoring(DataSourceDto.class));
    }

    @Test
    public void testGetUserCareTeamMember() {
        // Expected objects
        final User provider = super.createProvider(providerUserId);
        setUpMockitoExpectations(userId);

        final CareTeamRelation careTeamRelation = new CareTeamRelation();
        careTeamRelation.setCode(CareTeamRelation.Relation.FRIEND);
        final CareTeamRelationship careTeamRelationship = new CareTeamRelationship();
        careTeamRelationship.setCode(CareTeamRelationship.Relationship.FRIEND_FAMILY);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setEmergencyContact(Boolean.FALSE);
        rctm.setCreatedByResidentId(residentId);
        rctm.setCareTeamRole(provider.getEmployee().getCareTeamRole());
        rctm.setCareTeamRelation(careTeamRelation);
        rctm.setCareTeamRelationship(careTeamRelationship);
        rctm.setEmployee(provider.getEmployee());

        final CareteamMemberDto expectedDto = new CareteamMemberDto();
        expectedDto.setId(contactId);
        expectedDto.setUserId(providerUserId);
        expectedDto.setPhotoUrl("url" + providerUserId);
        expectedDto.setEditable(Boolean.TRUE);
        expectedDto.setCareTeamRole(BEHAVIORAL_HEALTH);
        expectedDto.setRelation(CareTeamRelation.Relation.FRIEND);
        expectedDto.setRelationship(CareTeamRelationship.Relationship.FRIEND_FAMILY);
        expectedDto.setEmergencyContact(Boolean.FALSE);
        expectedDto.setInvitationStatus(InvitationStatus.ACTIVE);
        final PersonDto person = new PersonDto();
        person.setNames(Collections.<NameDto>singletonList(null));
        person.setAddresses(Collections.<AddressDto>emptyList());
        expectedDto.setPerson(person);
        expectedDto.setDataSource(new DataSourceDto());
        expectedDto.setSsnLastFourDigits(provider.getSsnLastFourDigits());

        // Mockito expectations
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        when(residentCareTeamMemberDao.getCareTeamMembers(activeResidentIds, null, null))
                .thenReturn(Collections.singletonList(rctm));
        when(userDao.getFirstByEmployee(provider.getEmployee())).thenReturn(provider);
        when(avatarService.getPhotoUrl(providerUserId)).thenReturn("url" + providerUserId);

        // Execute the method being tested
        CareteamMemberDto result = careTeamService.getUserCareTeamMember(userId, contactId);

        // Validation
        assertThat(result, sameBeanAs(expectedDto)
                .ignoring(DataSourceDto.class).ignoring(NameDto.class));
    }

    @Test
    public void testGetUserCareTeamMember2() {
        // Expected objects
        final User provider = super.createProvider(providerUserId, null, phone, email, firstName, lastName);
        setUpMockitoExpectations(userId);

        final CareTeamRelation careTeamRelation = new CareTeamRelation();
        careTeamRelation.setCode(CareTeamRelation.Relation.FRIEND);
        final CareTeamRelationship careTeamRelationship = new CareTeamRelationship();
        careTeamRelationship.setCode(CareTeamRelationship.Relationship.FRIEND_FAMILY);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setEmergencyContact(Boolean.FALSE);
        rctm.setCreatedByResidentId(null);
        rctm.setCreatedById(TestDataGenerator.randomIdExceptOf(employeeId));
        rctm.setCreatedBy(new Employee());
        rctm.setCareTeamRole(provider.getEmployee().getCareTeamRole());
        rctm.setCareTeamRelation(careTeamRelation);
        rctm.setCareTeamRelationship(careTeamRelationship);
        rctm.setEmployee(provider.getEmployee());

        final CareteamMemberDto expectedDto = new CareteamMemberDto();
        expectedDto.setId(contactId);
        expectedDto.setUserId(providerUserId);
        expectedDto.setPhotoUrl("url" + providerUserId);
        expectedDto.setEditable(Boolean.FALSE);
        expectedDto.setCareTeamRole(BEHAVIORAL_HEALTH);
        expectedDto.setRelation(CareTeamRelation.Relation.FRIEND);
        expectedDto.setRelationship(CareTeamRelationship.Relationship.FRIEND_FAMILY);
        expectedDto.setEmergencyContact(Boolean.FALSE);
        expectedDto.setInvitationStatus(InvitationStatus.ACTIVE);
        expectedDto.setContactEmail(email);
        expectedDto.setContactPhone(phone);
        final NameDto nameDto = new NameDto();
        nameDto.setUseCode("L");
        nameDto.setFullName(provider.getFullName());
        final TelecomDto emailDto = new TelecomDto();
        emailDto.setUseCode("EMAIL");
        emailDto.setValue(email);
        final TelecomDto phoneDto = new TelecomDto();
        phoneDto.setUseCode("HP");
        phoneDto.setValue(phone);
        final PersonDto person = new PersonDto();
        person.setNames(Collections.singletonList(nameDto));
        person.setTelecoms(Arrays.asList(emailDto, phoneDto));
        person.setAddresses(Collections.<AddressDto>emptyList());
        expectedDto.setPerson(person);
        expectedDto.setDataSource(new DataSourceDto());
        expectedDto.setSsnLastFourDigits(provider.getSsnLastFourDigits());

        // user name is not specified in PHR mobile app, but it's set in Web profile (in Employee rather than in Person.Name)
        provider.setFirstName(null);
        provider.setLastName(null);
        provider.getEmployee().getPerson().setNames(Collections.<Name>emptyList());

        // Mockito expectations
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        when(residentCareTeamMemberDao.getCareTeamMembers(activeResidentIds, null, null))
                .thenReturn(Collections.singletonList(rctm));
        when(userDao.getFirstByEmployee(provider.getEmployee())).thenReturn(provider);
        when(avatarService.getPhotoUrl(providerUserId)).thenReturn("url" + providerUserId);

        // Execute the method being tested
        CareteamMemberDto result = careTeamService.getUserCareTeamMember(userId, contactId);

        // Validation
        assertThat(result, sameBeanAs(expectedDto)
                .ignoring(DataSourceDto.class));
    }

    @Test
    public void testGetUserCareTeamMember3() {
        // Expected objects
        final User provider = super.createProvider(providerUserId, null, phone, email, firstName, lastName);
        setUpMockitoExpectations(userId);

        final CareTeamRelation careTeamRelation = new CareTeamRelation();
        careTeamRelation.setCode(CareTeamRelation.Relation.FRIEND);
        final CareTeamRelationship careTeamRelationship = new CareTeamRelationship();
        careTeamRelationship.setCode(CareTeamRelationship.Relationship.FRIEND_FAMILY);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setEmergencyContact(Boolean.FALSE);
        rctm.setCreatedByResidentId(null);
        rctm.setCreatedById(TestDataGenerator.randomIdExceptOf(employeeId));
        rctm.setCreatedBy(new Employee());
        rctm.setCareTeamRole(provider.getEmployee().getCareTeamRole());
        rctm.setCareTeamRelation(careTeamRelation);
        rctm.setCareTeamRelationship(careTeamRelationship);
        rctm.setEmployee(provider.getEmployee());

        final CareteamMemberDto expectedDto = new CareteamMemberDto();
        expectedDto.setId(contactId);
        expectedDto.setUserId(providerUserId);
        expectedDto.setPhotoUrl("url" + providerUserId);
        expectedDto.setEditable(Boolean.FALSE);
        expectedDto.setCareTeamRole(BEHAVIORAL_HEALTH);
        expectedDto.setRelation(CareTeamRelation.Relation.FRIEND);
        expectedDto.setRelationship(CareTeamRelationship.Relationship.FRIEND_FAMILY);
        expectedDto.setEmergencyContact(Boolean.FALSE);
        expectedDto.setInvitationStatus(InvitationStatus.ACTIVE);
        expectedDto.setContactEmail(email);
        expectedDto.setContactPhone(phone);
        final TelecomDto emailDto = new TelecomDto();
        emailDto.setUseCode("EMAIL");
        emailDto.setValue(email);
        final TelecomDto phoneDto = new TelecomDto();
        phoneDto.setUseCode("HP");
        phoneDto.setValue(phone);
        final PersonDto person = new PersonDto();
        person.setNames(Collections.<NameDto>emptyList());
        person.setTelecoms(Arrays.asList(emailDto, phoneDto));
        person.setAddresses(Collections.<AddressDto>emptyList());
        expectedDto.setPerson(person);
        expectedDto.setDataSource(new DataSourceDto());
        expectedDto.setSsnLastFourDigits(provider.getSsnLastFourDigits());

        // user name is not specified
        provider.setFirstName(null);
        provider.setLastName(null);
        provider.getEmployee().getPerson().setNames(Collections.<Name>emptyList());
        provider.getEmployee().setFirstName(null);
        provider.getEmployee().setLastName(null);

        // Mockito expectations
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        when(residentCareTeamMemberDao.getCareTeamMembers(activeResidentIds, null, null))
                .thenReturn(Collections.singletonList(rctm));
        when(userDao.getFirstByEmployee(provider.getEmployee())).thenReturn(provider);
        when(avatarService.getPhotoUrl(providerUserId)).thenReturn("url" + providerUserId);

        // Execute the method being tested
        CareteamMemberDto result = careTeamService.getUserCareTeamMember(userId, contactId);

        // Validation
        assertThat(result, sameBeanAs(expectedDto)
                .ignoring(DataSourceDto.class));
    }

    @Test
    public void testGetResidentCareTeamMember() {
        // Expected objects
        final User provider = super.createProvider(providerUserId);
        setUpMockitoExpectations(userId);

        final CareTeamRelation careTeamRelation = new CareTeamRelation();
        careTeamRelation.setCode(CareTeamRelation.Relation.FRIEND);
        final CareTeamRelationship careTeamRelationship = new CareTeamRelationship();
        careTeamRelationship.setCode(CareTeamRelationship.Relationship.FRIEND_FAMILY);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setEmergencyContact(Boolean.FALSE);
        rctm.setCreatedByResidentId(residentId);
        rctm.setCareTeamRole(provider.getEmployee().getCareTeamRole());
        rctm.setCareTeamRelation(careTeamRelation);
        rctm.setCareTeamRelationship(careTeamRelationship);
        rctm.setEmployee(provider.getEmployee());

        final CareteamMemberDto expectedDto = new CareteamMemberDto();
        expectedDto.setId(contactId);
        expectedDto.setUserId(providerUserId);
        expectedDto.setPhotoUrl("url" + providerUserId);
        expectedDto.setEditable(Boolean.TRUE);
        expectedDto.setCareTeamRole(BEHAVIORAL_HEALTH);
        expectedDto.setRelation(CareTeamRelation.Relation.FRIEND);
        expectedDto.setRelationship(CareTeamRelationship.Relationship.FRIEND_FAMILY);
        expectedDto.setEmergencyContact(Boolean.FALSE);
        expectedDto.setInvitationStatus(InvitationStatus.ACTIVE);
        final NameDto nameDto = new NameDto();
        nameDto.setFullName(provider.getFullName());
        final PersonDto person = new PersonDto();
        person.setNames(Collections.singletonList(nameDto));
        person.setAddresses(Collections.<AddressDto>emptyList());
        expectedDto.setPerson(person);
        expectedDto.setDataSource(new DataSourceDto());

        // Mockito expectations
        when(residentCareTeamMemberDao.getResidentCareTeamMemberByEmployeeIdAndResidentId(employeeId, residentId)).thenReturn(rctm);
        when(userDao.getFirstByEmployee(provider.getEmployee())).thenReturn(provider);
        when(avatarService.getPhotoUrl(providerUserId)).thenReturn("url" + providerUserId);

        // Execute the method being tested
        CareteamMemberDto result = careTeamService.getResidentCareTeamMember(residentId, employeeId);

        // Validation
        assertThat(result, sameBeanAs(expectedDto)
                .ignoring(DataSourceDto.class));
    }

    @Test
    public void testDeleteUserCareTeamMember() {
        // Expected objects
        final User provider = super.createProvider(providerUserId);
        setUpMockitoExpectations(userId);

        final CareTeamRelation careTeamRelation = new CareTeamRelation();
        careTeamRelation.setCode(CareTeamRelation.Relation.FRIEND);
        final CareTeamRelationship careTeamRelationship = new CareTeamRelationship();
        careTeamRelationship.setCode(CareTeamRelationship.Relationship.FRIEND_FAMILY);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setEmergencyContact(Boolean.FALSE);
        rctm.setEmployee(provider.getEmployee());
        rctm.setCreatedByResidentId(residentId);
        rctm.setCareTeamRelationship(careTeamRelationship);
        rctm.setCareTeamRelation(careTeamRelation);
        //provider.setResidentCareTeamMembers(new HashSet<>(Collections.singleton(rctm)));

        // Mockito expectations
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        when(userDao.getFirstByEmployee(provider.getEmployee())).thenReturn(provider);

        // Execute the method being tested
        Boolean result = careTeamService.deleteUserCareTeamMember(userId, contactId);

        // Validation
        assertEquals(Boolean.TRUE, result);
        verify(residentCareTeamMemberDao).delete(contactId);
        verify(residentUpdateQueueProducer).putToResidentUpdateQueue(residentId, ResidentUpdateType.CARE_TEAM);
    }

    @Test(expected = PhrException.class)
    public void testDeleteUserCareTeamMemberThrowsNotAssociated() {
        // Expected objects
        final User provider = super.createProvider(providerUserId);

        final CareTeamRelation careTeamRelation = new CareTeamRelation();
        careTeamRelation.setCode(CareTeamRelation.Relation.FRIEND);
        final CareTeamRelationship careTeamRelationship = new CareTeamRelationship();
        careTeamRelationship.setCode(CareTeamRelationship.Relationship.FRIEND_FAMILY);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setEmergencyContact(Boolean.FALSE);
        rctm.setEmployee(provider.getEmployee());
        rctm.setCreatedByResidentId(residentId2);
        rctm.setCareTeamRelationship(careTeamRelationship);
        rctm.setCareTeamRelation(careTeamRelation);
        //provider.setResidentCareTeamMembers(new HashSet<>(Collections.singleton(rctm)));

        // Mockito expectations
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        when(userDao.getFirstByEmployee(provider.getEmployee())).thenReturn(provider);

        // Execute the method being tested
        careTeamService.deleteUserCareTeamMember(userId, contactId);
    }

    @Test(expected = PhrException.class)
    public void testUpdateUserCareTeamMemberThrowsAccessForbidden() {
        // Execute the method being tested
        careTeamService.updateUserCareTeamMember(userId, contactId, new CareteamMemberDto());
    }

    /**
     * User-consumer (patient) invites an existing user-provider (already registered in PHR mobile) to his/her care team.
     */
    @Test
    public void testInviteFriendUserExists() {
        // Expected objects
        final User provider = super.createProvider(providerUserId, ssn, phone, email, firstName, lastName);

        final CareTeamRelation.Relation relation = CareTeamRelation.Relation.FAMILY;
        final CareTeamRelationship.Relationship relationship = CareTeamRelationship.Relationship.FRIEND_FAMILY;
        final CareTeamRoleCode role = CareTeamRoleCode.ROLE_PARENT_GUARDIAN;
        final String PARENT_GUARDIAN = "Parent/Guardian";
        final CareTeamRelation careTeamRelation = new CareTeamRelation();
        careTeamRelation.setCode(relation);
        final CareTeamRelationship careTeamRelationship = new CareTeamRelationship();
        careTeamRelationship.setCode(relationship);
        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setCode(role);
        careTeamRole.setName(PARENT_GUARDIAN);

        final CareteamMemberDto expectedDto = new CareteamMemberDto();
        expectedDto.setUserId(providerUserId);
        expectedDto.setPhotoUrl("url" + providerUserId);
        expectedDto.setEditable(Boolean.TRUE);
        expectedDto.setCareTeamRole(null);
        expectedDto.setRelation(relation);
        expectedDto.setRelationship(CareTeamRelationship.Relationship.FRIEND_FAMILY);
        expectedDto.setCareTeamRole(PARENT_GUARDIAN);
        expectedDto.setEmergencyContact(Boolean.FALSE);
        expectedDto.setInvitationStatus(InvitationStatus.ACTIVE);
        expectedDto.setContactStatus(ContactStatus.EXISTING_ACTIVE);
        expectedDto.setPhysicianInfo(null);
        expectedDto.setContactPhone(phone);
        expectedDto.setContactEmail(email);
        expectedDto.setSsnLastFourDigits(StringUtils.right(ssn, 4));
        final NameDto nameDto = new NameDto();
        nameDto.setFullName(provider.getFullName());
        final TelecomDto emailDto = new TelecomDto();
        emailDto.setUseCode("EMAIL");
        emailDto.setValue(email);
        final TelecomDto phoneDto = new TelecomDto();
        phoneDto.setUseCode("HP");
        phoneDto.setValue(phone);
        final PersonDto person = new PersonDto();
        person.setNames(Collections.singletonList(nameDto));
        person.setTelecoms(Arrays.asList(emailDto, phoneDto));
        person.setAddresses(Collections.<AddressDto>emptyList());
        expectedDto.setPerson(person);
        expectedDto.setDataSource(new DataSourceDto());

        // Mockito expectations
        final User consumer = setUpMockitoExpectations(userId);
        when(careTeamRoleDao.getByCode(role)).thenReturn(careTeamRole);
        when(careTeamRelationshipDao.getByCode(relationship)).thenReturn(careTeamRelationship);
        when(careTeamRelationDao.getByCode(relation)).thenReturn(careTeamRelation);
        when(accessRightsService.getDefaultAccessRights()).thenReturn(allAccessRights());
        when(residentCareTeamMemberDao.create(any(ResidentCareTeamMember.class))).then(returnsFirstArg());
        when(residentCareTeamMemberDao.merge(any(ResidentCareTeamMember.class))).then(returnsFirstArg());
        when(userDao.findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(provider));
        when(userDao.findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(provider));
        when(userDao.getFirstByEmployee(provider.getEmployee())).thenReturn(provider);
        whenAskedForEmployeeThenReturn(provider.getEmployee());
        when(profileService.isGuardian(provider)).thenReturn(Boolean.TRUE);
        when(avatarService.getPhotoUrl(providerUserId)).thenReturn("url" + providerUserId);
        when(privilegesService.canInviteFriendToCareTeam()).thenReturn(Boolean.TRUE);
        when(notificationsFacade.notifyFriendFamilyAboutInvitationToCareTeam(eq(consumer), eq(provider), eq(provider.getEmployee()), any(ResidentCareTeamMember.class)))
                .thenReturn(ContactStatus.EXISTING_ACTIVE);

        // Execute the method being tested
        CareteamMemberDto result = careTeamService.inviteFriend(userId, ssn, email, phone, firstName, lastName, relation);

        // Validation
        assertThat(result, sameBeanAs(expectedDto)
                .ignoring("id").ignoring(DataSourceDto.class));
        verify(notificationsFacade).notifyFriendFamilyAboutInvitationToCareTeam(eq(consumer), eq(provider), eq(provider.getEmployee()), any(ResidentCareTeamMember.class));
        verify(notificationPreferencesService).createDefaultCareTeamMemberNotificationPreferences(any(ResidentCareTeamMember.class));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
        verify(careTeamSecurityUtils, atLeastOnce()).getCurrentUser();
        verify(privilegesService, never()).canInviteFriendToCareTeam();
        verify(phrResidentService, never()).createAssociatedResidentFromUserData(any(User.class));
        verify(contactService, never()).createEmployeeForInvitedFriend(any(Resident.class), anyString(), anyString(), anyString(), anyString(), any(CareTeamRole.class));
        verify(residentUpdateQueueProducer).putToResidentUpdateQueue(residentId, ResidentUpdateType.CARE_TEAM);
        verifyNoMoreInteractions(careTeamSecurityUtils, notificationsFacade, userRegistrationApplicationService);
    }

    /**
     * User-consumer (patient) invites an existing user-consumer (already registered in PHR mobile, but not registered in Web Simply Connect system) to his/her care team.
     * The invited user-consumer gets a new role and becomes user-provider (guardian).
     * The invited user receives (email) invitation to Web Simply Connect system.
     */
//    @Test
    public void testInviteFriendUserExistsButNoPersonalProfile() {
        // Expected objects
        final User consumer2 = super.createConsumer(providerUserId, ssn, phone, email, firstName, lastName);
        final Employee employee = super.createEmployee(email, firstName, lastName, consumer2.getResident().getPerson());
        employee.setStatus(EmployeeStatus.ACTIVE);

        final CareTeamRelation.Relation relation = CareTeamRelation.Relation.PARENT;
        final CareTeamRelationship.Relationship relationship = CareTeamRelationship.Relationship.FRIEND_FAMILY;
        final CareTeamRoleCode role = CareTeamRoleCode.ROLE_PARENT_GUARDIAN;
        final String PARENT_GUARDIAN = "Parent/Guardian";
        final CareTeamRelation careTeamRelation = new CareTeamRelation();
        careTeamRelation.setCode(relation);
        final CareTeamRelationship careTeamRelationship = new CareTeamRelationship();
        careTeamRelationship.setCode(relationship);
        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setCode(role);
        careTeamRole.setName(PARENT_GUARDIAN);
        final RegistrationApplication application = new RegistrationApplication();
        application.setEmployee(employee);

        final CareteamMemberDto expectedDto = new CareteamMemberDto();
        //expectedDto.setUserId(providerUserId);
        //expectedDto.setPhotoUrl("url" + providerUserId);
        expectedDto.setEditable(Boolean.TRUE);
        expectedDto.setCareTeamRole(null);
        expectedDto.setRelation(relation);
        expectedDto.setRelationship(CareTeamRelationship.Relationship.FRIEND_FAMILY);
        expectedDto.setCareTeamRole(PARENT_GUARDIAN);
        expectedDto.setEmergencyContact(Boolean.FALSE);
        expectedDto.setInvitationStatus(InvitationStatus.PENDING);
        expectedDto.setContactStatus(ContactStatus.CREATED);
        expectedDto.setContactPhone(phone);
        expectedDto.setContactEmail(email);
        //expectedDto.setSsnLastFourDigits(StringUtils.right(ssn, 4));
        expectedDto.setPhysicianInfo(null);
        final NameDto nameDto = new NameDto();
        nameDto.setFullName(consumer2.getFullName());
        final TelecomDto emailDto = new TelecomDto();
        emailDto.setUseCode("EMAIL");
        emailDto.setValue(email);
        final TelecomDto phoneDto = new TelecomDto();
        phoneDto.setUseCode("HP");
        phoneDto.setValue(phone);
        final PersonDto person = new PersonDto();
        person.setNames(Collections.singletonList(nameDto));
        person.setTelecoms(Arrays.asList(emailDto, phoneDto));
        person.setAddresses(Collections.<AddressDto>emptyList());
        expectedDto.setPerson(person);
        expectedDto.setDataSource(new DataSourceDto());

        // Mockito expectations
        final User consumer = setUpMockitoExpectations(userId);
        when(careTeamRoleDao.getByCode(role)).thenReturn(careTeamRole);
        when(careTeamRelationshipDao.getByCode(relationship)).thenReturn(careTeamRelationship);
        when(careTeamRelationDao.getByCode(relation)).thenReturn(careTeamRelation);
        when(accessRightsService.getDefaultAccessRights()).thenReturn(allAccessRights());
        when(residentCareTeamMemberDao.create(any(ResidentCareTeamMember.class))).then(returnsFirstArg());
        when(residentCareTeamMemberDao.merge(any(ResidentCareTeamMember.class))).then(returnsFirstArg());
        when(userDao.findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(consumer2));
        when(userDao.findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(consumer2));
        when(profileService.isGuardian(consumer2)).thenReturn(Boolean.FALSE);
        whenAskedForEmployeeThenReturn(null);
        when(contactService.createEmployeeForInvitedFriend(consumer.getResident(), email, phone, firstName, lastName, careTeamRole))
                .thenReturn(employee);
        when(avatarService.getPhotoUrl(providerUserId)).thenReturn("url" + providerUserId);
        //when(databasesService.getUnaffiliatedDatabase()).thenReturn(employee.getDatabase());
        when(privilegesService.canInviteFriendToCareTeam()).thenReturn(Boolean.TRUE);
        when(notificationsFacade.notifyFriendFamilyAboutInvitationToCareTeam(eq(consumer), isNull(User.class), eq(employee), any(ResidentCareTeamMember.class)))
                .thenReturn(ContactStatus.CREATED);
        when(userRegistrationApplicationService.createRegistrationApplicationForInvitee(
                eq(consumer), eq(employee), eq(ssn), eq(phone), eq(email), eq(firstName), eq(lastName)))
                .thenReturn(application);

        // Execute the method being tested
        CareteamMemberDto result = careTeamService.inviteFriend(userId, ssn, email, phone, firstName, lastName, relation);

        // Validation
        assertThat(result, sameBeanAs(expectedDto)
                .ignoring("id").ignoring(DataSourceDto.class));
        verify(notificationsFacade).notifyFriendFamilyAboutInvitationToCareTeam(eq(consumer), isNull(User.class), eq(employee), any(ResidentCareTeamMember.class));
        verify(notificationPreferencesService).createDefaultCareTeamMemberNotificationPreferences(any(ResidentCareTeamMember.class));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
        verify(careTeamSecurityUtils, atLeastOnce()).getCurrentUser();
        verify(privilegesService, never()).canInviteFriendToCareTeam();
        verify(contactService).createEmployeeForInvitedFriend(consumer.getResident(), email, phone, firstName, lastName, careTeamRole);
        verify(phrResidentService, never()).createAssociatedResidentFromUserData(any(User.class));
        verify(userRegistrationApplicationService).createRegistrationApplicationForInvitee(
                eq(consumer), eq(employee), eq(ssn), eq(phone), eq(email), eq(firstName), eq(lastName));
        verify(userRegistrationApplicationService).save(application);
        verify(residentUpdateQueueProducer).putToResidentUpdateQueue(consumer2.getResident().getId(), ResidentUpdateType.CARE_TEAM);
        verifyNoMoreInteractions(careTeamSecurityUtils, notificationsFacade, userRegistrationApplicationService);
    }

    /**
     * User-consumer (patient) invites an existing web user (not registered in PHR mobile, but registered in Web Simply Connect system) to his/her care team.
     * The invited user receives (email) invitation to PHR mobile.
     */
    @Test
    public void testInviteFriendUserDoesNotExist() {
        // Expected objects
        final Person person = createPerson(phone, email, firstName, lastName, PersonTelecomCode.HP);
        final Employee employee = super.createEmployee(email, firstName, lastName, person);

        final CareTeamRelation.Relation relation = CareTeamRelation.Relation.PARENT;
        final CareTeamRelationship.Relationship relationship = CareTeamRelationship.Relationship.FRIEND_FAMILY;
        final CareTeamRoleCode role = CareTeamRoleCode.ROLE_PARENT_GUARDIAN;
        final String PARENT_GUARDIAN = "Parent/Guardian";
        final CareTeamRelation careTeamRelation = new CareTeamRelation();
        careTeamRelation.setCode(relation);
        final CareTeamRelationship careTeamRelationship = new CareTeamRelationship();
        careTeamRelationship.setCode(relationship);
        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setCode(role);
        careTeamRole.setName(PARENT_GUARDIAN);
        final RegistrationApplication application = new RegistrationApplication();
        application.setEmployee(employee);

        final CareteamMemberDto expectedDto = new CareteamMemberDto();
        expectedDto.setUserId(providerUserId);
        expectedDto.setPhotoUrl(null);
        expectedDto.setEditable(Boolean.TRUE);
        expectedDto.setCareTeamRole(null);
        expectedDto.setRelation(relation);
        expectedDto.setRelationship(CareTeamRelationship.Relationship.FRIEND_FAMILY);
        expectedDto.setCareTeamRole(PARENT_GUARDIAN);
        expectedDto.setEmergencyContact(Boolean.FALSE);
        expectedDto.setInvitationStatus(InvitationStatus.ACTIVE);
        expectedDto.setContactStatus(ContactStatus.EXISTING_ACTIVE);
        expectedDto.setPhysicianInfo(null);
        //expectedDto.setSsnLastFourDigits(StringUtils.right(ssn, 4));
        expectedDto.setContactPhone(phone);
        expectedDto.setContactEmail(email);
        final NameDto nameDto = new NameDto();
        nameDto.setFullName(employee.getFullName());
        final TelecomDto emailDto = new TelecomDto();
        emailDto.setUseCode("EMAIL");
        emailDto.setValue(email);
        final TelecomDto phoneDto = new TelecomDto();
        phoneDto.setUseCode("HP");
        phoneDto.setValue(phone);
        final PersonDto personDto = new PersonDto();
        personDto.setNames(Collections.singletonList(nameDto));
        personDto.setTelecoms(Arrays.asList(emailDto, phoneDto));
        personDto.setAddresses(Collections.<AddressDto>emptyList());
        expectedDto.setPerson(personDto);
        expectedDto.setDataSource(new DataSourceDto());

        // Mockito expectations
        final User consumer = setUpMockitoExpectations(userId);
        when(careTeamRoleDao.getByCode(role)).thenReturn(careTeamRole);
        when(careTeamRelationshipDao.getByCode(relationship)).thenReturn(careTeamRelationship);
        when(careTeamRelationDao.getByCode(relation)).thenReturn(careTeamRelation);
        when(accessRightsService.getDefaultAccessRights()).thenReturn(allAccessRights());
        when(residentCareTeamMemberDao.create(any(ResidentCareTeamMember.class))).then(returnsFirstArg());
        when(residentCareTeamMemberDao.merge(any(ResidentCareTeamMember.class))).then(returnsFirstArg());
        when(userDao.save(any(User.class))).then(MockitoAnswers.returnsPersistedUser());
        when(userDao.saveAndFlush(any(User.class))).then(MockitoAnswers.returnsPersistedUser());
        when(userDao.findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized))
                .thenReturn(Collections.<User>emptyList());
        when(userDao.findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized))
                .thenReturn(Collections.<User>emptyList());
        whenAskedForEmployeeThenReturn(employee);
        when(contactService.createEmployeeForInvitedFriend(consumer.getResident(), email, phone, firstName, lastName, careTeamRole))
                .thenReturn(employee);
        //when(databasesService.getUnaffiliatedDatabase()).thenReturn(employee.getDatabase());
        when(privilegesService.canInviteFriendToCareTeam()).thenReturn(Boolean.TRUE);
        when(notificationsFacade.notifyFriendFamilyAboutInvitationToCareTeam(eq(consumer), any(User.class), eq(employee), any(ResidentCareTeamMember.class)))
                .thenReturn(ContactStatus.EXISTING_ACTIVE);
        when(userRegistrationApplicationService.createRegistrationApplicationForInvitee(
                eq(consumer), eq(employee), eq(ssn), eq(phone), eq(email), eq(firstName), eq(lastName)))
                .thenReturn(application);

        // Execute the method being tested
        CareteamMemberDto result = careTeamService.inviteFriend(userId, ssn, email, phone, firstName, lastName, relation);

        // Validation
        assertThat(result, sameBeanAs(expectedDto)
                .ignoring("id").ignoring("userId").ignoring(DataSourceDto.class));
        verify(notificationsFacade).notifyFriendFamilyAboutInvitationToCareTeam(eq(consumer), any(User.class), eq(employee), any(ResidentCareTeamMember.class));
        verify(notificationPreferencesService).createDefaultCareTeamMemberNotificationPreferences(any(ResidentCareTeamMember.class));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
        verify(careTeamSecurityUtils, atLeastOnce()).getCurrentUser();
        verify(privilegesService, never()).canInviteFriendToCareTeam();
        verify(contactService, never()).createEmployeeForInvitedFriend(
                any(Resident.class), anyString(), anyString(), anyString(), anyString(), any(CareTeamRole.class));
        verify(phrResidentService, never()).createAssociatedResidentFromUserData(any(User.class));
        verify(userRegistrationApplicationService).createRegistrationApplicationForInvitee(
                eq(consumer), eq(employee), eq(ssn), eq(phone), eq(email), eq(firstName), eq(lastName));
        verify(userRegistrationApplicationService).save(application);
        verify(residentUpdateQueueProducer).putToResidentUpdateQueue(residentId, ResidentUpdateType.CARE_TEAM);
        verifyNoMoreInteractions(careTeamSecurityUtils, notificationsFacade, userRegistrationApplicationService);
    }

    /**
     * User-consumer (patient) invites a non-existing user (registered neither in PHR mobile, nor in Web Simply Connect system) to his/her care team.
     * User-provider account is automatically created for the invited user.
     * The invited user receives (email) invitation to Web Simply Connect system.
     */
//    @Test
    public void testInviteFriendBrandNewUser() {
        // Expected objects
        final Person person = createPerson(phone, email, firstName, lastName, PersonTelecomCode.HP);
        final Employee employee = super.createEmployee(email, firstName, lastName, person);
        employee.setStatus(EmployeeStatus.ACTIVE);

        final CareTeamRelation.Relation relation = CareTeamRelation.Relation.PARENT;
        final CareTeamRelationship.Relationship relationship = CareTeamRelationship.Relationship.FRIEND_FAMILY;
        final CareTeamRoleCode role = CareTeamRoleCode.ROLE_PARENT_GUARDIAN;
        final String PARENT_GUARDIAN = "Parent/Guardian";
        final CareTeamRelation careTeamRelation = new CareTeamRelation();
        careTeamRelation.setCode(relation);
        final CareTeamRelationship careTeamRelationship = new CareTeamRelationship();
        careTeamRelationship.setCode(relationship);
        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setCode(role);
        careTeamRole.setName(PARENT_GUARDIAN);
        final RegistrationApplication application = new RegistrationApplication();
        application.setEmployee(employee);

        final CareteamMemberDto expectedDto = new CareteamMemberDto();
        expectedDto.setUserId(providerUserId);
        expectedDto.setPhotoUrl(null);
        expectedDto.setEditable(Boolean.TRUE);
        expectedDto.setCareTeamRole(null);
        expectedDto.setRelation(relation);
        expectedDto.setRelationship(CareTeamRelationship.Relationship.FRIEND_FAMILY);
        expectedDto.setCareTeamRole(PARENT_GUARDIAN);
        expectedDto.setEmergencyContact(Boolean.FALSE);
        expectedDto.setInvitationStatus(InvitationStatus.PENDING);
        expectedDto.setContactStatus(ContactStatus.CREATED);
        expectedDto.setPhysicianInfo(null);
        //expectedDto.setSsnLastFourDigits(StringUtils.right(ssn, 4));
        expectedDto.setContactPhone(phone);
        expectedDto.setContactEmail(email);
        final NameDto nameDto = new NameDto();
        nameDto.setFullName(employee.getFullName());
        final TelecomDto emailDto = new TelecomDto();
        emailDto.setUseCode("EMAIL");
        emailDto.setValue(email);
        final TelecomDto phoneDto = new TelecomDto();
        phoneDto.setUseCode("HP");
        phoneDto.setValue(phone);
        final PersonDto personDto = new PersonDto();
        personDto.setNames(Collections.singletonList(nameDto));
        personDto.setTelecoms(Arrays.asList(emailDto, phoneDto));
        personDto.setAddresses(Collections.<AddressDto>emptyList());
        expectedDto.setPerson(personDto);
        expectedDto.setDataSource(new DataSourceDto());

        // Mockito expectations
        final User consumer = setUpMockitoExpectations(userId);
        when(careTeamRoleDao.getByCode(role)).thenReturn(careTeamRole);
        when(careTeamRelationshipDao.getByCode(relationship)).thenReturn(careTeamRelationship);
        when(careTeamRelationDao.getByCode(relation)).thenReturn(careTeamRelation);
        when(accessRightsService.getDefaultAccessRights()).thenReturn(allAccessRights());
        when(residentCareTeamMemberDao.create(any(ResidentCareTeamMember.class))).then(returnsFirstArg());
        when(residentCareTeamMemberDao.merge(any(ResidentCareTeamMember.class))).then(returnsFirstArg());
        when(userDao.save(any(User.class))).then(MockitoAnswers.returnsPersistedUser());
        when(userDao.saveAndFlush(any(User.class))).then(MockitoAnswers.returnsPersistedUser());
        when(userDao.findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized))
                .thenReturn(Collections.<User>emptyList());
        when(userDao.findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized))
                .thenReturn(Collections.<User>emptyList());
        whenAskedForEmployeeThenReturn(null);
        when(contactService.createEmployeeForInvitedFriend(consumer.getResident(), email, phone, firstName, lastName, careTeamRole))
                .thenReturn(employee);
        //when(databasesService.getUnaffiliatedDatabase()).thenReturn(employee.getDatabase());
        when(privilegesService.canInviteFriendToCareTeam()).thenReturn(Boolean.TRUE);
        when(notificationsFacade.notifyFriendFamilyAboutInvitationToCareTeam(eq(consumer), any(User.class), eq(employee), any(ResidentCareTeamMember.class)))
                .thenReturn(ContactStatus.CREATED);
        when(userRegistrationApplicationService.createRegistrationApplicationForInvitee(
                eq(consumer), eq(employee), eq(ssn), eq(phone), eq(email), eq(firstName), eq(lastName)))
                .thenReturn(application);

        // Execute the method being tested
        CareteamMemberDto result = careTeamService.inviteFriend(userId, ssn, email, phone, firstName, lastName, relation);

        // Validation
        assertThat(result, sameBeanAs(expectedDto)
                .ignoring("id").ignoring("userId").ignoring(DataSourceDto.class));
        verify(notificationsFacade).notifyFriendFamilyAboutInvitationToCareTeam(eq(consumer), any(User.class), eq(employee), any(ResidentCareTeamMember.class));
        verify(notificationPreferencesService).createDefaultCareTeamMemberNotificationPreferences(any(ResidentCareTeamMember.class));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
        verify(careTeamSecurityUtils, atLeastOnce()).getCurrentUser();
        verify(privilegesService, never()).canInviteFriendToCareTeam();
        verify(contactService).createEmployeeForInvitedFriend(consumer.getResident(), email, phone, firstName, lastName, careTeamRole);
        verify(phrResidentService, never()).createAssociatedResidentFromUserData(any(User.class));
        verify(userRegistrationApplicationService).createRegistrationApplicationForInvitee(
                eq(consumer), eq(employee), eq(ssn), eq(phone), eq(email), eq(firstName), eq(lastName));
        verify(userRegistrationApplicationService).save(application);
        verify(residentUpdateQueueProducer).putToResidentUpdateQueue(residentId, ResidentUpdateType.CARE_TEAM);
        verifyNoMoreInteractions(careTeamSecurityUtils, notificationsFacade);
    }

    /**
     * User-provider (guardian/physician) invites an existing user-provider (already registered in PHR mobile) to a care team of an
     * existing user-consumer (patient).
     */
    @Test
    public void testInviteFriendAsProviderUserExists() {
        // Expected objects
        final Long consumerUserId = TestDataGenerator.randomIdExceptOf(userId, providerUserId);
        final User provider = super.createProvider(providerUserId, ssn, phone, email, firstName, lastName);

        final CareTeamRelation.Relation relation = CareTeamRelation.Relation.FAMILY;
        final CareTeamRelationship.Relationship relationship = CareTeamRelationship.Relationship.FRIEND_FAMILY;
        final CareTeamRoleCode role = CareTeamRoleCode.ROLE_PARENT_GUARDIAN;
        final String PARENT_GUARDIAN = "Parent/Guardian";
        final CareTeamRelation careTeamRelation = new CareTeamRelation();
        careTeamRelation.setCode(relation);
        final CareTeamRelationship careTeamRelationship = new CareTeamRelationship();
        careTeamRelationship.setCode(relationship);
        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setCode(role);
        careTeamRole.setName(PARENT_GUARDIAN);

        final CareteamMemberDto expectedDto = new CareteamMemberDto();
        expectedDto.setUserId(providerUserId);
        expectedDto.setPhotoUrl("url" + providerUserId);
        expectedDto.setEditable(Boolean.TRUE);
        expectedDto.setCareTeamRole(null);
        expectedDto.setRelation(relation);
        expectedDto.setRelationship(CareTeamRelationship.Relationship.FRIEND_FAMILY);
        expectedDto.setCareTeamRole(PARENT_GUARDIAN);
        expectedDto.setEmergencyContact(Boolean.FALSE);
        expectedDto.setInvitationStatus(InvitationStatus.ACTIVE);
        expectedDto.setContactStatus(ContactStatus.EXISTING_ACTIVE);
        expectedDto.setPhysicianInfo(null);
        expectedDto.setSsnLastFourDigits(StringUtils.right(ssn, 4));
        expectedDto.setContactEmail(email);
        expectedDto.setContactPhone(phone);
        final NameDto nameDto = new NameDto();
        nameDto.setFullName(provider.getFullName());
        final TelecomDto emailDto = new TelecomDto();
        emailDto.setUseCode("EMAIL");
        emailDto.setValue(email);
        final TelecomDto phoneDto = new TelecomDto();
        phoneDto.setUseCode("HP");
        phoneDto.setValue(phone);
        final PersonDto person = new PersonDto();
        person.setNames(Collections.singletonList(nameDto));
        person.setTelecoms(Arrays.asList(emailDto, phoneDto));
        person.setAddresses(Collections.<AddressDto>emptyList());
        expectedDto.setPerson(person);
        expectedDto.setDataSource(new DataSourceDto());

        // Mockito expectations
        final User current = setUpMockitoExpectationsAsProvider(userId);
        final User consumer = setUpMockitoExpectations(consumerUserId);
        when(careTeamRoleDao.getByCode(role)).thenReturn(careTeamRole);
        when(careTeamRelationshipDao.getByCode(relationship)).thenReturn(careTeamRelationship);
        when(careTeamRelationDao.getByCode(relation)).thenReturn(careTeamRelation);
        when(accessRightsService.getDefaultAccessRights()).thenReturn(allAccessRights());
        when(residentCareTeamMemberDao.create(any(ResidentCareTeamMember.class))).then(returnsFirstArg());
        when(residentCareTeamMemberDao.merge(any(ResidentCareTeamMember.class))).then(returnsFirstArg());
        when(userDao.findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(provider));
        when(userDao.findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(provider));
        when(profileService.isGuardian(provider)).thenReturn(Boolean.TRUE);
        when(avatarService.getPhotoUrl(providerUserId)).thenReturn("url" + providerUserId);
        when(privilegesService.canInviteFriendToCareTeam()).thenReturn(Boolean.TRUE);
        when(userDao.getFirstByEmployee(provider.getEmployee())).thenReturn(provider);
        whenAskedForEmployeeThenReturn(provider.getEmployee());
        when(notificationsFacade.notifyFriendFamilyAboutInvitationToCareTeam(eq(consumer), eq(provider), eq(provider.getEmployee()), any(ResidentCareTeamMember.class)))
                .thenReturn(ContactStatus.EXISTING_ACTIVE);

        // Execute the method being tested
        CareteamMemberDto result = careTeamService.inviteFriend(consumerUserId, ssn, email, phone, firstName, lastName, relation);

        // Validation
        assertThat(result, sameBeanAs(expectedDto)
                .ignoring("id").ignoring(DataSourceDto.class));
        verify(notificationsFacade).notifyFriendFamilyAboutInvitationToCareTeam(eq(consumer), eq(provider), eq(provider.getEmployee()), any(ResidentCareTeamMember.class));
        verify(notificationPreferencesService).createDefaultCareTeamMemberNotificationPreferences(any(ResidentCareTeamMember.class));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(consumerUserId, AccessRight.Code.MY_CT_VISIBILITY);
        verify(careTeamSecurityUtils, atLeastOnce()).getCurrentUser();
        verify(privilegesService).canInviteFriendToCareTeam();
        verify(phrResidentService, never()).createAssociatedResidentFromUserData(any(User.class));
        verify(contactService, never()).createEmployeeForInvitedFriend(any(Resident.class), anyString(), anyString(), anyString(), anyString(), any(CareTeamRole.class));
        verify(residentUpdateQueueProducer).putToResidentUpdateQueue(consumer.getResident().getId(), ResidentUpdateType.CARE_TEAM);
        verifyNoMoreInteractions(careTeamSecurityUtils, notificationsFacade, userRegistrationApplicationService);
    }

    /**
     * Edge case.
     * User-provider (guardian/physician without consumer account) invites an existing user-provider (already registered in PHR mobile)
     * to his/her own care team.
     */
    @Test(expected = PhrException.class)
    public void testInviteFriendAsProviderToProviderCareTeamThrowsNotFoundPatientInfo() {
        // Expected objects
        final CareTeamRelation.Relation relation = CareTeamRelation.Relation.FAMILY;

        // Mockito expectations
        final User current = setUpMockitoExpectationsAsProvider(userId);
        when(profileService.isPatient(current)).thenReturn(Boolean.FALSE);
        when(privilegesService.canInviteFriendToCareTeam()).thenReturn(Boolean.TRUE);

        // Execute the method being tested
        careTeamService.inviteFriend(userId, ssn, email, phone, firstName, lastName, relation);

        // Validation
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
        verify(privilegesService).canInviteFriendToCareTeam();
        verify(profileService).isPatient(current);
        verify(residentUpdateQueueProducer).putToResidentUpdateQueue(current.getResident().getId(), ResidentUpdateType.CARE_TEAM);
    }

    /**
     * User-consumer (patient) invites a non-existing user (not registered in PHR mobile, not present in Web Simply Connect system) to his/her care team,
     * but a new user cannot be automatically created, because the specified email is already in use by another user.
     *
     * @throws PhrException expected in this test case
     */
    @Test(expected = PhrException.class)
    public void testInviteFriendThrowsInviteeEmailConflict() {
        // Expected objects
        final User provider = super.createProvider(providerUserId, null, phone, "bad" + email, firstName, lastName);

        final CareTeamRelation.Relation relation = CareTeamRelation.Relation.GUARDIAN;
        final CareTeamRoleCode role = CareTeamRoleCode.ROLE_PARENT_GUARDIAN;
        final String PARENT_GUARDIAN = "Parent/Guardian";
        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setCode(role);
        careTeamRole.setName(PARENT_GUARDIAN);

        // Mockito expectations
        setUpMockitoExpectations(userId);
        when(careTeamRoleDao.getByCode(role)).thenReturn(careTeamRole);
        when(userDao.findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized))
                .thenReturn(Collections.<User>emptyList());
        when(userDao.findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized))
                .thenReturn(Collections.<User>emptyList());
        whenAskedForEmployeeThenReturn(null);
        when(privilegesService.canInviteFriendToCareTeam()).thenReturn(Boolean.TRUE);
        when(contactService.createEmployeeForInvitedFriend(any(Resident.class), eq(email), anyString(), anyString(), anyString(), any(CareTeamRole.class)))
                .thenThrow(new PhrException("Employee already exists."));

        // Execute the method being tested
        careTeamService.inviteFriend(userId, ssn, email, phone, firstName, lastName, relation);

        // Validation
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
        verify(privilegesService).canInviteFriendToCareTeam();
        verifyNoMoreInteractions(careTeamSecurityUtils);
        verifyZeroInteractions(residentUpdateQueueProducer);
    }

    /**
     * Edge case.
     * User-consumer (patient) invites  an existing user-provider (already registered in PHR mobile) to his/her care team again
     * specifying the same care team role.
     *
     * @throws PhrException expected in this test case
     */
    @Test(expected = PhrException.class)
    public void testInviteFriendThrowsAlreadyExists() {
        // Expected objects
        final User provider = super.createProvider(providerUserId, null, phone, email, firstName, lastName);

        final CareTeamRelation.Relation relation = CareTeamRelation.Relation.GUARDIAN;
        final CareTeamRoleCode role = CareTeamRoleCode.ROLE_PARENT_GUARDIAN;
        final String PARENT_GUARDIAN = "Parent/Guardian";
        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setCode(role);
        careTeamRole.setName(PARENT_GUARDIAN);
        Database database = provider.getDatabase();

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setEmergencyContact(Boolean.FALSE);
        rctm.setEmployee(provider.getEmployee());
        rctm.setCreatedByResidentId(residentId);
        rctm.setCareTeamRole(careTeamRole);

        // Mockito expectations
        setUpMockitoExpectations(userId);
        when(careTeamRoleDao.getByCode(role)).thenReturn(careTeamRole);
        when(residentCareTeamMemberDao.getResidentCareTeamMembersByEmployeeAndRole(eq(residentId), eq(employeeId), anyLong()))
                .thenReturn(Collections.singletonList(rctm));
        when(userDao.findUsersBySsnAndEmailAndPhoneNormalized(ssn, emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(provider));
        when(userDao.findUsersByEmailAndPhoneNormalized(emailNormalized, phoneNormalized))
                .thenReturn(Collections.singletonList(provider));
        when(userDao.findUserByDatabaseAndEmailNormalizedAndAutocreatedIsFalse(database, emailNormalized))
                .thenReturn(provider);
        whenAskedForEmployeeThenReturn(provider.getEmployee());
        when(privilegesService.canInviteFriendToCareTeam()).thenReturn(Boolean.TRUE);
        when(profileService.isGuardian(provider)).thenReturn(Boolean.TRUE);

        // Execute the method being tested
        careTeamService.inviteFriend(userId, null, email, phone, firstName, lastName, relation);

        // Validation
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
        verify(privilegesService).canInviteFriendToCareTeam();
        verifyNoMoreInteractions(careTeamSecurityUtils);
        verifyZeroInteractions(residentUpdateQueueProducer);
    }

    /**
     * @throws PhrException expected in this test case
     */
    @Test(expected = PhrException.class)
    public void testInviteFriendThrowsAccessForbidden() {
        // Expected objects
        final CareTeamRelation.Relation relation = CareTeamRelation.Relation.GUARDIAN;

        // Mockito expectations
        doThrow(new PhrException(PhrExceptionType.ACCESS_FORBIDDEN))
                .when(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);

        // Execute the method being tested
        careTeamService.inviteFriend(userId, null, email, phone, firstName, lastName, relation);

        // Validation
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
        verifyNoMoreInteractions(careTeamSecurityUtils);
        verifyZeroInteractions(residentUpdateQueueProducer);
    }

    /**
     * @throws PhrException expected in this test case
     */
    @Test(expected = PhrException.class)
    public void testInviteFriendAsProviderThrowsAccessForbidden() {
        // Expected objects
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);
        final CareTeamRelation.Relation relation = CareTeamRelation.Relation.GUARDIAN;

        // Mockito expectations
        setUpMockitoExpectations(consumerId);
        setUpMockitoExpectationsAsProvider(userId);
        when(privilegesService.canInviteFriendToCareTeam()).thenReturn(Boolean.FALSE);

        // Execute the method being tested
        careTeamService.inviteFriend(consumerId, null, email, phone, firstName, lastName, relation);

        // Validation
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(consumerId, AccessRight.Code.MY_CT_VISIBILITY);
        verify(privilegesService).canInviteFriendToCareTeam();
        verifyNoMoreInteractions(careTeamSecurityUtils);
        verifyZeroInteractions(residentUpdateQueueProducer);
    }

    @Test
    public void testInvitePhysician() {
        // Expected objects
        final Long physicianId = TestDataGenerator.randomId();
        final User provider = super.createProvider(providerUserId);
        final Physician physician = new Physician();
        physician.setId(physicianId);
        physician.setDiscoverable(Boolean.TRUE);
        physician.setVerified(Boolean.TRUE);
        physician.setUserMobile(provider);
        final PhysicianCategory physicianCategory = new PhysicianCategory();
        physicianCategory.setDisplayName("Behavioral health");
        final PhysicianCategory physicianCategory2 = new PhysicianCategory();
        physicianCategory2.setDisplayName("Primary physician");
        physician.setCategories(new HashSet<>(Arrays.asList(physicianCategory, physicianCategory2)));

        final CareTeamRelationship careTeamRelationship = new CareTeamRelationship();
        careTeamRelationship.setCode(CareTeamRelationship.Relationship.MEDICAL_STAFF);

        final PhysicianDto expectedPhysician = new PhysicianDto();
        expectedPhysician.setId(physicianId);
        expectedPhysician.setUserId(providerUserId);
        expectedPhysician.setFullName(provider.getFullName());
        expectedPhysician.setPhotoUrl("url" + providerUserId);
        expectedPhysician.setSpeciality(BEHAVIORAL_HEALTH);
        final CareteamMemberDto expectedDto = new CareteamMemberDto();
        expectedDto.setUserId(providerUserId);
        expectedDto.setPhotoUrl("url" + providerUserId);
        expectedDto.setEditable(Boolean.TRUE);
        expectedDto.setCareTeamRole(BEHAVIORAL_HEALTH);
        expectedDto.setRelation(null);
        expectedDto.setRelationship(CareTeamRelationship.Relationship.MEDICAL_STAFF);
        expectedDto.setEmergencyContact(Boolean.FALSE);
        expectedDto.setInvitationStatus(InvitationStatus.ACTIVE);
        expectedDto.setContactStatus(ContactStatus.EXISTING_ACTIVE);
        expectedDto.setPhysicianInfo(expectedPhysician);
        final NameDto nameDto = new NameDto();
        nameDto.setFullName(provider.getFullName());
        final PersonDto person = new PersonDto();
        person.setNames(Collections.singletonList(nameDto));
        person.setAddresses(Collections.<AddressDto>emptyList());
        expectedDto.setPerson(person);
        expectedDto.setDataSource(new DataSourceDto());

        // Mockito expectations
        final User consumer = setUpMockitoExpectations(userId);
        when(userDao.findOne(providerUserId)).thenReturn(provider);
        when(physiciansService.getPhysicianOrThrow(physicianId)).thenReturn(physician);
        when(careTeamRelationshipDao.getByCode(CareTeamRelationship.Relationship.MEDICAL_STAFF)).thenReturn(careTeamRelationship);
        when(accessRightsService.getDefaultAccessRights()).thenReturn(allAccessRights());
        when(residentCareTeamMemberDao.create(any(ResidentCareTeamMember.class))).then(returnsFirstArg());
        when(residentCareTeamMemberDao.merge(any(ResidentCareTeamMember.class))).then(returnsFirstArg());
        when(residentCareTeamMemberDao.getResidentCareTeamMembersByEmployeeAndRole(eq(residentId), eq(employeeId), anyLong()))
                .thenReturn(Collections.<ResidentCareTeamMember>emptyList());
        when(careTeamRoleDao.getByCode(provider.getEmployee().getCareTeamRole().getCode())).thenReturn(provider.getEmployee().getCareTeamRole());
        when(avatarService.getPhotoUrl(providerUserId)).thenReturn("url" + providerUserId);
        when(physiciansService.transformListItem(physician)).thenReturn(expectedPhysician);
        when(physiciansService.getPhysicianByUserId(providerUserId)).thenReturn(physician);

        // Execute the method being tested
        CareteamMemberDto result = careTeamService.invitePhysician(userId, physicianId);

        // Validation
        assertThat(result, sameBeanAs(expectedDto)
                .ignoring("id").ignoring(DataSourceDto.class));
        verify(notificationsFacade).notifyMedicalStaffAboutInvitationToCareTeam(eq(consumer), eq(provider), any(ResidentCareTeamMember.class));
        verify(notificationPreferencesService).createDefaultCareTeamMemberNotificationPreferences(any(ResidentCareTeamMember.class));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
        verify(careTeamSecurityUtils, atLeastOnce()).getCurrentUser();
        verify(residentUpdateQueueProducer).putToResidentUpdateQueue(consumer.getResident().getId(), ResidentUpdateType.CARE_TEAM);
        verifyNoMoreInteractions(careTeamSecurityUtils, notificationsFacade);
    }

    @Test
    public void testGetRecentActivity() {
        // Expected objects
        final User provider = super.createProvider(providerUserId);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setEmployee(provider.getEmployee());
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setCreatedByResidentId(residentId);

        final Date date = TestDataGenerator.randomDate();
        final Date date2 = TestDataGenerator.randomDateBefore(date);
        final Date date3 = TestDataGenerator.randomDateBefore(date2);
        final Date date4 = TestDataGenerator.randomDateBefore(date3);
        final Long activityId1 = TestDataGenerator.randomId();
        final Long activityId2 = TestDataGenerator.randomIdExceptOf(activityId1);
        final Long activityId3 = TestDataGenerator.randomIdExceptOf(activityId1, activityId2);
        final Long activityId4 = TestDataGenerator.randomIdExceptOf(activityId1, activityId2, activityId3);
        final Long eventId = TestDataGenerator.randomId();

        final CallActivity callActivity = new CallActivity();
        callActivity.setDate(date);
        callActivity.setEmployee(provider.getEmployee());
        callActivity.setId(activityId1);
        callActivity.setIncoming(Boolean.TRUE);
        callActivity.setDuration(200);

        final InvitationActivity invitationActivity = new InvitationActivity();
        invitationActivity.setDate(date4);
        invitationActivity.setEmployee(provider.getEmployee());
        invitationActivity.setId(activityId2);
        invitationActivity.setStatus(InvitationActivity.Status.ACCEPTED);

        final VideoActivity videoActivity = new VideoActivity();
        videoActivity.setDate(date2);
        videoActivity.setEmployee(provider.getEmployee());
        videoActivity.setId(activityId3);
        videoActivity.setIncoming(Boolean.FALSE);
        videoActivity.setDuration(300);

        final EventActivity eventActivity = new EventActivity();
        eventActivity.setDate(date3);
        eventActivity.setEmployee(provider.getEmployee());
        eventActivity.setId(activityId4);
        eventActivity.setEventId(eventId);
        eventActivity.setResponsibility(Responsibility.R);
        final EventType eventType = new EventType();
        eventType.setDescription("test description");
        eventActivity.setEventType(eventType);
        eventActivity.setEventTypeId(eventId);

        final CallActivityDto expectedCallActivityDto = new CallActivityDto();
        expectedCallActivityDto.setType(ActivityDto.Type.CALL);
        expectedCallActivityDto.setDate(date.getTime());
        expectedCallActivityDto.setDuration(200);
        expectedCallActivityDto.setCallType(ActivityDto.CallType.INCOMING);
        final InvitationActivityDto expectedInvitationActivityDto = new InvitationActivityDto();
        expectedInvitationActivityDto.setType(ActivityDto.Type.INVITATION);
        expectedInvitationActivityDto.setDate(date4.getTime());
        expectedInvitationActivityDto.setStatus(InvitationActivity.Status.ACCEPTED);
        final VideoActivityDto expectedVideoActivityDto = new VideoActivityDto();
        expectedVideoActivityDto.setType(ActivityDto.Type.VIDEO);
        expectedVideoActivityDto.setDate(date2.getTime());
        expectedVideoActivityDto.setDuration(300);
        expectedVideoActivityDto.setVideoType(ActivityDto.CallType.OUTGOING);
        final EventActivityDto expectedEventActivityDto = new EventActivityDto();
        expectedEventActivityDto.setType(ActivityDto.Type.EVENT);
        expectedEventActivityDto.setDate(date3.getTime());
        expectedEventActivityDto.setUnread(Boolean.TRUE);
        expectedEventActivityDto.setResponsibility(Responsibility.R.getDescription());
        expectedEventActivityDto.setEventId(eventId);
        expectedEventActivityDto.setEventType("test description");

        final Pageable pageable = PaginationUtils.buildPageable(10, 0);

        // Mockito expectations
        setUpMockitoExpectations(userId);
        when(userDao.findOne(providerUserId)).thenReturn(provider);
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        when(activityService.getRecentActivity(eq(userId), eq(provider.getEmployee()), any(Pageable.class)))
                .thenReturn(Arrays.asList(invitationActivity, eventActivity, videoActivity, callActivity));
        when(eventReadStatusDao.existsByUserIdAndEventId(userId, eventId)).thenReturn(Boolean.FALSE);

        // Execute the method being tested
        List<ActivityDto> result = careTeamService.getRecentActivity(userId, contactId, pageable);

        assertThat(result, hasSize(4));
        assertThat(result.get(0), instanceOf(InvitationActivityDto.class));
        assertThat((InvitationActivityDto) result.get(0), sameBeanAs(expectedInvitationActivityDto));
        assertThat(result.get(1), instanceOf(EventActivityDto.class));
        assertThat((EventActivityDto) result.get(1), sameBeanAs(expectedEventActivityDto));
        assertThat(result.get(2), instanceOf(VideoActivityDto.class));
        assertThat((VideoActivityDto) result.get(2), sameBeanAs(expectedVideoActivityDto));
        assertThat(result.get(3), instanceOf(CallActivityDto.class));
        assertThat((CallActivityDto) result.get(3), sameBeanAs(expectedCallActivityDto));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
    }

    @Test
    public void testGetRecentActivityAsProvider() {
        // Expected objects
        final User current = setUpMockitoExpectationsAsProvider(userId);
        final Long consumerUserId = TestDataGenerator.randomIdExceptOf(userId);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setEmployee(current.getEmployee());
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setCreatedByResidentId(residentId);

        final Date date = TestDataGenerator.randomDate();
        final Date date2 = TestDataGenerator.randomDateBefore(date);
        final Date date3 = TestDataGenerator.randomDateBefore(date2);
        final Date date4 = TestDataGenerator.randomDateBefore(date3);
        final Long activityId1 = TestDataGenerator.randomId();
        final Long activityId2 = TestDataGenerator.randomIdExceptOf(activityId1);
        final Long activityId3 = TestDataGenerator.randomIdExceptOf(activityId1, activityId2);
        final Long activityId4 = TestDataGenerator.randomIdExceptOf(activityId1, activityId2, activityId3);
        final Long eventId = TestDataGenerator.randomId();

        final CallActivity callActivity = new CallActivity();
        callActivity.setDate(date);
        callActivity.setEmployee(current.getEmployee());
        callActivity.setId(activityId1);
        callActivity.setIncoming(Boolean.TRUE);
        callActivity.setDuration(200);

        final InvitationActivity invitationActivity = new InvitationActivity();
        invitationActivity.setDate(date4);
        invitationActivity.setEmployee(current.getEmployee());
        invitationActivity.setId(activityId2);
        invitationActivity.setStatus(InvitationActivity.Status.ACCEPTED);

        final VideoActivity videoActivity = new VideoActivity();
        videoActivity.setDate(date2);
        videoActivity.setEmployee(current.getEmployee());
        videoActivity.setId(activityId3);
        videoActivity.setIncoming(Boolean.FALSE);
        videoActivity.setDuration(300);

        final EventActivity eventActivity = new EventActivity();
        eventActivity.setDate(date3);
        eventActivity.setEmployee(current.getEmployee());
        eventActivity.setId(activityId4);
        eventActivity.setEventId(eventId);
        eventActivity.setResponsibility(Responsibility.R);
        final EventType eventType = new EventType();
        eventType.setDescription("test description");
        eventActivity.setEventType(eventType);
        eventActivity.setEventTypeId(eventId);

        final CallActivityDto expectedCallActivityDto = new CallActivityDto();
        expectedCallActivityDto.setType(ActivityDto.Type.CALL);
        expectedCallActivityDto.setDate(date.getTime());
        expectedCallActivityDto.setDuration(200);
        expectedCallActivityDto.setCallType(ActivityDto.CallType.OUTGOING);
        final InvitationActivityDto expectedInvitationActivityDto = new InvitationActivityDto();
        expectedInvitationActivityDto.setType(ActivityDto.Type.INVITATION);
        expectedInvitationActivityDto.setDate(date4.getTime());
        expectedInvitationActivityDto.setStatus(InvitationActivity.Status.ACCEPTED);
        final VideoActivityDto expectedVideoActivityDto = new VideoActivityDto();
        expectedVideoActivityDto.setType(ActivityDto.Type.VIDEO);
        expectedVideoActivityDto.setDate(date2.getTime());
        expectedVideoActivityDto.setDuration(300);
        expectedVideoActivityDto.setVideoType(ActivityDto.CallType.INCOMING);
        final EventActivityDto expectedEventActivityDto = new EventActivityDto();
        expectedEventActivityDto.setType(ActivityDto.Type.EVENT);
        expectedEventActivityDto.setDate(date3.getTime());
        expectedEventActivityDto.setUnread(Boolean.FALSE);
        expectedEventActivityDto.setResponsibility(Responsibility.R.getDescription());
        expectedEventActivityDto.setEventId(eventId);
        expectedEventActivityDto.setEventType("test description");

        final Pageable pageable = PaginationUtils.buildPageable(10, 0);

        // Mockito expectations
        setUpMockitoExpectations(consumerUserId);
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        when(activityService.getRecentActivity(eq(consumerUserId), eq(current.getEmployee()), any(Pageable.class)))
                .thenReturn(Arrays.asList(invitationActivity, eventActivity, videoActivity, callActivity));
        when(eventReadStatusDao.existsByUserIdAndEventId(userId, eventId)).thenReturn(Boolean.TRUE);

        // Execute the method being tested
        List<ActivityDto> result = careTeamService.getRecentActivity(consumerUserId, contactId, pageable);

        assertThat(result, hasSize(4));
        assertThat(result.get(0), instanceOf(InvitationActivityDto.class));
        assertThat((InvitationActivityDto) result.get(0), sameBeanAs(expectedInvitationActivityDto));
        assertThat(result.get(1), instanceOf(EventActivityDto.class));
        assertThat((EventActivityDto) result.get(1), sameBeanAs(expectedEventActivityDto));
        assertThat(result.get(2), instanceOf(VideoActivityDto.class));
        assertThat((VideoActivityDto) result.get(2), sameBeanAs(expectedVideoActivityDto));
        assertThat(result.get(3), instanceOf(CallActivityDto.class));
        assertThat((CallActivityDto) result.get(3), sameBeanAs(expectedCallActivityDto));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(consumerUserId, AccessRight.Code.MY_CT_VISIBILITY);
    }

    @Test
    public void testGetRecentActivityCount() {
        // Expected objects
        final User provider = super.createProvider(providerUserId);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setEmployee(provider.getEmployee());
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setCreatedByResidentId(residentId);

        // Mockito expectations
        setUpMockitoExpectations(userId);
        when(userDao.findOne(providerUserId)).thenReturn(provider);
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        when(activityService.countRecentActivity(eq(userId), eq(provider.getEmployee()))).thenReturn(4L);

        // Execute the method being tested
        long result = careTeamService.getRecentActivityCount(userId, contactId);

        assertEquals(4L, result);
    }

    @Test(expected = PhrException.class)
    public void testGetRecentActivityCountThrowsNotAssociated() {
        // Expected objects
        final User provider = super.createProvider(providerUserId);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setEmployee(provider.getEmployee());
        rctm.setResident(resident2);
        rctm.setResidentId(residentId2);
        rctm.setCreatedByResidentId(residentId);

        // Mockito expectations
        setUpMockitoExpectations(userId);
        when(userDao.findOne(providerUserId)).thenReturn(provider);
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        when(activityService.countRecentActivity(eq(userId), eq(provider.getEmployee()))).thenReturn(4L);

        // Execute the method being tested
        careTeamService.getRecentActivityCount(userId, contactId);
    }

    @Test
    public void testGetRecentActivity2() {
        // Expected objects
        final User provider = super.createProvider(providerUserId);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setEmployee(provider.getEmployee());
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setCreatedByResidentId(residentId);

        final Pageable pageable = PaginationUtils.buildPageable(100, 0);

        // Mockito expectations
        setUpMockitoExpectations(userId);
        when(userDao.findOne(providerUserId)).thenReturn(provider);
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        when(activityService.getRecentActivity(eq(userId), eq(provider.getEmployee()), any(Pageable.class)))
                .thenReturn(Collections.<Activity>emptyList());

        // Execute the method being tested
        List<? extends ActivityDto> result = careTeamService.getRecentActivity(userId, contactId, pageable);

        assertThat(result, hasSize(0));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
    }

    @Test
    public void testLogActivity() {
        // Expected objects
        final User provider = super.createProvider(providerUserId);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setEmployee(provider.getEmployee());
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setCreatedByResidentId(residentId);

        // Mockito expectations
        setUpMockitoExpectations(userId);
        when(userDao.findOne(providerUserId)).thenReturn(provider);
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);

        // Execute the method being tested
        careTeamService.logActivity(userId, contactId, new CallActivityDto());

        verify(activityService).logCallActivity(eq(userId), eq(provider.getEmployee()), any(ActivityDto.class));
    }

    @Test
    public void testCanManageAccessRights() {
        // Expected objects
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setCreatedByResidentId(residentId);

        // Mockito expectations
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        setUpMockitoExpectations(userId);

        // Execute the method being tested
        boolean result = careTeamService.canManageAccessRights(contactId);

        // Validation
        assertEquals(true, result);
    }

    @Test
    public void testCanManageAccessRightsFalse() {
        // Expected objects
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);

        // Mockito expectations
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        setUpMockitoExpectations(userId);

        // Execute the method being tested
        boolean result = careTeamService.canManageAccessRights(contactId);

        // Validation
        assertEquals(false, result);
    }

    @Test
    public void testGetAccessRights() {
        // Expected objects
        final Set<AccessRight> accessRights = new HashSet<>();
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setCreatedByResidentId(residentId);
        rctm.setAccessRights(accessRights);

        // Mockito expectations
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        setUpMockitoExpectations(userId);

        // Execute the method being tested
        final Map<AccessRight.Code, Boolean> result = careTeamService.getAccessRights(userId, contactId);

        assertNotNull(result);
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId);
    }

    @Test
    public void testUpdateAccessRights() {
        // Expected objects
        final Set<AccessRight> accessRights = new HashSet<>();
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setCreatedByResidentId(residentId);
        rctm.setAccessRights(accessRights);

        final HashMap<AccessRight.Code, Boolean> accessRightsMap = new HashMap<AccessRight.Code, Boolean>() {{
            put(AccessRight.Code.MY_PHR, Boolean.TRUE);
            put(AccessRight.Code.EVENT_NOTIFICATIONS, Boolean.FALSE);
            put(AccessRight.Code.MY_CT_VISIBILITY, Boolean.TRUE);
            put(AccessRight.Code.MEDICATIONS_LIST, Boolean.FALSE);
        }};

        // Mockito expectations
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        setUpMockitoExpectations(userId);

        // Execute the method being tested
        careTeamService.updateAccessRights(userId, contactId, accessRightsMap);

        // Validation
        verify(accessRightsService).updateAccessRights(rctm, accessRightsMap);
        verify(residentCareTeamMemberDao).merge(rctm);
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
    }

    @Test(expected = PhrException.class)
    public void testUpdateAccessRightsAsProviderThrowsAccessForbidden2() {
        // Expected objects
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);
        final User provider = setUpMockitoExpectationsAsProvider(userId);
        final User consumer = setUpMockitoExpectations(consumerId);
        consumer.setResident(resident);
        consumer.setResidentId(residentId);

        final Set<AccessRight> accessRights = new HashSet<>();
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setCreatedByResidentId(null);
        rctm.setCreatedById(provider.getEmployeeId());
        rctm.setAccessRights(accessRights);

        final HashMap<AccessRight.Code, Boolean> accessRightsMap = new HashMap<AccessRight.Code, Boolean>() {{
            put(AccessRight.Code.MY_PHR, Boolean.TRUE);
            put(AccessRight.Code.EVENT_NOTIFICATIONS, Boolean.FALSE);
            put(AccessRight.Code.MY_CT_VISIBILITY, Boolean.TRUE);
            put(AccessRight.Code.MEDICATIONS_LIST, Boolean.TRUE);
        }};

        // Mockito expectations
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);

        // Execute the method being tested
        careTeamService.updateAccessRights(consumerId, contactId, accessRightsMap);

        // Validation
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(consumerId, AccessRight.Code.MY_CT_VISIBILITY);
    }

    @Test(expected = PhrException.class)
    public void testUpdateAccessRightsThrowsAccessForbidden() {
        // Expected objects
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setCreatedByResidentId(null);
        rctm.setCreatedById(null);

        final HashMap<AccessRight.Code, Boolean> accessRightsMap = new HashMap<>();

        // Mockito expectations
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        setUpMockitoExpectations(userId);

        // Execute the method being tested
        careTeamService.updateAccessRights(userId, contactId, accessRightsMap);
    }

    @Test(expected = PhrException.class)
    public void testUpdateAccessRightsAsProviderThrowsAccessForbidden() {
        // Expected objects
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);
        final Long employeeId2 = TestDataGenerator.randomIdExceptOf(employeeId);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        rctm.setCreatedByResidentId(null);
        rctm.setCreatedById(employeeId2);

        final HashMap<AccessRight.Code, Boolean> accessRightsMap = new HashMap<>();

        // Mockito expectations
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        setUpMockitoExpectations(consumerId);
        setUpMockitoExpectationsAsProvider(userId);

        // Execute the method being tested
        careTeamService.updateAccessRights(consumerId, contactId, accessRightsMap);
    }

    // === Utility

    private static Set<AccessRight> allAccessRights() {
        final Set<AccessRight> accessRights = new HashSet<>();

        for (AccessRight.Code code : AccessRight.Code.values()) {
            AccessRight accessRight = new AccessRight();
            accessRight.setCode(code);
            accessRight.setDisplayName(code.toString());
            accessRight.setId((long) code.ordinal());

            accessRights.add(accessRight);
        }

        return accessRights;
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme