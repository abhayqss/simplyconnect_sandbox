package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.MedicationDao;
import com.scnsoft.eldermark.dao.phr.UserDao;
import com.scnsoft.eldermark.dao.phr.UserResidentRecordsDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.AccessRight;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.MedicationInfoDto;
import com.scnsoft.eldermark.web.security.CareTeamSecurityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 6/29/2017.
 */
public class MedicationServiceTest extends BaseServiceTest {

    @Mock
    private MedicationDao medicationDao;
    @Mock
    private CareTeamSecurityUtils careTeamSecurityUtils;
    @Mock
    private UserDao userDao;
    @Mock
    private UserResidentRecordsDao userResidentRecordsDao;

    @InjectMocks
    private MedicationService medicationService;

    // Shared test data
    private final Long medicationId = TestDataGenerator.randomId();

    @Before
    public void printState() {
        System.out.printf("### Random variables ###\nUser ID: %d\nResident ID: %d\nMedication ID: %d\n\n",
                userId, residentId, medicationId);
    }

    private void verifySecurity(Long consumerId) {
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(consumerId, AccessRight.Code.MY_PHR);
        verify(careTeamSecurityUtils).checkAccessToUserInfoOrThrow(consumerId, AccessRight.Code.MEDICATIONS_LIST);
    }

