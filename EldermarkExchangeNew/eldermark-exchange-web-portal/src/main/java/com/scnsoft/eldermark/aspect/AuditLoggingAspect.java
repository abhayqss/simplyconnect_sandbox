package com.scnsoft.eldermark.aspect;

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
import com.scnsoft.eldermark.beans.projection.ClientCareTeamAuditLogDetailsAware;
import com.scnsoft.eldermark.beans.projection.CommunityCareTeamAuditLogDetailsAware;
import com.scnsoft.eldermark.dto.ContactDto;
import com.scnsoft.eldermark.dto.IncidentReportDto;
import com.scnsoft.eldermark.dto.ReleaseNoteListItemDto;
import com.scnsoft.eldermark.dto.UserManualDocumentDto;
import com.scnsoft.eldermark.dto.conversation.EditConversationDto;
import com.scnsoft.eldermark.dto.conversation.call.InitiateCallDto;
import com.scnsoft.eldermark.dto.document.CommunityDocumentFilterDto;
import com.scnsoft.eldermark.dto.document.folder.DocumentFolderDto;
import com.scnsoft.eldermark.dto.filter.ClientDocumentFilter;
import com.scnsoft.eldermark.dto.lab.LabResearchReviewDto;
import com.scnsoft.eldermark.dto.password.PasswordResetDto;
import com.scnsoft.eldermark.dto.prospect.ProspectFilterDto;
import com.scnsoft.eldermark.dto.referral.ReferralDto;
import com.scnsoft.eldermark.dto.report.ReportFilterDto;
import com.scnsoft.eldermark.dto.security.LoginDto;
import com.scnsoft.eldermark.dto.signature.SubmitTemplateSignatureRequestsDto;
import com.scnsoft.eldermark.entity.audit.event.AuditLogPublishedEvent;
import com.scnsoft.eldermark.entity.document.folder.IdNameCommunityIdAndCommunityOrganizationIdAware;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestPdcFlowCallbackLog;
import com.scnsoft.eldermark.facade.AuditLogFacade;
import com.scnsoft.eldermark.security.JwtTokenFacade;
import com.scnsoft.eldermark.service.ClientCareTeamMemberService;
import com.scnsoft.eldermark.service.CommunityCareTeamMemberService;
import com.scnsoft.eldermark.service.document.folder.DocumentFolderService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Aspect
public class AuditLoggingAspect {

    @Autowired
    private AuditLogFacade auditLogFacade;

    @Autowired
    private JwtTokenFacade jwtTokenFacade;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private DocumentFolderService documentFolderService;

    @Autowired
    private ClientCareTeamMemberService clientCareTeamMemberService;

    @Autowired
    private CommunityCareTeamMemberService communityCareTeamMemberService;

    @Pointcut(value = "execution(* com.scnsoft.eldermark.web.controller.AuthController.login(com.scnsoft.eldermark.dto.security.LoginDto," +
            "javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)) && " +
            "args(loginDto,request,response)", argNames = "loginDto,request,response")
    public void loginMethod(LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
    }

    @AfterReturning(value = "loginMethod(loginDto,request,response)", argNames = "loginDto,request,response")
    public void logLogin(LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        //todo create constructor with runnable
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(1L, address -> auditLogFacade.logLogin()));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.web.controller.AuthController.logout(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)) &&" +
            " args(request,response)", argNames = "request,response")
    public void logoutMethod(HttpServletRequest request, HttpServletResponse response) {
    }

    @AfterReturning(value = "logoutMethod(request,response)", argNames = "request,response")
    public void logLogout(HttpServletRequest request, HttpServletResponse response) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(jwtTokenFacade.getJwtFromRequest(request), token -> auditLogFacade.logLogout(token)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.AuthFacade.resetPassword(..)) && args(dto)")
    public void resetPassword(PasswordResetDto dto) {
    }

    @Before(value = "resetPassword(dto)", argNames = "dto")
    public void logResetPassword(PasswordResetDto dto) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(dto.getToken(), token -> auditLogFacade.logResetPassword(token)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.ClientFacade.activateClient(..)) && args(clientId, ..)")
    public void activateClient(Long clientId) {
    }

    @AfterReturning(value = "activateClient(clientId)", argNames = "clientId")
    public void logActivateClient(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logActivateClient(clientId)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.ClientFacade.deactivateClient(..)) && args(clientId, ..)")
    public void deactivateClient(Long clientId) {
    }

    @AfterReturning(value = "deactivateClient(clientId)", argNames = "clientId")
    public void logDeactivateClient(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logDeactivateClient(clientId)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientDocumentFacade.downloadCcd(..)) && args(clientId, ..)")
    public void downloadCcd(Long clientId) {
    }

    @AfterReturning(value = "downloadCcd(clientId)", argNames = "clientId")
    public void logDownloadCcd(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logCCDGenerateAndDownload(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientDocumentFacade.downloadFacesheet(..)) && args(clientId, ..)")
    public void downloadFacesheet(Long clientId) {
    }

    @AfterReturning(value = "downloadFacesheet(clientId)", argNames = "clientId")
    public void logDownloadFacesheet(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logFacesheetGenerateAndDownload(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientDocumentFacade.downloadFacesheet(..)) && args(clientId, ..)")
    public void viewFacesheet(Long clientId) {
    }

    @AfterReturning(value = "viewFacesheet(clientId)", argNames = "clientId")
    public void logViewFacesheet(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logFacesheetGenerateAndView(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientDocumentFacade.clientCcdToHtml(..)) && args(clientId, ..)")
    public void viewCcd(Long clientId) {
    }

    @AfterReturning(value = "viewCcd(clientId)", argNames = "clientId")
    public void logViewCcd(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logCCDGenerateAndView(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientDocumentFacade.find(..)) && args(documentFilter, ..)")
    public void viewClientDocuments(ClientDocumentFilter documentFilter) {
    }

    @AfterReturning(pointcut = "viewClientDocuments(documentFilter)", argNames = "documentFilter")
    public void logViewDocuments(ClientDocumentFilter documentFilter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(documentFilter, filter -> auditLogFacade.logViewDocuments(filter)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientDocumentFacade.save(..))")
    public void uploadDocument() {
    }

    @AfterReturning(pointcut = "uploadDocument()", returning = "documentId")
    public void logUploadDocument(Long documentId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(documentId, id -> auditLogFacade.logDocumentUpload(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientDocumentFacade.download(..)) && args(documentId, ..)")
    public void downloadDocument(Long documentId) {
    }

    @AfterReturning(value = "downloadDocument(documentId)", argNames = "documentId")
    public void logDownloadDocument(Long documentId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(documentId, id -> auditLogFacade.logDocumentDownload(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientDocumentFacade.findById(..)) && args(documentId)")
    public void viewDocument(Long documentId) {
    }

    @AfterReturning(value = "viewDocument(documentId)", argNames = "documentId")
    public void logViewDocument(Long documentId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(documentId, id -> auditLogFacade.logDocumentView(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientDocumentFacade.edit(..))")
    public void documentEdit() {
    }

    @AfterReturning(value = "documentEdit()", returning = "documentId")
    public void logDocumentEdit(Long documentId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(documentId, id -> auditLogFacade.logDocumentEdit(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientDocumentFacade.deleteById(..)) && args(documentId, ..)")
    public void deleteDocument(Long documentId) {
    }

    @AfterReturning(value = "deleteDocument(documentId)", argNames = "documentId")
    public void logDocumentDelete(Long documentId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(documentId, id -> auditLogFacade.logDocumentDelete(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientFacade.add(..))")
    public void createClient() {
    }

    @AfterReturning(pointcut = "createClient()", returning = "clientId")
    public void logCreateClient(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logCreateClient(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientFacade.edit(..))")
    public void updateClient() {
    }

    @AfterReturning(pointcut = "updateClient()", returning = "clientId")
    public void logUpdateClient(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logUpdateClient(id)));
    }


    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientFacade.find(..)) && args(clientFilter, ..)")
    public void viewClients(ClientFilter clientFilter) {
    }

    @AfterReturning(pointcut = "viewClients(clientFilter)", argNames = "clientFilter")
    public void logViewClients(ClientFilter clientFilter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientFilter, filter -> auditLogFacade.logViewClients(filter)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientFacade.findById(..)) && args(clientId)")
    public void viewClient(Long clientId) {
    }

    @AfterReturning(value = "viewClient(clientId)", argNames = "clientId")
    public void logViewClient(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logViewClient(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientMedicationFacade.findById(..)) && args(medicationId)")
    public void viewMedication(Long medicationId) {
    }

    @AfterReturning(pointcut = "viewMedication(medicationId)", argNames = "medicationId")
    public void logViewMedication(Long medicationId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(medicationId, id -> auditLogFacade.logViewMedication(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientAllergyFacade.findById(..)) && args(allergyId)")
    public void viewAllergy(Long allergyId) {
    }

    @AfterReturning(pointcut = "viewAllergy(allergyId)", argNames = "allergyId")
    public void logViewAllergy(Long allergyId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(allergyId, id -> auditLogFacade.logViewAllergy(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientProblemFacade.findById(..)) && args(problemId)")
    public void viewProblem(Long problemId) {
    }

    @AfterReturning(pointcut = "viewProblem(problemId)", argNames = "problemId")
    public void logViewProblem(Long problemId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(problemId, id -> auditLogFacade.logViewProblem(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.TransportationFacade.requestNewRide(..)) && args(clientId)")
    public void requestRide(Long clientId) {
    }

    @AfterReturning(value = "requestRide(clientId)", argNames = "clientId")
    public void logRequestRide(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logRequestRide(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.TransportationFacade.rideHistory(..)) && args(clientId)")
    public void rideHistory(Long clientId) {
    }

    @AfterReturning(value = "rideHistory(clientId)", argNames = "clientId")
    public void logRideHistory(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logRideHistory(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientServicePlanFacade.find(..)) && args(servicePlanFilter, ..)")
    public void viewServicePlans(ServicePlanFilter servicePlanFilter) {
    }

    @AfterReturning(pointcut = "viewServicePlans(servicePlanFilter)", argNames = "servicePlanFilter")
    public void logViewServicePlans(ServicePlanFilter servicePlanFilter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(servicePlanFilter, filter -> auditLogFacade.logViewServicePlans(filter)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientServicePlanFacade.findById(..)) && args(servicePlanId)")
    public void viewServicePlan(Long servicePlanId) {
    }

    @AfterReturning(pointcut = "viewServicePlan(servicePlanId)", argNames = "servicePlanId")
    public void logViewServicePlan(Long servicePlanId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(servicePlanId, id -> auditLogFacade.logViewServicePlan(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientServicePlanFacade.edit(..))")
    public void updateServicePlan() {
    }

    @AfterReturning(pointcut = "updateServicePlan()", returning = "servicePlanId")
    public void logUpdateServicePlan(Long servicePlanId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(servicePlanId, id -> auditLogFacade.logUpdateServicePlan(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientServicePlanFacade.writeServicePlanPDFToResponse(..)) && args(servicePlanId, ..)")
    public void downloadServicePlan(Long servicePlanId) {
    }

    @AfterReturning(pointcut = "downloadServicePlan(servicePlanId)", argNames = "servicePlanId")
    public void logDownloadServicePlan(Long servicePlanId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(servicePlanId, id -> auditLogFacade.logDownloadServicePlan(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientServicePlanFacade.add(..))")
    public void createServicePlan() {
    }

    @AfterReturning(pointcut = "createServicePlan()", returning = "servicePlanId")
    public void logCreateServicePlan(Long servicePlanId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(servicePlanId, id -> auditLogFacade.logCreateServicePlan(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientAssessmentFacade.find(..)) && args(clientId, ..)")
    public void viewAssessments(Long clientId) {
    }

    @AfterReturning(pointcut = "viewAssessments(clientId)", argNames = "clientId")
    public void logViewAssessments(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logViewAssessments(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientAssessmentFacade.findClientAssessmentById(..)) && args(assessmentResultId)")
    public void viewAssessment(Long assessmentResultId) {
    }

    @AfterReturning(pointcut = "viewAssessment(assessmentResultId)", argNames = "assessmentResultId")
    public void logViewAssessment(Long assessmentResultId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(assessmentResultId, id -> auditLogFacade.logViewAssessment(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientAssessmentFacade.edit(..))")
    public void updateAssessment() {
    }

    @AfterReturning(pointcut = "updateAssessment()", returning = "clientAssessmentResultId")
    public void logUpdateAssessment(Long clientAssessmentResultId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientAssessmentResultId, id -> auditLogFacade.logUpdateAssessment(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientAssessmentFacade.add(..))")
    public void createAssessment() {
    }

    @AfterReturning(pointcut = "createAssessment()", returning = "clientAssessmentResultId")
    public void logCreateAssessment(Long clientAssessmentResultId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientAssessmentResultId, id -> auditLogFacade.logCreateAssessment(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ClientAssessmentFacade.findSurveyJson(..)) && args(assessmentResultId, ..)" +
            "|| execution(* com.scnsoft.eldermark.facade.ClientAssessmentFacade.export(..)) && args(assessmentResultId, ..)")
    public void downloadAssessment(Long assessmentResultId) {
    }

    @AfterReturning(pointcut = "downloadAssessment(assessmentResultId)", argNames = "assessmentResultId")
    public void logDownloadAssessment(Long assessmentResultId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(assessmentResultId, id -> auditLogFacade.logDownloadAssessment(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.EventFacade.findEventsOrNotes(..)) && args(eventNoteFilter, ..)")
    public void viewEventsAndNotes(EventNoteFilter eventNoteFilter) {
    }

    @AfterReturning(pointcut = "viewEventsAndNotes(eventNoteFilter)", argNames = "eventNoteFilter")
    public void logViewEventsAndNotes(EventNoteFilter eventNoteFilter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(eventNoteFilter, filter -> auditLogFacade.logViewEventsAndNotes(filter)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.EventFacade.findById(..)) && args(eventId)")
    public void viewEvent(Long eventId) {
    }

    @AfterReturning(pointcut = "viewEvent(eventId)", argNames = "eventId")
    public void logViewEvent(Long eventId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(eventId, id -> auditLogFacade.logViewEvent(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.EventFacade.add(..))")
    public void createEvent() {
    }

    @AfterReturning(pointcut = "createEvent()", returning = "eventId")
    public void logCreateEvent(Long eventId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(eventId, id -> auditLogFacade.logCreateEvent(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.NoteFacade.findById(..)) && args(noteId)")
    public void viewNote(Long noteId) {
    }

    @AfterReturning(pointcut = "viewNote(noteId)", argNames = "noteId")
    public void logViewNote(Long noteId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(noteId, id -> auditLogFacade.logViewNote(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.NoteFacade.add(..))")
    public void createNote() {
    }

    @AfterReturning(pointcut = "createNote()", returning = "noteId")
    public void logCreateNote(Long noteId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(noteId, id -> auditLogFacade.logCreateNote(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.NoteFacade.edit(..))")
    public void editNote() {
    }

    @AfterReturning(pointcut = "editNote()", returning = "noteId")
    public void logEditNote(Long noteId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(noteId, id -> auditLogFacade.logEditNote(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.IncidentReportFacade.find(..)) && args(incidentReportFilter, ..)")
    public void viewIncidentReports(IncidentReportFilter incidentReportFilter) {
    }

    @AfterReturning(pointcut = "viewIncidentReports(incidentReportFilter)", argNames = "incidentReportFilter")
    public void logViewIncidentReports(IncidentReportFilter incidentReportFilter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(incidentReportFilter, filter -> auditLogFacade.logViewIncidentReports(filter)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.IncidentReportFacade.findById(..)) && args(incidentReportId)")
    public void viewIncidentReport(Long incidentReportId) {
    }

    @AfterReturning(pointcut = "viewIncidentReport(incidentReportId)", argNames = "incidentReportId")
    public void logViewIncidentReport(Long incidentReportId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(incidentReportId, id -> auditLogFacade.logViewIncidentReport(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.IncidentReportFacade.submit(..)) && args(incidentReportDto)")
    public void submitIncidentReport(IncidentReportDto incidentReportDto) {
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.IncidentReportFacade.saveDraft(..)) && args(incidentReportDto)")
    public void saveDraftIncidentReport(IncidentReportDto incidentReportDto) {
    }

    @Pointcut(value = "submitIncidentReport(incidentReportDto) || saveDraftIncidentReport(incidentReportDto)", argNames = "incidentReportDto")
    public void createOrEditIncidentReport(IncidentReportDto incidentReportDto) {
    }

    @AfterReturning(pointcut = "createOrEditIncidentReport(incidentReportDto)", returning = "incidentReportId", argNames = "incidentReportId,incidentReportDto")
    public void logCreateOrEditIncidentReport(Long incidentReportId, IncidentReportDto incidentReportDto) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(Pair.of(incidentReportId, incidentReportDto.getId() == null),
                pair -> auditLogFacade.logCreateOrEditIncidentReport(pair)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.IncidentReportFacade.downloadById(..)) && args(incidentReportId, ..)")
    public void downloadIncidentReport(Long incidentReportId) {
    }

    @AfterReturning(pointcut = "downloadIncidentReport(incidentReportId)", argNames = "incidentReportId")
    public void logDownloadIncidentReport(Long incidentReportId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(incidentReportId, id -> auditLogFacade.logDownloadIncidentReport(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.LabResearchOrderFacade.find(..)) && args(labResearchOrderFilter, ..)")
    public void viewLabOrders(LabResearchOrderFilter labResearchOrderFilter) {
    }

    @AfterReturning(pointcut = "viewLabOrders(labResearchOrderFilter)", argNames = "labResearchOrderFilter")
    public void logViewLabOrders(LabResearchOrderFilter labResearchOrderFilter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(labResearchOrderFilter, filter -> auditLogFacade.logViewLabOrders(filter)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.LabResearchOrderFacade.findById(..)) && args(labResearchOrderId)")
    public void viewLabOrder(Long labResearchOrderId) {
    }

    @AfterReturning(pointcut = "viewLabOrder(labResearchOrderId)", argNames = "labResearchOrderId")
    public void logViewLabOrder(Long labResearchOrderId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(labResearchOrderId, id -> auditLogFacade.logViewLabOrder(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.LabResearchOrderFacade.add(..))")
    public void createLabOrder() {
    }

    @AfterReturning(pointcut = "createLabOrder()", returning = "labResearchOrderId")
    public void logCreateLabOrder(Long labResearchOrderId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(labResearchOrderId, id -> auditLogFacade.logCreateLabOrder(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.LabResearchOrderFacade.review(..)) && args(labResearchReviewDto)")
    public void reviewLabOrders(LabResearchReviewDto labResearchReviewDto) {
    }

    @AfterReturning(pointcut = "reviewLabOrders(labResearchReviewDto)", argNames = "labResearchReviewDto")
    public void logReviewLabOrders(LabResearchReviewDto labResearchReviewDto) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(labResearchReviewDto.getOrderIds(), ids -> auditLogFacade.logReviewLabOrders(ids)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.CareTeamMemberFacade.findById(..)) && args(careTeamMemberId)")
    public void viewCareTeamMember(Long careTeamMemberId) {
    }

    @AfterReturning(pointcut = "viewCareTeamMember(careTeamMemberId)", argNames = "careTeamMemberId")
    public void logViewCareTeamMember(Long careTeamMemberId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(careTeamMemberId, id -> auditLogFacade.logViewCareTeamMember(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.CareTeamMemberFacade.add(..))")
    public void createCareTeamMember() {
    }

    @AfterReturning(pointcut = "createCareTeamMember()", returning = "careTeamMemberId")
    public void logCreateCareTeamMember(Long careTeamMemberId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(careTeamMemberId, id -> auditLogFacade.logCreateCareTeamMember(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.CareTeamMemberFacade.edit(..))")
    public void editCareTeamMember() {
    }

    @AfterReturning(pointcut = "editCareTeamMember()", returning = "careTeamMemberId")
    public void logEditCareTeamMember(Long careTeamMemberId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(careTeamMemberId, id -> auditLogFacade.logEditCareTeamMember(id)));
    }

    @Before(value = "execution(* com.scnsoft.eldermark.facade.CareTeamMemberFacade.deleteById(..)) && args(careTeamMemberId)")
    public void deleteCareTeamMember(Long careTeamMemberId) {
        clientCareTeamMemberService.findByClientCtmId(careTeamMemberId, ClientCareTeamAuditLogDetailsAware.class)
                .ifPresentOrElse(ctm -> {
                            applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(careTeamMemberId, id -> auditLogFacade.logDeleteCareTeamMember(id, ctm, null)));
                        },
                        () -> {
                            var careTeamMember = communityCareTeamMemberService.findById(careTeamMemberId, CommunityCareTeamAuditLogDetailsAware.class);
                            applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(careTeamMemberId, id -> auditLogFacade.logDeleteCareTeamMember(id, null, careTeamMember)));
                        });
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ContactFacade.find(..)) && args(contactFilter, ..)")
    public void viewContacts(ContactFilter contactFilter) {
    }

    @AfterReturning(pointcut = "viewContacts(contactFilter)", argNames = "contactFilter")
    public void logViewContacts(ContactFilter contactFilter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(contactFilter, filter -> auditLogFacade.logViewContacts(filter)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ContactFacade.findById(..)) && args(contactId)")
    public void viewContact(Long contactId) {
    }

    @AfterReturning(pointcut = "viewContact(contactId)", argNames = "contactId")
    public void logViewContact(Long contactId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(contactId, id -> auditLogFacade.logViewContact(id)));
    }


    @Pointcut("execution(* com.scnsoft.eldermark.facade.ContactFacade.add(..))")
    public void createContact() {
    }

    @AfterReturning(pointcut = "createContact()", returning = "contactId")
    public void logCreateContact(Long contactId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(contactId, id -> auditLogFacade.logCreateContact(id, false)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ContactFacade.edit(..)) && args(contact)")
    public void editContact(ContactDto contact) {
    }

    @AfterReturning(pointcut = "editContact(contact)", argNames = "contact")
    public void logEditContact(ContactDto contact) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(contact, c -> auditLogFacade.logEditContact(c)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ContactFacade.invite(..)) && args(contactId)")
    public void reinviteContact(Long contactId) {
    }

    @AfterReturning(pointcut = "reinviteContact(contactId)", argNames = "contactId")
    public void logReinviteContact(Long contactId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(contactId, id -> auditLogFacade.logReinviteContact(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.AuthFacade.createPasswordAndActivateAccount(..)) && args(passwordResetDto)")
    public void acceptInviteContact(PasswordResetDto passwordResetDto) {
    }

    @AfterReturning(pointcut = "acceptInviteContact(passwordResetDto)", argNames = "passwordResetDto")
    public void logAcceptInviteContact(PasswordResetDto passwordResetDto) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(passwordResetDto.getToken(), token -> auditLogFacade.logAcceptInviteContact(token)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.OrganizationFacade.find(..)) && args(.., name)")
    public void viewOrganizations(String name) {
    }

    @AfterReturning(pointcut = "viewOrganizations(name)", argNames = "name")
    public void logViewOrganizations(String name) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(name, n -> auditLogFacade.logViewOrganizations(n)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.OrganizationFacade.findById(..)) && args(organizationId, ..)")
    public void viewOrganization(Long organizationId) {
    }

    @AfterReturning(pointcut = "viewOrganization(organizationId)", argNames = "organizationId")
    public void logViewOrganization(Long organizationId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(organizationId, id -> auditLogFacade.logViewOrganization(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.OrganizationFacade.edit(..))")
    public void editOrganization() {
    }

    @AfterReturning(pointcut = "editOrganization()", returning = "organizationId")
    public void logEditOrganization(Long organizationId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(organizationId, id -> auditLogFacade.logEditOrganization(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.OrganizationFacade.add(..))")
    public void createOrganization() {
    }

    @AfterReturning(pointcut = "createOrganization()", returning = "organizationId")
    public void logCreateOrganization(Long organizationId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(organizationId, id -> auditLogFacade.logCreateOrganization(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.CommunityFacade.findByOrgId(..)) && @annotation(com.scnsoft.eldermark.annotations.AuditLog) && args(organizationId, ..)")
    public void viewCommunities(Long organizationId) {
    }

    @AfterReturning(pointcut = "viewCommunities(organizationId)", argNames = "organizationId")
    public void logViewCommunities(Long organizationId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(organizationId, id -> auditLogFacade.logViewCommunities(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.CommunityFacade.findById(..)) && args(communityId, ..)")
    public void viewCommunity(Long communityId) {
    }

    @AfterReturning(pointcut = "viewCommunity(communityId)", argNames = "communityId")
    public void logViewCommunity(Long communityId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(communityId, id -> auditLogFacade.logViewCommunity(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.CommunityFacade.edit(..))")
    public void editCommunity() {
    }

    @AfterReturning(pointcut = "editCommunity()", returning = "communityId")
    public void logEditCommunity(Long communityId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(communityId, id -> auditLogFacade.logEditCommunity(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.CommunityFacade.add(..))")
    public void createCommunity() {
    }

    @AfterReturning(pointcut = "createCommunity()", returning = "communityId")
    public void logCreateCommunity(Long communityId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(communityId, id -> auditLogFacade.logCreateCommunity(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ReferralFacade.findInbounds(..)) && args(referralFilter, ..)")
    public void viewInboundReferrals(ReferralFilter referralFilter) {
    }

    @AfterReturning(pointcut = "viewInboundReferrals(referralFilter)", argNames = "referralFilter")
    public void logViewInboundReferrals(ReferralFilter referralFilter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(referralFilter, filter -> auditLogFacade.logViewInboundReferrals(filter)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ReferralFacade.findInboundById(..)) && args(requestId)")
    public void viewInboundReferral(Long requestId) {
    }

    @AfterReturning(pointcut = "viewInboundReferral(requestId)", argNames = "requestId")
    public void logViewInboundReferral(Long requestId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(requestId, id -> auditLogFacade.logViewInboundReferral(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ReferralFacade.findOutbounds(..)) && args(referralFilter, ..)")
    public void viewOutboundReferrals(ReferralFilter referralFilter) {
    }

    @AfterReturning(pointcut = "viewOutboundReferrals(referralFilter)", argNames = "referralFilter")
    public void logViewOutboundReferrals(ReferralFilter referralFilter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(referralFilter, filter -> auditLogFacade.logViewOutboundReferrals(filter)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ReferralFacade.findOutboundById(..)) && args(referralId)")
    public void viewOutboundReferral(Long referralId) {
    }

    @AfterReturning(pointcut = "viewOutboundReferral(referralId)", argNames = "referralId")
    public void logViewOutboundReferral(Long referralId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(referralId, id -> auditLogFacade.logViewOutboundReferral(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ReferralFacade.add(com.scnsoft.eldermark.dto.referral.ReferralDto)) && args(referralDto)")
    public void createReferral(ReferralDto referralDto) {
    }

    @AfterReturning(pointcut = "createReferral(referralDto)", returning = "referralId", argNames = "referralId,referralDto")
    public void logCreateReferral(Long referralId, ReferralDto referralDto) {
        if (referralDto.getMarketplace() != null) {
            applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(referralId, id -> auditLogFacade.logCreateReferralFromMarketplace(id)));
        } else {
            applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(referralId, id -> auditLogFacade.logCreateReferral(id)));
        }
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ReferralFacade.cancel(..)) && args(referralId)")
    public void cancelReferralRequest(Long referralId) {
    }

    @AfterReturning(pointcut = "cancelReferralRequest(referralId)", argNames = "referralId")
    public void logCancelReferralRequest(Long referralId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(referralId, id -> auditLogFacade.logCancelReferralRequest(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ReferralFacade.preadmit(..)) && args(requestId)")
    public void preadmitReferralRequest(Long requestId) {
    }

    @AfterReturning(pointcut = "preadmitReferralRequest(requestId)", argNames = "requestId")
    public void logPreadmitReferralRequest(Long requestId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(requestId, id -> auditLogFacade.logPreadmitReferralRequest(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ReferralFacade.accept(..)) && args(requestId, ..)")
    public void acceptReferralRequest(Long requestId) {
    }

    @AfterReturning(pointcut = "acceptReferralRequest(requestId)", argNames = "requestId")
    public void logAcceptReferralRequest(Long requestId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(requestId, id -> auditLogFacade.logAcceptReferralRequest(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.ReferralFacade.decline(..)) && args(requestId, ..)")
    public void declineReferralRequest(Long requestId) {
    }

    @AfterReturning(pointcut = "declineReferralRequest(requestId)", argNames = "requestId")
    public void logDeclineReferralRequest(Long requestId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(requestId, id -> auditLogFacade.logDeclineReferralRequest(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.facade.VideoCallConversationFacade.findHistory(..)) && args(employeeId, ..)")
    public void viewCallHistory(Long employeeId) {
    }

    @AfterReturning(pointcut = "viewCallHistory(employeeId)", argNames = "employeeId")
    public void logViewCallHistory(Long employeeId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(employeeId, id -> auditLogFacade.logViewCallHistory(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.ContactFacade.add(com.scnsoft.eldermark.dto.ContactDto)) && args(contactDto)", argNames = "contactDto")
    public void createAssociatedScAccount(ContactDto contactDto) {
    }

    @AfterReturning(pointcut = "createAssociatedScAccount(contactDto)", argNames = "contactId,contactDto", returning = "contactId")
    public void logCreateAssociatedScAccount(Long contactId, ContactDto contactDto) {
        if (!CollectionUtils.isEmpty(contactDto.getAssociatedClientIds())) {
            applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(contactDto.getAssociatedClientIds(), id -> auditLogFacade.logCreateContact(contactId, true)));
        }
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.client.expense.ClientExpenseFacade.find(..)) && args(clientId, ..)")
    public void viewClientExpenses(Long clientId) {
    }

    @AfterReturning(pointcut = "viewClientExpenses(clientId)", argNames = "clientId")
    public void logViewClientExpenses(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logViewExpenses(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.client.expense.ClientExpenseFacade.create(..))")
    public void createClientExpenses() {
    }

    @AfterReturning(pointcut = "createClientExpenses()", returning = "expenseId")
    public void logCreateClientExpenses(Long expenseId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(expenseId, id -> auditLogFacade.logCreateExpense(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.client.expense.ClientExpenseFacade.findById(..)) && args(expenseId)")
    public void viewClientExpense(Long expenseId) {
    }

    @AfterReturning(pointcut = "viewClientExpense(expenseId)", argNames = "expenseId")
    public void logViewClientExpense(Long expenseId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(expenseId, id -> auditLogFacade.logViewExpense(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.document.CommunityDocumentAndFolderFacade.find(..)) && args(documentFilter, ..)")
    public void viewCompanyDocuments(CommunityDocumentFilterDto documentFilter) {
    }

    @AfterReturning(pointcut = "viewCompanyDocuments(documentFilter)", argNames = "documentFilter")
    public void logViewCompanyDocuments(CommunityDocumentFilterDto documentFilter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(documentFilter, filter -> auditLogFacade.logViewDocuments(filter)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.DocumentFacade.save(..))")
    public void uploadCompanyDocument() {
    }

    @AfterReturning(pointcut = "uploadCompanyDocument()", returning = "documentId")
    public void logUploadCompanyDocument(Long documentId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(documentId, id -> auditLogFacade.logUploadCompanyDocument(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.document.CommunityDocumentAndFolderFacade.download(..)) && args(stringDocumentIds, ..)")
    public void downloadCompanyDocuments(List<String> stringDocumentIds) {
    }

    @AfterReturning(pointcut = "downloadCompanyDocuments(stringDocumentIds)", argNames = "stringDocumentIds")
    public void logDownloadCompanyDocuments(List<String> stringDocumentIds) {
        var documentIds = stringDocumentIds.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(documentIds, ids -> auditLogFacade.logDownloadCompanyDocuments(ids)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.DocumentFacade.download(..)) && args(documentId,..)")
    public void downloadCompanyDocument(Long documentId) {
    }

    @AfterReturning(pointcut = "downloadCompanyDocument(documentId)", argNames = "documentId")
    public void logDownloadCompanyDocument(Long documentId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(List.of(documentId), ids -> auditLogFacade.logDownloadCompanyDocuments(ids)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.document.folder.DocumentFolderFacade.add(..))")
    public void createFolder() {
    }

    @AfterReturning(pointcut = "createFolder()", returning = "folderId")
    public void logCreateFolder(Long folderId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(folderId, id -> auditLogFacade.logCreateFolder(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.document.folder.DocumentFolderFacade.delete(..)) && args(folderId, ..)")
    public void deleteFolder(Long folderId) {
    }

    @AfterReturning(pointcut = "deleteFolder(folderId)", argNames = "folderId")
    public void logDeleteFolder(Long folderId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(folderId, id -> auditLogFacade.logDeleteFolder(id)));
    }

    @Before(value = "execution(* com.scnsoft.eldermark.facade.document.folder.DocumentFolderFacade.edit(..)) && args(folderDto)", argNames = "folderDto")
    public void editFolder(DocumentFolderDto folderDto) {
        var folderId = folderDto.getId();
        var aware = documentFolderService.findById(folderId, IdNameCommunityIdAndCommunityOrganizationIdAware.class);
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(folderId, id -> auditLogFacade.logEditFolder(id, aware, folderDto.getName())));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.MarketplaceCommunityFacade.findByCommunityId(..)) && args(communityId, ..)")
    public void viewMarketplaceCommunityDetails(Long communityId) {
    }

    @AfterReturning(pointcut = "viewMarketplaceCommunityDetails(communityId)", argNames = "communityId")
    public void logViewMarketplaceCommunityDetails(Long communityId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(communityId, id -> auditLogFacade.logViewMarketplaceCommunityDetails(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.MarketplaceCommunityFacade.findPartners(..)) && args(communityId, ..)")
    public void viewMarketplacePartnerProviders(Long communityId) {
    }

    @AfterReturning(pointcut = "viewMarketplacePartnerProviders(communityId)", argNames = "communityId")
    public void logViewMarketplacePartnerProviders(Long communityId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(communityId, id -> auditLogFacade.logViewMarketplacePartnerProviders(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.MarketplaceCommunityFacade.find(..)) && args(filter,..)")
    public void viewMarketplace(MarketplaceFilter filter) {
    }

    @AfterReturning(pointcut = "viewMarketplace(filter)", argNames = "filter")
    public void logViewMarketplace(MarketplaceFilter filter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(filter, marketplaceFilter -> auditLogFacade.logViewMarketPlace(marketplaceFilter)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.signature.DocumentSignatureTemplateFacade.uploadTemplate(..))")
    public void createTemplate() {
    }

    @AfterReturning(pointcut = "createTemplate()", returning = "templateId")
    public void logCreateTemplate(Long templateId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(templateId, id -> auditLogFacade.logCreateTemplate(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.signature.DocumentSignatureTemplateFacade.updateTemplate(..))")
    public void updateTemplate() {
    }

    @AfterReturning(pointcut = "updateTemplate()", returning = "templateId")
    public void logUpdateTemplate(Long templateId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(templateId, id -> auditLogFacade.logUpdateTemplate(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.signature.DocumentSignatureTemplateFacade.deleteTemplate(..)) && args(templateId)")
    public void deleteTemplate(Long templateId) {
    }

    @AfterReturning(pointcut = "deleteTemplate(templateId)", argNames = "templateId")
    public void logDeleteTemplate(Long templateId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(templateId, id -> auditLogFacade.logDeleteTemplate(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.document.CommunityDocumentAndFolderFacade.find(..)) && args(documentFilter, ..)")
    public void viewTemplates(CommunityDocumentFilterDto documentFilter) {
    }

    @AfterReturning(pointcut = "viewTemplates(documentFilter)", argNames = "documentFilter")
    public void logViewTemplates(CommunityDocumentFilterDto documentFilter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(documentFilter, filter -> auditLogFacade.logViewDocuments(filter)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.ClientAppointmentFacade.find(..)) && args(filter, ..)")
    public void viewAppointments(ClientAppointmentFilter filter) {
    }

    @AfterReturning(pointcut = "viewAppointments(filter)", argNames = "filter")
    public void logViewAppointments(ClientAppointmentFilter filter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(filter, appointmentFilter -> auditLogFacade.logViewAppointments(appointmentFilter)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.ClientAppointmentFacade.findById(..)) && args(appointmentId)")
    public void viewAppointment(Long appointmentId) {
    }

    @AfterReturning(pointcut = "viewAppointment(appointmentId)", argNames = "appointmentId")
    public void logViewAppointment(Long appointmentId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(appointmentId, id -> auditLogFacade.logViewAppointment(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.ClientAppointmentFacade.create(..))")
    public void createAppointment() {
    }

    @AfterReturning(pointcut = "createAppointment()", returning = "appointmentId")
    public void logCreateAppointment(Long appointmentId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(appointmentId, id -> auditLogFacade.logCreateAppointment(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.ClientAppointmentFacade.edit(..))")
    public void updateAppointment() {
    }

    @AfterReturning(pointcut = "updateAppointment()", returning = "appointmentId")
    public void logUpdateAppointment(Long appointmentId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(appointmentId, id -> auditLogFacade.logUpdateAppointment(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.ClientAppointmentFacade.cancel(..)) && args(appointmentId, ..)")
    public void cancelAppointment(Long appointmentId) {
    }

    @AfterReturning(pointcut = "cancelAppointment(appointmentId)", argNames = "appointmentId")
    public void logCancelAppointment(Long appointmentId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(appointmentId, id -> auditLogFacade.logCancelAppointment(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.ClientAppointmentFacade.export(..)) && args(filter, ..)")
    public void exportAppointments(ClientAppointmentFilter filter) {
    }

    @AfterReturning(pointcut = "exportAppointments(filter)", argNames = "filter")
    public void logExportAppointments(ClientAppointmentFilter filter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(filter, appointmentFilter -> auditLogFacade.logExportAppointments(appointmentFilter)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.ClientFacade.findRecords(..)) && args(filter, ..)")
    public void recordSearch(ClientRecordSearchFilter filter) {
    }

    @AfterReturning(pointcut = "recordSearch(filter)", argNames = "filter")
    public void logRecordSearch(ClientRecordSearchFilter filter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(filter, recordSearchFilter -> auditLogFacade.logRecordSearch(recordSearchFilter)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.ReportsFacade.downloadReport(..)) && args(filter,..)")
    public void exportReport(ReportFilterDto filter) {
    }

    @AfterReturning(pointcut = "exportReport(filter)", argNames = "filter")
    public void logExportReport(ReportFilterDto filter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(filter, reportFilter -> auditLogFacade.logExportReport(reportFilter)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.ConversationFacade.getUsersByConversationSids(..)) && args(conversationSids)")
    public void viewChats(List<String> conversationSids) {
    }

    @AfterReturning(pointcut = "viewChats(conversationSids)", argNames = "conversationSids")
    public void logViewChats(List<String> conversationSids) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(conversationSids, sids -> auditLogFacade.logViewChats(sids)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.ConversationFacade.create(..))")
    public void createChat() {
    }

    @AfterReturning(pointcut = "createChat()", returning = "conversationSid")
    public void logCreateChat(String conversationSid) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(conversationSid, sid -> auditLogFacade.logCreateChat(sid)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.ConversationFacade.addParticipants(com.scnsoft.eldermark.dto.conversation.EditConversationDto)) && args(editConversationDto) || execution(* com.scnsoft.eldermark.facade.ConversationFacade.deleteParticipants(com.scnsoft.eldermark.dto.conversation.EditConversationDto)) && args(editConversationDto)")
    public void updateChatParticipants(EditConversationDto editConversationDto) {
    }

    @AfterReturning(pointcut = "updateChatParticipants(editConversationDto)", argNames = "editConversationDto")
    public void logUpdateChatParticipants(EditConversationDto editConversationDto) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(editConversationDto, sid -> auditLogFacade.logUpdateChatParticipants(editConversationDto)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.VideoCallConversationFacade.initiateCall(com.scnsoft.eldermark.dto.conversation.call.InitiateCallDto)) && args(initiateCallDto)")
    public void startCall(InitiateCallDto initiateCallDto) {
    }

    @AfterReturning(pointcut = "startCall(initiateCallDto)", argNames = "initiateCallDto")
    public void logStartCall(InitiateCallDto initiateCallDto) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(initiateCallDto, dto -> auditLogFacade.logStartCall(dto)));
    }

    @Pointcut(
            value = "execution(* com.scnsoft.eldermark.facade.signature.DocumentSignatureRequestFacade.submitRequests(com.scnsoft.eldermark.dto.signature.SubmitTemplateSignatureRequestsDto)) && args(requestsDto)",
            argNames = "requestsDto"
    )
    public void sendSignatureRequest(SubmitTemplateSignatureRequestsDto requestsDto) {
    }

    @AfterReturning(pointcut = "sendSignatureRequest(requestsDto)", argNames = "requestsDto,requestIds", returning = "requestIds")
    public void logSendSignatureRequest(SubmitTemplateSignatureRequestsDto requestsDto, List<Long> requestIds) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(requestIds, ids -> auditLogFacade.logSendSignatureRequest(ids)));
    }

    @Pointcut(
            value = "execution(* com.scnsoft.eldermark.facade.signature.DocumentSignatureRequestFacade.cancelRequest(..)) && args(requestId)",
            argNames = "requestId"
    )
    public void cancelSignatureRequest(Long requestId) {
    }

    @AfterReturning(pointcut = "cancelSignatureRequest(requestId)", argNames = "requestId")
    public void logCancelSignatureRequest(Long requestId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(requestId, id -> auditLogFacade.logCancelSignatureRequest(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.signature.DocumentSignatureRequestFacade.renewRequest(..))")
    public void renewSignatureRequest() {
    }

    @AfterReturning(pointcut = "renewSignatureRequest()", returning = "requestId")
    public void logRenewSignatureRequest(Long requestId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(requestId, id -> auditLogFacade.logRenewSignatureRequest(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.signature.DocumentSignatureRequestFacade.resendPin(..)) && args(requestId)")
    public void resendPIN(Long requestId) {
    }

    @AfterReturning(pointcut = "resendPIN(requestId)", argNames = "requestId")
    public void logResendPIN(Long requestId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(requestId, id -> auditLogFacade.logResendPIN(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.signature.DocumentSignatureBulkRequestFacade.submitBulkRequest(..))")
    public void sendBulkSignatureRequest() {
    }

    @AfterReturning(pointcut = "sendBulkSignatureRequest()", returning = "bulkRequestId")
    public void logSendBulkSignatureRequest(Long bulkRequestId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(bulkRequestId, id -> auditLogFacade.logSendBulkSignatureRequest(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.signature.DocumentSignatureBulkRequestFacade.cancelRequest(..)) && args(bulkRequestId, ..)")
    public void cancelBulkSignatureRequest(Long bulkRequestId) {
    }

    @AfterReturning(pointcut = "cancelBulkSignatureRequest(bulkRequestId)", argNames = "bulkRequestId")
    public void logCancelBulkSignatureRequest(Long bulkRequestId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(bulkRequestId, id -> auditLogFacade.logCancelSignatureBulkRequest(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.signature.DocumentSignatureBulkRequestFacade.renewBulkRequest(..))")
    public void renewBulkSignatureRequest() {
    }

    @AfterReturning(pointcut = "renewBulkSignatureRequest()", returning = "bulkRequestId")
    public void logRenewBulkSignatureRequest(Long bulkRequestId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(bulkRequestId, id -> auditLogFacade.logRenewSignatureBulkRequest(id)));
    }

    @Pointcut(
            value = "execution(* com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestService.processStatusUpdateCallback(com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest, com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestPdcFlowCallbackLog)) " +
                    "&& args(request,logEntry)",
            argNames = "request,logEntry"
    )
    public void signDocument(DocumentSignatureRequest request, DocumentSignatureRequestPdcFlowCallbackLog logEntry) {
    }

    @AfterReturning(pointcut = "signDocument(request,logEntry)", argNames = "request,logEntry")
    public void logSignDocument(DocumentSignatureRequest request, DocumentSignatureRequestPdcFlowCallbackLog logEntry) {
        if (StringUtils.isEmpty(logEntry.getPdcflowErrorCode())) {
            applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(request.getId(), id -> auditLogFacade.logSignDocument(id)));
        }
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.UserManualFacade.find(..))")
    public void viewUserManuals() {
    }

    @AfterReturning(pointcut = "viewUserManuals()", returning = "manualDocumentDtos")
    public void logViewUserManuals(List<UserManualDocumentDto> manualDocumentDtos) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(manualDocumentDtos, (manuals) -> auditLogFacade.logViewUserManuals(manuals)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.UserManualFacade.downloadById(..)) && args(manualId, ..)")
    public void downloadUserManual(Long manualId) {
    }

    @AfterReturning(pointcut = "downloadUserManual(manualId)", argNames = "manualId")
    public void logDownloadUserManual(Long manualId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(manualId, id -> auditLogFacade.logDownloadUserManual(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.UserManualFacade.upload(..))")
    public void uploadUserManual() {
    }

    @AfterReturning(pointcut = "uploadUserManual()", returning = "manualId")
    public void logUploadUserManual(Long manualId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(manualId, id -> auditLogFacade.logUploadUserManual(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.ReleaseNoteFacade.find(..))")
    public void viewReleaseNotes() {
    }

    @AfterReturning(pointcut = "viewReleaseNotes()", returning = "releaseNoteListItemDtos")
    public void logViewReleaseNotes(List<ReleaseNoteListItemDto> releaseNoteListItemDtos) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(releaseNoteListItemDtos, releaseNotes -> auditLogFacade.logViewReleaseNotes(releaseNotes)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.ReleaseNoteFacade.downloadById(..)) && args(releaseNoteId, ..)")
    public void downloadReleaseNote(Long releaseNoteId) {
    }

    @AfterReturning(pointcut = "downloadReleaseNote(releaseNoteId)", argNames = "releaseNoteId")
    public void logDownloadReleaseNote(Long releaseNoteId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(releaseNoteId, id -> auditLogFacade.logDownloadReleaseNote(id)));
    }


    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.ReleaseNoteFacade.save(..))")
    public void uploadReleaseNote() {
    }

    @AfterReturning(pointcut = "uploadReleaseNote()", returning = "releaseNoteId")
    public void logUploadReleaseNote(Long releaseNoteId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(releaseNoteId, id -> auditLogFacade.logUploadReleaseNote(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.SupportTicketFacade.create(..))")
    public void createSupportTicket() {
    }

    @AfterReturning(pointcut = "createSupportTicket()", returning = "supportTicketId")
    public void logCreateSupportTicket(Long supportTicketId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(supportTicketId, id -> auditLogFacade.logCreateSupportTicket(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.prospect.ProspectFacade.add(..))")
    public void createProspect() {
    }

    @AfterReturning(pointcut = "createProspect()", returning = "prospectId")
    public void logCreateProspect(Long prospectId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(prospectId, id -> auditLogFacade.logCreateProspect(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.prospect.ProspectFacade.edit(..))")
    public void updateProspect() {
    }

    @AfterReturning(pointcut = "updateProspect()", returning = "prospectId")
    public void logUpdateProspect(Long prospectId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(prospectId, id -> auditLogFacade.logUpdateProspect(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.prospect.ProspectFacade.deactivate(..)) && args(prospectId, ..)")
    public void deactivateProspect(Long prospectId) {
    }

    @AfterReturning(pointcut = "deactivateProspect(prospectId)", argNames = "prospectId")
    public void logDeactivateProspect(Long prospectId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(prospectId, id -> auditLogFacade.logDeactivateProspect(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.prospect.ProspectFacade.activate(..)) && args(prospectId, ..)")
    public void activateProspect(Long prospectId) {
    }

    @AfterReturning(pointcut = "activateProspect(prospectId)", argNames = "prospectId")
    public void logActivateProspect(Long prospectId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(prospectId, id -> auditLogFacade.logActivateProspect(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.prospect.ProspectFacade.find(..)) && args(prospectFilterDto, ..)")
    public void viewProspects(ProspectFilterDto prospectFilterDto) {
    }

    @AfterReturning(pointcut = "viewProspects(prospectFilterDto)", argNames = "prospectFilterDto")
    public void logViewProspects(ProspectFilterDto prospectFilterDto) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(prospectFilterDto, filter -> auditLogFacade.logViewProspects(filter)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.facade.prospect.ProspectFacade.findById(..)) && args(prospectId)")
    public void viewProspect(Long prospectId) {
    }

    @AfterReturning(pointcut = "viewProspect(prospectId)", argNames = "prospectId")
    public void logViewProspects(Long prospectId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(prospectId, id -> auditLogFacade.logViewProspect(id)));
    }
}
