package com.scnsoft.eldermark.service;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.entity.document.DocumentReport;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlan;

import java.io.IOException;
import java.time.ZoneId;
import java.util.List;

public interface ServicePlanPdfGenerationService {

    DocumentReport generatePdfReport(ServicePlan servicePlan, List<Long> domainIds, ZoneId zoneId)
            throws DocumentException, IOException;

}
