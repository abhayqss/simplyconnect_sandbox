package com.scnsoft.eldermark.services.integration.qualifacts;

import com.scnsoft.eldermark.entity.Document;

public interface QualifactsDocumentsGateway {

    void sendDocumentToQualifacts(Document document);

}
