package com.scnsoft.eldermark.api.external.facade;


import com.scnsoft.eldermark.api.external.service.ResidentsService;
import com.scnsoft.eldermark.entity.document.DocumentReport;
import com.scnsoft.eldermark.service.document.cda.generator.CcdGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ExternalApiDocumentFacadeImpl implements ExternalApiDocumentFacade {

    private static final Logger logger = LoggerFactory.getLogger(ExternalApiDocumentFacadeImpl.class);

    private final CcdGeneratorService ccdGeneratorService;
    private final ResidentsService residentsService;

    @Autowired
    public ExternalApiDocumentFacadeImpl(CcdGeneratorService ccdGeneratorService, ResidentsService residentsService) {
        this.ccdGeneratorService = ccdGeneratorService;
        this.residentsService = residentsService;
    }

    @Override
    public DocumentReport generateContinuityOfCareDocument(Long residentId, boolean isAggregated) {
        logger.info("Loading CCD for client [{}], aggregated={}", residentId, isAggregated);
        residentsService.checkAccessOrThrow(residentId);
        var ccdReport = ccdGeneratorService.generate(residentId, isAggregated);
        return ccdReport;
    }
}
