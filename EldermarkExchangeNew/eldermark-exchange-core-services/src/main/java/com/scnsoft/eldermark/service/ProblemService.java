package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ClientProblemFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;

@Deprecated
public interface ProblemService {

    String ICD_10_CM_CODE_SYSTEM_CODE = "2.16.840.1.113883.6.90";

    String ICD_9_CM_CODE_SYSTEM_CODE = "2.16.840.1.113883.6.103";

    Long count(Long clientId, Boolean active, Boolean resolved, Boolean other);

    ProblemObservation findById(Long problemId);

    Page<ProblemObservation> find(Long clientId, Boolean active, Boolean resolved, Boolean other, Pageable pageRequest);

}
