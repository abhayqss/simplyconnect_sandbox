package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.IncidentReportFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.IncidentReportSecurityAwareEntity;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.document.DocumentReport;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

public interface IncidentReportService extends SecurityAwareEntityService<IncidentReportSecurityAwareEntity, Long>,
        ProjectingService<Long> {

    Page<IncidentReport> find(IncidentReportFilter filter, PermissionFilter predicatePermissionFilter, Pageable pageable);

    Page<IncidentReport> findHistoryById(Long id, Pageable pageable);

    IncidentReport findByEventId(Long eventId);

    DocumentReport writePDFById(Long id, HttpServletResponse response, ZoneId zoneId);

    IncidentReport findById(Long id);

    Long saveDraft(IncidentReport incidentReport);

    Long submit(IncidentReport incidentReport);

    //todo 2 methods below should be moved to appropriate service
    String getResidentCareTeamMemberRoleByEmployeeIdAndResidentId(Long employeeId, Long residentId);

    String getCommunityCareTeamMemberRoleByEmployeeId(Long employeeId, Long databaseId);

    Long findIncidentReportId(Long eventId);

    List<Employee> findReviewers(IncidentReport incidentReport);

    void deleteById(Long id);

    Optional<Instant> findOldestDateByOrganization(IncidentReportFilter filter, PermissionFilter predicatePermissionFilter);

    Optional<Instant> findNewestDateByOrganization(IncidentReportFilter filter, PermissionFilter predicatePermissionFilter);

    void validateHasNoConversation(Long id);

    void assignConversation(Long id, String conversationSid);
}
