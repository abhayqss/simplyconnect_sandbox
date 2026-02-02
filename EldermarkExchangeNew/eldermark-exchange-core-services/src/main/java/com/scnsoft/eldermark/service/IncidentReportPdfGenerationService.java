package com.scnsoft.eldermark.service;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.entity.document.DocumentReport;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport;

import java.io.IOException;
import java.time.ZoneId;

public interface IncidentReportPdfGenerationService {

    DocumentReport generatePdfReport(IncidentReport incidentReport, ZoneId zoneId) throws DocumentException, IOException;

}
