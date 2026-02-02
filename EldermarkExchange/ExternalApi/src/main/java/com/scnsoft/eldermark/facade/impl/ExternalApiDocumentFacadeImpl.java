package com.scnsoft.eldermark.facade.impl;

import com.scnsoft.eldermark.facade.ExternalApiDocumentFacade;
import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.service.ResidentsService;
import com.scnsoft.eldermark.services.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ExternalApiDocumentFacadeImpl implements ExternalApiDocumentFacade {

    private static final String CCD_REPORT_TYPE = "ccd";

    private final DocumentFacade documentFacade;
    private final ResidentsService residentsService;

    @Autowired
    public ExternalApiDocumentFacadeImpl(DocumentFacade documentFacade, ResidentsService residentsService) {
        this.documentFacade = documentFacade;
        this.residentsService = residentsService;
    }

    @Override
    public Report generateContinuityOfCareDocument(Long residentId, boolean isAggregated) {
        residentsService.checkAccessOrThrow(residentId);
        final Report ccdReport = documentFacade.generateReport(residentId, isAggregated, CCD_REPORT_TYPE);
        return ccdReport;
    }
}
