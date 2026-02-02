package com.scnsoft.eldermark.services;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.entity.incident.IncidentReport;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IncidentReportService {

    IncidentReport find(Long incidentReportId);

    void writeIncidentReportPDFByEventId(HttpServletResponse response, Long eventId, Integer timeZoneOffset)
            throws DocumentException, IOException;

    void writeIncidentReportPDFById(HttpServletResponse response, Long incidentReportId, Integer timeZoneOffset) throws DocumentException, IOException;


    Long saveIncidentReportDraft(IncidentReport incidentReport);

    Long submitIncidentReport(IncidentReport incidentReport) throws IOException, DocumentException;


    IncidentReport getIncidentReportForEvent(Long eventId);

    //todo 2 methods below should be moved to appropriate service
    String getResidentCareTeamMemberRoleByEmployeeIdAndResidentId(Long employeeId, Long residentId);

    String getCommunityCareTeamMemberRoleByEmployeeId(Long employeeId, Long databaseId);

    boolean hasAccessToIr(Long employeeId, IncidentReport incidentReport);

    boolean hasAccessToIrByEventId(Long employeeId, Long eventId);
}