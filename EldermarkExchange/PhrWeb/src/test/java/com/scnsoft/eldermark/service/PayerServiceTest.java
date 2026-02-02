package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.PolicyActivityDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.PayerInfoDto;
import com.scnsoft.eldermark.web.entity.PeriodDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 6/29/2017.
 */
public class PayerServiceTest extends BaseServiceTest {

    @Mock
    private PolicyActivityDao policyActivityDao;
    @Mock
    private CareTeamSecurityUtils careTeamSecurityUtils;
    @Mock
    private UserDao userDao;
    @Mock
    private UserResidentRecordsDao userResidentRecordsDao;

    @InjectMocks
    private PayerService payerService;

    // Shared test data
    private final Long payerId = TestDataGenerator.randomId();
    private final Date timeHigh = TestDataGenerator.randomDate();
    private final Date timeLow = TestDataGenerator.randomDateBefore(timeHigh);

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\nResident ID: %d\nMedication ID: %d\n\n",
                userId, residentId, payerId);
    }

    private void verifySecurity(Long consumerId) {
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(consumerId, AccessRight.Code.MY_PHR);
    }

    private User setUpMockitoExpectations(Long consumerUserId) {
        final User consumer = super.createConsumer(consumerUserId);

        when(userResidentRecordsDao.getActiveResidentIdsByUserId(consumerUserId)).thenReturn(activeResidentIds);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerUserId)).thenReturn(allResidentIds);
        if (userId.equals(consumerUserId)) {
            when(userDao.findOne(userId)).thenReturn(consumer);
        }

        final Organization organization = new Organization();
        organization.setName("org name");
        final Participant participant = new Participant();
        participant.setTimeHigh(timeHigh);
        participant.setTimeLow(timeLow);
        final PolicyActivity policyActivity = new PolicyActivity();
        policyActivity.setPayerOrganization(organization);
        policyActivity.setParticipant(participant);
        policyActivity.setParticipantMemberId("member id");
        final Payer payer = new Payer();
        payer.setId(payerId);
        payer.setResident(consumer.getResident());
        payer.setResidentId(consumer.getResidentId());
        payer.setDatabase(consumer.getResident().getDatabase());
        payer.setPolicyActivities(Collections.singletonList(policyActivity));
        policyActivity.setPayer(payer);
        policyActivity.setDatabase(consumer.getResident().getDatabase());

        final Database database = new Database();
        database.setId(TestDataGenerator.randomId());
        database.setName(TestDataGenerator.randomName());
        resident2.setDatabase(database);

        final Participant participant2 = new Participant();
        participant2.setTimeHigh(null);
        participant2.setTimeLow(timeLow);
        final PolicyActivity policyActivity2 = new PolicyActivity();
        policyActivity2.setPayerOrganization(null);
        policyActivity2.setParticipant(participant2);
        final Payer payer2 = new Payer();
        payer2.setId(payerId + 1);
        payer2.setResident(resident2);
        payer2.setResidentId(residentId2);
        payer2.setDatabase(database);
        payer2.setPolicyActivities(Collections.singletonList(policyActivity2));
        policyActivity2.setPayer(payer2);
        policyActivity2.setDatabase(database);

        when(policyActivityDao.listResidentPolicyActivitiesWithoutDuplicates(eq(activeResidentIds), any(Sort.class))).thenReturn(Arrays.asList(policyActivity));
        when(policyActivityDao.listResidentPolicyActivitiesWithoutDuplicates(eq(allResidentIds), any(Sort.class))).thenReturn(Arrays.asList(policyActivity, policyActivity2));

        return consumer;
    }

    @Test
    public void testGetUserPayers() {
        final User user = setUpMockitoExpectations(userId);

        // Expected objects
        final PeriodDto periodDto = new PeriodDto();
        periodDto.setStartDate(timeLow.getTime());
        periodDto.setEndDate(timeHigh.getTime());
        final PayerInfoDto expectedPayerInfo = new PayerInfoDto();
        expectedPayerInfo.setMemberId("member id");
        expectedPayerInfo.setCompanyName("org name");
        expectedPayerInfo.setCoveragePeriod(periodDto);
        expectedPayerInfo.setDataSource(DataSourceService.transform(user.getResident().getDatabase(), user.getResidentId()));

        // Execute the method being tested
        List<PayerInfoDto> result = payerService.getUserPayers(userId);

        // Validation
        assertThat(result, hasSize(1));
        assertThat(result.get(0), sameBeanAs(expectedPayerInfo)
                .ignoring("coveragePeriod.startDateStr").ignoring("coveragePeriod.endDateStr"));
        verifySecurity(userId);
    }

    @Test
    public void testGetUserPayersAsProvider() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);
        final User user = setUpMockitoExpectations(consumerId);

        // Expected objects
        final PeriodDto periodDto = new PeriodDto();
        periodDto.setStartDate(timeLow.getTime());
        periodDto.setEndDate(timeHigh.getTime());
        final PayerInfoDto expectedPayerInfo = new PayerInfoDto();
        expectedPayerInfo.setMemberId("member id");
        expectedPayerInfo.setCompanyName("org name");
        expectedPayerInfo.setCoveragePeriod(periodDto);
        expectedPayerInfo.setDataSource(DataSourceService.transform(user.getResident().getDatabase(), user.getResidentId()));

        // Execute the method being tested
        List<PayerInfoDto> result = payerService.getUserPayers(consumerId);

        // Validation
        assertThat(result, hasSize(2));
        verifySecurity(consumerId);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme