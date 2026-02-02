package com.scnsoft.eldermark.facade;

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
import com.scnsoft.eldermark.beans.audit.AuditLogFilterDto;
import com.scnsoft.eldermark.beans.projection.ClientCareTeamAuditLogDetailsAware;
import com.scnsoft.eldermark.beans.projection.CommunityCareTeamAuditLogDetailsAware;
import com.scnsoft.eldermark.beans.projection.EmployeeIdNamesAware;
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
import com.scnsoft.eldermark.entity.audit.event.AuditLogPublishedEvent;
import com.scnsoft.eldermark.entity.document.folder.IdNameCommunityIdAndCommunityOrganizationIdAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface AuditLogFacade {

    Page<AuditLogListItemDto> find(AuditLogFilterDto filter, Pageable pageable);

    Long findOldestDateByOrganization(Long organizationId);

    Long findNewestDateByOrganization(Long organizationId);

    boolean canViewList();

    void export(AuditLogFilterDto filter, HttpServletResponse response);

    void logLogin();

    void logLogout(String token);

    void logResetPassword(String token);

    void logActivateClient(Long clientId);

    void logDeactivateClient(Long clientId);

    void logViewDocuments(ClientDocumentFilter documentFilter);

    void logDocumentDownload(Long documentId);

    void logDocumentUpload(Long documentId);

    void logDocumentEdit(Long documentId);

    void logDocumentDelete(Long documentId);

    void logDocumentView(Long documentId);

    void logCCDGenerateAndView(Long clientId);

    void logCCDGenerateAndDownload(Long clientId);

    void logFacesheetGenerateAndView(Long clientId);

    void logFacesheetGenerateAndDownload(Long clientId);

    void logCreateClient(Long clientId);

    void logUpdateClient(Long clientId);

    void logViewClients(ClientFilter clientFilter);

    void logViewClient(Long clientId);

    void logViewMedication(Long medicationId);

    void logViewAllergy(Long allergyId);

    void logViewProblem(Long problemId);

    void logRequestRide(Long clientId);

    void logRideHistory(Long clientId);

    void logViewCallHistory(Long employeeId);

    void logViewServicePlans(ServicePlanFilter servicePlanFilter);

    void logViewServicePlan(Long servicePlanId);

    void logUpdateServicePlan(Long servicePlanId);

    void logDownloadServicePlan(Long servicePlanId);

    void logCreateServicePlan(Long servicePlanId);

    void logViewAssessments(Long clientId);

    void logViewAssessment(Long assessmentResultId);

    void logUpdateAssessment(Long assessmentResultId);

    void logCreateAssessment(Long assessmentResultId);

    void logDownloadAssessment(Long clientAssessmentResultId);

    void logViewEventsAndNotes(EventNoteFilter eventNoteFilter);

    void logViewEvent(Long eventId);

    void logCreateEvent(Long eventId);

    void logViewNote(Long noteId);

    void logCreateNote(Long noteId);

    void logEditNote(Long noteId);

    void logViewIncidentReports(IncidentReportFilter incidentReportFilter);

    void logViewIncidentReport(Long incidentReportId);

    void logCreateOrEditIncidentReport(Pair<Long, Boolean> pair);

    void logDownloadIncidentReport(Long incidentReportId);

    void logViewLabOrders(LabResearchOrderFilter labResearchOrderFilter);

    void logViewLabOrder(Long labResearchOrderId);

    void logCreateLabOrder(Long labResearchOrderId);

    void logReviewLabOrders(Iterable<Long> labResearchOrderIds);

    void logViewCareTeamMember(Long careTeamMemberId);

    void logCreateCareTeamMember(Long careTeamMemberId);

    void logEditCareTeamMember(Long careTeamMemberId);

    void logDeleteCareTeamMember(Long careTeamMemberId, ClientCareTeamAuditLogDetailsAware clientCtm, CommunityCareTeamAuditLogDetailsAware communityCtm);

    void logViewContacts(ContactFilter contactFilter);

    void logViewContact(Long contactId);

    void logCreateContact(Long contactId, boolean isAssociatedContact);

    void logEditContact(ContactDto contact);

    void logReinviteContact(Long contactId);

    void logAcceptInviteContact(String token);

    void logViewOrganizations(String name);

    void logViewOrganization(Long organizationId);

    void logEditOrganization(Long organizationId);

    void logCreateOrganization(Long organizationId);

    void logViewCommunities(Long organizationId);

    void logViewCommunity(Long communityId);

    void logEditCommunity(Long communityId);

    void logCreateCommunity(Long communityId);

    void logViewInboundReferrals(ReferralFilter referralFilter);

    void logViewInboundReferral(Long requestId);

    void logViewOutboundReferrals(ReferralFilter referralFilter);

    void logViewOutboundReferral(Long referralId);

    void logCreateReferral(Long referralId);

    void logCreateReferralFromMarketplace(Long referralId);

    void logCancelReferralRequest(Long referralId);

    void logPreadmitReferralRequest(Long requestId);

    void logAcceptReferralRequest(Long requestId);

    void logDeclineReferralRequest(Long requestId);

    void logViewExpenses(Long clientId);

    void logViewExpense(Long expenseId);

    void logCreateExpense(Long expenseId);

    void logViewDocuments(CommunityDocumentFilterDto filter);

    void logUploadCompanyDocument(Long documentId);

    void logDownloadCompanyDocuments(List<Long> documentIds);

    void logCreateFolder(Long folderId);

    void logDeleteFolder(Long folderId);

    void logEditFolder(Long folderId, IdNameCommunityIdAndCommunityOrganizationIdAware aware, String newFolderName);

    void logViewMarketplaceCommunityDetails(Long communityId);

    void logViewMarketplacePartnerProviders(Long communityId);

    void logViewMarketPlace(MarketplaceFilter marketplaceFilter);

    void logCreateTemplate(Long templateId);

    void logUpdateTemplate(Long templateId);

    void logDeleteTemplate(Long templateId);

    void logViewAppointments(ClientAppointmentFilter filter);

    void logViewAppointment(Long appointmentId);

    void logCreateAppointment(Long appointmentId);

    void logUpdateAppointment(Long appointmentId);

    void logCancelAppointment(Long appointmentId);

    void logExportAppointments(ClientAppointmentFilter filter);

    void logRecordSearch(ClientRecordSearchFilter filter);

    void logExportReport(ReportFilterDto reportFilter);

    void logViewChats(List<String> conversationSids);

    void logCreateChat(String conversationSid);

    void logUpdateChatParticipants(EditConversationDto editConversationDto);

    void logStartCall(InitiateCallDto initiateCallDto);

    void logSendSignatureRequest(List<Long> signatureRequestIds);

    void logCancelSignatureRequest(Long requestId);

    void logRenewSignatureRequest(Long requestId);

    void logResendPIN(Long requestId);

    void logSendBulkSignatureRequest(Long bulkSignatureRequestId);

    void logCancelSignatureBulkRequest(Long bulkSignatureRequestId);

    void logRenewSignatureBulkRequest(Long bulkSignatureRequestId);

    void logSignDocument(Long requestId);

    void logViewUserManuals(List<UserManualDocumentDto> manuals);

    void logDownloadUserManual(Long manualId);

    void logUploadUserManual(Long manualId);

    void logViewReleaseNotes(List<ReleaseNoteListItemDto> releaseNotes);

    void logDownloadReleaseNote(Long releaseNoteId);

    void logUploadReleaseNote(Long releaseNoteId);

    void logCreateSupportTicket(Long supportTicketId);

    void logCreateProspect(Long prospectId);
    void logUpdateProspect(Long prospectId);
    void logDeactivateProspect(Long prospectId);
    void logActivateProspect(Long prospectId);
    void logViewProspects(ProspectFilterDto filter);
    void logViewProspect(Long prospectId);

    <T> void processPublishedEvent(AuditLogPublishedEvent<T> event);
}
