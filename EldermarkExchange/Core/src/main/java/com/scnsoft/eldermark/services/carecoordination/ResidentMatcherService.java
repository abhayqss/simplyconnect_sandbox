package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.duke.MatchResult;
import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.shared.carecoordination.PatientDto;

import java.util.List;

/**
 * Created by knetkachou on 1/16/2017.
 */
public interface ResidentMatcherService {

//    public MatchResult findMatchedPatients(PatientDto patient, Long organizationId, boolean verbose);
    MatchResult findMatchedPatients(PatientDto patient, boolean verbose);
    List<CareCoordinationResident> findFullMatchedResidents(PatientDto patient, Long organizationId, String dbOid);

//    CareCoordinationResident findFullMatchedAdtResident(String rbaDatabase, AdtDto adtDto);
}
