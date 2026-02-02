package com.scnsoft.eldermark.facades.carecoordination;

import com.scnsoft.eldermark.shared.carecoordination.PatientDto;
import com.scnsoft.eldermark.shared.carecoordination.patients.PatientListItemDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by pzhurba on 05-Oct-15.
 */
@Transactional
public interface PatientFacade {

    PatientDto getPatientDto(Long patientId, boolean showSsn, boolean checkEditable);

    PatientDto getPatientDetailsDto(Long patientId);

    PatientDto getEditPatientDto(Long patientId);

    PatientDto getTransportationPatientDto(Long patientId);

    Long createOrEditPatient(Long patientId, PatientDto patientDto);

    Boolean toggleActivation(Long patientId);

    List<PatientDto> findMatchedPatients(PatientDto dto);

    List<PatientListItemDto> getMergedResidents(Long patientId, Boolean showDeactivated);

//    Long getCommunityId(Long patientId);
}
