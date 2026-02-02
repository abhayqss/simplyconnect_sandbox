package com.scnsoft.eldermark.beans.projection;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;

import java.time.Instant;

public interface DocumentSignatureHistoryEventDetailsAware extends DocumentIdAware, DateAware {

    String getRequestSignatureTemplateTitle();

    DocumentSignatureRequestStatus getRequestStatus();

    Instant getDocumentDeletionTime();
}
