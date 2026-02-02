package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.ClinicalDocumentVO;
import org.springframework.transaction.annotation.Transactional;

public interface CdaParsingService <D> {
    @Transactional
    ClinicalDocumentVO parse(D document, Resident resident);
    @Transactional
    Resident parsePatientOnly(D document, Organization organization);
}
