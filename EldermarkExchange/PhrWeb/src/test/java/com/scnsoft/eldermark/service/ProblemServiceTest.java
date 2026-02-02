package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.ProblemDao;
import com.scnsoft.eldermark.dao.healthdata.ProblemObservationDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Problem;
import com.scnsoft.eldermark.entity.ProblemObservation;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.service.transformer.ProblemInfoConverter;
import com.scnsoft.eldermark.service.transformer.populator.Populator;
import com.scnsoft.eldermark.service.transformer.populator.ProblemInfoPopulator;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.DataSourceDto;
import com.scnsoft.eldermark.web.entity.PeriodDto;
import com.scnsoft.eldermark.web.entity.ProblemInfoDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static com.scnsoft.eldermark.entity.CodeSystem.LOINC;
import static com.scnsoft.eldermark.entity.CodeSystem.SNOMED_CT;
import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 6/29/2017.
 */
public class ProblemServiceTest extends BaseServiceTest {

    @Mock
    private ProblemDao problemDao;
    @Mock
    private ProblemObservationDao problemObservationDao;
    @Mock
    private CareTeamSecurityUtils careTeamSecurityUtils;
    @Mock
    private UserDao userDao;
    @Mock
    private UserResidentRecordsDao userResidentRecordsDao;

    @InjectMocks
    private ProblemService problemService;

    private ProblemInfoConverter problemInfoDtoConverter;

