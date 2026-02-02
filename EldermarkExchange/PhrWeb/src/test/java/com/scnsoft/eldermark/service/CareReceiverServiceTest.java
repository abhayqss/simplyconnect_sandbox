package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.carecoordination.ResidentCareTeamMemberDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.entity.phr.UserResidentRecord;
import com.scnsoft.eldermark.service.palatiumcare.LocationService;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.Gender;
import com.scnsoft.eldermark.shared.ccd.AddressDto;
import com.scnsoft.eldermark.shared.ccd.NameDto;
import com.scnsoft.eldermark.shared.ccd.PersonDto;
import com.scnsoft.eldermark.shared.ccd.TelecomDto;
import com.scnsoft.eldermark.util.MockitoAnswers;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.CareReceiverDto;
import com.scnsoft.eldermark.web.entity.DataSourceDto;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 7/4/2017.
 */
public class CareReceiverServiceTest extends BaseServiceTest {

    @Mock
    private ResidentCareTeamMemberDao residentCareTeamMemberDao;
    @Mock
    private HealthProviderService healthProviderService;
    @SuppressWarnings("unused")
    @Mock
    private AvatarService avatarService;
    @Mock
    private PrivilegesService privilegesService;
    @Mock
    private UserDao userDao;
    @SuppressWarnings("unused")
    @Mock
    private LocationService locationService;

    @InjectMocks
    private CareReceiverService careReceiverService;

