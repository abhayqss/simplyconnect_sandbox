package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.dto.singature.SubmitTemplateSignatureBulkRequest;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureBulkRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;

import java.time.Instant;
import java.util.List;

public interface DocumentSignatureBulkRequestService {
    DocumentSignatureBulkRequest submitRequest(SubmitTemplateSignatureBulkRequest submitBulkRequest);

    DocumentSignatureBulkRequest renewBulkRequest(Long bulkRequestId, Instant toInstant, Long templateId, Employee currentEmployee);

    DocumentSignatureBulkRequest findById(Long id);

    List<DocumentSignatureRequest> fetchRequestsByIdAndStatusIn(Long id, List<DocumentSignatureRequestStatus> statuses);

    void cancelBulkRequest(Long id, Long templateId, Employee currentEmployee);
}