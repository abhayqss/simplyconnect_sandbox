package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.ImmunizationDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.DataSourceDto;
import com.scnsoft.eldermark.web.entity.ImmunizationInfoDto;
import com.scnsoft.eldermark.web.entity.PeriodDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 6/28/2017.
 */
public class ImmunizationServiceTest extends BaseServiceTest {

    @Mock
    private ImmunizationDao immunizationDao;
    @Mock
    private CareTeamSecurityUtils careTeamSecurityUtils;
    @Mock
    private UserDao userDao;
    @Mock
    private UserResidentRecordsDao userResidentRecordsDao;

    @InjectMocks
    private ImmunizationService immunizationService;

    // Shared test data
    private final Long immunizationId = TestDataGenerator.randomId();

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\nResident ID: %d\nImmunization ID: %d\n\n",
                userId, residentId, immunizationId);
    }

    private void verifySecurity(Long consumerId) {
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(consumerId, AccessRight.Code.MY_PHR);
    }

    private User setUpMockitoExpectations(Long consumerUserId) {
        final User currentUser = super.createConsumer(consumerUserId);

        when(userResidentRecordsDao.getActiveResidentIdsByUserId(consumerUserId)).thenReturn(activeResidentIds);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerUserId)).thenReturn(allResidentIds);
        if (userId.equals(consumerUserId)) {
            when(userDao.findOne(userId)).thenReturn(currentUser);
        }

        final Date timeHigh = TestDataGenerator.randomDate();
        final Date timeLow = TestDataGenerator.randomDateBefore(timeHigh);

        final Immunization immunization = new Immunization();
        immunization.setId(immunizationId);
        immunization.setResident(resident);
        immunization.setDatabase(currentUser.getResident().getDatabase());
        immunization.setIndications(Collections.<Indication>emptyList());
        immunization.setImmunizationStarted(timeLow);
        immunization.setImmunizationStopped(timeHigh);

        when(immunizationDao.listResidentImmunizationsWithoutDuplicates(eq(activeResidentIds), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(immunization)));
        when(immunizationDao.listResidentImmunizationsWithoutDuplicates(eq(allResidentIds), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(immunization)));
        when(immunizationDao.findOne(immunizationId)).thenReturn(immunization);
        when(immunizationDao.findOne(immunizationId - 1)).thenReturn(null);

        return currentUser;
    }

    @Test
    public void testGetUserImmunizations() {
        setUpMockitoExpectations(userId);

        // Execute the method being tested
        Page<ImmunizationInfoDto> page = immunizationService.getUserImmunizations(userId, null);
        final List<ImmunizationInfoDto> result = page.getContent();

        // Validation
        assertThat(result, hasSize(1));
        //verify(immunizationDao).listByResidentId(residentId);
        verify(immunizationDao).listResidentImmunizationsWithoutDuplicates(eq(activeResidentIds), any(Pageable.class));
        verifyNoMoreInteractions(immunizationDao);
        verifySecurity(userId);
    }

    @Test
    public void testGetUserImmunizationsAsProvider() {
        final Long consumerUserId = TestDataGenerator.randomIdExceptOf(userId);
        setUpMockitoExpectations(consumerUserId);

        // Execute the method being tested
        Page<ImmunizationInfoDto> page = immunizationService.getUserImmunizations(consumerUserId, null);
        final List<ImmunizationInfoDto> result = page.getContent();

        // Validation
        assertThat(result, hasSize(1));
        //verify(immunizationDao).listByResidentId(residentId);
        //verify(immunizationDao).listByResidentId(residentId2);
        //verify(immunizationDao).listByResidentId(residentId3);
        verify(immunizationDao).listResidentImmunizationsWithoutDuplicates(eq(allResidentIds), any(Pageable.class));
        verifySecurity(consumerUserId);
    }

    @Test
    public void testGetUserImmunization() {
        setUpMockitoExpectations(userId);

        // Execute the method being tested
        immunizationService.getUserImmunization(userId, immunizationId);

        // Validation
        verify(immunizationDao).findOne(immunizationId);
        verifyNoMoreInteractions(immunizationDao);
        verifySecurity(userId);
    }

    @Test(expected = PhrException.class)
    public void testGetUserImmunizationThrowsImmunizationNotFound() {
        setUpMockitoExpectations(userId);

        // Execute the method being tested
        final ImmunizationInfoDto userImmunization = immunizationService.getUserImmunization(userId, immunizationId - 1);

        // Validation
        assertNull(userImmunization);
        verifySecurity(userId);
    }

    @Test(expected = PhrException.class)
    public void testGetUserImmunizationThrowsAccessForbidden() {
        // Expected objects
        final Immunization immunization2 = new Immunization();
        immunization2.setId(immunizationId + 1);
        immunization2.setResident(resident2);

        // Mockito expectations
        setUpMockitoExpectations(userId);
        when(immunizationDao.findOne(immunizationId + 1)).thenReturn(immunization2);

        // Execute the method being tested
        immunizationService.getUserImmunization(userId, immunizationId + 1);

        // Validation
        verifySecurity(userId);
    }

    @Test
    public void testTransform() {
        // Expected objects
        final User user = setUpMockitoExpectations(userId);

        final Database database1 = user.getResident().getDatabase();
        final Date timeHigh = TestDataGenerator.randomDate();
        final Date timeLow = TestDataGenerator.randomDateBefore(timeHigh);

        final CcdCode codeInd1 = new CcdCode();
        codeInd1.setDisplayName("test 1");
        final CcdCode valueInd1 = new CcdCode();
        valueInd1.setDisplayName("test 2");
        final Indication indication1 = new Indication();
        indication1.setCode(codeInd1);
        indication1.setValue(valueInd1);

        final ImmunizationMedicationInformation imi = new ImmunizationMedicationInformation();
        imi.setText("text imi");
        final ReactionObservation reactionObservation = new ReactionObservation();
        reactionObservation.setReactionText("reaction text");
        final CcdCode refusalCode = new CcdCode();
        refusalCode.setDisplayName("refusal code");
        final ImmunizationRefusalReason irr = new ImmunizationRefusalReason();
        irr.setCode(refusalCode);
        final CcdCode route = new CcdCode();
        route.setDisplayName("injection");
        final CcdCode site = new CcdCode();
        site.setDisplayName("knee");
        final Instructions instructions = new Instructions();
        instructions.setText("instructions text");

        final Immunization immunization = new Immunization();
        immunization.setId(immunizationId);
        immunization.setResident(resident);
        immunization.setDatabase(database1);
        immunization.setIndications(Collections.<Indication>emptyList());
        immunization.setImmunizationStarted(timeLow);
        immunization.setImmunizationStopped(timeHigh);
        immunization.setIndications(Arrays.asList(indication1));
        immunization.setImmunizationMedicationInformation(imi);
        immunization.setText("administrator");
        immunization.setDoseQuantity(2);
        immunization.setDoseUnits("mg");
        immunization.setRepeatNumber(10);
        immunization.setReactionObservation(reactionObservation);
        immunization.setRefusal(Boolean.TRUE);
        immunization.setImmunizationRefusalReason(irr);
        immunization.setStatusCode("active");
        immunization.setRoute(route);
        immunization.setSite(site);
        immunization.setInstructions(instructions);

        final Map<String, String> expectedIndications = new HashMap<>();
        expectedIndications.put("test 1", "test 2");
        final PeriodDto expectedPeriod = new PeriodDto();
        expectedPeriod.setStartDate(timeLow.getTime());
        expectedPeriod.setEndDate(timeHigh.getTime());
        final ImmunizationInfoDto expectedImmunization = new ImmunizationInfoDto();
        expectedImmunization.setId(immunizationId);
        //expectedImmunization.setDataSource(DataSourceService.transform(database1, residentId));
        expectedImmunization.setPeriod(expectedPeriod);
        expectedImmunization.setIndications(expectedIndications);
        expectedImmunization.setImmunizationName("text imi");
        expectedImmunization.setAdministeredBy("administrator");
        expectedImmunization.setDoseQuantity(2.0);
        expectedImmunization.setDoseUnit("mg");
        expectedImmunization.setRepeat(10);
        expectedImmunization.setReaction("reaction text");
        expectedImmunization.setRefusal(Boolean.TRUE);
        expectedImmunization.setRefusalReason("refusal code");
        expectedImmunization.setStatus("Active");
        expectedImmunization.setRoute("injection");
        expectedImmunization.setSite("knee");
        expectedImmunization.setInstructions("instructions text");
        DataSourceDto dataSourceDto = new DataSourceDto();
        dataSourceDto.setId(resident.getDatabaseId());
        dataSourceDto.setName(resident.getDatabase().getName());
        dataSourceDto.setResidentId(resident.getId());
        expectedImmunization.setDataSource(dataSourceDto);

        // Execute the method being tested
        final ImmunizationInfoDto result = ImmunizationService.transform(immunization);

        // Validation
        assertThat(result, sameBeanAs(expectedImmunization)
                .ignoring("period.startDateStr").ignoring("period.endDateStr"));
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme