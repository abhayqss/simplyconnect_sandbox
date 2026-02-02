package com.scnsoft.eldermark.services.ccd.section;

import com.google.common.base.Optional;
import com.scnsoft.eldermark.shared.ccd.CcdCodeDto;
import com.scnsoft.eldermark.shared.ccd.ProblemObservationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProblemService {

    String ICD_10_CM_CODE_SYSTEM_CODE = "2.16.840.1.113883.6.90";
    String ICD_9_CM_CODE_SYSTEM_CODE = "2.16.840.1.113883.6.103";

    Page<CcdCodeDto> listDiagnosisCodes(String searchString, Pageable pageRequest);

    List<CcdCodeDto> listDiagnosisCodesWithSameName(Long diagnosisCodeId);

    void createProblemObservation(ProblemObservationDto problemObservationDto, Long residentId);

    void editProblemObservation(ProblemObservationDto problemObservationDto, Long residentId);

    ProblemObservationDto getProblemObservationDto(Long problemObservationId);

    Optional<Long> getPrimaryObservationId(Long residentId);

    void deleteProblemObservation(Long problemObservationId);
}
