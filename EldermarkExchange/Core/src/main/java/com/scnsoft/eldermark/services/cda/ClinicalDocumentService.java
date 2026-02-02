package com.scnsoft.eldermark.services.cda;

import com.scnsoft.eldermark.entity.Resident;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ClinicalDocumentService {

    @Transactional(readOnly = true)
    ClinicalDocumentVO getClinicalDocument(Long residentId);
    @Transactional(readOnly = true)
    ClinicalDocumentVO getClinicalDocument(Long mainResidentId, List<Long> residentIds);
    @Transactional
    void deleteByResidentId(Long residentId);
    @Transactional
    void saveClinicalDocument(Resident resident, ClinicalDocumentVO clinicalDocumentVO);

}