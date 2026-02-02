package com.scnsoft.eldermark.consana.sync.client.services.converters;

import com.scnsoft.eldermark.consana.sync.client.model.entities.Database;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Organization;
import com.scnsoft.eldermark.consana.sync.client.model.entities.Resident;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ConsanaPatientUpdateType;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateQueueDto;
import com.scnsoft.eldermark.consana.sync.client.model.queue.ResidentUpdateType;
import com.scnsoft.eldermark.consana.sync.client.predicates.ConsanaIntegrationEnabledPredicate;
import com.scnsoft.eldermark.consana.sync.client.services.entities.ResidentService;
import com.scnsoft.eldermark.consana.sync.common.services.db.SqlServerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResidentToPatientsConverterTest {

    @Mock
    private ResidentService residentService;

    @Mock
    private ConsanaIntegrationEnabledPredicate consanaIntegrationEnabledPredicate;

    @Mock
    private SqlServerService sqlServerService;

    @InjectMocks
    private ResidentToPatientsConverter instance;

    private static final Long DATABASE_ID = 1L;
    private static final String DATABASE_OID = "DATABASE_OID";
    private static final Long ORGANIZATION_ID = 2L;
    private static final String ORGANIZATION_OID = "ORGANIZATION_OID";


    private Long requestedResidentId;
    private Long updateTime;
    private ResidentUpdateQueueDto residentUpdateDto;
    private Resident resident1;
    private Resident resident2;

    @BeforeEach
    void prepare() {
        requestedResidentId = 3L;
        updateTime = Instant.now().toEpochMilli();
        residentUpdateDto = new ResidentUpdateQueueDto(requestedResidentId,
                EnumSet.of(ResidentUpdateType.MEDICATION, ResidentUpdateType.RESIDENT), updateTime);
        resident1 = prepareResident(requestedResidentId);
        resident1.setConsanaXrefId("xref" + requestedResidentId);
        resident2 = prepareResident(requestedResidentId + 1);
    }

    private Resident prepareResident(long id) {
        var resident = new Resident();
        resident.setId(id);
        resident.setDatabase(new Database(DATABASE_ID, DATABASE_OID));
        resident.setFacility(new Organization(ORGANIZATION_ID, ORGANIZATION_OID));

        return resident;
    }

    @Test
    void convert_NoConsanaEnabledResidents_ReturnsEmptyList() {
        when(residentService.getMergedResidents(requestedResidentId)).thenReturn(Stream.of(resident1, resident2));

        var result = instance.convert(residentUpdateDto);

        verify(sqlServerService).openKey();
        assertTrue(result.isEmpty());
    }

    @Test
    void convert_OneConsanaEnabledResident_ReturnsListWithDto() {
        when(residentService.getMergedResidents(requestedResidentId)).thenReturn(Stream.of(resident1, resident2));
        when(consanaIntegrationEnabledPredicate.test(resident1)).thenReturn(true);
        when(residentService.updateXrefId(resident1)).thenReturn(resident1);

        var resultList = instance.convert(residentUpdateDto);

        verify(sqlServerService).openKey();
        assertEquals(1, resultList.size());
        assertEquals(buildConsanaPatientUpdateDto(resident1), resultList.get(0));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void convert_AllConsanaEnabledResidents_ReturnsListWithDtos() {
        when(residentService.getMergedResidents(requestedResidentId)).thenReturn(Stream.of(resident1, resident2));
        when(consanaIntegrationEnabledPredicate.test(resident1)).thenReturn(true);
        when(consanaIntegrationEnabledPredicate.test(resident2)).thenReturn(true);
        when(residentService.updateXrefId(resident1)).thenReturn(resident1);
        when(residentService.updateXrefId(resident2)).thenReturn(resident2);

        var resultList = instance.convert(residentUpdateDto);

        verify(sqlServerService).openKey();
        assertEquals(2, resultList.size());
        assertEquals(buildConsanaPatientUpdateDto(resident1), resultList.get(0));
        assertEquals(buildConsanaPatientUpdateDto(resident2), resultList.get(1));
    }

    @Test
    void convert_HasOnlyDemographicsUpdate_UsesOnlyGivenResidentWithoutMerged() {
        convert_WhenUpdatesNotAffectingMerged_UsesOnlyGivenResidentWithoutMerged(EnumSet.of(ResidentUpdateType.RESIDENT));
    }

    @Test
    void convert_HasOnlyMergeUpdate_UsesOnlyGivenResidentWithoutMerged() {
        convert_WhenUpdatesNotAffectingMerged_UsesOnlyGivenResidentWithoutMerged(EnumSet.of(ResidentUpdateType.RESIDENT_MERGE));
    }

    @Test
    void convert_HasDemographicsAndMergeUpdate_UsesOnlyGivenResidentWithoutMerged() {
        convert_WhenUpdatesNotAffectingMerged_UsesOnlyGivenResidentWithoutMerged(EnumSet.of(ResidentUpdateType.RESIDENT, ResidentUpdateType.RESIDENT_MERGE));
    }

    private void convert_WhenUpdatesNotAffectingMerged_UsesOnlyGivenResidentWithoutMerged(Set<ResidentUpdateType> updateTypes) {
        when(residentService.getOne(requestedResidentId)).thenReturn(resident1);
        when(consanaIntegrationEnabledPredicate.test(resident1)).thenReturn(true);
        when(residentService.updateXrefId(resident1)).thenReturn(resident1);
        residentUpdateDto.setUpdateTypes(updateTypes);

        var resultList = instance.convert(residentUpdateDto);
        assertEquals(1, resultList.size());
        assertEquals(buildConsanaPatientUpdateDto(resident1), resultList.get(0));

        verify(sqlServerService).openKey();
        verify(residentService, never()).getMergedResidents(requestedResidentId);
    }

    private ConsanaPatientUpdateQueueDto buildConsanaPatientUpdateDto(Resident resident) {
        return new ConsanaPatientUpdateQueueDto(
                resident.getConsanaXrefId(), DATABASE_OID, ORGANIZATION_OID,
                ConsanaPatientUpdateType.PATIENT_UPDATE, updateTime);
    }
}