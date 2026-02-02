package com.scnsoft.eldermark.service.xds;

import com.scnsoft.eldermark.entity.document.Document;
import com.scnsoft.eldermark.entity.document.DocumentXdsConnectorFieldsAware;

public interface XdsRegistryConnectorService {

    void saveNewFileInRegistry(DocumentXdsConnectorFieldsAware document, Long clientId);

    void deprecateDocumentInRepository(String documentUUID);

    void approveDocumentInRepository(String documentUUID);

    void updateDocumentTitleInRepository(Document document);

    String synchronizeDocWithRepository(DocumentXdsConnectorFieldsAware doc, Long clientId);

    boolean isDocumentVisibleInRegistry(DocumentXdsConnectorFieldsAware doc);
}
