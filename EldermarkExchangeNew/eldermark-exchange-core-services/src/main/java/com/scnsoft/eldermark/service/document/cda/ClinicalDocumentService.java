package com.scnsoft.eldermark.service.document.cda;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.service.document.cda.schema.ClinicalDocumentVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ClinicalDocumentService {

    ClinicalDocumentVO getClinicalDocument(Long mainClientId, List<Long> clientIds);

    void deleteByResidentId(Long residentId);

    @Transactional
    void saveClinicalDocument(Client resident, ClinicalDocumentVO clinicalDocumentVO);
}