    // Shared test data
    private final Long problemId = TestDataGenerator.randomId();
    private final Long problemObservationId = TestDataGenerator.randomId();
    private final Date timeHigh = TestDataGenerator.randomDate();
    private final Date timeLow = TestDataGenerator.randomDateBefore(timeHigh);

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\nResident ID: %d\nProblem ID: %d\nProblem Observation ID: %d\n\n",
                userId, residentId, problemId, problemObservationId);
    }

    @Before
    public void injectConverter() {
        Populator<ProblemObservation, ProblemInfoDto> problemInfoItemPopulator = new ProblemInfoPopulator();
        problemInfoDtoConverter = new ProblemInfoConverter();
        problemInfoDtoConverter.setProblemInfoItemPopulator(problemInfoItemPopulator);
        problemService.setProblemInfoDtoConverter(problemInfoDtoConverter);
    }

    private void verifySecurity(Long consumerId) {
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(consumerId, AccessRight.Code.MY_PHR);
    }

    private User setUpMockitoExpectations(Long consumerUserId) {
        return setUpMockitoExpectations(consumerUserId, null);
    }

    private User setUpMockitoExpectations(Long consumerUserId, Pageable pageable) {
        final User consumer = super.createConsumer(consumerUserId);

        when(userResidentRecordsDao.getActiveResidentIdsByUserId(consumerUserId)).thenReturn(activeResidentIds);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerUserId)).thenReturn(allResidentIds);
        if (userId.equals(consumerUserId)) {
            when(userDao.findOne(userId)).thenReturn(consumer);
        }

        final ProblemObservation problemObservation = new ProblemObservation();
        problemObservation.setId(problemObservationId);
        problemObservation.setProblemDateTimeLow(timeLow);
        problemObservation.setProblemDateTimeHigh(timeHigh);
        problemObservation.setDatabase(consumer.getResident().getDatabase());
        problemObservation.setTranslations(Collections.<CcdCode>emptySet());
        final Problem problem = new Problem();
        problem.setId(problemId);
        problem.setResident(consumer.getResident());
        problem.setResidentId(consumer.getResidentId());
        problem.setDatabase(consumer.getResident().getDatabase());
        problem.setProblemObservations(Collections.singletonList(problemObservation));
        problem.setStatusCode("completed");
        problem.setTimeLow(timeLow);
        problem.setTimeHigh(timeHigh);
        problemObservation.setProblem(problem);

        final Database database = new Database();
        database.setId(TestDataGenerator.randomId());
        database.setName(TestDataGenerator.randomName());
        resident2.setDatabase(database);

        final ProblemObservation problemObservation2 = new ProblemObservation();
        problemObservation2.setId(problemObservationId + 1);
        problemObservation2.setProblemDateTimeLow(timeLow);
        problemObservation2.setProblemDateTimeHigh(null);
        problemObservation2.setDatabase(database);
        problemObservation2.setTranslations(Collections.<CcdCode>emptySet());
        final Problem problem2 = new Problem();
        problem2.setId(problemId + 1);
        problem2.setResident(resident2);
        problem2.setResidentId(residentId2);
        problem2.setDatabase(database);
        problem2.setProblemObservations(Collections.singletonList(problemObservation2));
        problem2.setStatusCode("active");
        problem2.setTimeLow(timeLow);
        problem2.setTimeHigh(null);
        problemObservation2.setProblem(problem2);

        final ProblemObservation problemObservation3 = new ProblemObservation();
        problemObservation3.setId(problemObservationId + 2);
        problemObservation3.setProblemDateTimeLow(null);
        problemObservation3.setProblemDateTimeHigh(timeHigh);
        problemObservation3.setDatabase(consumer.getResident().getDatabase());
        problemObservation3.setTranslations(Collections.<CcdCode>emptySet());
        final Problem problem3 = new Problem();
        problem3.setId(problemId + 2);
        problem3.setResident(consumer.getResident());
        problem3.setResidentId(consumer.getResidentId());
        problem3.setDatabase(consumer.getResident().getDatabase());
        problem3.setProblemObservations(Collections.singletonList(problemObservation3));
        problem3.setStatusCode(null);
        problem3.setTimeLow(null);
        problem3.setTimeHigh(timeHigh);
        problemObservation3.setProblem(problem3);

        final ProblemObservation problemObservation4 = new ProblemObservation();
        problemObservation4.setId(problemObservationId + 3);
        problemObservation4.setProblemDateTimeLow(timeLow);
        problemObservation4.setProblemDateTimeHigh(null);
        problemObservation4.setDatabase(consumer.getResident().getDatabase());
        problemObservation4.setTranslations(Collections.<CcdCode>emptySet());
        final Problem problem4 = new Problem();
        problem4.setId(problemId + 3);
        problem4.setResident(consumer.getResident());
        problem4.setResidentId(consumer.getResidentId());
        problem4.setDatabase(consumer.getResident().getDatabase());
        problem4.setProblemObservations(Collections.singletonList(problemObservation4));
        problem4.setStatusCode("active");
        problem4.setTimeLow(timeLow);
        problem4.setTimeHigh(null);
        problemObservation4.setProblem(problem4);

        when(problemDao.listByResidentId(residentId)).thenReturn(Arrays.asList(problem, problem3, problem4));
        when(problemDao.listByResidentId(residentId2)).thenReturn(Arrays.asList(problem2));
        when(problemDao.listResidentProblems(residentId, true, false, false))
                .thenReturn(Arrays.asList(problem4));
        when(problemDao.listResidentProblems(residentId, false, true, false))
                .thenReturn(Arrays.asList(problem));
        when(problemDao.listResidentProblems(residentId, false, false, true))
                .thenReturn(Arrays.asList(problem3));
        when(problemDao.listResidentProblems(residentId2, true, false, false))
                .thenReturn(Arrays.asList(problem2));
        when(problemDao.listResidentProblems(activeResidentIds, true, false, false, pageable))
                .thenReturn(Arrays.asList(problem4));
        when(problemDao.listResidentProblems(activeResidentIds, false, true, false, pageable))
                .thenReturn(Arrays.asList(problem));
        when(problemDao.listResidentProblems(activeResidentIds, false, false, true, pageable))
                .thenReturn(Arrays.asList(problem3));
        when(problemDao.listResidentProblems(allResidentIds, true, false, false, pageable))
                .thenReturn(Arrays.asList(problem2, problem4));
        when(problemDao.listResidentProblems(allResidentIds, false, true, false, pageable))
                .thenReturn(Arrays.asList(problem));
        when(problemDao.listResidentProblems(allResidentIds, false, false, true, pageable))
                .thenReturn(Arrays.asList(problem3));
        when(problemDao.get(problemId)).thenReturn(problem);
        when(problemDao.get(problemId + 1)).thenReturn(problem2);
        when(problemDao.get(problemId + 2)).thenReturn(problem3);
        when(problemDao.get(problemId + 3)).thenReturn(problem4);
        when(problemDao.get(problemId - 1)).thenReturn(null);

        when(problemObservationDao.listResidentProblemsWithoutDuplicates(eq(activeResidentIds), eq(true), eq(false), eq(false), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(problemObservation4)));
        when(problemObservationDao.listResidentProblemsWithoutDuplicates(eq(activeResidentIds), eq(false), eq(true), eq(false), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(problemObservation)));
        when(problemObservationDao.listResidentProblemsWithoutDuplicates(eq(activeResidentIds), eq(false), eq(false), eq(true), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(problemObservation3)));
        when(problemObservationDao.listResidentProblemsWithoutDuplicates(eq(allResidentIds), eq(true), eq(false), eq(false), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(problemObservation2, problemObservation4)));
        when(problemObservationDao.listResidentProblemsWithoutDuplicates(eq(allResidentIds), eq(false), eq(true), eq(false), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(problemObservation)));
        when(problemObservationDao.listResidentProblemsWithoutDuplicates(eq(allResidentIds), eq(false), eq(false), eq(true), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(problemObservation3)));
        when(problemObservationDao.getOne(problemObservationId)).thenReturn(problemObservation);
        when(problemObservationDao.getOne(problemObservationId + 1)).thenReturn(problemObservation2);
        when(problemObservationDao.getOne(problemObservationId + 2)).thenReturn(problemObservation3);
        when(problemObservationDao.getOne(problemObservationId + 3)).thenReturn(problemObservation4);
        when(problemObservationDao.getOne(problemObservationId - 1)).thenReturn(null);

        return consumer;
    }

    @Test
    public void testGetUserProblemsActive() {
        final Pageable pageable = PaginationUtils.buildPageable(100, 0);
        setUpMockitoExpectations(userId, pageable);

        // Execute the method being tested
        Page<ProblemInfoDto> page = problemService.getUserProblemsActive(userId, pageable);
        final List<ProblemInfoDto> result = page.getContent();

        // Validation
        assertThat(result, hasSize(1));
        assertEquals((long) result.get(0).getId(), problemObservationId + 3);
        verifySecurity(userId);
    }

    @Test
    public void testGetUserProblemsActiveAsProvider() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);
        final Pageable pageable = PaginationUtils.buildPageable(100, 0);
        setUpMockitoExpectations(consumerId, pageable);

        // Execute the method being tested
        Page<ProblemInfoDto> page = problemService.getUserProblemsActive(consumerId, pageable);
        final List<ProblemInfoDto> result = page.getContent();

        // Validation
        assertThat(result, hasSize(2));
        verify(problemObservationDao).listResidentProblemsWithoutDuplicates(eq(allResidentIds), eq(true), eq(false), eq(false), any(Pageable.class));
        verifyNoMoreInteractions(problemDao);
        verifySecurity(consumerId);
    }

    @Test
    public void testGetUserProblemsResolved() {
        final Pageable pageable = PaginationUtils.buildPageable(100, 0);
        setUpMockitoExpectations(userId, pageable);

        // Execute the method being tested
        Page<ProblemInfoDto> page = problemService.getUserProblemsResolved(userId, pageable);
        final List<ProblemInfoDto> result = page.getContent();

        // Validation
        assertThat(result, hasSize(1));
        assertEquals(result.get(0).getId(), problemObservationId);
        verifySecurity(userId);
    }

    @Test
    public void testGetUserProblemsOther() {
        final Pageable pageable = PaginationUtils.buildPageable(100, 0);
        setUpMockitoExpectations(userId, pageable);

        // Execute the method being tested
        Page<ProblemInfoDto> page = problemService.getUserProblemsOther(userId, pageable);
        final List<ProblemInfoDto> result = page.getContent();

        // Validation
        assertThat(result, hasSize(1));
        assertEquals((long) result.get(0).getId(), problemObservationId + 2);
        verifySecurity(userId);
    }

    @Test
    public void testGetUserProblem() {
        setUpMockitoExpectations(userId);

        // Execute the method being tested
        ProblemInfoDto result = problemService.getUserProblem(userId, problemObservationId);

        // Validation
        assertNotNull(result);
        assertEquals(result.getId(), problemObservationId);
        verifySecurity(userId);
    }

    @Test(expected = PhrException.class)
    public void testGetUserProblemThrowsProblemNotFound() {
        setUpMockitoExpectations(userId);

        // Execute the method being tested
        final ProblemInfoDto userProblem = problemService.getUserProblem(userId, problemObservationId - 1);

        // Validation
        assertNull(userProblem);
        verifySecurity(userId);
    }

    @Test(expected = PhrException.class)
    public void testGetUserProblemThrowsAccessForbidden() {
        setUpMockitoExpectations(userId);

        // Execute the method being tested
        final ProblemInfoDto userProblem = problemService.getUserProblem(userId, problemObservationId + 1);

        // Validation
        assertNull(userProblem);
        verifySecurity(userId);
    }

    @Test
    public void testTransform() {
        // Expected objects
        final User consumer = setUpMockitoExpectations(userId);

        final CcdCode observationCode = new CcdCode();
        observationCode.setCode("code");
        observationCode.setCodeSystemName(SNOMED_CT.getDisplayName());
        final CcdCode translationCode = new CcdCode();
        translationCode.setCode("code 2");
        translationCode.setCodeSystemName(LOINC.getDisplayName());
        final CcdCode problemType = new CcdCode();
        problemType.setCode("code 3");
        problemType.setDisplayName("code 3 text");

        final ProblemObservation problemObservation = new ProblemObservation();
        problemObservation.setId(problemObservationId);
        problemObservation.setProblemDateTimeLow(timeLow);
        problemObservation.setProblemDateTimeHigh(timeHigh);
        problemObservation.setDatabase(consumer.getResident().getDatabase());
        problemObservation.setTranslations(Collections.singleton(translationCode));
        problemObservation.setProblemCode(observationCode);
        problemObservation.setHealthStatusObservationText("health status");
        problemObservation.setProblemName("problem name");
        problemObservation.setProblemType(problemType);
        problemObservation.setProblemStatusText("completed");
        problemObservation.setAgeObservationUnit("a");
        problemObservation.setAgeObservationValue(50);
        final Problem problem = new Problem();
        problem.setId(problemId);
        problem.setResident(consumer.getResident());
        problem.setDatabase(consumer.getResident().getDatabase());
        problem.setProblemObservations(Collections.singletonList(problemObservation));
        problem.setStatusCode("completed");
        problem.setTimeLow(timeLow);
        problem.setTimeHigh(timeHigh);
        problemObservation.setProblem(problem);

        final PeriodDto expectedPeriod = new PeriodDto();
        expectedPeriod.setStartDate(timeLow.getTime());
        expectedPeriod.setEndDate(timeHigh.getTime());
        final ProblemInfoDto expectedProblemInfoDto = new ProblemInfoDto();
        expectedProblemInfoDto.setId(problemObservationId);
        expectedProblemInfoDto.setStatus("Completed");
        expectedProblemInfoDto.setTranslations(new HashMap<String, String>(){{
            put("code 2", LOINC.getDisplayName());
        }});
        expectedProblemInfoDto.setPeriod(expectedPeriod);
        expectedProblemInfoDto.setAgeObservationUnit("a");
        expectedProblemInfoDto.setAgeObservationValue(50);
        expectedProblemInfoDto.setDiagnosisCode("code");
        expectedProblemInfoDto.setDiagnosisCodeSet(SNOMED_CT.getDisplayName());
        expectedProblemInfoDto.setHealthStatusObservation("health status");
        expectedProblemInfoDto.setProblemName("problem name");
        expectedProblemInfoDto.setProblemType("code 3 text");

        // Execute the method being tested
        ProblemInfoDto result = problemInfoDtoConverter.convert(problemObservation);

        // Validation
        assertThat(result, sameBeanAs(expectedProblemInfoDto)
                .ignoring("period.startDateStr").ignoring("period.endDateStr")
                .ignoring(DataSourceDto.class));
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme