package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.services.Report;

public interface ExternalApiDocumentFacade {

    Report generateContinuityOfCareDocument(Long residentId, boolean isAggregated);
}
