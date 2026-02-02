package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.ResidentCareTeamMember;
import com.scnsoft.eldermark.entity.externalapi.NucleusInfo;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.phr.AccountType;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.services.externalapi.NucleusInfoService;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.NucleusInfoDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 4/19/2018.
 */
public class VideoCallNucleusServiceTest extends BaseServiceTest {

    @Mock
    private CareReceiverService careReceiverService;
    @Mock
    private CareTeamService careTeamService;
    @Mock
    private UserDao userDao;
    @Mock
    private UserResidentRecordsDao userResidentRecordsDao;
    @Mock
    private CareTeamSecurityUtils careTeamSecurityUtils;
    @Mock
    private NucleusInfoService nucleusInfoService;
    @Spy
    private DozerBeanMapper dozer;

    @InjectMocks
    private VideoCallNucleusService videoCallNucleusService;

    private final String nucleusUserId = UUID.randomUUID().toString();
    private final Long ctmId = TestDataGenerator.randomId();

    private User setUpMockitoExpectations(Long consumerUserId) {
        final User currentUser = super.setUpMockitoExpectationsForCurrentUser(consumerUserId);

        final boolean isConsumer = Objects.equals(consumerUserId, userId);
        if (isConsumer) {
            when(userResidentRecordsDao.getActiveResidentIdsByUserId(consumerUserId)).thenReturn(activeResidentIds);
            when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerUserId)).thenReturn(allResidentIds);
        }
        when(userDao.findOne(userId)).thenReturn(currentUser);

        return currentUser;
    }

    private List<NucleusInfoDto> setUpMockitoExpectationsForNucleusInfoService(User user, String nucleusUserIdOfEmployee, String nucleusUserIdOfResident) {
        final List<NucleusInfo> persisted;
        final List<NucleusInfoDto> expected;

        final NucleusInfoDto dto = new NucleusInfoDto();
        dto.setResidentId(user.getResidentId());
        dto.setEmployeeId(user.getEmployeeId());

        if (StringUtils.isEmpty(nucleusUserIdOfResident) && StringUtils.isEmpty(nucleusUserIdOfEmployee)) {
            persisted = Collections.emptyList();
            expected = Collections.emptyList();
        } else if (StringUtils.isEmpty(nucleusUserIdOfResident)) {
            // for provider
            persisted = Collections.emptyList();
            dto.setNucleusUserId(nucleusUserIdOfEmployee);
            expected = Collections.singletonList(dto);
        } else {
            // for consumer
            final NucleusInfo info = new NucleusInfo();
            info.setResidentId(user.getResidentId());
            info.setEmployeeId(user.getEmployeeId());
            info.setNucleusUserId(nucleusUserIdOfResident);
            persisted = Collections.singletonList(info);
            dto.setNucleusUserId(nucleusUserIdOfResident);
            expected = Collections.singletonList(dto);
        }

        when(nucleusInfoService.findByResidentIds(activeResidentIds)).thenReturn(persisted);
        if (StringUtils.isNotEmpty(nucleusUserIdOfResident)) {
            when(nucleusInfoService.findByResidentId(user.getResidentId())).thenReturn(nucleusUserIdOfResident);
        }
        if (StringUtils.isNotEmpty(nucleusUserIdOfEmployee)) {
            when(nucleusInfoService.findByEmployeeId(employeeId)).thenReturn(nucleusUserIdOfEmployee);
        }

        return expected;
    }

    private void setUpMockitoExpectationsForCareTeam(User consumer, User provider, Long ctmId) {
        final ResidentCareTeamMember persisted = new ResidentCareTeamMember();
        persisted.setId(ctmId);
        persisted.setResidentId(consumer.getResidentId());
        persisted.setResident(consumer.getResident());
        persisted.setEmployee(provider.getEmployee());

        when(careTeamService.getResidentCareTeamMemberOrThrow(consumer.getId(), ctmId)).thenReturn(persisted);
        when(careReceiverService.getCareReceiverOrThrow(provider.getId(), ctmId)).thenReturn(persisted);
    }

    // ==================================================================================================

    @Test
    public void testListNucleusInfoEmpty() throws Exception {
        final Long consumerId = userId;

        // Mockito expectations
        User user = setUpMockitoExpectations(consumerId);
        final List<NucleusInfoDto> expected = setUpMockitoExpectationsForNucleusInfoService(user, null, null);

        // Execute the method being tested
        List<NucleusInfoDto> result = videoCallNucleusService.listNucleusInfo(AccountType.Type.CONSUMER);

        // Validation
        assertThat(result, sameBeanAs(expected));
    }

    @Test
    public void testListNucleusInfoEmptyAsProvider() throws Exception {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Mockito expectations
        User user = setUpMockitoExpectations(consumerId);
        final List<NucleusInfoDto> expected = setUpMockitoExpectationsForNucleusInfoService(user, null, null);

        // Execute the method being tested
        List<NucleusInfoDto> result = videoCallNucleusService.listNucleusInfo(AccountType.Type.PROVIDER);

        // Validation
        assertThat(result, sameBeanAs(expected));
    }

    @Test
    public void testListNucleusInfo() throws Exception {
        final Long consumerId = userId;

        // Mockito expectations
        final User user = setUpMockitoExpectations(consumerId);
        final List<NucleusInfoDto> expected = setUpMockitoExpectationsForNucleusInfoService(user, null, nucleusUserId);

        // Execute the method being tested
        List<NucleusInfoDto> result = videoCallNucleusService.listNucleusInfo(AccountType.Type.CONSUMER);

        // Validation
        assertThat(result, sameBeanAs(expected));
    }

    @Test
    public void testListNucleusInfoAsProvider() throws Exception {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Mockito expectations
        final User user = setUpMockitoExpectations(consumerId);
        final List<NucleusInfoDto> expected = setUpMockitoExpectationsForNucleusInfoService(user, nucleusUserId, null);

        // Execute the method being tested
        List<NucleusInfoDto> result = videoCallNucleusService.listNucleusInfo(AccountType.Type.PROVIDER);

        // Validation
        assertThat(result, sameBeanAs(expected));
    }

    @Test
    public void testListNucleusInfoForCareReceiverAsProvider() throws Exception {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);
        final String providerNucleusUserId = UUID.randomUUID().toString();

        final User provider = setUpMockitoExpectations(consumerId);
        final User consumer = createConsumer(consumerId);

        // Mockito expectations
        setUpMockitoExpectationsForCareTeam(consumer, provider, ctmId);
        final List<NucleusInfoDto> expected = setUpMockitoExpectationsForNucleusInfoService(consumer, null, nucleusUserId);
        setUpMockitoExpectationsForNucleusInfoService(provider, providerNucleusUserId, null);

        // Execute the method being tested
        final List<NucleusInfoDto> result = videoCallNucleusService.listNucleusInfoForCareReceiver(userId, ctmId);

        // Validation
        assertThat(result, sameBeanAs(expected));
    }

    @Test
    public void testListNucleusInfoForCareTeamMember() throws Exception {
        final Long consumerId = userId;
        final Long providerId = TestDataGenerator.randomIdExceptOf(userId);
        final String consumerNucleusUserId = UUID.randomUUID().toString();

        final User consumer = setUpMockitoExpectations(consumerId);
        final User provider = createProvider(providerId);

        setUpMockitoExpectationsForCareTeam(consumer, provider, ctmId);
        setUpMockitoExpectationsForNucleusInfoService(consumer, null, consumerNucleusUserId);
        final List<NucleusInfoDto> expected = setUpMockitoExpectationsForNucleusInfoService(provider, nucleusUserId, null);

        // Execute the method being tested
        List<NucleusInfoDto> result = videoCallNucleusService.listNucleusInfoForCareTeamMember(userId, ctmId, AccountType.Type.CONSUMER);

        // Validation
        assertThat(result, sameBeanAs(expected));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(userId, AccessRight.Code.MY_CT_VISIBILITY);
    }

    @Test
    public void testListNucleusInfoForCareTeamMemberAsProvider() throws Exception {
        final Long providerId = userId;
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);
        final Long providerId2 = TestDataGenerator.randomIdExceptOf(providerId, consumerId);
        final Long ctmId2 = TestDataGenerator.randomIdExceptOf(ctmId);
        final String providerNucleusUserId = nucleusUserId;
        final String providerNucleusUserId2 = UUID.randomUUID().toString();

        final User provider = setUpMockitoExpectations(consumerId);
        final User consumer = createConsumer(consumerId);
        final User provider2 = createProvider(providerId2);

        setUpMockitoExpectationsForCareTeam(consumer, provider, ctmId);
        setUpMockitoExpectationsForCareTeam(consumer, provider2, ctmId2);
        setUpMockitoExpectationsForNucleusInfoService(provider, providerNucleusUserId, null);
        final List<NucleusInfoDto> expected = setUpMockitoExpectationsForNucleusInfoService(provider2, providerNucleusUserId2, null);

        // Execute the method being tested
        List<NucleusInfoDto> result = videoCallNucleusService.listNucleusInfoForCareTeamMember(consumerId, ctmId2, AccountType.Type.PROVIDER);

        // Validation
        assertThat(result, sameBeanAs(expected));
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(consumerId, AccessRight.Code.MY_CT_VISIBILITY);
    }

    @Ignore("TODO")
    @Test
    public void testGetCalleeInfoByNucleusId() throws Exception {
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme