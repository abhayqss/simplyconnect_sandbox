package com.scnsoft.eldermark.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.beans.ClientAppointmentFilter;
import com.scnsoft.eldermark.beans.ClientFilter;
import com.scnsoft.eldermark.beans.ClientRecordSearchFilter;
import com.scnsoft.eldermark.beans.ContactFilter;
import com.scnsoft.eldermark.beans.EventNoteFilter;
import com.scnsoft.eldermark.beans.IncidentReportFilter;
import com.scnsoft.eldermark.beans.LabResearchOrderFilter;
import com.scnsoft.eldermark.beans.MarketplaceFilter;
import com.scnsoft.eldermark.beans.ReferralFilter;
import com.scnsoft.eldermark.beans.ServicePlanFilter;
import com.scnsoft.eldermark.beans.audit.AuditLogAction;
import com.scnsoft.eldermark.beans.audit.AuditLogFilter;
import com.scnsoft.eldermark.beans.audit.AuditLogFilterDto;
import com.scnsoft.eldermark.beans.projection.ClientCareTeamAuditLogDetailsAware;
import com.scnsoft.eldermark.beans.projection.ClientIdClientOrganizationIdClientCommunityIdAware;
import com.scnsoft.eldermark.beans.projection.CommunityCareTeamAuditLogDetailsAware;
import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.beans.projection.DocumentAndFolderTypeAware;
import com.scnsoft.eldermark.beans.projection.DocumentSignatureTemplateAuditLogDetailsAware;
import com.scnsoft.eldermark.beans.projection.DocumentTitleClientIdClientOrganizationIdClientCommunityIdAware;
import com.scnsoft.eldermark.beans.projection.EmployeeStatusAware;
import com.scnsoft.eldermark.beans.projection.IdAndOrganizationIdAndCommunityIdAware;
import com.scnsoft.eldermark.beans.projection.IdAndOrganizationIdAware;
import com.scnsoft.eldermark.beans.projection.OrganizationIdAndCommunityIdAware;
import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;
import com.scnsoft.eldermark.beans.projection.OrganizationIdCommunityIdAssociatedClientsAware;
import com.scnsoft.eldermark.beans.projection.SignatureRequestTemplateNameAware;
import com.scnsoft.eldermark.beans.projection.SignatureRequestTemplateNameEmployeeIdAware;
import com.scnsoft.eldermark.beans.projection.TitleAware;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.AuditLogListItemDto;
import com.scnsoft.eldermark.dto.ContactDto;
import com.scnsoft.eldermark.dto.ReleaseNoteListItemDto;
import com.scnsoft.eldermark.dto.UserManualDocumentDto;
import com.scnsoft.eldermark.dto.conversation.EditConversationDto;
import com.scnsoft.eldermark.dto.conversation.call.InitiateCallDto;
import com.scnsoft.eldermark.dto.document.CommunityDocumentFilterDto;
import com.scnsoft.eldermark.dto.filter.ClientDocumentFilter;
import com.scnsoft.eldermark.dto.prospect.ProspectFilterDto;
import com.scnsoft.eldermark.dto.report.ReportFilterDto;
import com.scnsoft.eldermark.entity.EmployeeRequestType;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogAllergyRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogAppointmentRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogAssessmentRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogChatRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogClientCareTeamMemberRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogClientDocumentRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogCommunityCareTeamMemberRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogCommunityRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogCompanyDocumentRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogContactRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogDocumentFolderRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogEventRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogExpenseRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogIncidentReportRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogLabResearchOrderRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogMarketplaceRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogMedicationRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogNoteRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogOrganizationRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogProblemRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogReferralRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogReferralRequestRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogReleaseNoteRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogReportRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogSearchFilter;
import com.scnsoft.eldermark.entity.audit.AuditLogServicePlanRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogSignatureBulkRequestRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogSignatureRequestRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogSignatureTemplateRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogSupportTicketRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogUserManualRelation;
import com.scnsoft.eldermark.entity.audit.AuditLog_;
import com.scnsoft.eldermark.entity.audit.event.AuditLogPublishedEvent;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.Document;
import com.scnsoft.eldermark.entity.document.DocumentAndFolderType;
import com.scnsoft.eldermark.entity.document.folder.IdNameCommunityIdAndCommunityOrganizationIdAware;
import com.scnsoft.eldermark.entity.event.incident.IncidentEventClientIdClientOrganizationIdClientCommunityIdAware;
import com.scnsoft.eldermark.entity.note.NoteClientIdClientIdsTypeAware;
import com.scnsoft.eldermark.entity.note.NoteType;
import com.scnsoft.eldermark.entity.referral.ReferralClientIdClientOrganizationIdClientCommunityIdAware;
import com.scnsoft.eldermark.security.JwtTokenFacade;
import com.scnsoft.eldermark.service.ClientAllergyService;
import com.scnsoft.eldermark.service.ClientAppointmentService;
import com.scnsoft.eldermark.service.ClientAssessmentResultService;
import com.scnsoft.eldermark.service.ClientCareTeamMemberService;
import com.scnsoft.eldermark.service.ClientMedicationService;
import com.scnsoft.eldermark.service.ClientProblemService;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.CommunityCareTeamMemberService;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.ContactService;
import com.scnsoft.eldermark.service.EmployeeRequestService;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.EventService;
import com.scnsoft.eldermark.service.IncidentReportService;
import com.scnsoft.eldermark.service.LabResearchOrderService;
import com.scnsoft.eldermark.service.NoteService;
import com.scnsoft.eldermark.service.ProspectService;
import com.scnsoft.eldermark.service.ReferralService;
import com.scnsoft.eldermark.service.ReleaseNoteService;
import com.scnsoft.eldermark.service.ServicePlanService;
import com.scnsoft.eldermark.service.UserManualService;
import com.scnsoft.eldermark.service.audit.AuditLogExportGenerator;
import com.scnsoft.eldermark.service.audit.AuditLogFactory;
import com.scnsoft.eldermark.service.audit.AuditLogService;
import com.scnsoft.eldermark.service.client.expense.ClientExpenseService;
import com.scnsoft.eldermark.service.document.ClientDocumentService;
import com.scnsoft.eldermark.service.document.CommunityDocumentAndFolderService;
import com.scnsoft.eldermark.service.document.DocumentService;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureTemplateService;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.util.AuditLogUtils;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.document.DocumentAndFolderUtils;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class AuditLogFacadeImpl implements AuditLogFacade {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogFacadeImpl.class);

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private AuditLogFactory auditLogFactory;

    @Autowired
    private JwtTokenFacade jwtTokenFacade;

    @Autowired
    private EmployeeRequestService employeeRequestService;

    @Autowired
    private ServicePlanService servicePlanService;

    @Autowired
    private ClientAssessmentResultService assessmentResultService;

    @Autowired
    private ClientDocumentService clientDocumentService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentFolderService documentFolderService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private EventService eventService;

    @Autowired
    private ClientMedicationService clientMedicationService;

    @Autowired
    private ClientAllergyService clientAllergyService;

    @Autowired
    private ClientProblemService clientProblemService;

    @Autowired
    private IncidentReportService incidentReportService;

    @Autowired
    private LabResearchOrderService labResearchOrderService;

    @Autowired
    private ClientCareTeamMemberService clientCareTeamMemberService;

    @Autowired
    private CommunityCareTeamMemberService communityCareTeamMemberService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private ReferralService referralService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ClientExpenseService clientExpenseService;

    @Autowired
    private DocumentSignatureTemplateService documentSignatureTemplateService;

    @Autowired
    private CommunityDocumentAndFolderService communityDocumentAndFolderService;

    @Autowired
    private ClientAppointmentService clientAppointmentService;

    @Autowired
    private DocumentSignatureRequestService documentSignatureRequestService;

    @Autowired
    private UserManualService userManualService;

    @Autowired
    private ReleaseNoteService releaseNoteService;

    @Autowired
    private ProspectService prospectService;

    @Autowired
    private ListAndItemConverter<Pair<AuditLog, ZoneId>, AuditLogListItemDto> auditLogListItemDtoConverter;

    @Autowired
    private Converter<AuditLogFilterDto, AuditLogFilter> auditLogFilterConverter;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuditLogExportGenerator auditLogExportGenerator;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@auditLogSecurityService.canViewList()")
    public Page<AuditLogListItemDto> find(AuditLogFilterDto filter, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return auditLogService.find(
                        auditLogFilterConverter.convert(filter),
                        permissionFilter,
                        PaginationUtils.applyEntitySort(pageable, AuditLogListItemDto.class)
                )
                .map(log -> auditLogListItemDtoConverter.convert(Pair.of(log, filter.getZoneId())));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@auditLogSecurityService.canViewList()")
    public Long findOldestDateByOrganization(Long organizationId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var auditLogFilter = new AuditLogFilterDto();
        auditLogFilter.setOrganizationId(organizationId);
        return auditLogService.findOldestDate(auditLogFilterConverter.convert(auditLogFilter), permissionFilter)
                .map(Instant::toEpochMilli)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@auditLogSecurityService.canViewList()")
    public Long findNewestDateByOrganization(Long organizationId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var auditLogFilter = new AuditLogFilterDto();
        auditLogFilter.setOrganizationId(organizationId);
        return auditLogService.findNewestDate(auditLogFilterConverter.convert(auditLogFilter), permissionFilter)
                .map(Instant::toEpochMilli)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canViewList() {
        return auditLogService.canViewList();
    }

    @Override
    @PreAuthorize("@auditLogSecurityService.canViewList()")
    public void export(AuditLogFilterDto filter, HttpServletResponse response) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var auditLogs = auditLogService.find(auditLogFilterConverter.convert(filter), permissionFilter,
                PaginationUtils.setSort(Pageable.unpaged(), Sort.Order.by(AuditLog_.EMPLOYEE), Sort.Order.desc(AuditLog_.DATE), Sort.Order.by(AuditLog_.ACTION)));
        var workbook = auditLogExportGenerator.generate(auditLogs.getContent(), filter);
        WriterUtils.copyDocumentContentToResponse(workbook, "Audit logs export", response);
    }

    @Override
    public void logLogin() {
        var employee = loggedUserService.getCurrentEmployee();
        createLog(Set.of(employee.getOrganizationId()),
                CareCoordinationUtils.setOfNullable(employee.getCommunityId()),
                new HashSet<>(employee.getAssociatedClientIds()),
                null,
                AuditLogAction.LOG_IN,
                null,
                null);
    }

    @Override
    public void logLogout(String token) {
        if (StringUtils.isEmpty(token)) {
            return;
        }
        var employeeId = jwtTokenFacade.getUserIdFromJWT(token, true);
        var aware = contactService.findById(employeeId, OrganizationIdCommunityIdAssociatedClientsAware.class);
        createLog(Set.of(aware.getOrganizationId()), CareCoordinationUtils.setOfNullable(aware.getCommunityId()),
                new HashSet<>(aware.getAssociatedClientIds()), null, AuditLogAction.LOG_OUT, null, null);
    }

    @Override
    public void logResetPassword(String token) {
        if (StringUtils.isEmpty(token)) {
            return;
        }
        var employeeRequest = employeeRequestService.findByToken(token, EmployeeRequestType.RESET_PASSWORD);
        var employee = employeeRequest.getTargetEmployee();
        var auditLog = auditLogFactory.createClientLog(
                Set.of(employee.getOrganizationId()),
                CareCoordinationUtils.setOfNullable(employee.getCommunityId()),
                employee.getId(),
                new HashSet<>(employee.getAssociatedClientIds()),
                AuditLogUtils.getRemoteAddress(),
                AuditLogAction.PASSWORD_RESET,
                null,
                null,
                false);
        auditLogService.save(auditLog);
    }

    @Override
    public void logViewDocuments(ClientDocumentFilter documentFilter) {
        var auditLogSearchFilter = createAuditLogSearchFilter(documentFilter.getTitle(), documentFilter);
        var aware = clientService.findById(documentFilter.getClientId(), IdAndOrganizationIdAndCommunityIdAware.class);
        createLog(
                aware.getOrganizationId(),
                CareCoordinationUtils.setOfNullable(aware.getCommunityId()),
                aware.getId(),
                AuditLogAction.DOCUMENT_VIEW_LISTING,
                auditLogSearchFilter
        );
    }

    @Override
    public void logActivateClient(Long clientId) {
        logClient(clientId, AuditLogAction.CLIENT_ACTIVATE);
    }

    @Override
    public void logDeactivateClient(Long clientId) {
        logClient(clientId, AuditLogAction.CLIENT_DEACTIVATE);
    }

    @Override
    public void logDocumentDownload(Long documentId) {
        logClientDocument(documentId, AuditLogAction.DOCUMENT_DOWNLOAD);
    }

    @Override
    public void logDocumentUpload(Long documentId) {
        logClientDocument(documentId, AuditLogAction.DOCUMENT_UPLOAD);
    }

    @Override
    public void logDocumentEdit(Long documentId) {
        logClientDocument(documentId, AuditLogAction.DOCUMENT_EDIT);
    }

    @Override
    public void logDocumentDelete(Long documentId) {
        logClientDocument(documentId, AuditLogAction.DOCUMENT_DELETE);
    }

    @Override
    public void logDocumentView(Long documentId) {
        logClientDocument(documentId, AuditLogAction.DOCUMENT_VIEW);
    }

    @Override
    public void logCCDGenerateAndView(Long clientId) {
        logClient(clientId, AuditLogAction.CCD_GENERATE_AND_VIEW);
    }

    @Override
    public void logCCDGenerateAndDownload(Long clientId) {
        logClient(clientId, AuditLogAction.CCD_GENERATE_AND_DOWNLOAD);
    }

    @Override
    public void logFacesheetGenerateAndView(Long clientId) {
        logClient(clientId, AuditLogAction.FACESHEET_GENERATE_AND_VIEW);
    }

    @Override
    public void logFacesheetGenerateAndDownload(Long clientId) {
        logClient(clientId, AuditLogAction.FACESHEET_GENERATE_AND_DOWNLOAD);
    }

    @Override
    public void logCreateClient(Long clientId) {
        logClient(clientId, AuditLogAction.CLIENT_CREATE);
    }

    @Override
    public void logUpdateClient(Long clientId) {
        logClient(clientId, AuditLogAction.CLIENT_UPDATE);
    }

    @Override
    public void logViewClients(ClientFilter clientFilter) {
        var auditLogSearchFilter = createAuditLogSearchFilter(null, clientFilter);
        createLog(
                clientFilter.getOrganizationId(),
                clientFilter.getCommunityIds(),
                clientFilter.getClientId(),
                AuditLogAction.CLIENT_VIEW_LISTING,
                auditLogSearchFilter
        );
    }

    @Override
    public void logViewClient(Long clientId) {
        logClient(clientId, AuditLogAction.CLIENT_VIEW);
    }

    private void logClient(Long clientId, AuditLogAction action) {
        var aware = clientService.findById(clientId, OrganizationIdAndCommunityIdAware.class);
        createLog(aware.getOrganizationId(), aware.getCommunityId(), clientId, null, action);
    }

    @Override
    public void logViewMedication(Long medicationId) {
        var aware = clientMedicationService.findById(medicationId, ClientIdClientOrganizationIdClientCommunityIdAware.class);
        var auditLogRelation = new AuditLogMedicationRelation();
        auditLogRelation.setMedicationId(medicationId);
        createLog(
                aware.getClientOrganizationId(),
                aware.getClientCommunityId(),
                aware.getClientId(),
                AuditLogAction.MEDICATION_VIEW,
                auditLogRelation
        );
    }

    @Override
    public void logViewAllergy(Long allergyId) {
        var aware = clientAllergyService.findById(allergyId, ClientIdClientOrganizationIdClientCommunityIdAware.class);
        var auditLogRelation = new AuditLogAllergyRelation();
        auditLogRelation.setAllergyId(allergyId);
        createLog(
                aware.getClientOrganizationId(),
                aware.getClientCommunityId(),
                aware.getClientId(),
                AuditLogAction.ALLERGY_VIEW,
                auditLogRelation
        );
    }

    @Override
    public void logViewProblem(Long problemId) {
        var aware = clientProblemService.findById(problemId, ClientIdClientOrganizationIdClientCommunityIdAware.class);
        var auditLogRelation = new AuditLogProblemRelation();
        auditLogRelation.setProblemId(problemId);
        createLog(
                aware.getClientOrganizationId(),
                aware.getClientCommunityId(),
                aware.getClientId(),
                AuditLogAction.PROBLEM_VIEW,
                auditLogRelation
        );
    }

    @Override
    public void logRequestRide(Long clientId) {
        logClient(clientId, AuditLogAction.RIDE_REQUEST);
    }

    @Override
    public void logRideHistory(Long clientId) {
        logClient(clientId, AuditLogAction.RIDE_HISTORY);
    }

    @Override
    public void logViewServicePlans(ServicePlanFilter servicePlanFilter) {
        var auditLogSearchFilter = createAuditLogSearchFilter(servicePlanFilter.getSearchText(), servicePlanFilter);
        var aware = clientService.findById(servicePlanFilter.getClientId(), OrganizationIdAndCommunityIdAware.class);
        createLog(
                aware.getOrganizationId(),
                Set.of(aware.getCommunityId()),
                servicePlanFilter.getClientId(),
                AuditLogAction.SERVICE_PLAN_VIEW_LISTING,
                auditLogSearchFilter
        );
    }

    @Override
    public void logViewServicePlan(Long servicePlanId) {
        logServicePlan(servicePlanId, AuditLogAction.SERVICE_PLAN_VIEW);
    }

    @Override
    public void logUpdateServicePlan(Long servicePlanId) {
        logServicePlan(servicePlanId, AuditLogAction.SERVICE_PLAN_UPDATE);
    }

    @Override
    public void logDownloadServicePlan(Long servicePlanId) {
        logServicePlan(servicePlanId, AuditLogAction.SERVICE_PLAN_DOWNLOAD);
    }

    @Override
    public void logCreateServicePlan(Long servicePlanId) {
        logServicePlan(servicePlanId, AuditLogAction.SERVICE_PLAN_CREATE);
    }

    private void logServicePlan(Long servicePlanId, AuditLogAction action) {
        var aware = servicePlanService.findById(servicePlanId, ClientIdClientOrganizationIdClientCommunityIdAware.class);
        var auditLogRelation = new AuditLogServicePlanRelation();
        auditLogRelation.setServicePlantId(servicePlanId);
        createLog(aware.getClientOrganizationId(), aware.getClientCommunityId(), aware.getClientId(), action, auditLogRelation);
    }

    @Override
    public void logViewAssessments(Long clientId) {
        logClient(clientId, AuditLogAction.ASSESSMENT_VIEW_LISTING);
    }

    @Override
    public void logViewAssessment(Long assessmentResultId) {
        logAssessment(assessmentResultId, AuditLogAction.ASSESSMENT_VIEW);
    }

    @Override
    public void logUpdateAssessment(Long assessmentResultId) {
        logAssessment(assessmentResultId, AuditLogAction.ASSESSMENT_EDIT);
    }

    @Override
    public void logCreateAssessment(Long assessmentResultId) {
        logAssessment(assessmentResultId, AuditLogAction.ASSESSMENT_CREATE);
    }

    @Override
    public void logDownloadAssessment(Long assessmentResultId) {
        logAssessment(assessmentResultId, AuditLogAction.ASSESSMENT_DOWNLOAD);
    }

    private void logAssessment(Long assessmentResultId, AuditLogAction action) {
        var aware = assessmentResultService.findById(assessmentResultId, ClientIdClientOrganizationIdClientCommunityIdAware.class);
        var auditLogRelation = new AuditLogAssessmentRelation();
        auditLogRelation.setAssessmentResultId(assessmentResultId);
        createLog(
                aware.getClientOrganizationId(),
                aware.getClientCommunityId(),
                aware.getClientId(),
                action,
                auditLogRelation
        );
    }

    @Override
    public void logViewEventsAndNotes(EventNoteFilter eventNoteFilter) {
        if (eventNoteFilter.getClientId() != null) {
            var auditLogSearchFilter = createAuditLogSearchFilter(null, eventNoteFilter);
            createLog(
                    eventNoteFilter.getOrganizationId(),
                    eventNoteFilter.getCommunityIds(),
                    eventNoteFilter.getClientId(),
                    AuditLogAction.EVENT_NOTE_VIEW_LISTING,
                    auditLogSearchFilter
            );
        }
    }

    @Override
    public void logViewEvent(Long eventId) {
        logEvent(eventId, AuditLogAction.EVENT_VIEW);
    }

    @Override
    public void logCreateEvent(Long eventId) {
        logEvent(eventId, AuditLogAction.EVENT_CREATE);
    }

    private void logEvent(Long eventId, AuditLogAction action) {
        var aware = eventService.findById(eventId, ClientIdClientOrganizationIdClientCommunityIdAware.class);
        var auditLogRelation = new AuditLogEventRelation();
        auditLogRelation.setEventId(eventId);
        createLog(
                aware.getClientOrganizationId(),
                aware.getClientCommunityId(),
                aware.getClientId(),
                action,
                auditLogRelation
        );
    }

    @Override
    public void logViewNote(Long noteId) {
        logNote(noteId, AuditLogAction.NOTE_VIEW);
    }

    @Override
    public void logCreateNote(Long noteId) {
        logNote(noteId, AuditLogAction.NOTE_CREATE);
    }

    @Override
    public void logEditNote(Long noteId) {
        logNote(noteId, AuditLogAction.NOTE_EDIT);
    }

    private void logNote(Long noteId, AuditLogAction action) {
        var noteAware = noteService.findById(noteId, NoteClientIdClientIdsTypeAware.class);
        var auditLogRelation = new AuditLogNoteRelation();
        auditLogRelation.setNoteId(noteId);
        if (NoteType.GROUP_NOTE == noteAware.getType()) {
            var aware = clientService.findAllById(noteAware.getNoteClientIds(), OrganizationIdAndCommunityIdAware.class);
            var organizationIds = aware.stream()
                    .map(OrganizationIdAware::getOrganizationId)
                    .collect(Collectors.toSet());
            var communityIds = aware.stream()
                    .map(CommunityIdAware::getCommunityId)
                    .collect(Collectors.toSet());

            createLog(
                    organizationIds,
                    communityIds,
                    new HashSet<>(noteAware.getNoteClientIds()),
                    null,
                    action,
                    auditLogRelation,
                    null
            );
        } else {
            var aware = clientService.findById(noteAware.getClientId(), OrganizationIdAndCommunityIdAware.class);
            createLog(
                    aware.getOrganizationId(),
                    aware.getCommunityId(),
                    noteAware.getClientId(),
                    action,
                    auditLogRelation
            );
        }
    }

    @Override
    public void logViewIncidentReports(IncidentReportFilter incidentReportFilter) {
        var auditLogSearchFilter = createAuditLogSearchFilter(null, incidentReportFilter);
        createLog(
                incidentReportFilter.getOrganizationId(),
                incidentReportFilter.getCommunityIds(),
                incidentReportFilter.getClientId(),
                AuditLogAction.INCIDENT_REPORT_VIEW_LISTING,
                auditLogSearchFilter
        );
    }

    @Override
    public void logViewIncidentReport(Long incidentReportId) {
        logIncidentReport(incidentReportId, AuditLogAction.INCIDENT_REPORT_VIEW);
    }

    @Override
    public void logCreateOrEditIncidentReport(Pair<Long, Boolean> pair) {
        logIncidentReport(
                pair.getFirst(),
                pair.getSecond() ?
                        AuditLogAction.INCIDENT_REPORT_CREATE :
                        AuditLogAction.INCIDENT_REPORT_EDIT
        );
    }

    @Override
    public void logDownloadIncidentReport(Long incidentReportId) {
        logIncidentReport(incidentReportId, AuditLogAction.INCIDENT_REPORT_DOWNLOAD);
    }

    private void logIncidentReport(Long incidentReportId, AuditLogAction action) {
        var aware = incidentReportService.findById(incidentReportId, IncidentEventClientIdClientOrganizationIdClientCommunityIdAware.class);
        var auditLogRelation = new AuditLogIncidentReportRelation();
        auditLogRelation.setIncidentReportId(incidentReportId);
        createLog(
                aware.getEventClientOrganizationId(),
                aware.getEventClientCommunityId(),
                aware.getEventClientId(),
                action,
                auditLogRelation
        );
    }

    @Override
    public void logViewLabOrders(LabResearchOrderFilter labResearchOrderFilter) {
        var auditLogSearchFilter = createAuditLogSearchFilter(null, labResearchOrderFilter);
        createLog(
                labResearchOrderFilter.getOrganizationId(),
                labResearchOrderFilter.getCommunityIds(),
                labResearchOrderFilter.getClientId(),
                AuditLogAction.LAB_ORDER_VIEW_LISTING,
                auditLogSearchFilter
        );
    }

    @Override
    public void logViewLabOrder(Long labResearchOrderId) {
        logLabOrder(labResearchOrderId, AuditLogAction.LAB_ORDER_VIEW);
    }

    @Override
    public void logCreateLabOrder(Long labResearchOrderId) {
        logLabOrder(labResearchOrderId, AuditLogAction.LAB_ORDER_CREATE);
    }

    @Override
    public void logReviewLabOrders(Iterable<Long> labResearchOrderIds) {
        labResearchOrderIds.forEach(id -> logLabOrder(id, AuditLogAction.LAB_ORDER_REVIEW));
    }

    private void logLabOrder(Long labResearchOrderId, AuditLogAction action) {
        var aware = labResearchOrderService.findById(labResearchOrderId, ClientIdClientOrganizationIdClientCommunityIdAware.class);
        var auditLogRelation = new AuditLogLabResearchOrderRelation();
        auditLogRelation.setLabResearchOrderId(labResearchOrderId);
        createLog(
                aware.getClientOrganizationId(),
                aware.getClientCommunityId(),
                aware.getClientId(),
                action,
                auditLogRelation
        );
    }

    @Override
    public void logViewCareTeamMember(Long careTeamMemberId) {
        logCareTeamMember(
                careTeamMemberId,
                AuditLogAction.CLIENT_CARE_TEAM_VIEW,
                null
        );
    }

    @Override
    public void logCreateCareTeamMember(Long careTeamMemberId) {
        logCareTeamMember(
                careTeamMemberId,
                AuditLogAction.CLIENT_CARE_TEAM_CREATE,
                AuditLogAction.COMMUNITY_CARE_TEAM_MEMBER_CREATE
        );
    }

    @Override
    public void logEditCareTeamMember(Long careTeamMemberId) {
        logCareTeamMember(
                careTeamMemberId,
                AuditLogAction.CLIENT_CARE_TEAM_EDIT,
                AuditLogAction.COMMUNITY_CARE_TEAM_MEMBER_EDIT
        );
    }

    @Override
    public void logDeleteCareTeamMember(
            Long careTeamMemberId,
            ClientCareTeamAuditLogDetailsAware clientCtm,
            CommunityCareTeamAuditLogDetailsAware communityCtm
    ) {
        if (clientCtm != null) {
            logClientCareTeamMember(careTeamMemberId, clientCtm, AuditLogAction.CLIENT_CARE_TEAM_DELETE);
        }

        if (communityCtm != null) {
            logCommunityCareTeamMember(careTeamMemberId, communityCtm, AuditLogAction.COMMUNITY_CARE_TEAM_MEMBER_DELETE);
        }
    }

    private void logCareTeamMember(Long careTeamMemberId, AuditLogAction clientAction, AuditLogAction communityAction) {
        clientCareTeamMemberService.findByClientCtmId(careTeamMemberId, ClientCareTeamAuditLogDetailsAware.class)
                .ifPresentOrElse(ctm -> logClientCareTeamMember(careTeamMemberId, ctm, clientAction),
                        () -> {
                            var careTeamMember = communityCareTeamMemberService.findById(careTeamMemberId, CommunityCareTeamAuditLogDetailsAware.class);
                            logCommunityCareTeamMember(careTeamMemberId, careTeamMember, communityAction);
                        });
    }

    private void logCommunityCareTeamMember(Long careTeamMemberId, CommunityCareTeamAuditLogDetailsAware communityCtm, AuditLogAction action) {
        var auditLogRelation = new AuditLogCommunityCareTeamMemberRelation();
        auditLogRelation.setCareTeamMemberId(careTeamMemberId);
        auditLogRelation.setCareTeamMemberFullName(communityCtm.getEmployeeFullName());
        auditLogRelation.setCareTeamMemberCommunityName(communityCtm.getCommunityName());
        var communityId = communityCtm.getCommunityId();
        var aware = communityService.findById(communityId, OrganizationIdAware.class);
        createLog(aware.getOrganizationId(), communityId, null, action, auditLogRelation);
    }

    private void logClientCareTeamMember(Long careTeamMemberId, ClientCareTeamAuditLogDetailsAware clientCtm, AuditLogAction action) {
        var auditLogRelation = new AuditLogClientCareTeamMemberRelation();
        auditLogRelation.setCareTeamMemberFullName(clientCtm.getEmployeeFullName());
        auditLogRelation.setCareTeamMemberId(careTeamMemberId);
        var clientId = clientCtm.getClientId();
        var aware = clientService.findById(clientId, OrganizationIdAndCommunityIdAware.class);
        createLog(aware.getOrganizationId(), aware.getCommunityId(), clientId, action, auditLogRelation);
    }

    @Override
    public void logViewContacts(ContactFilter contactFilter) {
        var auditLogSearchFilter = createAuditLogSearchFilter(null, contactFilter);
        createLog(
                contactFilter.getOrganizationId(),
                contactFilter.getCommunityIds(),
                contactFilter.getClientId(),
                AuditLogAction.CONTACT_VIEW_LISTING,
                auditLogSearchFilter
        );
    }

    @Override
    public void logViewContact(Long contactId) {
        logContact(contactId, AuditLogAction.CONTACT_VIEW);
    }

    @Override
    public void logCreateContact(Long contactId, boolean isAssociatedContact) {
        if (isAssociatedContact) {
            logContact(contactId, AuditLogAction.CREATE_ASSOCIATED_SC_USER);
        } else {
            logContact(contactId, AuditLogAction.CONTACT_CREATE);
        }
    }

    @Override
    public void logEditContact(ContactDto contact) {
        var exStatus = contactService.findById(contact.getId(), EmployeeStatusAware.class).getStatus();
        if (EmployeeStatus.ACTIVE != exStatus && !contact.getEnableContact()) {
            logContact(contact.getId(), AuditLogAction.CONTACT_INACTIVATE);
        } else {
            logContact(contact.getId(), AuditLogAction.CONTACT_EDIT);
        }
    }

    @Override
    public void logReinviteContact(Long contactId) {
        logContact(contactId, AuditLogAction.CONTACT_REINVITE);
    }

    @Override
    public void logAcceptInviteContact(String token) {
        var employeeRequest = employeeRequestService.findByToken(token, EmployeeRequestType.INVITE);
        var targetEmployee = employeeRequest.getTargetEmployee();
        logContact(targetEmployee.getId(), AuditLogAction.CONTACT_INVITE_ACCEPTED);
    }

    private void logContact(Long contactId, AuditLogAction action) {
        var aware = contactService.findById(contactId, OrganizationIdCommunityIdAssociatedClientsAware.class);
        var auditLogRelation = new AuditLogContactRelation();
        auditLogRelation.setContactId(contactId);
        createLog(
                Set.of(aware.getOrganizationId()),
                Set.of(aware.getCommunityId()),
                new HashSet<>(aware.getAssociatedClientIds()),
                null,
                action,
                auditLogRelation,
                null
        );
    }

    @Override
    public void logViewOrganizations(String name) {
        var auditLogSearchFilter = createAuditLogSearchFilter(name, name);
        createLog(null, (Set<Long>) null, null, AuditLogAction.ORGANIZATION_VIEW_LISTING, auditLogSearchFilter);
    }

    @Override
    public void logViewOrganization(Long organizationId) {
        logOrganization(organizationId, AuditLogAction.ORGANIZATION_VIEW);
    }

    @Override
    public void logEditOrganization(Long organizationId) {
        logOrganization(organizationId, AuditLogAction.ORGANIZATION_EDIT);
    }

    @Override
    public void logCreateOrganization(Long organizationId) {
        logOrganization(organizationId, AuditLogAction.ORGANIZATION_CREATE);
    }

    private void logOrganization(Long organizationId, AuditLogAction action) {
        var auditLogRelation = new AuditLogOrganizationRelation();
        auditLogRelation.setOrganizationId(organizationId);
        createLog(organizationId, null, null, action, auditLogRelation);
    }

    @Override
    public void logViewCommunities(Long organizationId) {
        var auditLogSearchFilter = createAuditLogSearchFilter(null, organizationId);
        createLog(organizationId, (Set<Long>) null, null, AuditLogAction.COMMUNITY_VIEW_LISTING, auditLogSearchFilter);
    }

    @Override
    public void logViewCommunity(Long communityId) {
        logCommunity(communityId, AuditLogAction.COMMUNITY_VIEW);
    }

    @Override
    public void logEditCommunity(Long communityId) {
        logCommunity(communityId, AuditLogAction.COMMUNITY_EDIT);
    }

    @Override
    public void logCreateCommunity(Long communityId) {
        logCommunity(communityId, AuditLogAction.COMMUNITY_CREATE);
    }

    private void logCommunity(Long communityId, AuditLogAction action) {
        var auditLogRelation = new AuditLogCommunityRelation();
        auditLogRelation.setCommunityId(communityId);
        var organizationId = communityService.findById(communityId, OrganizationIdAware.class).getOrganizationId();
        createLog(organizationId, communityId, null, action, auditLogRelation);
    }

    @Override
    public void logViewInboundReferrals(ReferralFilter referralFilter) {
        var auditLogSearchFilter = createAuditLogSearchFilter(null, referralFilter);
        createLog(
                referralFilter.getOrganizationId(),
                referralFilter.getCommunityIds(),
                referralFilter.getClientId(),
                AuditLogAction.REFERRAL_INBOUND_VIEW_LISTING,
                auditLogSearchFilter
        );
    }

    @Override
    public void logViewInboundReferral(Long requestId) {
        logReferralRequest(requestId, AuditLogAction.REFERRAL_INBOUND_VIEW);
    }

    @Override
    public void logViewOutboundReferrals(ReferralFilter referralFilter) {
        var auditLogSearchFilter = createAuditLogSearchFilter(null, referralFilter);
        createLog(
                referralFilter.getOrganizationId(),
                referralFilter.getCommunityIds(),
                referralFilter.getClientId(),
                AuditLogAction.REFERRAL_OUTBOUND_VIEW_LISTING,
                auditLogSearchFilter
        );
    }

    @Override
    public void logViewOutboundReferral(Long referralId) {
        logReferral(referralId, AuditLogAction.REFERRAL_OUTBOUND_VIEW);
    }

    @Override
    public void logCreateReferral(Long referralId) {
        logReferral(referralId, AuditLogAction.REFERRAL_CREATE);
    }

    @Override
    public void logCreateReferralFromMarketplace(Long referralId) {
        logReferral(referralId, AuditLogAction.REFERRAL_CREATE_FROM_MARKETPLACE);
    }

    @Override
    public void logCancelReferralRequest(Long referralId) {
        logReferral(referralId, AuditLogAction.REFERRAL_REQUEST_CANCEL);
    }

    @Override
    public void logPreadmitReferralRequest(Long requestId) {
        logReferralRequest(requestId, AuditLogAction.REFERRAL_REQUEST_PRE_ADMIT);
    }

    @Override
    public void logAcceptReferralRequest(Long requestId) {
        logReferralRequest(requestId, AuditLogAction.REFERRAL_REQUEST_ACCEPT);
    }

    @Override
    public void logDeclineReferralRequest(Long requestId) {
        logReferralRequest(requestId, AuditLogAction.REFERRAL_REQUEST_DECLINE);
    }

    @Override
    public void logViewCallHistory(Long employeeId) {
        var aware = employeeService.findById(employeeId, OrganizationIdCommunityIdAssociatedClientsAware.class);
        createLog(
                Set.of(aware.getOrganizationId()),
                Set.of(aware.getCommunityId()),
                new HashSet<>(aware.getAssociatedClientIds()),
                null,
                AuditLogAction.CALL_VIEW_LISTING,
                null,
                null
        );
    }

    @Override
    public void logViewExpenses(Long clientId) {
        logClient(clientId, AuditLogAction.EXPENSE_VIEW_LISTING);
    }

    @Override
    public void logViewExpense(Long expenseId) {
        logExpense(expenseId, AuditLogAction.EXPENSE_VIEW);
    }

    @Override
    public void logCreateExpense(Long expenseId) {
        logExpense(expenseId, AuditLogAction.EXPENSE_CREATE);
    }

    @Override
    public void logViewDocuments(CommunityDocumentFilterDto filter) {
        var documentAndFolderTypeAware = communityDocumentAndFolderService.fetchByIdAndType(
                DocumentAndFolderUtils.toFolderId(filter.getFolderId()),
                DocumentAndFolderType.TEMPLATE_FOLDER,
                DocumentAndFolderTypeAware.class
        );
        if (documentAndFolderTypeAware != null && BooleanUtils.isTrue(DocumentAndFolderType.TEMPLATE_FOLDER.equals(documentAndFolderTypeAware.getType()))) {
            logDocumentsView(filter, AuditLogAction.ESIGN_BUILDER_TEMPLATE_VIEW_LISTING);
        } else {
            logDocumentsView(filter, AuditLogAction.COMPANY_DOCUMENT_VIEW_LISTING);
        }
    }

    @Override
    public void logUploadCompanyDocument(Long documentId) {
        var aware = documentService.findDocumentById(documentId);
        var auditLogCompanyDocumentRelation = new AuditLogCompanyDocumentRelation();
        auditLogCompanyDocumentRelation.setDocuments(List.of(aware));
        createLog(Set.of(aware.getCommunity().getOrganizationId()),
                Set.of(aware.getCommunity().getId()),
                null,
                Set.of(documentId),
                AuditLogAction.COMPANY_DOCUMENT_UPLOAD,
                auditLogCompanyDocumentRelation,
                null);
    }

    @Override
    public void logDownloadCompanyDocuments(List<Long> documentIds) {
        var awares = documentService.findAllById(documentIds, Document.class);
        var organizationIds = awares.stream()
                .map(Document::getCommunity)
                .map(Community::getOrganizationId)
                .collect(Collectors.toSet());
        var communityIds = awares.stream()
                .map(Document::getCommunity)
                .map(Community::getId)
                .collect(Collectors.toSet());
        var auditLogCompanyDocumentRelation = new AuditLogCompanyDocumentRelation();
        auditLogCompanyDocumentRelation.setDocuments(awares);

        createLog(organizationIds,
                communityIds,
                null,
                new HashSet<>(documentIds),
                AuditLogAction.COMPANY_DOCUMENT_DOWNLOAD,
                auditLogCompanyDocumentRelation,
                null);
    }

    @Override
    public void logCreateFolder(Long folderId) {
        logFolder(folderId, AuditLogAction.CREATE_FOLDER);
    }

    @Override
    public void logDeleteFolder(Long folderId) {
        logFolder(folderId, AuditLogAction.DELETE_FOLDER);
    }

    @Override
    public void logEditFolder(Long folderId, IdNameCommunityIdAndCommunityOrganizationIdAware aware, String newFolderName) {
        var auditLogRelation = new AuditLogDocumentFolderRelation();
        auditLogRelation.setFolderId(folderId);
        auditLogRelation.setFolderName(newFolderName);
        auditLogRelation.setOldFolderName(aware.getName());
        createLog(
                aware.getCommunityOrganizationId(),
                aware.getCommunityId(),
                null,
                AuditLogAction.EDIT_FOLDER,
                auditLogRelation
        );
    }

    @Override
    public void logViewMarketplaceCommunityDetails(Long communityId) {
        logMarketplace(communityId, AuditLogAction.MARKETPLACE_VIEW_COMMUNITY_DETAILS);
    }

    @Override
    public void logViewMarketplacePartnerProviders(Long communityId) {
        logMarketplace(communityId, AuditLogAction.MARKETPLACE_VIEW_PARTNER_PROVIDERS);
    }

    @Override
    public void logViewMarketPlace(MarketplaceFilter marketplaceFilter) {
        var auditLogSearchFilter = createAuditLogSearchFilter(null, marketplaceFilter);
        createLog(
                null,
                null,
                null,
                null,
                AuditLogAction.MARKETPLACE_VIEW,
                null,
                auditLogSearchFilter
        );
    }

    @Override
    public void logCreateTemplate(Long templateId) {
        logSignatureTemplate(templateId, AuditLogAction.ESIGN_BUILDER_TEMPLATE_CREATE);
    }

    @Override
    public void logUpdateTemplate(Long templateId) {
        logSignatureTemplate(templateId, AuditLogAction.ESIGN_BUILDER_TEMPLATE_EDIT);
    }

    @Override
    public void logDeleteTemplate(Long templateId) {
        logSignatureTemplate(templateId, AuditLogAction.ESIGN_BUILDER_TEMPLATE_DELETE);
    }

    @Override
    public void logViewAppointments(ClientAppointmentFilter filter) {
        var auditLogSearchFilter = createAuditLogSearchFilter(null, filter);
        createLog(
                Set.of(filter.getOrganizationId()),
                filter.getCommunityIds(),
                filter.getClientIds(),
                null,
                AuditLogAction.APPOINTMENT_VIEW_LISTING,
                null,
                auditLogSearchFilter
        );
    }

    @Override
    public void logViewAppointment(Long appointmentId) {
        logAppointment(appointmentId, AuditLogAction.APPOINTMENT_VIEW);
    }

    @Override
    public void logCreateAppointment(Long appointmentId) {
        logAppointment(appointmentId, AuditLogAction.APPOINTMENT_CREATE);
    }

    @Override
    public void logUpdateAppointment(Long appointmentId) {
        logAppointment(appointmentId, AuditLogAction.APPOINTMENT_EDIT);
    }

    @Override
    public void logCancelAppointment(Long appointmentId) {
        logAppointment(appointmentId, AuditLogAction.APPOINTMENT_CANCEL);
    }

    @Override
    public void logExportAppointments(ClientAppointmentFilter filter) {
        var auditLogSearchFilter = createAuditLogSearchFilter(null, filter);
        createLog(
                Set.of(filter.getOrganizationId()),
                filter.getCommunityIds(),
                filter.getClientIds(),
                null,
                AuditLogAction.APPOINTMENT_EXPORT,
                null,
                auditLogSearchFilter
        );
    }

    @Override
    public void logRecordSearch(ClientRecordSearchFilter filter) {
        var auditLogSearchFilter = createAuditLogSearchFilter(null, filter);
        createLog(
                null,
                null,
                null,
                null,
                AuditLogAction.RECORD_SEARCH,
                null,
                auditLogSearchFilter
        );
    }

    @Override
    public void logExportReport(ReportFilterDto reportFilter) {
        var auditLogSearchFilter = createAuditLogSearchFilter(null, reportFilter);
        var organizationId = communityService.findAllById(reportFilter.getCommunityIds(), OrganizationIdAware.class)
                .stream().findFirst()
                .map(OrganizationIdAware::getOrganizationId)
                .orElse(null);

        var auditLogReportRelation = new AuditLogReportRelation();
        auditLogReportRelation.setReportType(reportFilter.getReportType());
        createLog(
                Set.of(organizationId),
                new HashSet<>(reportFilter.getCommunityIds()),
                null,
                null,
                AuditLogAction.REPORT_EXPORT,
                auditLogReportRelation,
                auditLogSearchFilter
        );
    }

    @Override
    public void logViewChats(List<String> conversationSids) {
        createLog(
                null,
                null,
                null,
                null,
                AuditLogAction.CHAT_VIEW_LISTING
        );
    }

    @Override
    public void logCreateChat(String conversationSid) {
        logChatAndCall(conversationSid, AuditLogAction.CHAT_CREATE);
    }

    @Override
    public void logUpdateChatParticipants(EditConversationDto editConversationDto) {
        logChatAndCall(editConversationDto.getConversationSid(), AuditLogAction.CHAT_PARTICIPANT_UPDATE);
    }

    @Override
    public void logStartCall(InitiateCallDto initiateCallDto) {
        logChatAndCall(initiateCallDto.getConversationSid(), AuditLogAction.CALL_START);
    }

    @Override
    public void logSendSignatureRequest(List<Long> signatureRequestIds) {
        if (!CollectionUtils.isEmpty(signatureRequestIds)) {
            logSignatureRequests(signatureRequestIds, AuditLogAction.SIGNATURE_REQUEST_SUBMIT);
        }
    }

    @Override
    public void logCancelSignatureRequest(Long requestId) {
        logSignatureRequest(requestId, AuditLogAction.SIGNATURE_REQUEST_CANCEL);
    }

    @Override
    public void logRenewSignatureRequest(Long requestId) {
        logSignatureRequest(requestId, AuditLogAction.SIGNATURE_REQUEST_RESUBMIT);
    }

    @Override
    public void logResendPIN(Long requestId) {
        logSignatureRequest(requestId, AuditLogAction.PIN_RESEND);
    }

    @Override
    public void logSendBulkSignatureRequest(Long bulkSignatureRequestId) {
        logSignatureBulkRequest(bulkSignatureRequestId, AuditLogAction.SIGNATURE_BULK_REQUEST_SUBMIT);
    }

    @Override
    public void logCancelSignatureBulkRequest(Long bulkSignatureRequestId) {
        logSignatureBulkRequest(bulkSignatureRequestId, AuditLogAction.SIGNATURE_BULK_REQUEST_CANCEL);
    }

    @Override
    public void logRenewSignatureBulkRequest(Long bulkSignatureRequestId) {
        logSignatureBulkRequest(bulkSignatureRequestId, AuditLogAction.SIGNATURE_BULK_REQUEST_RESUBMIT);
    }

    @Override
    public void logSignDocument(Long requestId) {
        var requestAware = documentSignatureRequestService.findById(requestId, SignatureRequestTemplateNameEmployeeIdAware.class);
        var auditLogRelation = new AuditLogSignatureRequestRelation();
        auditLogRelation.setSignatureRequestIds(List.of(requestId));
        createLog(
                Set.of(requestAware.getClientOrganizationId()),
                Set.of(requestAware.getClientCommunityId()),
                Set.of(requestAware.getClientId()),
                null,
                AuditLogAction.DOCUMENT_SIGN,
                auditLogRelation,
                null,
                requestAware.getRequestedById()
        );
    }

    @Override
    public void logViewUserManuals(List<UserManualDocumentDto> manuals) {
        logHelp(AuditLogAction.USER_MANUAL_VIEW_LISTING, null);
    }

    @Override
    public void logDownloadUserManual(Long manualId) {
        logUserManual(manualId, AuditLogAction.USER_MANUAL_DOWNLOAD);
    }

    @Override
    public void logUploadUserManual(Long manualId) {
        logUserManual(manualId, AuditLogAction.USER_MANUAL_UPLOAD);
    }

    @Override
    public void logViewReleaseNotes(List<ReleaseNoteListItemDto> releaseNotes) {
        logHelp(AuditLogAction.RELEASE_NOTE_VIEW_LISTING, null);
    }

    @Override
    public void logDownloadReleaseNote(Long releaseNoteId) {
        logReleaseNote(releaseNoteId, AuditLogAction.RELEASE_NOTE_DOWNLOAD);
    }

    @Override
    public void logUploadReleaseNote(Long releaseNoteId) {
        logReleaseNote(releaseNoteId, AuditLogAction.RELEASE_NOTE_UPLOAD);
    }

    @Override
    public void logCreateSupportTicket(Long supportTicketId) {
        var auditLogSupportTicketRelation = new AuditLogSupportTicketRelation();
        auditLogSupportTicketRelation.setSupportTicketId(supportTicketId);
        logHelp(AuditLogAction.SUPPORT_TICKET_CREATE, auditLogSupportTicketRelation);
    }

    @Override
    public void logCreateProspect(Long prospectId) {
        logProspect(prospectId, AuditLogAction.PROSPECT_CREATE);
    }

    @Override
    public void logUpdateProspect(Long prospectId) {
        logProspect(prospectId, AuditLogAction.PROSPECT_UPDATE);
    }

    @Override
    public void logDeactivateProspect(Long prospectId) {
        logProspect(prospectId, AuditLogAction.PROSPECT_DEACTIVATE);
    }

    @Override
    public void logActivateProspect(Long prospectId) {
        logProspect(prospectId, AuditLogAction.PROSPECT_ACTIVATE);
    }

    @Override
    public void logViewProspects(ProspectFilterDto filter) {
        var auditLogSearchFilter = createAuditLogSearchFilter(null, filter);
        createProspectLog(
                filter.getOrganizationId(),
                filter.getCommunityIds(),
                null,
                AuditLogAction.PROSPECT_VIEW_LISTING,
                auditLogSearchFilter
        );
    }

    @Override
    public void logViewProspect(Long prospectId) {
        logProspect(prospectId, AuditLogAction.PROSPECT_VIEW);
    }

    private void createProspectLog(Long organizationId, List<Long> communityIds, Long prospectId, AuditLogAction action, AuditLogSearchFilter filter) {
        createProspectLog(organizationId != null ? Set.of(organizationId) : null,
                communityIds != null ? new HashSet<>(communityIds) : null,
                prospectId != null ? Set.of(prospectId) : null,
                null,
                action,
                null,
                filter
        );
    }


    private void logProspect(Long prospectId, AuditLogAction action) {
        var aware = prospectService.findById(prospectId, OrganizationIdAndCommunityIdAware.class);
        createProspectLog(aware.getOrganizationId(), aware.getCommunityId(), prospectId, null, action);
    }

    private void logClientDocument(Long documentId, AuditLogAction action) {
        var aware = clientDocumentService.findById(documentId, DocumentTitleClientIdClientOrganizationIdClientCommunityIdAware.class);
        var auditLogClientDocumentRelation = new AuditLogClientDocumentRelation();
        auditLogClientDocumentRelation.setDocumentId(documentId);
        auditLogClientDocumentRelation.setDocumentTitle(aware.getDocumentTitle());
        createLog(
                Set.of(aware.getClientOrganizationId()),
                Set.of(aware.getClientCommunityId()),
                Set.of(aware.getClientId()),
                Set.of(documentId),
                action,
                auditLogClientDocumentRelation,
                null
        );
    }

    private void logMarketplace(Long communityId, AuditLogAction action) {
        var aware = communityService.findById(communityId, OrganizationIdAware.class);
        var auditLogRelation = new AuditLogMarketplaceRelation();
        auditLogRelation.setMarketplaceCommunityId(communityId);
        createLog(
                aware.getOrganizationId(),
                communityId,
                null,
                action,
                auditLogRelation
        );
    }

    private void logChatAndCall(String conversationSid, AuditLogAction action) {
        var auditLogChatRelation = new AuditLogChatRelation();
        auditLogChatRelation.setConversationSid(conversationSid);
        createLog(
                null,
                null,
                null,
                action,
                auditLogChatRelation
        );
    }

    private void logReleaseNote(Long releaseNoteId, AuditLogAction action) {
        var aware = releaseNoteService.findById(releaseNoteId, TitleAware.class);
        var auditLogReleaseNoteRelation = new AuditLogReleaseNoteRelation();
        auditLogReleaseNoteRelation.setReleaseNoteId(releaseNoteId);
        auditLogReleaseNoteRelation.setReleaseNoteTitle(aware.getTitle());
        logHelp(action, auditLogReleaseNoteRelation);
    }

    private void logUserManual(Long manualId, AuditLogAction action) {
        var aware = userManualService.findById(manualId, TitleAware.class);
        var auditLogUserManualRelation = new AuditLogUserManualRelation();
        auditLogUserManualRelation.setUserManualId(manualId);
        auditLogUserManualRelation.setUserManualTitle(aware.getTitle());
        logHelp(action, auditLogUserManualRelation);
    }

    private void logHelp(AuditLogAction action, AuditLogRelation auditLogRelation) {
        var employee = loggedUserService.getCurrentEmployee();

        createLog(Set.of(employee.getOrganizationId()),
                Set.of(employee.getCommunityId()),
                new HashSet<>(employee.getAssociatedClientIds()),
                null,
                action,
                auditLogRelation,
                null);
    }

    private void logSignatureRequest(Long requestId, AuditLogAction action) {
        var requestAware = documentSignatureRequestService.findById(requestId, SignatureRequestTemplateNameAware.class);
        var auditLogRelation = new AuditLogSignatureRequestRelation();
        auditLogRelation.setSignatureRequestIds(List.of(requestId));
        createLog(
                Set.of(requestAware.getClientOrganizationId()),
                Set.of(requestAware.getClientCommunityId()),
                Set.of(requestAware.getClientId()),
                null,
                action,
                auditLogRelation,
                null
        );
    }

    private void logSignatureRequests(List<Long> signatureRequestIds, AuditLogAction action) {
        var signatureRequestAwares = documentSignatureRequestService.findAllById(signatureRequestIds, SignatureRequestTemplateNameAware.class);

        var auditLogRelation = new AuditLogSignatureRequestRelation();
        auditLogRelation.setSignatureRequestIds(signatureRequestIds);

        var clientIds = signatureRequestAwares.stream()
                .map(SignatureRequestTemplateNameAware::getClientId)
                .collect(Collectors.toSet());

        var clientOrganizationIds = signatureRequestAwares.stream()
                .map(SignatureRequestTemplateNameAware::getClientOrganizationId)
                .collect(Collectors.toSet());

        var clientCommunityIds = signatureRequestAwares.stream()
                .map(SignatureRequestTemplateNameAware::getClientCommunityId)
                .collect(Collectors.toSet());

        createLog(
                clientOrganizationIds,
                clientCommunityIds,
                clientIds,
                null,
                action,
                auditLogRelation,
                null
        );
    }

    private void logSignatureBulkRequest(Long bulkSignatureRequestId, AuditLogAction action) {
        var signatureRequestAwares = documentSignatureRequestService.findAllByBulkRequestId(bulkSignatureRequestId, SignatureRequestTemplateNameAware.class);
        var auditLogRelation = new AuditLogSignatureBulkRequestRelation();
        auditLogRelation.setSignatureBulkRequestId(bulkSignatureRequestId);

        var clientIds = signatureRequestAwares.stream()
                .map(SignatureRequestTemplateNameAware::getClientId)
                .collect(Collectors.toSet());

        var clientOrganizationIds = signatureRequestAwares.stream()
                .map(SignatureRequestTemplateNameAware::getClientOrganizationId)
                .collect(Collectors.toSet());

        var clientCommunityIds = signatureRequestAwares.stream()
                .map(SignatureRequestTemplateNameAware::getClientCommunityId)
                .collect(Collectors.toSet());

        createLog(
                clientOrganizationIds,
                clientCommunityIds,
                clientIds,
                null,
                action,
                auditLogRelation,
                null
        );
    }

    private void logAppointment(Long appointmentId, AuditLogAction action) {
        var aware = clientAppointmentService.findById(appointmentId, ClientIdClientOrganizationIdClientCommunityIdAware.class);

        var auditLogAppointmentRelation = new AuditLogAppointmentRelation();
        auditLogAppointmentRelation.setAppointmentId(appointmentId);
        createLog(
                aware.getClientOrganizationId(),
                aware.getClientCommunityId(),
                aware.getClientId(),
                action,
                auditLogAppointmentRelation
        );
    }

    private void logDocumentsView(CommunityDocumentFilterDto filter, AuditLogAction esignBuilderTemplateViewListing) {
        var auditLogSearchFilter = createAuditLogSearchFilter(filter.getTitle(), filter);
        var aware = communityService.findById(filter.getCommunityId(), IdAndOrganizationIdAware.class);
        createLog(
                aware.getOrganizationId(),
                Set.of(aware.getId()),
                null,
                esignBuilderTemplateViewListing,
                auditLogSearchFilter
        );
    }

    private void logSignatureTemplate(Long templateId, AuditLogAction action) {
        var aware = documentSignatureTemplateService.findById(templateId, DocumentSignatureTemplateAuditLogDetailsAware.class);

        if (BooleanUtils.isTrue(aware.getIsManuallyCreated())) {
            var auditLogRelation = new AuditLogSignatureTemplateRelation();
            auditLogRelation.setSignatureTemplateId(templateId);
            auditLogRelation.setSignatureTemplateName(aware.getTitle());

            createLog(
                    aware.getOrganizationIds(),
                    aware.getCommunityIds(),
                    null,
                    null,
                    action,
                    auditLogRelation,
                    null
            );
        }
    }

    private void logFolder(Long folderId, AuditLogAction action) {
        var aware = documentFolderService.findById(folderId, IdNameCommunityIdAndCommunityOrganizationIdAware.class);
        var auditLogRelation = new AuditLogDocumentFolderRelation();
        auditLogRelation.setFolderId(folderId);
        auditLogRelation.setFolderName(aware.getName());
        createLog(
                aware.getCommunityOrganizationId(),
                aware.getCommunityId(),
                null,
                action,
                auditLogRelation
        );
    }

    private void logExpense(Long expenseId, AuditLogAction action) {
        var aware = clientExpenseService.findById(expenseId, ClientIdClientOrganizationIdClientCommunityIdAware.class);
        var auditLogRelation = new AuditLogExpenseRelation();
        auditLogRelation.setExpenseId(expenseId);
        aware.ifPresent(clientExpense ->
                createLog(
                        clientExpense.getClientOrganizationId(),
                        clientExpense.getClientCommunityId(),
                        clientExpense.getClientId(),
                        action,
                        auditLogRelation
                ));

    }

    private void logReferral(Long referralId, AuditLogAction action) {
        var aware = referralService.findReferralById(referralId, ClientIdClientOrganizationIdClientCommunityIdAware.class);
        var auditLogRelation = new AuditLogReferralRelation();
        auditLogRelation.setReferralId(referralId);
        createLog(
                aware.getClientOrganizationId(),
                aware.getClientCommunityId(),
                aware.getClientId(),
                action,
                auditLogRelation
        );
    }

    private void logReferralRequest(Long requestId, AuditLogAction action) {
        var aware = referralService.findRequestById(
                requestId, ReferralClientIdClientOrganizationIdClientCommunityIdAware.class);
        var auditLogRelation = new AuditLogReferralRequestRelation();
        auditLogRelation.setReferralRequestId(requestId);
        createLog(
                aware.getReferralClientOrganizationId(),
                aware.getReferralClientCommunityId(),
                aware.getReferralClientId(),
                action,
                auditLogRelation
        );
    }

    private void createLog(Long organizationId, Long communityId, Long clientId, Set<Long> documentIds, AuditLogAction action) {
        createLog(organizationId != null ? Set.of(organizationId) : null,
                communityId != null ? Set.of(communityId) : null,
                clientId != null ? Set.of(clientId) : null,
                documentIds,
                action,
                null,
                null
        );
    }

    private void createProspectLog(Long organizationId, Long communityId, Long prospectId, Set<Long> documentIds, AuditLogAction action) {
        createProspectLog(organizationId != null ? Set.of(organizationId) : null,
                communityId != null ? Set.of(communityId) : null,
                prospectId != null ? Set.of(prospectId) : null,
                documentIds,
                action,
                null,
                null
        );
    }

    //todo switch to set in filters
    private void createLog(Long organizationId, List<Long> communityIds, Long clientId, AuditLogAction action, AuditLogSearchFilter auditLogSearchFilter) {
        createLog(organizationId != null ? Set.of(organizationId) : null,
                communityIds != null ? new HashSet<>(communityIds) : null,
                clientId != null ? Set.of(clientId) : null,
                null,
                action,
                null,
                auditLogSearchFilter
        );
    }

    private void createLog(Long organizationId, Set<Long> communityIds, Long clientId, AuditLogAction action, AuditLogSearchFilter auditLogSearchFilter) {
        createLog(organizationId != null ? Set.of(organizationId) : null,
                communityIds,
                clientId != null ? Set.of(clientId) : null,
                null,
                action,
                null,
                auditLogSearchFilter
        );
    }

    private void createLog(Long organizationId, Long communityId, Long clientId, AuditLogAction action, AuditLogRelation auditLogRelation) {
        createLog(organizationId != null ? Set.of(organizationId) : null,
                communityId != null ? Set.of(communityId) : null,
                clientId != null ? Set.of(clientId) : null,
                null,
                action,
                auditLogRelation,
                null
        );
    }

    private void createLog(Set<Long> organizationIds, Set<Long> communityIds, Set<Long> clientIds, Set<Long> documentIds, AuditLogAction action, AuditLogRelation auditLogRelation, AuditLogSearchFilter auditLogSearchFilter) {
        var employeeId = loggedUserService.getCurrentEmployeeId();
        createLog(organizationIds, communityIds, clientIds, documentIds, action, auditLogRelation, auditLogSearchFilter, employeeId);
    }

    private void createProspectLog(Set<Long> organizationIds, Set<Long> communityIds, Set<Long> prospectIds, Set<Long> documentIds, AuditLogAction action, AuditLogRelation auditLogRelation, AuditLogSearchFilter auditLogSearchFilter) {
        var employeeId = loggedUserService.getCurrentEmployeeId();
        createProspectLog(organizationIds, communityIds, prospectIds, documentIds, action, auditLogRelation, auditLogSearchFilter, employeeId);
    }

    private void createLog(Set<Long> organizationIds, Set<Long> communityIds, Set<Long> clientIds, Set<Long> documentIds, AuditLogAction action, AuditLogRelation auditLogRelation, AuditLogSearchFilter auditLogSearchFilter, Long employeeId) {
        AuditLog auditLog;
        if (CollectionUtils.isEmpty(documentIds)) {
            auditLog = auditLogFactory.createClientLog(
                    organizationIds,
                    communityIds,
                    employeeId,
                    clientIds,
                    AuditLogUtils.getRemoteAddress(),
                    action,
                    auditLogRelation,
                    auditLogSearchFilter,
                    false
            );
        } else {
            auditLog = auditLogFactory.createDocumentsLog(
                    organizationIds,
                    communityIds,
                    employeeId,
                    clientIds,
                    documentIds,
                    AuditLogUtils.getRemoteAddress(),
                    action,
                    auditLogRelation
            );
        }
        auditLogService.save(auditLog);
    }

    private void createProspectLog(Set<Long> organizationIds, Set<Long> communityIds, Set<Long> prospectIds, Set<Long> documentIds, AuditLogAction action, AuditLogRelation auditLogRelation, AuditLogSearchFilter auditLogSearchFilter, Long employeeId) {
        var auditLog = auditLogFactory.createProspectLog(
                organizationIds,
                communityIds,
                employeeId,
                prospectIds,
                AuditLogUtils.getRemoteAddress(),
                action,
                auditLogRelation,
                auditLogSearchFilter,
                false
        );
        //TODO add prospect documents log creation
        auditLogService.save(auditLog);
    }

    private AuditLogSearchFilter createAuditLogSearchFilter(String searchValue, Object filter) {
        var auditLogSearchFilter = new AuditLogSearchFilter();
        auditLogSearchFilter.setSearchValue(searchValue);
        try {
            auditLogSearchFilter.setJson(objectMapper.writeValueAsString(filter));
        } catch (JsonProcessingException e) {
            logger.error("Error converting search filter to json : {}", filter, e);
        }
        return auditLogSearchFilter;
    }

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION, fallbackExecution = true)
    public <T> void processPublishedEvent(AuditLogPublishedEvent<T> event) {
        event.getConsumer().accept(event.getParam());
    }
}
