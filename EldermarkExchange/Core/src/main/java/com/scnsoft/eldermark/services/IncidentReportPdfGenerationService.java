package com.scnsoft.eldermark.services;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.entity.incident.IncidentReport;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.TimeZone;

public interface IncidentReportPdfGenerationService {

    void generatePdfReport(HttpServletResponse response, Long eventId, TimeZone timeZone)
            throws DocumentException, IOException;

    void generatePdfReport(HttpServletResponse response, IncidentReport incidentReport, TimeZone timeZone)
            throws DocumentException, IOException;

    byte[] getPdfBytes(IncidentReport incidentReport) throws DocumentException, IOException;

    String getPdfFileName(IncidentReport incidentReport);

}
