package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.MedicationInfoDto;
import com.scnsoft.eldermark.api.shared.exception.PhrException;
import com.scnsoft.eldermark.api.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.api.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.api.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.beans.ClientMedicationFilter;
import com.scnsoft.eldermark.dao.ClientMedicationDao;
import com.scnsoft.eldermark.dao.specification.ClientMedicationSpecificationGenerator;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.ccd.Indication;
import com.scnsoft.eldermark.entity.medication.ClientMedication;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicationsServiceTest {
    @Mock
    private ResidentsService residentsService;
    @Mock
    private ClientMedicationDao medicationDao;
    @Mock
    private ClientMedicationSpecificationGenerator medicationSpecifications;

    @Mock
    private Specification<ClientMedication> specification;

    @InjectMocks
    private MedicationsServiceImpl medicationsService;

    /**
     * Current user ID
     */
    protected final Long userId = TestDataGenerator.randomId();
    /**
     * Current user access token
     */
    protected final Long residentId = TestDataGenerator.randomId();
    protected final Client resident = new Client(residentId);

    @Test
    public void testGetInactive() throws Exception {
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);
        final String medicationName = TestDataGenerator.randomName();
        var medicationStopped = TestDataGenerator.randomDate();
        var medicationStarted = TestDataGenerator.randomDateBefore(medicationStopped);

        var filter = prepareFilter(residentId, false);

        final MedicationInformation medicationInfo = new MedicationInformation();
        medicationInfo.setProductNameText(medicationName);
        final ClientMedication medication = new ClientMedication();
        medication.setMedicationInformation(medicationInfo);
        medication.setMedicationStarted(medicationStarted.toInstant());
        medication.setMedicationStopped(medicationStopped.toInstant());

        final MedicationInfoDto expectedMedicationDto = new MedicationInfoDto();
        expectedMedicationDto.setMedicationName(medicationName);
        expectedMedicationDto.setStartedDate(medicationStarted.getTime());
        expectedMedicationDto.setStoppedDate(medicationStopped.getTime());

        // Mockito expectations
        when(medicationSpecifications.byFilter(refEq(filter))).thenReturn(specification);
        when(medicationDao.findAll(specification, pageable))
                .thenReturn((Page<ClientMedication>) new PageImpl<>(Arrays.asList(medication)));

        // Execute the method being tested
        Page<MedicationInfoDto> result = medicationsService.getInactive(residentId, pageable);

        // Validation
        assertThat(result).usingRecursiveFieldByFieldElementComparator().containsOnly(expectedMedicationDto);

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
        final ClientMedication medication = new ClientMedication();
        medication.setMedicationInformation(medicationInfo);
        medication.setMedicationStarted(medicationStarted.toInstant());
        medication.setIndications(Arrays.asList(indication));

        final MedicationInfoDto expectedMedicationDto = new MedicationInfoDto();
        expectedMedicationDto.setMedicationName(medicationName);
        expectedMedicationDto.setStartedDate(medicationStarted.getTime());
        expectedMedicationDto.setIndications(Arrays.asList(indicationDisplayName));

        var filter = prepareFilter(residentId, true);
        // Mockito expectations
        when(medicationSpecifications.byFilter(refEq(filter))).thenReturn(specification);

        when(medicationDao.findAll(specification, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(medication)));

        // Execute the method being tested
        Page<MedicationInfoDto> result = medicationsService.getActive(residentId, pageable);

        // Validation
        assertThat(result).usingRecursiveFieldByFieldElementComparator()
                .containsOnly(expectedMedicationDto);
        verify(residentsService).checkAccessOrThrow(residentId);
    }

    @Test
    public void testGetActiveEmpty() throws Exception {
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);

        var filter = prepareFilter(residentId, true);

        // Mockito expectations
        when(medicationSpecifications.byFilter(refEq(filter))).thenReturn(specification);
        when(medicationDao.findAll(specification, pageable))
                .thenReturn(Page.empty());

        // Execute the method being tested
        Page<MedicationInfoDto> result = medicationsService.getActive(residentId, pageable);

        // Validation
        assertThat(result).isEmpty();
        verify(residentsService).checkAccessOrThrow(residentId);
    }

    @Test
    public void testGetActiveThrowsAccessForbidden() throws Exception {
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);

        // Mockito expectations
        doThrow(new PhrException(PhrExceptionType.ACCESS_FORBIDDEN)).when(residentsService).checkAccessOrThrow(residentId);

        // Execute the method being tested
        assertThrows(PhrException.class, () -> medicationsService.getActive(residentId, pageable));
        verifyNoInteractions(medicationDao);
    }

    private ClientMedicationFilter prepareFilter(Long clientId, boolean active) {
        var filter = new ClientMedicationFilter();
        filter.setClientId(clientId);
        filter.setIncludeActive(active);
        filter.setIncludeInactive(!active);
        filter.setIncludeUnknown(active);
        return filter;
    }
}
