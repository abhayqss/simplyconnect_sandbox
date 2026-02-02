package com.scnsoft.eldermark.services.inbound.marco;

import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.inbound.marco.MarcoIntegrationDocument;
import com.scnsoft.eldermark.exception.integration.marco.MarcoUnassignedReason;

import java.io.File;

public interface MarcoDocumentsService {

    Document uploadDocument(MarcoDocumentMetadata metadata, File document);

    MarcoIntegrationDocument createNewMarcoIntegrationDocumentLog(MarcoDocumentMetadata metadata, Document document);

    MarcoIntegrationDocument createNewMarcoIntegrationDocumentLog(MarcoDocumentMetadata metadata, MarcoUnassignedReason unassignedReason);

}
