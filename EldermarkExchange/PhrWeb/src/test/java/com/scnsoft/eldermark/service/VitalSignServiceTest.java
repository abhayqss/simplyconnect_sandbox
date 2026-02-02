package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.VitalSignDao;
import com.scnsoft.eldermark.dao.phr.VitalSignReferenceInfoDao;
import com.scnsoft.eldermark.dao.healthdata.VitalSignObservationDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.dao.projections.VitalSignTypeAndDate;
import com.scnsoft.eldermark.entity.VitalSignObservation;
import com.scnsoft.eldermark.entity.phr.VitalSignReferenceInfo;
import com.scnsoft.eldermark.entity.phr.VitalSignType;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.shared.web.entity.ReportPeriod;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.*;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.collection.IsIn.isIn;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 6/29/2017.
 */
public class VitalSignServiceTest extends BaseServiceTest {

    @Mock
    private VitalSignDao vitalSignDao;
    @Mock
    private VitalSignObservationDao vitalSignObservationDao;
    @Mock
    private VitalSignReferenceInfoDao vitalSignReferenceInfoDao;
    @Mock
    private CareTeamSecurityUtils careTeamSecurityUtils;
    @Mock
    private UserDao userDao;
    @Mock
    private UserResidentRecordsDao userResidentRecordsDao;

    @InjectMocks
    private VitalSignService vitalSignService;

