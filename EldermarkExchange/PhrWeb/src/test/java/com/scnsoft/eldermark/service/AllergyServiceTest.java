package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.AllergyDao;
import com.scnsoft.eldermark.dao.healthdata.AllergyObservationDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.Allergy;
import com.scnsoft.eldermark.entity.AllergyObservation;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.ReactionObservation;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.AllergyInfoDto;
import com.scnsoft.eldermark.web.entity.DataSourceDto;
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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 6/15/2017.
 */
public class AllergyServiceTest extends BaseServiceTest {

    // Not used: replaced by AllergyObservationDao
    @Mock
    private AllergyDao allergyDao;

    @Mock
    private AllergyObservationDao allergyObservationDao;

    @Mock
    private UserDao userDao;

    @Mock
    private UserResidentRecordsDao userResidentRecordsDao;

    @Mock
    private CareTeamSecurityUtils careTeamSecurityUtils;

    @InjectMocks
    private AllergyService allergyService;

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\nResident ID: %d\n\n",
                userId, residentId);
    }

    private void verifySecurity(Long consumerId) {
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(consumerId, AccessRight.Code.MY_PHR);
    }

    private void setUpMockitoExpectations(Long consumerUserId) {
        final User currentUser = super.setUpMockitoExpectationsForCurrentUser(consumerUserId);

        when(userResidentRecordsDao.getActiveResidentIdsByUserId(consumerUserId)).thenReturn(activeResidentIds);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerUserId)).thenReturn(allResidentIds);
        when(userDao.findOne(userId)).thenReturn(currentUser);

        final String statusCodeA = "active";
        final String statusCodeI = "inactive";
        final String statusCodeR = "resolved";
        final Date timeHigh = TestDataGenerator.randomDate();
        final Date timeLow = TestDataGenerator.randomDateBefore(timeHigh);

        final Database database1 = new Database();
        database1.setId(1L);
        database1.setName("Datasource 01");

        final Allergy allergy1a = new Allergy();
        allergy1a.setResident(resident);
        allergy1a.setTimeLow(timeLow);
        allergy1a.setStatusCode(statusCodeA);
        final AllergyObservation allergyObservation1a = new AllergyObservation();
        allergyObservation1a.setAdverseEventTypeText("drug allergy");
        allergyObservation1a.setDatabase(database1);
        allergyObservation1a.setAllergy(allergy1a);
        allergy1a.setAllergyObservations(Collections.singleton(allergyObservation1a));
        final ReactionObservation reactionObservation1 = new ReactionObservation();
        reactionObservation1.setReactionText("rash");
        final ReactionObservation reactionObservation2 = new ReactionObservation();
        reactionObservation2.setReactionText("Hives");
        allergyObservation1a.setReactionObservations(new HashSet<>(Arrays.asList(reactionObservation1, reactionObservation2)));

        final Allergy allergy2a = new Allergy();
        allergy2a.setResident(resident2);
        allergy2a.setTimeLow(timeLow);
        allergy2a.setStatusCode(statusCodeA);
        final AllergyObservation allergyObservation2a = new AllergyObservation();
        allergyObservation2a.setAdverseEventTypeText("drug allergy");
        allergyObservation2a.setDatabase(database1);
        allergyObservation2a.setAllergy(allergy2a);
        allergy2a.setAllergyObservations(Collections.singleton(allergyObservation2a));

        final Allergy allergy3a = new Allergy();
        allergy3a.setResident(resident3);
        allergy3a.setTimeLow(timeLow);
        allergy3a.setStatusCode(statusCodeA);
        final AllergyObservation allergyObservation3a = new AllergyObservation();
        allergyObservation3a.setAdverseEventTypeText("drug allergy");
        allergyObservation3a.setDatabase(database1);
        allergyObservation3a.setAllergy(allergy3a);
        allergy3a.setAllergyObservations(Collections.singleton(allergyObservation3a));

        final Allergy allergy4a = new Allergy();
        allergy4a.setResident(resident3);
        allergy4a.setTimeLow(timeLow);
        allergy4a.setStatusCode(statusCodeA);
        final AllergyObservation allergyObservation4a = new AllergyObservation();
        allergyObservation4a.setAdverseEventTypeText("food allergy");
        allergyObservation4a.setDatabase(database1);
        allergyObservation4a.setAllergy(allergy4a);
        allergy4a.setAllergyObservations(Collections.singleton(allergyObservation4a));

        final Allergy allergy1i = new Allergy();
        allergy1i.setResident(resident);
        allergy1i.setTimeLow(timeLow);
        allergy1i.setStatusCode(statusCodeI);
        final AllergyObservation allergyObservation1i = new AllergyObservation();
        allergyObservation1i.setAdverseEventTypeText("drug allergy");
        allergyObservation1i.setDatabase(database1);
        allergyObservation1i.setAllergy(allergy1i);
        allergy1i.setAllergyObservations(Collections.singleton(allergyObservation1i));

        final Allergy allergy2i = new Allergy();
        allergy2i.setResident(resident2);
        allergy2i.setTimeLow(timeLow);
        allergy2i.setStatusCode(statusCodeI);
        final AllergyObservation allergyObservation2i = new AllergyObservation();
        allergyObservation2i.setAdverseEventTypeText("drug allergy");
        allergyObservation2i.setDatabase(database1);
        allergyObservation2i.setAllergy(allergy2i);
        allergy2i.setAllergyObservations(Collections.singleton(allergyObservation2i));

        final Allergy allergy3i = new Allergy();
        allergy3i.setResident(resident3);
        allergy3i.setTimeLow(timeLow);
        allergy3i.setStatusCode(statusCodeI);
        final AllergyObservation allergyObservation3i = new AllergyObservation();
        allergyObservation3i.setAdverseEventTypeText("drug allergy");
        allergyObservation3i.setDatabase(database1);
        allergyObservation3i.setAllergy(allergy3i);
        allergy3i.setAllergyObservations(Collections.singleton(allergyObservation3i));

        final Allergy allergy1r = new Allergy();
        allergy1r.setResident(resident);
        allergy1r.setTimeLow(timeLow);
        allergy1r.setTimeLow(timeHigh);
        allergy1r.setStatusCode(statusCodeR);
        final AllergyObservation allergyObservation1r = new AllergyObservation();
        allergyObservation1r.setAdverseEventTypeText("drug allergy");
        allergyObservation1r.setDatabase(database1);
        allergyObservation1r.setAllergy(allergy1r);
        allergy1r.setAllergyObservations(Collections.singleton(allergyObservation1r));

        final Allergy allergy2r = new Allergy();
        allergy2r.setResident(resident2);
        allergy2r.setTimeLow(timeLow);
        allergy2r.setTimeLow(timeHigh);
        allergy2r.setStatusCode(statusCodeR);
        final AllergyObservation allergyObservation2r = new AllergyObservation();
        allergyObservation2r.setAdverseEventTypeText("drug allergy");
        allergyObservation2r.setDatabase(database1);
        allergyObservation2r.setAllergy(allergy2r);
        allergy2r.setAllergyObservations(Collections.singleton(allergyObservation2r));

        final Allergy allergy3r = new Allergy();
        allergy3r.setResident(resident3);
        allergy3r.setTimeLow(timeLow);
        allergy3r.setTimeLow(timeHigh);
        allergy3r.setStatusCode(statusCodeR);
        final AllergyObservation allergyObservation3r = new AllergyObservation();
        allergyObservation3r.setAdverseEventTypeText("drug allergy");
        allergyObservation3r.setDatabase(database1);
        allergyObservation3r.setAllergy(allergy3r);
        allergy3r.setAllergyObservations(Collections.singleton(allergyObservation3r));

        final Allergy allergy4r = new Allergy();
        allergy4r.setResident(resident);
        allergy4r.setTimeLow(timeLow);
        allergy4r.setTimeHigh(timeHigh);
        allergy4r.setStatusCode(statusCodeR);
        final AllergyObservation allergyObservation4r = new AllergyObservation();
        allergyObservation4r.setAdverseEventTypeText("food allergy");
        allergyObservation4r.setDatabase(database1);
        allergyObservation4r.setAllergy(allergy4r);
        allergy4r.setAllergyObservations(Collections.singleton(allergyObservation4r));

        when(allergyDao.listResidentAllergies(residentId, true, false, false))
                .thenReturn(Arrays.asList(allergy1a));
        when(allergyDao.listResidentAllergies(residentId2, true, false, false))
                .thenReturn(Arrays.asList(allergy2a));
        when(allergyDao.listResidentAllergies(residentId3, true, false, false))
                .thenReturn(Arrays.asList(allergy3a, allergy4a));
        when(allergyObservationDao.listResidentAllergiesWithoutDuplicates(eq(activeResidentIds), eq(true), eq(false), eq(false), isNull(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(allergyObservation1a)));
        when(allergyObservationDao.listResidentAllergiesWithoutDuplicates(eq(allResidentIds), eq(true), eq(false), eq(false), isNull(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(allergyObservation1a, allergyObservation2a, allergyObservation3a, allergyObservation4a)));

        when(allergyDao.listResidentAllergies(residentId, false, true, false))
                .thenReturn(Arrays.asList(allergy1i));
        when(allergyDao.listResidentAllergies(residentId2, false, true, false))
                .thenReturn(Arrays.asList(allergy2i));
        when(allergyDao.listResidentAllergies(residentId3, false, true, false))
                .thenReturn(Arrays.asList(allergy3i));
        when(allergyObservationDao.listResidentAllergiesWithoutDuplicates(eq(activeResidentIds), eq(false), eq(true), eq(false), isNull(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(allergyObservation1i)));
        when(allergyObservationDao.listResidentAllergiesWithoutDuplicates(eq(allResidentIds), eq(false), eq(true), eq(false), isNull(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(allergyObservation1i, allergyObservation2i, allergyObservation3i)));

        when(allergyDao.listResidentAllergies(residentId, false, false, true))
                .thenReturn(Arrays.asList(allergy1r, allergy4r));
        when(allergyDao.listResidentAllergies(residentId2, false, false, true))
                .thenReturn(Arrays.asList(allergy2r));
        when(allergyDao.listResidentAllergies(residentId3, false, false, true))
                .thenReturn(Arrays.asList(allergy3r));
        when(allergyObservationDao.listResidentAllergiesWithoutDuplicates(eq(activeResidentIds), eq(false), eq(false), eq(true), isNull(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(allergyObservation1r, allergyObservation4r)));
        when(allergyObservationDao.listResidentAllergiesWithoutDuplicates(eq(allResidentIds), eq(false), eq(false), eq(true), isNull(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(allergyObservation1r, allergyObservation2r, allergyObservation3r, allergyObservation4r)));

        when(allergyObservationDao.countResidentAllergiesWithoutDuplicates(eq(activeResidentIds)))
                .thenReturn(4L);
        when(allergyObservationDao.countResidentAllergiesWithoutDuplicates(eq(allResidentIds)))
                .thenReturn(11L);
    }

    @Test
    public void testGetUserAllergiesActive() {
        final Long consumerId = userId;

        // Mockito expectations
        setUpMockitoExpectations(consumerId);

        // Execute the method being tested
        Page<AllergyInfoDto> page = allergyService.getUserAllergiesActive(consumerId, null);
        final List<AllergyInfoDto> result = page.getContent();

        // Validation
        assertThat(result, hasSize(1));
        verify(allergyObservationDao).listResidentAllergiesWithoutDuplicates(activeResidentIds, true, false, false, null);
        verifySecurity(consumerId);
    }

    @Test
    public void testGetUserAllergiesInactive() {
        final Long consumerId = userId;

        // Mockito expectations
        setUpMockitoExpectations(consumerId);

        // Execute the method being tested
        Page<AllergyInfoDto> page = allergyService.getUserAllergiesInactive(consumerId, null);
        final List<AllergyInfoDto> result = page.getContent();

        // Validation
        assertThat(result, hasSize(1));
        verify(allergyObservationDao).listResidentAllergiesWithoutDuplicates(activeResidentIds, false, true, false, null);
        verifySecurity(consumerId);
    }

    @Test
    public void testGetUserAllergiesResolved() {
        final Long consumerId = userId;

        // Mockito expectations
        setUpMockitoExpectations(consumerId);

        // Execute the method being tested
        Page<AllergyInfoDto> page = allergyService.getUserAllergiesResolved(consumerId, null);
        final List<AllergyInfoDto> result = page.getContent();

        // Validation
        assertThat(result, hasSize(2));
        verify(allergyObservationDao).listResidentAllergiesWithoutDuplicates(activeResidentIds, false, false, true, null);
        verifySecurity(consumerId);
    }

    @Test
    public void testGetUserAllergiesActiveAsProvider() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Mockito expectations
        setUpMockitoExpectations(consumerId);

        // Execute the method being tested
        Page<AllergyInfoDto> page = allergyService.getUserAllergiesActive(consumerId, null);
        final List<AllergyInfoDto> result = page.getContent();

        // Validation
        assertThat(result, hasSize(4));
        //verify(allergyDao).listResidentAllergies(residentId, true, false, false);
        //verify(allergyDao).listResidentAllergies(residentId2, true, false, false);
        //verify(allergyDao).listResidentAllergies(residentId3, true, false, false);
        verify(allergyObservationDao).listResidentAllergiesWithoutDuplicates(allResidentIds, true, false, false, null);
        verifySecurity(consumerId);
    }

    @Test
    public void testGetUserAllergiesInactiveAsProvider() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Mockito expectations
        setUpMockitoExpectations(consumerId);

        // Execute the method being tested
        Page<AllergyInfoDto> page = allergyService.getUserAllergiesInactive(consumerId, null);
        final List<AllergyInfoDto> result = page.getContent();

        // Validation
        assertThat(result, hasSize(3));
        //verify(allergyDao).listResidentAllergies(residentId, false, true, false);
        //verify(allergyDao).listResidentAllergies(residentId2, false, true, false);
        //verify(allergyDao).listResidentAllergies(residentId3, false, true, false);
        verify(allergyObservationDao).listResidentAllergiesWithoutDuplicates(allResidentIds, false, true, false, null);
        verifySecurity(consumerId);
    }

    @Test
    public void testGetUserAllergiesResolvedAsProvider() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);

        // Mockito expectations
        setUpMockitoExpectations(consumerId);

        // Execute the method being tested
        Page<AllergyInfoDto> page = allergyService.getUserAllergiesResolved(consumerId, null);
        final List<AllergyInfoDto> result = page.getContent();

        // Validation
        assertThat(result, hasSize(4));
        //verify(allergyDao).listResidentAllergies(residentId, false, false, true);
        //verify(allergyDao).listResidentAllergies(residentId2, false, false, true);
        //verify(allergyDao).listResidentAllergies(residentId3, false, false, true);
        verify(allergyObservationDao).listResidentAllergiesWithoutDuplicates(allResidentIds, false, false, true, null);
        verifySecurity(consumerId);
    }

    @Test
    public void testTransform() {
        // Expected objects
        final Date timeLow = TestDataGenerator.randomDate();
        final Date timeHigh = TestDataGenerator.randomDate();
        final String ALLERGY_TYPE = "environmental allergy";
        final String PRODUCT_TEXT = "Bees";
        final Database database1 = new Database();
        database1.setId(1L);
        database1.setName("Datasource 01");

        final Allergy allergy1i = new Allergy();
        allergy1i.setResident(resident);
        allergy1i.setResidentId(residentId);
        allergy1i.setTimeLow(timeLow);
        allergy1i.setTimeHigh(timeHigh);
        allergy1i.setStatusCode("inactive");
        final AllergyObservation allergyObservation1i = new AllergyObservation();
        allergyObservation1i.setProductText(PRODUCT_TEXT);
        allergyObservation1i.setAdverseEventTypeText(ALLERGY_TYPE);
        allergyObservation1i.setDatabase(database1);
        allergyObservation1i.setAllergy(allergy1i);
        allergy1i.setAllergyObservations(Collections.singleton(allergyObservation1i));
        final ReactionObservation reactionObservation1 = new ReactionObservation();
        reactionObservation1.setReactionText("rash");
        final ReactionObservation reactionObservation2 = new ReactionObservation();
        reactionObservation2.setReactionText("Hives");
        allergyObservation1i.setReactionObservations(new HashSet<>(Arrays.asList(reactionObservation1, reactionObservation2)));

        final DataSourceDto dataSourceDto = new DataSourceDto();
        dataSourceDto.setId(1L);
        dataSourceDto.setName("Datasource 01");
        dataSourceDto.setResidentId(residentId);
        final AllergyInfoDto expectedAllergy = new AllergyInfoDto();
        expectedAllergy.setAllergenType(AllergyInfoDto.AllergenType.ENVIRONMENT);
        expectedAllergy.setAllergyType(ALLERGY_TYPE);
        expectedAllergy.setProductText(PRODUCT_TEXT);
        expectedAllergy.setEndDate(timeHigh.getTime());
        expectedAllergy.setReaction("rash, Hives");
        expectedAllergy.setDataSource(dataSourceDto);

        // Execute the method being tested
        AllergyInfoDto result = AllergyService.transform(allergyObservation1i);

        // Validation
        assertThat(result, sameBeanAs(expectedAllergy).ignoring("reaction"));
        assertThat(result.getReaction(), anyOf(equalTo("rash, Hives"), equalTo("Hives, rash")));
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme