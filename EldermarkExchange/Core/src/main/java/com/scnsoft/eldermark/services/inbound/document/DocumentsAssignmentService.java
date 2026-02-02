package com.scnsoft.eldermark.services.inbound.document;

import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.inbound.document.DocumentAssignmentInboundFile;
import com.scnsoft.eldermark.entity.inbound.document.DocumentAssignmentLog;
import com.scnsoft.eldermark.exception.integration.inbound.document.DocumentAssignmentErrorType;

public interface DocumentsAssignmentService {

    Document uploadDocument(DocumentAssignmentInboundFile inboundFile);

    DocumentAssignmentLog createDocumentAssignmentLog(DocumentAssignmentInboundFile inboundFile, Document document);

    DocumentAssignmentLog createDocumentAssignmentLog(DocumentAssignmentInboundFile inboundFile, DocumentAssignmentErrorType unassignedReason);

}
