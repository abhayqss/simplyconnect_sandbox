package com.scnsoft.eldermark.api.external.facade;

import com.scnsoft.eldermark.entity.document.DocumentReport;

public interface ExternalApiDocumentFacade {

    DocumentReport generateContinuityOfCareDocument(Long residentId, boolean isAggregated);
}
