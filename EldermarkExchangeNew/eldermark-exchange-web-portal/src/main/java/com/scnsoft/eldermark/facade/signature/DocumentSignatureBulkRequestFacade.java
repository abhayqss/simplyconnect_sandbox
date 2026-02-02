package com.scnsoft.eldermark.facade.signature;

import com.scnsoft.eldermark.dto.signature.DocumentSignatureBulkRequestInfoDto;
import com.scnsoft.eldermark.dto.signature.DocumentSignatureBulkRequestRenewDto;
import com.scnsoft.eldermark.dto.singature.SubmitTemplateSignatureBulkRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;

import java.util.List;

public interface DocumentSignatureBulkRequestFacade {
    Long renewBulkRequest(DocumentSignatureBulkRequestRenewDto dto);

    Long submitBulkRequest(SubmitTemplateSignatureBulkRequest dto);

    List<DocumentSignatureBulkRequestInfoDto> fetchRequests(Long id, List<DocumentSignatureRequestStatus> statuses);

    void cancelRequest(Long id, Long templateId);
}