    // Shared test data
    private final Date timeHigh = TestDataGenerator.randomDate();
    private final Date timeLow = TestDataGenerator.randomDateBefore(timeHigh);

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\nResident ID: %d\n\nThe below WARNING message is expected.\n",
                userId, residentId);
    }

    @Before
    public void initCache() {
        // Expected objects
        final VitalSignReferenceInfo info1 = new VitalSignReferenceInfo();
        info1.setId(1L);
        info1.setCode(VitalSignType.RESP.code());
        info1.setReferenceInfo(VitalSignType.RESP.displayName());   // reference info IS NOT the same as code display name, though for testing it's pretty reasonable
        final VitalSignReferenceInfo info2 = new VitalSignReferenceInfo();
        info2.setId(2L);
        info2.setCode(VitalSignType.HEART_BEAT.code());
        info2.setReferenceInfo(VitalSignType.HEART_BEAT.displayName());
        final VitalSignReferenceInfo info3 = new VitalSignReferenceInfo();
        info3.setId(3L);
        info3.setCode("new-code");
        info3.setReferenceInfo("new-reference-info");

        // Mockito expectations
        when(vitalSignReferenceInfoDao.findAll()).thenReturn(Arrays.asList(info1, info2, info3));

        // Execute post construct method
        vitalSignService.initCache();
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

        return consumer;
    }

    @Test
    public void testGetVitalSignDetails() {
        setUpMockitoExpectations(userId);

        // Expected objects
        final VitalSignType temp = VitalSignType.TEMP;
        final ReportPeriod week = ReportPeriod.WEEK;

        final VitalSignObservation vso = new VitalSignObservation();
        vso.setId(TestDataGenerator.randomId());
        vso.setValue(36.6);
        vso.setEffectiveTime(timeLow);
        vso.setUnit("degree");

        final VitalSignObservationDto expectedVSO = new VitalSignObservationDto();
        expectedVSO.setValue(36.6);
        expectedVSO.setDateTime(timeLow.getTime());
        final VitalSignObservationReport expectedReport = new VitalSignObservationReport(temp.name(), temp.displayName());
        expectedReport.setUnit("degree");
        expectedReport.setResults(Arrays.asList(expectedVSO));

        // Mockito expectations
        when(vitalSignDao.listResidentVitalSigns(eq(activeResidentIds), eq(temp.code()), any(Pair.class), eq(100)))
                .thenReturn(Arrays.asList(vso));
        when(vitalSignObservationDao.listResidentVitalSignObservationsWithoutDuplicates(
                eq(activeResidentIds), eq(temp.code()), any(Date.class), any(Date.class), any(Pageable.class)))
                .thenReturn(Arrays.asList(vso));

        // Execute the method being tested
        VitalSignObservationReport result = vitalSignService.getVitalSignDetails(userId, temp, week, 100, 0);

        // Validation
        assertThat(result, sameBeanAs(expectedReport)
                .ignoring("results.dateTimeStr").ignoring(DataSourceDto.class));
        verifySecurity(userId);
    }

    @Test
    public void testGetVitalSignDetailsNull() {
        setUpMockitoExpectations(userId);

        // Expected objects
        final VitalSignType temp = VitalSignType.TEMP;
        final ReportPeriod week = ReportPeriod.WEEK;

        final VitalSignObservationReport expectedReport = new VitalSignObservationReport(temp.name(), temp.displayName());
        expectedReport.setUnit(null);
        expectedReport.setResults(Collections.<VitalSignObservationDto>emptyList());

        // Mockito expectations
        when(vitalSignDao.listResidentVitalSigns(eq(activeResidentIds), eq(temp.code()), any(Pair.class), eq(100)))
                .thenReturn(Collections.<VitalSignObservation>emptyList());
        when(vitalSignObservationDao.listResidentVitalSignObservationsWithoutDuplicates(
                eq(activeResidentIds), eq(temp.code()), any(Date.class), any(Date.class), any(Pageable.class)))
                .thenReturn(Collections.<VitalSignObservation>emptyList());

        // Execute the method being tested
        VitalSignObservationReport result = vitalSignService.getVitalSignDetails(userId, temp, week, 100, 0);

        // Validation
        assertThat(result, sameBeanAs(expectedReport));
        verifySecurity(userId);
    }

    @Test
    public void testGetVitalSignDetailsAsProvider() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);
        setUpMockitoExpectations(consumerId);

        // Expected objects
        final VitalSignType temp = VitalSignType.TEMP;
        final ReportPeriod week = ReportPeriod.WEEK;

        final VitalSignObservation vso = new VitalSignObservation();
        vso.setId(TestDataGenerator.randomId());
        vso.setValue(36.6);
        vso.setEffectiveTime(timeLow);
        vso.setUnit("degree");

        final VitalSignObservationDto expectedVSO = new VitalSignObservationDto();
        expectedVSO.setValue(36.6);
        expectedVSO.setDateTime(timeLow.getTime());
        final VitalSignObservationReport expectedReport = new VitalSignObservationReport(temp.name(), temp.displayName());
        expectedReport.setUnit("degree");
        expectedReport.setResults(Arrays.asList(expectedVSO));

        // Mockito expectations
        when(vitalSignDao.listResidentVitalSigns(eq(allResidentIds), eq(temp.code()), any(Pair.class), eq(100)))
                .thenReturn(Arrays.asList(vso));
        when(vitalSignObservationDao.listResidentVitalSignObservationsWithoutDuplicates(
                eq(allResidentIds), eq(temp.code()), any(Date.class), any(Date.class), any(Pageable.class)))
                .thenReturn(Arrays.asList(vso));

        // Execute the method being tested
        VitalSignObservationReport result = vitalSignService.getVitalSignDetails(consumerId, temp, week, 100, 0);

        // Validation
        assertThat(result, sameBeanAs(expectedReport)
                .ignoring("results.dateTimeStr").ignoring(DataSourceDto.class));
        verifySecurity(consumerId);
    }

    @Test
    public void testGetVitalSignLatestResults() {
        setUpMockitoExpectations(userId);

        // Expected objects
        final VitalSignObservation vso = new VitalSignObservation();
        vso.setId(TestDataGenerator.randomId());
        vso.setValue(36.6);
        vso.setEffectiveTime(timeLow);
        vso.setUnit("degree");
        final VitalSignObservation vso2 = new VitalSignObservation();
        vso2.setId(TestDataGenerator.randomId());
        vso2.setValue(80.0);
        vso2.setEffectiveTime(timeHigh);
        vso2.setUnit("bpm");

        final Map<String, VitalSignObservation> expectedMap = new HashMap<String, VitalSignObservation>() {{
            put(VitalSignType.TEMP.code(), vso);
            put(VitalSignType.HEART_BEAT.code(), vso2);
        }};
        final Map<String, DateDto> expectedResults = getExpectedHashMap();

        // Mockito expectations
        when(vitalSignDao.listResidentVitalSigns(activeResidentIds, VitalSignType.TEMP.code(), null, 1))
                .thenReturn(Arrays.asList(vso));
        when(vitalSignDao.listResidentVitalSigns(activeResidentIds, VitalSignType.HEART_BEAT.code(), null, 1))
                .thenReturn(Arrays.asList(vso2));
        when(vitalSignDao.listLatestResidentVitalSigns(activeResidentIds)).thenReturn(expectedMap);
        when(vitalSignObservationDao.listLatestResidentVitalSignObservations(activeResidentIds)).thenReturn(Arrays.asList(vso, vso2));

        // Execute the method being tested
        Map<String, VitalSignObservationDto> result = vitalSignService.getVitalSignLatestResults(userId);

        // Validation
        assertThat(expectedResults.keySet(), everyItem(isIn(result.keySet())));
        assertThat(result.keySet(), everyItem(isIn(expectedResults.keySet())));
        assertEquals((Long) timeLow.getTime(), result.get(VitalSignType.TEMP.name()).getDateTime());
        assertEquals((Long) timeHigh.getTime(), result.get(VitalSignType.HEART_BEAT.name()).getDateTime());
        verifySecurity(userId);
    }

    @Test
    public void testGetVitalSignLatestResultsAsProvider() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);
        setUpMockitoExpectations(consumerId);

        // Expected objects
        final VitalSignObservation vso = new VitalSignObservation();
        vso.setId(TestDataGenerator.randomId());
        vso.setValue(36.6);
        vso.setEffectiveTime(timeLow);
        vso.setUnit("degree");
        final VitalSignObservation vso2 = new VitalSignObservation();
        vso2.setId(TestDataGenerator.randomId());
        vso2.setValue(80.0);
        vso2.setEffectiveTime(timeHigh);
        vso2.setUnit("bpm");

        final Map<String, VitalSignObservation> expectedMap = new HashMap<String, VitalSignObservation>() {{
            put(VitalSignType.TEMP.code(), vso);
            put(VitalSignType.HEART_BEAT.code(), vso2);
        }};
        final Map<String, DateDto> expectedResults = getExpectedHashMap();

        // Mockito expectations
        when(vitalSignDao.listResidentVitalSigns(allResidentIds, VitalSignType.TEMP.code(), null, 1))
                .thenReturn(Arrays.asList(vso));
        when(vitalSignDao.listResidentVitalSigns(allResidentIds, VitalSignType.HEART_BEAT.code(), null, 1))
                .thenReturn(Arrays.asList(vso2));
        when(vitalSignDao.listLatestResidentVitalSigns(allResidentIds)).thenReturn(expectedMap);
        when(vitalSignObservationDao.listLatestResidentVitalSignObservations(allResidentIds)).thenReturn(Arrays.asList(vso, vso2));

        // Execute the method being tested
        Map<String, VitalSignObservationDto> result = vitalSignService.getVitalSignLatestResults(consumerId);

        // Validation
        assertThat(expectedResults.keySet(), everyItem(isIn(result.keySet())));
        assertThat(result.keySet(), everyItem(isIn(expectedResults.keySet())));
        assertEquals((Long) timeLow.getTime(), result.get(VitalSignType.TEMP.name()).getDateTime());
        assertEquals((Long) timeHigh.getTime(), result.get(VitalSignType.HEART_BEAT.name()).getDateTime());
        verifySecurity(consumerId);
    }

    @Test
    public void testGetVitalSignEarliestMeasurementDates() {
        setUpMockitoExpectations(userId);

        // Expected objects
        final VitalSignTypeAndDate mockResult1 = Mockito.mock(VitalSignTypeAndDate.class);
        final VitalSignTypeAndDate mockResult2 = Mockito.mock(VitalSignTypeAndDate.class);
        final Map<String, Date> earliestResults = new HashMap<String, Date>() {{
            put(VitalSignType.RESP.code(), timeLow);
            put(VitalSignType.HEART_BEAT.code(), timeHigh);
        }};

        final Map<String, DateDto> expectedResults = getExpectedHashMap();

        // Mockito expectations
        when(vitalSignDao.listEarliestResidentVitalSigns(activeResidentIds)).thenReturn(earliestResults);
        when(vitalSignObservationDao.listEarliestResidentVitalSignObservationDates(activeResidentIds)).thenReturn(Arrays.asList(mockResult1, mockResult2));
        when(mockResult1.getType()).thenReturn(VitalSignType.RESP.code());
        when(mockResult1.getDate()).thenReturn(timeLow);
        when(mockResult2.getType()).thenReturn(VitalSignType.HEART_BEAT.code());
        when(mockResult2.getDate()).thenReturn(timeHigh);

        // Execute the method being tested
        Map<String, DateDto> result = vitalSignService.getVitalSignEarliestMeasurementDates(userId);

        // Validation
        assertThat(expectedResults.keySet(), everyItem(isIn(result.keySet())));
        assertThat(result.keySet(), everyItem(isIn(expectedResults.keySet())));
        assertEquals((Long) timeLow.getTime(), result.get(VitalSignType.RESP.name()).getDateTime());
        assertEquals((Long) timeHigh.getTime(), result.get(VitalSignType.HEART_BEAT.name()).getDateTime());
        verifySecurity(userId);
    }

    @Test
    public void testGetVitalSignEarliestMeasurementDatesAsProvider() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);
        setUpMockitoExpectations(consumerId);

        // Expected objects
        final VitalSignTypeAndDate mockResult1 = Mockito.mock(VitalSignTypeAndDate.class);
        final VitalSignTypeAndDate mockResult2 = Mockito.mock(VitalSignTypeAndDate.class);
        final Map<String, Date> earliestResults = new HashMap<String, Date>() {{
            put(VitalSignType.RESP.code(), timeLow);
            put(VitalSignType.HEART_BEAT.code(), timeHigh);
        }};

        final Map<String, DateDto> expectedResults = getExpectedHashMap();

        // Mockito expectations
        when(vitalSignDao.listEarliestResidentVitalSigns(allResidentIds)).thenReturn(earliestResults);
        when(vitalSignObservationDao.listEarliestResidentVitalSignObservationDates(allResidentIds)).thenReturn(Arrays.asList(mockResult1, mockResult2));
        when(mockResult1.getType()).thenReturn(VitalSignType.RESP.code());
        when(mockResult1.getDate()).thenReturn(timeLow);
        when(mockResult2.getType()).thenReturn(VitalSignType.HEART_BEAT.code());
        when(mockResult2.getDate()).thenReturn(timeHigh);

        // Execute the method being tested
        Map<String, DateDto> result = vitalSignService.getVitalSignEarliestMeasurementDates(consumerId);

        // Validation
        assertThat(expectedResults.keySet(), everyItem(isIn(result.keySet())));
        assertThat(result.keySet(), everyItem(isIn(expectedResults.keySet())));
        assertEquals((Long) timeLow.getTime(), result.get(VitalSignType.RESP.name()).getDateTime());
        assertEquals((Long) timeHigh.getTime(), result.get(VitalSignType.HEART_BEAT.name()).getDateTime());
        verifySecurity(consumerId);
    }

    @Test
    public void testGetVitalSignReferenceInfo() {
        // Execute the method being tested
        String result = vitalSignService.getVitalSignReferenceInfo(VitalSignType.RESP);

        // Validation
        assertEquals(VitalSignType.RESP.displayName(), result);
    }

    // ===== Utility methods =====

    private HashMap<String, DateDto> getExpectedHashMap() {
        return new HashMap<String, DateDto>() {{
            put(VitalSignType.RESP.name(), null);
            put(VitalSignType.HEART_BEAT.name(), null);
            put(VitalSignType.O2_SAT.name(), null);
            put(VitalSignType.CIRCUMFERENCE.name(), null);
            put(VitalSignType.HEIGHT.name(), null);
            put(VitalSignType.HEIGHT_LYING.name(), null);
            put(VitalSignType.INTR_DIASTOLIC.name(), null);
            put(VitalSignType.INTR_SYSTOLIC.name(), null);
            put(VitalSignType.TEMP.name(), null);
            put(VitalSignType.WEIGHT.name(), null);
        }};
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme