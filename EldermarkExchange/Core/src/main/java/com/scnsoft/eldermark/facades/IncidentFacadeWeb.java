package com.scnsoft.eldermark.facades;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.dto.IncidentReportDto;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IncidentFacadeWeb {

    void getIncidentReportPDF(HttpServletResponse response, Long eventId, Integer timeZoneOffset) throws DocumentException, IOException;

    IncidentReportDto initIncidentReport(Long eventId);

    IncidentReportDto getIncidentReportDetails(Long id);

    Long saveIncidentReportDraft(Long eventId, IncidentReportDto incidentReportDto);

    Long submitIncidentReport(Long eventId, IncidentReportDto incidentReportDto) throws IOException, DocumentException;

    //todo add eventId
    boolean canCurrentUserCreateIncidentReport(Long eventId);

}