    private User setUpMockitoExpectations(Long consumerUserId) {
        final User consumer = super.createConsumer(consumerUserId);

        when(userResidentRecordsDao.getActiveResidentIdsByUserId(consumerUserId)).thenReturn(activeResidentIds);
        when(userResidentRecordsDao.getAllResidentIdsByUserId(consumerUserId)).thenReturn(allResidentIds);
        if (userId.equals(consumerUserId)) {
            when(userDao.findOne(userId)).thenReturn(consumer);
        }

        final Date timeHigh = TestDataGenerator.randomDate();
        final Date timeLow = TestDataGenerator.randomDateBefore(timeHigh);

        final MedicationInformation medicationInformation = new MedicationInformation();
        medicationInformation.setProductNameText("product name");
        final Medication medication = new Medication();
        medication.setId(medicationId);
        medication.setResident(consumer.getResident());
        medication.setDatabase(consumer.getResident().getDatabase());
        medication.setMedicationInformation(medicationInformation);
        medication.setIndications(Collections.<Indication>emptyList());
        medication.setMedicationStarted(timeLow);
        medication.setMedicationStopped(timeHigh);

        final MedicationInformation medicationInformation2 = new MedicationInformation();
        medicationInformation2.setProductNameText("product name");
        final Medication medication2 = new Medication();
        medication2.setId(medicationId + 1);
        medication2.setResident(consumer.getResident());
        medication2.setDatabase(consumer.getResident().getDatabase());
        medication2.setMedicationInformation(medicationInformation);
        medication2.setIndications(Collections.<Indication>emptyList());
        medication2.setMedicationStarted(timeLow);
        medication2.setMedicationStopped(null);

        final MedicationInformation medicationInformation3 = new MedicationInformation();
        medicationInformation3.setProductNameText("product name");
        final Medication medication3 = new Medication();
        medication3.setId(medicationId - 1);
        medication3.setResident(consumer.getResident());
        medication3.setDatabase(consumer.getResident().getDatabase());
        medication3.setMedicationInformation(medicationInformation);
        medication3.setIndications(Collections.<Indication>emptyList());
        medication3.setMedicationStarted(null);
        medication3.setMedicationStopped(timeHigh);

        final Database database = new Database();
        database.setId(TestDataGenerator.randomId());
        database.setName(TestDataGenerator.randomName());
        resident2.setDatabase(database);

        final MedicationInformation medicationInformation4 = new MedicationInformation();
        medicationInformation4.setProductNameText("product name");
        final Medication medication4 = new Medication();
        medication4.setId(medicationId + 2);
        medication4.setResident(resident2);
        medication4.setDatabase(database);
        medication4.setMedicationInformation(medicationInformation);
        medication4.setIndications(Collections.<Indication>emptyList());
        medication4.setMedicationStarted(null);
        medication4.setMedicationStopped(timeHigh);

        when(medicationDao.listResidentMedications(eq(residentId), eq(true), eq(false), isNull(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(medication2)));
        when(medicationDao.listResidentMedications(eq(residentId), eq(false), eq(true), isNull(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(medication, medication3)));
        when(medicationDao.listResidentMedications(eq(residentId2), eq(false), eq(true), isNull(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(medication4)));
        when(medicationDao.listResidentMedicationsWithoutDuplicates(eq(activeResidentIds), eq(false), eq(true), isNull(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(medication, medication3)));
        when(medicationDao.listResidentMedicationsWithoutDuplicates(eq(activeResidentIds), eq(true), eq(false), isNull(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(medication2)));
        when(medicationDao.listResidentMedicationsWithoutDuplicates(eq(allResidentIds), eq(false), eq(true), isNull(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(medication, medication3, medication4)));
        when(medicationDao.listResidentMedicationsWithoutDuplicates(eq(allResidentIds), eq(true), eq(false), isNull(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(medication2)));

        return consumer;
    }

    @Test
    public void testGetUserMedicationsActive() {
        setUpMockitoExpectations(userId);

        // Execute the method being tested
        Page<MedicationInfoDto> result = medicationService.getUserMedicationsActive(userId, null);

        // Validation
        assertThat(result.getContent(), hasSize(1));
        verify(medicationDao).listResidentMedicationsWithoutDuplicates(activeResidentIds, true, false, null);
        verifySecurity(userId);
    }

    @Test
    public void testGetUserMedicationsHistory() {
        setUpMockitoExpectations(userId);

        // Execute the method being tested
        Page<MedicationInfoDto> result = medicationService.getUserMedicationsHistory(userId, null);

        // Validation
        assertThat(result.getContent(), hasSize(2));
        verify(medicationDao).listResidentMedicationsWithoutDuplicates(activeResidentIds, false,  true,null);
        verifySecurity(userId);
    }

    @Test
    public void testGetUserMedicationsHistoryAsProvider() {
        final Long consumerId = TestDataGenerator.randomIdExceptOf(userId);
        setUpMockitoExpectations(consumerId);

        // Execute the method being tested
        Page<MedicationInfoDto> result = medicationService.getUserMedicationsHistory(consumerId, null);

        // Validation
        assertThat(result.getContent(), hasSize(3));
        verify(medicationDao).listResidentMedicationsWithoutDuplicates(allResidentIds, false,  true,null);
        verifySecurity(consumerId);
    }

    @Test
    public void testTransform() {
        final User consumer = setUpMockitoExpectations(userId);

        // Expected objects
        final Date timeHigh = TestDataGenerator.randomDate();
        final Date timeLow = TestDataGenerator.randomDateBefore(timeHigh);

        final CcdCode indicationCode = new CcdCode();
        indicationCode.setDisplayName("indication code");
        final CcdCode indicationValue = new CcdCode();
        indicationValue.setDisplayName("indication value");
        final Indication indication = new Indication();
        indication.setCode(indicationCode);
        indication.setValue(indicationValue);

        final MedicationInformation medicationInformation = new MedicationInformation();
        medicationInformation.setProductNameText("product name");
        final Medication medication = new Medication();
        medication.setId(medicationId);
        medication.setResident(resident);
        medication.setDatabase(consumer.getResident().getDatabase());
        medication.setMedicationInformation(medicationInformation);
        medication.setIndications(Collections.<Indication>emptyList());
        medication.setMedicationStarted(timeLow);
        medication.setMedicationStopped(timeHigh);
        medication.setIndications(Collections.singletonList(indication));
        medication.setFreeTextSig("directions free text");

        final MedicationInfoDto expectedMedicationInfo = new MedicationInfoDto();
        expectedMedicationInfo.setIndications(Arrays.asList("indication value"));
        expectedMedicationInfo.setMedicationName("product name");
        expectedMedicationInfo.setStartedDate(timeLow.getTime());
        expectedMedicationInfo.setStoppedDate(timeHigh.getTime());
        expectedMedicationInfo.setDirections("directions free text");

        // Execute the method being tested
        MedicationInfoDto result = MedicationService.transform(medication);

        // Validation
        assertThat(result, sameBeanAs(expectedMedicationInfo)
                .ignoring("startedDateStr").ignoring("stoppedDateStr"));
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme