package com.scnsoft.eldermark.mobile.aspect;

import com.scnsoft.eldermark.beans.conversation.EmployeeSearchWithFavouriteFilter;
import com.scnsoft.eldermark.entity.audit.event.AuditLogPublishedEvent;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestPdcFlowCallbackLog;
import com.scnsoft.eldermark.mobile.dto.LoginDto;
import com.scnsoft.eldermark.mobile.dto.auth.password.PasswordResetDto;
import com.scnsoft.eldermark.mobile.dto.ccd.medication.MedicationDto;
import com.scnsoft.eldermark.mobile.dto.conversation.EditConversationDto;
import com.scnsoft.eldermark.mobile.dto.conversation.call.InitiateCallDto;
import com.scnsoft.eldermark.mobile.dto.employee.EmployeeDto;
import com.scnsoft.eldermark.mobile.facade.AuditLogFacade;
import com.scnsoft.eldermark.mobile.filter.MobileClientFilter;
import com.scnsoft.eldermark.mobile.filter.MobileDocumentFilter;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@Aspect
public class AuditLoggingAspect {

    @Autowired
    private AuditLogFacade auditLogFacade;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    //todo test
    @Pointcut(value = "execution(* com.scnsoft.eldermark.mobile.controller.AuthController.login(..)) && args(loginDto)", argNames = "loginDto")
    public void login(LoginDto loginDto) {
    }

