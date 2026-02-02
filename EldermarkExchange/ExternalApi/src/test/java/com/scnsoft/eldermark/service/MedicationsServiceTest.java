package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.healthdata.MedicationDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.MedicationInfoDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 4/14/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class MedicationsServiceTest {

    @Mock
    private ResidentsService residentsService;
    @Mock
    private MedicationDao medicationDao;

    @InjectMocks
    private MedicationsService medicationsService;

    /**
     * Current user ID
     */
    protected final Long userId = TestDataGenerator.randomId();
    /**
     * Current user access token
     */
    protected final Long residentId = TestDataGenerator.randomId();
    protected final Resident resident = new Resident(residentId);

    @Test
    public void testGetInactive() throws Exception {
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);
        final String medicationName = TestDataGenerator.randomName();
        final Date medicationStopped = TestDataGenerator.randomDate();
        final Date medicationStarted = TestDataGenerator.randomDateBefore(medicationStopped);
        final MedicationInformation medicationInfo = new MedicationInformation();
        medicationInfo.setProductNameText(medicationName);
        final Medication medication = new Medication();
        medication.setMedicationInformation(medicationInfo);
        medication.setMedicationStarted(medicationStarted);
        medication.setMedicationStopped(medicationStopped);

        final MedicationInfoDto expectedMedicationDto = new MedicationInfoDto();
        expectedMedicationDto.setMedicationName(medicationName);
        expectedMedicationDto.setStartedDate(medicationStarted.getTime());
        expectedMedicationDto.setStoppedDate(medicationStopped.getTime());

        // Mockito expectations
        when(medicationDao.listResidentMedications(residentId, false, true, pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(medication)));

        // Execute the method being tested
        Page<MedicationInfoDto> result = medicationsService.getInactive(residentId, pageable);

        // Validation
        assertThat(result.getContent(), hasSize(1));
        final MedicationInfoDto actualMedicationDto = result.getContent().get(0);
        assertThat(actualMedicationDto, sameBeanAs(expectedMedicationDto));
        verify(residentsService).checkAccessOrThrow(residentId);
    }

    @Test
    public void testGetActive() throws Exception {
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);
        final String medicationName = TestDataGenerator.randomName();
        final Date medicationStarted = TestDataGenerator.randomDateBefore(new Date());
        // example from C-CDA R2: <value xsi:type="CD" code="57676002" codeSystem="2.16.840.1.113883.6.96" displayName="Joint pain" />
        final String indicationDisplayName = "Joint pain";
        final CcdCode indicationValue = new CcdCode();
        indicationValue.setDisplayName(indicationDisplayName);
        indicationValue.setCode("57676002");
        indicationValue.setCodeSystem(CodeSystem.SNOMED_CT.getOid());
        final Indication indication = new Indication();
        indication.setValue(indicationValue);

        final MedicationInformation medicationInfo = new MedicationInformation();
        medicationInfo.setProductNameText(medicationName);
        final Medication medication = new Medication();
        medication.setMedicationInformation(medicationInfo);
        medication.setMedicationStarted(medicationStarted);
        medication.setIndications(Arrays.asList(indication));

        final MedicationInfoDto expectedMedicationDto = new MedicationInfoDto();
        expectedMedicationDto.setMedicationName(medicationName);
        expectedMedicationDto.setStartedDate(medicationStarted.getTime());
        expectedMedicationDto.setIndications(Arrays.asList(indicationDisplayName));

        // Mockito expectations
        when(medicationDao.listResidentMedications(residentId, true, false, pageable))
                .thenReturn(new PageImpl<>(Arrays.asList(medication)));

        // Execute the method being tested
        Page<MedicationInfoDto> result = medicationsService.getActive(residentId, pageable);

        // Validation
        assertThat(result.getContent(), hasSize(1));
        final MedicationInfoDto actualMedicationDto = result.getContent().get(0);
        assertThat(actualMedicationDto, sameBeanAs(expectedMedicationDto));
        verify(residentsService).checkAccessOrThrow(residentId);
    }

    @Test
    public void testGetActiveEmpty() throws Exception {
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);

        // Mockito expectations
        when(medicationDao.listResidentMedications(residentId, true, false, pageable))
                .thenReturn(new PageImpl<>(Collections.<Medication>emptyList()));

        // Execute the method being tested
        Page<MedicationInfoDto> result = medicationsService.getActive(residentId, pageable);

        // Validation
        assertThat(result.getContent(), hasSize(0));
        verify(residentsService).checkAccessOrThrow(residentId);
    }

    @Test(expected = PhrException.class)
    public void testGetActiveThrowsAccessForbidden() throws Exception {
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);

        // Mockito expectations
        doThrow(new PhrException(PhrExceptionType.ACCESS_FORBIDDEN)).when(residentsService).checkAccessOrThrow(residentId);
        when(medicationDao.listResidentMedications(residentId, true, false, pageable))
                .thenReturn(new PageImpl<>(Collections.<Medication>emptyList()));

        // Execute the method being tested
        medicationsService.getActive(residentId, pageable);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme