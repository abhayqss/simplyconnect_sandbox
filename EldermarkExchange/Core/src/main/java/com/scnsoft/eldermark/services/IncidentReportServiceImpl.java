package com.scnsoft.eldermark.services;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.dao.DatabasesDao;
import com.scnsoft.eldermark.dao.carecoordination.EventDao;
import com.scnsoft.eldermark.dao.carecoordination.OrganizationCareTeamMemberDao;
import com.scnsoft.eldermark.dao.carecoordination.ResidentCareTeamMemberDao;
import com.scnsoft.eldermark.dao.incident.IncidentReportDao;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.incident.IncidentReport;
import com.scnsoft.eldermark.services.carecoordination.AuditableEntityService;
import com.scnsoft.eldermark.services.carecoordination.AuditableEntityServiceImpl;
import com.scnsoft.eldermark.services.carecoordination.ContactService;
import com.scnsoft.eldermark.services.mail.ExchangeMailService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class IncidentReportServiceImpl extends AuditableEntityServiceImpl<IncidentReport, IncidentReport> implements IncidentReportService, AuditableEntityService<IncidentReport, IncidentReport> {

    private static final Logger logger = LoggerFactory.getLogger(IncidentReportServiceImpl.class);

    @Autowired
    private IncidentReportPdfGenerationService incidentReportPdfGenerationService;

    @Autowired
    private IncidentReportDao incidentReportDao;

    @Autowired
    private EventDao eventDao;

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Autowired
    private DatabasesDao databasesDao;

    @Autowired
    private ResidentCareTeamMemberDao residentCareTeamMemberDao;

    @Autowired
    private OrganizationCareTeamMemberDao organizationCareTeamMemberDao;

    @Autowired
    private ContactService contactService;

    @Override
    public void writeIncidentReportPDFByEventId(HttpServletResponse response, Long eventId, Integer timeZoneOffset) throws DocumentException, IOException {
        TimeZone timeZone = getTimeZoneFromOffset(timeZoneOffset != null ? -timeZoneOffset : 0);
        incidentReportPdfGenerationService.generatePdfReport(response, eventId, timeZone);
    }

    @Override
    public IncidentReport find(Long incidentReportId) {
        return incidentReportDao.findOne(incidentReportId);
    }

    @Override
    public Long saveIncidentReportDraft(IncidentReport incidentReport) {
        return saveIncidentReport(incidentReport);
    }

    @Override
    public Long submitIncidentReport(IncidentReport incidentReport) throws IOException, DocumentException {
        incidentReport.setIsSubmit(true);
        Long id = saveIncidentReport(incidentReport);
        sendPdf(incidentReport);
        return id;
    }

    private Long saveIncidentReport(IncidentReport incidentReport) {
        return persistAuditableEntityWithoutPostCreate(incidentReport);
    }

    private void sendPdf(IncidentReport incidentReport) throws IOException, DocumentException {
        byte[] pdfBytes = incidentReportPdfGenerationService.getPdfBytes(incidentReport);
        String pdfAttachmentTitle = incidentReportPdfGenerationService.getPdfFileName(incidentReport);

        Long irDatabaseId = incidentReport.getEvent().getResident().getDatabaseId();
        List<String> reportGoverningEmails = databasesDao.getDatabaseById(irDatabaseId).getReportingGoverningEmails();
        if (CollectionUtils.isNotEmpty(reportGoverningEmails)) {
            exchangeMailService.sendIncidentReport(pdfBytes, reportGoverningEmails, pdfAttachmentTitle);
        } else {
            logger.info("mandated reporting governing body's email is not set. IR pdf will not be sent");
        }
    }

    @Override
    public void writeIncidentReportPDFById(HttpServletResponse response, Long incidentReportId, Integer timeZoneOffset) throws DocumentException, IOException {
        IncidentReport incidentReport = incidentReportDao.findOne(incidentReportId);
        TimeZone timeZone = getTimeZoneFromOffset(timeZoneOffset);
        incidentReportPdfGenerationService.generatePdfReport(response, incidentReport, timeZone);
    }

    @Override
    public IncidentReport getIncidentReportForEvent(Long eventId) {
        return incidentReportDao.getByEvent_IdAndArchivedIsFalse(eventId);
    }

    private TimeZone getTimeZoneFromOffset(Integer timeZoneOffset) {
        try {
            return TimeZone.getTimeZone(TimeZone.getAvailableIDs((int) TimeUnit.MINUTES.toMillis(timeZoneOffset))[0]);
        } catch (Exception e) {
            return TimeZone.getTimeZone(TimeZone.getAvailableIDs((int) TimeUnit.MINUTES.toMillis(0))[0]);
        }
    }

    @Override
    public String getResidentCareTeamMemberRoleByEmployeeIdAndResidentId(Long employeeId, Long residentId) {
        try {
            return residentCareTeamMemberDao.getResidentCareTeamMemberByEmployeeIdAndResidentId(employeeId, residentId).getCareTeamRole().getName();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getCommunityCareTeamMemberRoleByEmployeeId(Long employeeId, Long organizationId) {
        try {
            return organizationCareTeamMemberDao.getCareTeamMembersByEmployeeId(employeeId, organizationId).get(0).getCareTeamRole().getName();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean hasAccessToIr(Long employeeId, IncidentReport incidentReport) {
        return hasAccessToIrByCommunity(employeeId, incidentReport.getEvent().getResident().getFacility());
    }

    @Override
    public boolean hasAccessToIrByEventId(Long employeeId, Long eventId) {
        return hasAccessToIrByCommunity(employeeId, eventDao.get(eventId).getResident().getFacility());
    }

    private boolean hasAccessToIrByCommunity(Long employeeId, Organization organization) {
        if (BooleanUtils.isNotTrue(organization.getIrEnabled())) {
            return false;
        }

        List<Employee> employeeWithLinked = contactService.getLinkedEmployeeEntities(employeeId);

        for (Employee employee : employeeWithLinked) {
            if (BooleanUtils.isNotTrue(employee.getQaIncidentReports())) {
                continue;
            }

            if (CareTeamRoleCode.ROLE_SUPER_ADMINISTRATOR.equals(employee.getCareTeamRole().getCode())) {
                return true;
            }

            if (CareTeamRoleCode.ROLE_ADMINISTRATOR.equals(employee.getCareTeamRole().getCode())
                    && organization.getDatabaseId() == employee.getDatabaseId()) {
                return true;
            }

            if (employee.getCommunityId().equals(organization.getId())) {
                return true;
            }
        }
        return false;

    }

    @Override
    protected IncidentReport save(IncidentReport entity) {
        return incidentReportDao.save(entity);
    }

    @Override
    protected IncidentReport findById(Long id) {
        return incidentReportDao.findOne(id);
    }

    @Override
    protected IncidentReport dtoToEntity(IncidentReport incidentReport) {
        return incidentReport;
    }

    @Override
    protected void postCreate(IncidentReport entity, IncidentReport incidentReport) {
        //no actions required
    }
}