    @AfterReturning(value = "login(loginDto)", argNames = "loginDto")
    public void logLogin(LoginDto loginDto) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(1L, address -> auditLogFacade.logLogin()));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.mobile.facade.AuthFacade.resetPassword(..)) && args(dto)")
    public void resetPassword(PasswordResetDto dto) {
    }

    @Before(value = "resetPassword(dto)", argNames = "dto")
    public void logResetPassword(PasswordResetDto dto) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(dto.getToken(), token -> auditLogFacade.logResetPassword(token)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.mobile.facade.EmployeeFacade.find(..)) && args(filter, ..)")
    public void viewContacts(EmployeeSearchWithFavouriteFilter filter) {
    }

    @AfterReturning(pointcut = "viewContacts(filter)", argNames = "filter,dtos", returning = "dtos")
    public void logViewContacts(EmployeeSearchWithFavouriteFilter filter, Page<EmployeeDto> dtos) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(Pair.of(filter, dtos.getContent()), pair -> auditLogFacade.logViewContacts(pair)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.mobile.facade.EmployeeFacade.findById(..)) && args(contactId)")
    public void viewContact(Long contactId) {
    }

    @AfterReturning(pointcut = "viewContact(contactId)", argNames = "contactId")
    public void logViewContact(Long contactId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(contactId, id -> auditLogFacade.logViewContact(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.mobile.facade.ClientFacade.find(..)) && args(clientFilter, ..)")
    public void viewClients(MobileClientFilter clientFilter) {
    }

    @AfterReturning(pointcut = "viewClients(clientFilter)", argNames = "clientFilter")
    public void logViewClients(MobileClientFilter clientFilter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientFilter, filter -> auditLogFacade.logViewClients(filter)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.mobile.facade.ClientFacade.findById(..)) && args(clientId)")
    public void viewClient(Long clientId) {
    }

    @AfterReturning(value = "viewClient(clientId)", argNames = "clientId")
    public void logViewClient(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logViewClient(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.mobile.facade.ClientMedicationFacade.findById(..)) && args(medicationId)")
    public void viewMedication(Long medicationId) {
    }

    @AfterReturning(pointcut = "viewMedication(medicationId)", argNames = "medicationId")
    public void logViewMedication(Long medicationId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(medicationId, id -> auditLogFacade.logViewMedication(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.mobile.facade.ClientMedicationFacade.save(..))")
    public void createMedication() {
    }

    @AfterReturning(pointcut = "createMedication()", returning = "medicationId")
    public void logCreateMedication(Long medicationId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(medicationId, id -> auditLogFacade.logCreateMedication(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.mobile.facade.ClientMedicationFacade.save(..)) && args(medicationDto)")
    public void editMedication(MedicationDto medicationDto) {
    }

    @AfterReturning(pointcut = "editMedication(medicationDto)", argNames = "medicationDto")
    public void logEditMedication(MedicationDto medicationDto) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(medicationDto.getId(), id -> auditLogFacade.logEditMedication(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.mobile.facade.ClientDocumentFacade.find(..)) && args(documentFilter, ..)")
    public void viewClientDocuments(MobileDocumentFilter documentFilter) {
    }

    @AfterReturning(pointcut = "viewClientDocuments(documentFilter)", argNames = "documentFilter")
    public void logViewDocuments(MobileDocumentFilter documentFilter) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(documentFilter, filter -> auditLogFacade.logViewDocuments(filter)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.mobile.facade.ClientDocumentFacade.findById(..)) && args(documentId)")
    public void viewClientDocument(Long documentId) {
    }

    @AfterReturning(pointcut = "viewClientDocument(documentId)", argNames = "documentId")
    public void logViewClientDocument(Long documentId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(documentId, id -> auditLogFacade.logViewDocument(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.mobile.facade.ClientDocumentFacade.download(..)) && args(documentId, ..)")
    public void downloadClientDocument(Long documentId) {
    }

    @AfterReturning(pointcut = "downloadClientDocument(documentId)", argNames = "documentId")
    public void logDownloadClientDocument(Long documentId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(documentId, id -> auditLogFacade.logDownloadDocument(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.mobile.facade.ClientDocumentFacade.clientCcdToHtml(..)) && args(clientId)")
    public void downloadCcd(Long clientId) {
    }

    @AfterReturning(pointcut = "downloadCcd(clientId)", argNames = "clientId")
    public void logDownloadCcd(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logDownloadCcd(clientId)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.mobile.facade.ClientDocumentFacade.clientCcdToHtml(..)) && args(clientId)")
    public void viewCcd(Long clientId) {
    }

    @AfterReturning(pointcut = "viewCcd(clientId)", argNames = "clientId")
    public void logViewCcd(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logViewCcd(clientId)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.mobile.facade.ClientDocumentFacade.downloadFacesheet(..)) && args(clientId, ..)")
    public void downloadFacesheet(Long clientId) {
    }

    @AfterReturning(value = "downloadFacesheet(clientId)", argNames = "clientId")
    public void logDownloadFacesheet(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logFacesheetDownload(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.mobile.facade.ClientDocumentFacade.downloadFacesheet(..)) && args(clientId, ..)")
    public void viewFacesheet(Long clientId) {
    }

    @AfterReturning(value = "viewFacesheet(clientId)", argNames = "clientId")
    public void logViewFacesheet(Long clientId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(clientId, id -> auditLogFacade.logFacesheetView(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.mobile.facade.CareTeamMemberFacade.findById(..)) && args(careTeamMemberId)")
    public void viewCareTeamMember(Long careTeamMemberId) {
    }

    @AfterReturning(pointcut = "viewCareTeamMember(careTeamMemberId)", argNames = "careTeamMemberId")
    public void logViewCareTeamMember(Long careTeamMemberId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(careTeamMemberId, id -> auditLogFacade.logViewCareTeamMember(id)));
    }

    @Pointcut("execution(* com.scnsoft.eldermark.mobile.facade.MarketplaceCommunityFacade.fetchFeaturedServiceProviders(..)) && args(communityId)")
    public void viewMarketplace(Long communityId) {
    }

    @AfterReturning(pointcut = "viewMarketplace(communityId)", argNames = "communityId")
    public void logViewMarketplace(Long communityId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(communityId, id -> auditLogFacade.logViewMarketplace(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.mobile.facade.ConversationFacade.getUsersByConversationSids(..)) && args(conversationSids)")
    public void viewChats(List<String> conversationSids) {
    }

    @AfterReturning(pointcut = "viewChats(conversationSids)", argNames = "conversationSids")
    public void logViewChats(List<String> conversationSids) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(conversationSids, sids -> auditLogFacade.logViewChats(sids)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.mobile.facade.ConversationFacade.create(..))")
    public void createChat() {
    }

    @AfterReturning(pointcut = "createChat()", returning = "conversationSid")
    public void logCreateChat(String conversationSid) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(conversationSid, sid -> auditLogFacade.logCreateChat(sid)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.mobile.facade.ConversationFacade.addParticipants(com.scnsoft.eldermark.mobile.dto.conversation.EditConversationDto)) && args(editConversationDto)|| execution(* com.scnsoft.eldermark.mobile.facade.ConversationFacade.deleteParticipants(com.scnsoft.eldermark.mobile.dto.conversation.EditConversationDto)) && args(editConversationDto)")
    public void updateChatParticipants(EditConversationDto editConversationDto) {
    }

    @AfterReturning(pointcut = "updateChatParticipants(editConversationDto)", argNames = "editConversationDto")
    public void logUpdateChatParticipants(EditConversationDto editConversationDto) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(editConversationDto, sid -> auditLogFacade.logUpdateChatParticipants(editConversationDto)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.mobile.facade.VideoCallConversationFacade.initiateCall(com.scnsoft.eldermark.mobile.dto.conversation.call.InitiateCallDto)) && args(initiateCallDto)")
    public void startCall(InitiateCallDto initiateCallDto) {
    }

    @AfterReturning(pointcut = "startCall(initiateCallDto)", argNames = "initiateCallDto")
    public void logStartCall(InitiateCallDto initiateCallDto) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(initiateCallDto, dto -> auditLogFacade.logStartCall(dto)));
    }

    @Pointcut(
            value = "execution(* com.scnsoft.eldermark.mobile.facade.DocumentSignatureRequestFacade.cancelRequest(..)) && args(requestId)",
            argNames = "requestId"
    )
    public void cancelSignatureRequest(Long requestId) {
    }

    @AfterReturning(pointcut = "cancelSignatureRequest(requestId)", argNames = "requestId")
    public void logCancelSignatureRequest(Long requestId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(requestId, id -> auditLogFacade.logCancelSignatureRequest(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.mobile.facade.DocumentSignatureRequestFacade.renewRequest(..))")
    public void renewSignatureRequest() {
    }

    @AfterReturning(pointcut = "renewSignatureRequest()", returning = "requestId")
    public void logRenewSignatureRequest(Long requestId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(requestId, id -> auditLogFacade.logRenewSignatureRequest(id)));
    }

    @Pointcut(value = "execution(* com.scnsoft.eldermark.mobile.facade.DocumentSignatureRequestFacade.resendPin(..)) && args(requestId)")
    public void resendPIN(Long requestId) {
    }

    @AfterReturning(pointcut = "resendPIN(requestId)", argNames = "requestId")
    public void logResendPIN(Long requestId) {
        applicationEventPublisher.publishEvent(new AuditLogPublishedEvent<>(requestId, id -> auditLogFacade.logResendPIN(id)));
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
}