    // Shared test data
    private final Long contactId = TestDataGenerator.randomId();
    private final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);
    private final String ssn = TestDataGenerator.randomValidSsn();
    private final String phone = TestDataGenerator.randomPhone();
    private final String email = TestDataGenerator.randomEmail();
    private final String firstName = TestDataGenerator.randomName();
    private final String lastName = TestDataGenerator.randomName();

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID (current): %d\nCare team member ID (Contact ID): %d\nUser ID (consumer): %d\nSSN (consumer): %s\nPhone (consumer): %s\nEmail (consumer): %s\nFirst name (consumer): %s\nLast name (consumer): %s\n\n",
                userId, contactId, consumerId, ssn, phone, email, firstName, lastName);
    }

    @Before
    public void injectDozer() {
        final DozerBeanMapper dozer = new DozerBeanMapper();
        careReceiverService.setDozer(dozer);
    }

    private User setUpMockitoExpectations(Long consumerUserId) {
        final User user = super.createConsumer(consumerUserId, ssn, phone, email, firstName, lastName);

        final com.scnsoft.eldermark.entity.phr.UserResidentRecord record = new UserResidentRecord();
        record.setUserId(consumerUserId);
        record.setResidentId(user.getResidentId());

        when(healthProviderService.getUsersByResidentId(user.getResident().getId())).thenReturn(Collections.singleton(user));

        when(userDao.findOne(consumerUserId)).thenReturn(user);
        when(userDao.getOne(consumerUserId)).thenReturn(user);

        return user;
    }

    private User setUpMockitoExpectationsAsProvider(Long providerUserId) {
        final User user = super.createProvider(providerUserId);

        when(userDao.findOne(providerUserId)).thenReturn(user);
        when(userDao.getOne(providerUserId)).thenReturn(user);

        return user;
    }

    @Test
    public void testGetCareReceiver() {
        final User provider = setUpMockitoExpectationsAsProvider(userId);
        final User consumer = setUpMockitoExpectations(consumerId);

        // Expected objects
        final CareTeamRelation careTeamRelation = new CareTeamRelation();
        careTeamRelation.setCode(CareTeamRelation.Relation.FRIEND);
        final CareTeamRelationship careTeamRelationship = new CareTeamRelationship();
        careTeamRelationship.setCode(CareTeamRelationship.Relationship.FRIEND_FAMILY);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(consumer.getResident());
        rctm.setResidentId(consumer.getResidentId());
        rctm.setEmergencyContact(Boolean.FALSE);
        rctm.setCreatedByResidentId(consumer.getResidentId());
        rctm.setCareTeamRole(provider.getEmployee().getCareTeamRole());
        rctm.setCareTeamRelation(careTeamRelation);
        rctm.setCareTeamRelationship(careTeamRelationship);
        rctm.setEmployee(provider.getEmployee());
        rctm.setAccessRights(Collections.<AccessRight>emptySet());
        rctm.setCreatedBy(null);
        rctm.setCreatedById(null);

        final HashMap<AccessRight.Code, Boolean> accessRightsMap = new HashMap<AccessRight.Code, Boolean>() {{
            put(AccessRight.Code.MY_PHR, Boolean.FALSE);
            put(AccessRight.Code.EVENT_NOTIFICATIONS, Boolean.FALSE);
            put(AccessRight.Code.MY_CT_VISIBILITY, Boolean.FALSE);
            put(AccessRight.Code.MEDICATIONS_LIST, Boolean.FALSE);
        }};

        final CareReceiverDto expectedCareReceiverDto = new CareReceiverDto();
        expectedCareReceiverDto.setId(contactId);
        expectedCareReceiverDto.setUserId(consumerId);
        expectedCareReceiverDto.setRelation(careTeamRelation.getCode());
        expectedCareReceiverDto.setContactEmail(consumer.getEmail());
        expectedCareReceiverDto.setContactPhone(consumer.getPhone());
        expectedCareReceiverDto.setSsnLastFourDigits(consumer.getSsnLastFourDigits());
        expectedCareReceiverDto.setAccessRights(accessRightsMap);
        expectedCareReceiverDto.setCommunity(consumer.getResident().getFacility().getName());
        expectedCareReceiverDto.setCommunityId(consumer.getResident().getFacility().getId());
        expectedCareReceiverDto.setAge(consumer.getResident().getAge());
        expectedCareReceiverDto.setGender(Gender.MALE);
        expectedCareReceiverDto.setCanInviteFriend(Boolean.FALSE);
        final NameDto nameDto = new NameDto();
        nameDto.setFullName(consumer.getFullName());
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
        expectedCareReceiverDto.setPerson(person);
        final DataSourceDto dataSourceDto = new DataSourceDto();
        dataSourceDto.setResidentId(consumer.getResidentId());
        dataSourceDto.setName(consumer.getResident().getDatabase().getName());
        dataSourceDto.setId(consumer.getResident().getDatabaseId());
        expectedCareReceiverDto.setDataSource(dataSourceDto);

        // Mockito expectations
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);
        when(privilegesService.canInviteFriendToCareTeam()).thenReturn(Boolean.FALSE);

        // Execute the method being tested
        CareReceiverDto result = careReceiverService.getCareReceiver(userId, contactId);

        // Validation
        assertThat(result, sameBeanAs(expectedCareReceiverDto));
    }

    @Test(expected = PhrException.class)
    public void testGetCareReceiverThrowsNotAssociated() {
        // Expected objects
        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId);
        rctm.setResident(resident);
        rctm.setResidentId(residentId);
        final Employee wrongEmployee = new Employee();
        wrongEmployee.setId(TestDataGenerator.randomIdExceptOf(employeeId));
        rctm.setEmployee(wrongEmployee);

        // Mockito expectations
        setUpMockitoExpectationsAsProvider(userId);
        when(residentCareTeamMemberDao.get(contactId)).thenReturn(rctm);

        // Execute the method being tested
        careReceiverService.getCareReceiver(userId, contactId);
    }

    @Test
    public void testGetCareReceivers() {
        final User provider = setUpMockitoExpectationsAsProvider(userId);
        final User consumer = setUpMockitoExpectations(consumerId);

        // Expected objects
        final String email2 = TestDataGenerator.randomEmail();
        final String phone2 = TestDataGenerator.randomPhone();
        resident2.setFacility(consumer.getResident().getFacility());
        resident2.setDatabase(consumer.getResident().getDatabase());
        final Person person2 = createPerson(phone2, email2, firstName, lastName, PersonTelecomCode.HP);
        resident2.setPerson(person2);
        final Person person3 = createPerson(null, null, firstName, lastName, null);
        resident3.setPerson(person3);
        resident3.setFacility(consumer.getResident().getFacility());
        resident3.setDatabase(consumer.getResident().getDatabase());

        final CareTeamRelation careTeamRelation = new CareTeamRelation();
        careTeamRelation.setCode(CareTeamRelation.Relation.FRIEND);
        final CareTeamRelationship careTeamRelationship = new CareTeamRelationship();
        careTeamRelationship.setCode(CareTeamRelationship.Relationship.FRIEND_FAMILY);

        final ResidentCareTeamMember rctm = new ResidentCareTeamMember();
        rctm.setId(contactId - 1);
        rctm.setResident(consumer.getResident());
        rctm.setResidentId(consumer.getResidentId());
        rctm.setEmergencyContact(Boolean.FALSE);
        rctm.setCreatedByResidentId(consumer.getResidentId());
        rctm.setCareTeamRole(provider.getEmployee().getCareTeamRole());
        rctm.setCareTeamRelation(careTeamRelation);
        rctm.setCareTeamRelationship(careTeamRelationship);
        rctm.setEmployee(provider.getEmployee());
        rctm.setAccessRights(Collections.<AccessRight>emptySet());
        rctm.setCreatedBy(null);
        rctm.setCreatedById(null);

        final ResidentCareTeamMember rctm2 = new ResidentCareTeamMember();
        rctm2.setId(contactId);
        rctm2.setResident(resident2);
        rctm2.setResidentId(residentId2);
        rctm2.setEmergencyContact(Boolean.FALSE);
        rctm2.setCreatedByResidentId(null);
        rctm2.setCareTeamRole(provider.getEmployee().getCareTeamRole());
        rctm2.setCareTeamRelation(careTeamRelation);
        rctm2.setCareTeamRelationship(careTeamRelationship);
        rctm2.setEmployee(provider.getEmployee());
        rctm2.setAccessRights(Collections.<AccessRight>emptySet());  // simplification: actually Care Team Members assigned via Web SC have full read access to patient's data
        rctm2.setCreatedBy(provider.getEmployee());
        rctm2.setCreatedById(provider.getEmployeeId());

        final ResidentCareTeamMember rctm3 = new ResidentCareTeamMember();
        rctm3.setId(contactId + 1);
        rctm3.setResident(resident3);
        rctm3.setResidentId(residentId3);
        rctm3.setEmergencyContact(Boolean.TRUE);
        rctm3.setCreatedByResidentId(null);
        rctm3.setCareTeamRole(provider.getEmployee().getCareTeamRole());
        rctm3.setCareTeamRelation(careTeamRelation);
        rctm3.setCareTeamRelationship(careTeamRelationship);
        rctm3.setEmployee(provider.getEmployee());
        rctm3.setAccessRights(Collections.<AccessRight>emptySet());
        rctm3.setCreatedBy(null);
        rctm3.setCreatedById(null);

        final HashMap<AccessRight.Code, Boolean> accessRightsMap = new HashMap<AccessRight.Code, Boolean>() {{
            put(AccessRight.Code.MY_PHR, Boolean.FALSE);
            put(AccessRight.Code.EVENT_NOTIFICATIONS, Boolean.FALSE);
            put(AccessRight.Code.MY_CT_VISIBILITY, Boolean.FALSE);
            put(AccessRight.Code.MEDICATIONS_LIST, Boolean.FALSE);
        }};

        final CareReceiverDto expectedCareReceiverDto = new CareReceiverDto();
        expectedCareReceiverDto.setId(contactId - 1);
        expectedCareReceiverDto.setUserId(consumerId);
        expectedCareReceiverDto.setRelation(careTeamRelation.getCode());
        expectedCareReceiverDto.setContactEmail(consumer.getEmail());
        expectedCareReceiverDto.setContactPhone(consumer.getPhone());
        expectedCareReceiverDto.setSsnLastFourDigits(consumer.getSsnLastFourDigits());
        expectedCareReceiverDto.setAccessRights(accessRightsMap);
        expectedCareReceiverDto.setCommunity(consumer.getResident().getFacility().getName());
        expectedCareReceiverDto.setCommunityId(consumer.getResident().getFacility().getId());
        expectedCareReceiverDto.setAge(consumer.getResident().getAge());
        expectedCareReceiverDto.setGender(Gender.MALE);
        expectedCareReceiverDto.setCanInviteFriend(Boolean.TRUE);
        PersonDto expectedPerson = createExpectedPerson(consumer.getResident().getPerson());
        expectedCareReceiverDto.setPerson(expectedPerson);
        final DataSourceDto dataSourceDto = new DataSourceDto();
        dataSourceDto.setResidentId(consumer.getResidentId());
        dataSourceDto.setName(consumer.getResident().getDatabase().getName());
        dataSourceDto.setId(consumer.getResident().getDatabaseId());
        expectedCareReceiverDto.setDataSource(dataSourceDto);

        final CareReceiverDto expectedCareReceiverDto2 = new CareReceiverDto();
        expectedCareReceiverDto2.setId(contactId);
        expectedCareReceiverDto2.setUserId(null);
        expectedCareReceiverDto2.setRelation(careTeamRelation.getCode());
        expectedCareReceiverDto2.setContactEmail(email2);
        expectedCareReceiverDto2.setContactPhone(phone2);
        expectedCareReceiverDto2.setSsnLastFourDigits(resident2.getSsnLastFourDigits());
        expectedCareReceiverDto2.setAccessRights(accessRightsMap);
        expectedCareReceiverDto2.setCommunity(resident2.getFacility().getName());
        expectedCareReceiverDto2.setCommunityId(resident2.getFacility().getId());
        expectedCareReceiverDto2.setAge(null);
        expectedCareReceiverDto2.setGender(null);
        expectedCareReceiverDto2.setCanInviteFriend(Boolean.TRUE);
        final PersonDto expectedPerson2 = createExpectedPerson(person2);
        expectedCareReceiverDto2.setPerson(expectedPerson2);
        final DataSourceDto dataSourceDto2 = new DataSourceDto();
        dataSourceDto2.setResidentId(residentId2);
        dataSourceDto2.setName(resident2.getDatabase().getName());
        dataSourceDto2.setId(resident2.getDatabase().getId());
        expectedCareReceiverDto2.setDataSource(dataSourceDto2);

        final CareReceiverDto expectedCareReceiverDto3 = new CareReceiverDto();
        expectedCareReceiverDto3.setId(contactId + 1);
        expectedCareReceiverDto3.setUserId(null);
        expectedCareReceiverDto3.setRelation(careTeamRelation.getCode());
        expectedCareReceiverDto3.setContactEmail(null);
        expectedCareReceiverDto3.setContactPhone(null);
        expectedCareReceiverDto3.setSsnLastFourDigits(resident2.getSsnLastFourDigits());
        expectedCareReceiverDto3.setAccessRights(accessRightsMap);
        expectedCareReceiverDto3.setCommunity(resident2.getFacility().getName());
        expectedCareReceiverDto3.setCommunityId(resident2.getFacility().getId());
        expectedCareReceiverDto3.setAge(null);
        expectedCareReceiverDto3.setGender(null);
        expectedCareReceiverDto3.setCanInviteFriend(Boolean.TRUE);
        final PersonDto expectedPerson3 = createExpectedPerson(person3);
        expectedCareReceiverDto3.setPerson(expectedPerson3);
        final DataSourceDto dataSourceDto3 = new DataSourceDto();
        dataSourceDto3.setResidentId(residentId3);
        dataSourceDto3.setName(resident3.getDatabase().getName());
        dataSourceDto3.setId(resident3.getDatabase().getId());
        expectedCareReceiverDto3.setDataSource(dataSourceDto3);

        final List<CareReceiverDto> expectedCareReceivers = Arrays.asList(expectedCareReceiverDto, expectedCareReceiverDto2, expectedCareReceiverDto3);

        // Mockito expectations
        when(residentCareTeamMemberDao.getCareTeamMembersByEmployeeIds(eq(Collections.singleton(provider.getEmployeeId())), any(Pageable.class)))
                .thenReturn(Arrays.asList(rctm, rctm2, rctm3));
        when(userDao.save(any(User.class))).then(MockitoAnswers.returnsPersistedUser());
        when(userDao.saveAndFlush(any(User.class))).then(MockitoAnswers.returnsPersistedUser());
        when(healthProviderService.getUsersByResidentId(consumer.getResidentId())).thenReturn(Collections.singleton(consumer));
        when(privilegesService.canInviteFriendToCareTeam()).thenReturn(Boolean.TRUE);

        // Execute the method being tested
        List<CareReceiverDto> result = careReceiverService.getCareReceivers(userId, null);

        // Validation
        assertThat(result, sameBeanAs(expectedCareReceivers));
        verify(healthProviderService).getUsersByResidentId(residentId3);
        verify(healthProviderService).getUsersByResidentId(residentId2);
        verify(healthProviderService).getUsersByResidentId(consumer.getResidentId());
        verify(userDao, times(2)).saveAndFlush(any(User.class));
        verify(userDao, never()).saveAndFlush(consumer);
        verify(userDao, never()).saveAndFlush(provider);
        verify(healthProviderService, times(2)).updateUserResidentRecords(any(User.class));
    }

    @Test
    public void testCountCareReceivers() {
        final User provider = setUpMockitoExpectationsAsProvider(userId);

        // Mockito expectations
        when(residentCareTeamMemberDao.getCareTeamMembersCountByEmployeeIds(Collections.singleton(provider.getEmployeeId())))
                .thenReturn(3L);

        // Execute the method being tested
        Long result = careReceiverService.countCareReceivers(userId);

        // Validation
        assertThat(result, equalTo(3L));
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme