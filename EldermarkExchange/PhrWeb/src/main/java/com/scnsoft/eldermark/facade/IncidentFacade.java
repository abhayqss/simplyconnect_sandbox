package com.scnsoft.eldermark.facade;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.dto.IncidentReportDto;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IncidentFacade {

    void getIncidentReportPDF(HttpServletResponse response, Long eventId, Long userId, Integer timeZoneOffset)
            throws DocumentException, IOException;
    
    IncidentReportDto initIncidentReport(Long eventId, Long userId);

    IncidentReportDto getIncidentReportDetails(Long incidentReportId);

    Long submitIncidentReport(Long userId, Long eventId, IncidentReportDto incidentReportDto) throws IOException, DocumentException;

    Long saveIncidentReportDraft(Long userId, Long eventId, IncidentReportDto incidentReportDto);

    Boolean canCurrentUserCreateIncidentReport(Long eventId);

    void getIncidentReportPDFById(HttpServletResponse response, Long userId, Long incidentReportId, Integer timeZoneOffset) throws DocumentException, IOException;

}