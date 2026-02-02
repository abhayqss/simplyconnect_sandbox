package com.scnsoft.eldermark.openxds.api.facade;

import com.scnsoft.eldermark.openxds.api.dto.XdsDocumentDto;
import com.scnsoft.eldermark.openxds.api.dto.XdsDocumentRegistrySyncResult;

import java.time.Instant;

public interface XdsDocumentFacade {

    XdsDocumentRegistrySyncResult synchronizeAllDocumentsWithXdsRegistry(Instant from);

    Long uploadDocument(XdsDocumentDto uploadDocument);

    XdsDocumentDto getDocument(String documentUniqueId);

    void deleteDocument(String documentUniqueId);

}
