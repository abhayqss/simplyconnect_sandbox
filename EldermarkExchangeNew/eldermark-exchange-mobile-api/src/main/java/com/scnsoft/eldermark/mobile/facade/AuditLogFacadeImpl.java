package com.scnsoft.eldermark.mobile.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.beans.audit.AuditLogAction;
import com.scnsoft.eldermark.beans.conversation.EmployeeSearchWithFavouriteFilter;
import com.scnsoft.eldermark.beans.projection.ClientIdClientOrganizationIdClientCommunityIdAware;
import com.scnsoft.eldermark.beans.projection.OrganizationIdAndCommunityIdAware;
import com.scnsoft.eldermark.beans.projection.OrganizationIdCommunityIdAssociatedClientsAware;
import com.scnsoft.eldermark.beans.projection.SignatureRequestTemplateNameAware;
import com.scnsoft.eldermark.entity.ClientIdOrganizationIdCommunityIdAware;
import com.scnsoft.eldermark.entity.EmployeeRequestType;
import com.scnsoft.eldermark.entity.audit.AuditLogClientCareTeamMemberRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogChatRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogContactRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogMedicationRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogSearchFilter;
import com.scnsoft.eldermark.entity.audit.AuditLogSignatureRequestRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogRelation;
import com.scnsoft.eldermark.entity.audit.event.AuditLogPublishedEvent;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.mobile.dto.conversation.EditConversationDto;
import com.scnsoft.eldermark.mobile.dto.conversation.call.InitiateCallDto;
import com.scnsoft.eldermark.mobile.dto.employee.EmployeeDto;
import com.scnsoft.eldermark.mobile.filter.MobileClientFilter;
import com.scnsoft.eldermark.mobile.filter.MobileDocumentFilter;
import com.scnsoft.eldermark.service.ClientCareTeamMemberService;
import com.scnsoft.eldermark.service.ClientMedicationService;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.ContactService;
import com.scnsoft.eldermark.service.EmployeeRequestService;
import com.scnsoft.eldermark.service.audit.AuditLogFactory;
import com.scnsoft.eldermark.service.audit.AuditLogService;
import com.scnsoft.eldermark.service.document.ClientDocumentService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.AuditLogUtils;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class AuditLogFacadeImpl implements AuditLogFacade {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogFacadeImpl.class);

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private EmployeeRequestService employeeRequestService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientMedicationService clientMedicationService;

    @Autowired
    private AuditLogFactory auditLogFactory;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private ClientDocumentService clientDocumentService;

    @Autowired
    private ClientCareTeamMemberService clientCareTeamMemberService;

    @Autowired
    private DocumentSignatureRequestService documentSignatureRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void logLogin() {
        var employee = loggedUserService.getCurrentEmployee();
        createLog(
                Set.of(employee.getOrganizationId()),
                CareCoordinationUtils.setOfNullable(employee.getCommunityId()),
                new HashSet<>(employee.getAssociatedClientIds()),
                AuditLogAction.LOG_IN,
                null,
                null
        );
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
                employee.getAssociatedClientIds(),
                AuditLogUtils.getRemoteAddress(),
                AuditLogAction.PASSWORD_RESET,
                null,
                null,
                true
        );
        auditLogService.save(auditLog);
    }

    @Override
    public void logViewContacts(Pair<EmployeeSearchWithFavouriteFilter, List<EmployeeDto>> pair) {
        var auditLogSearchFilter = createAuditLogSearchFilter(null, pair.getFirst());
        var orgIds = new HashSet<Long>();
        var communityIds = new HashSet<Long>();
        for (var dto : pair.getSecond()) {
            orgIds.add(dto.getOrganizationId());
            communityIds.add(dto.getCommunityId());
        }
        createLog(
                orgIds,
                communityIds,
                null,
                AuditLogAction.CONTACT_VIEW_LISTING,
                null,
                auditLogSearchFilter
        );
    }

    @Override
    public void logViewContact(Long contactId) {
        var aware = contactService.findById(contactId, OrganizationIdCommunityIdAssociatedClientsAware.class);
        var auditLogRelation = new AuditLogContactRelation();
        auditLogRelation.setContactId(contactId);
        createLog(
                Set.of(aware.getOrganizationId()),
                Set.of(aware.getCommunityId()),
                new HashSet<>(aware.getAssociatedClientIds()),
                AuditLogAction.CONTACT_VIEW,
                auditLogRelation,
                null
        );
    }

    @Override
    public void logViewClients(MobileClientFilter clientFilter) {
        var auditLogSearchFilter = createAuditLogSearchFilter(clientFilter.getSearchText(), clientFilter);
        createLog(
                Set.of(clientFilter.getOrganizationId()),
                new HashSet<>(clientFilter.getCommunityIds()),
                null,
                AuditLogAction.CLIENT_VIEW_LISTING,
                null,
                auditLogSearchFilter
        );
    }

    @Override
    public void logViewClient(Long clientId) {
        var aware = clientService.findById(clientId, OrganizationIdCommunityIdAssociatedClientsAware.class);
        createLog(
                Set.of(aware.getOrganizationId()),
                Set.of(aware.getCommunityId()),
                Set.of(clientId),
                AuditLogAction.CLIENT_VIEW,
                null,
                null
        );
    }

    @Override
    public void logViewMedication(Long medicationId) {
        logMedication(medicationId, AuditLogAction.MEDICATION_VIEW);
    }

    @Override
    public void logCreateMedication(Long medicationId) {
        logMedication(medicationId, AuditLogAction.MEDICATION_CREATE);
    }

    @Override
    public void logEditMedication(Long medicationId) {
        logMedication(medicationId, AuditLogAction.MEDICATION_EDIT);
    }

    private void logMedication(Long medicationId, AuditLogAction action) {
        var aware = clientMedicationService.findById(medicationId, ClientIdClientOrganizationIdClientCommunityIdAware.class);
        var auditLogRelation = new AuditLogMedicationRelation();
        auditLogRelation.setMedicationId(medicationId);
        createLog(
                Set.of(aware.getClientOrganizationId()),
                Set.of(aware.getClientCommunityId()),
                Set.of(aware.getClientId()),
                action,
                auditLogRelation,
                null
        );
    }

    @Override
    public void logViewDocument(Long documentId) {
        var aware = clientDocumentService.findById(documentId, ClientIdOrganizationIdCommunityIdAware.class);
        createLog(
                Set.of(aware.getOrganizationId()),
                Set.of(aware.getCommunityId()),
                Set.of(aware.getClientId()),
                AuditLogAction.DOCUMENT_VIEW,
                null,
                null
        );
    }

    @Override
    public void logViewDocuments(MobileDocumentFilter documentFilter) {
        var aware = clientService.findById(documentFilter.getClientId(), ClientIdOrganizationIdCommunityIdAware.class);
        createLog(
                Set.of(aware.getOrganizationId()),
                Set.of(aware.getCommunityId()),
                Set.of(aware.getClientId()),
                AuditLogAction.DOCUMENT_VIEW_LISTING,
                null,
                null
        );
    }

    @Override
    public void logDownloadCcd(Long clientId) {
        var aware = clientService.findById(clientId, ClientIdOrganizationIdCommunityIdAware.class);
        createLog(Set.of(aware.getOrganizationId()),
                Set.of(aware.getCommunityId()),
                Set.of(aware.getClientId()),
                AuditLogAction.CCD_GENERATE_AND_DOWNLOAD,
                null,
                null
        );
    }

    @Override
    public void logViewCcd(Long clientId) {
        var aware = clientService.findById(clientId, ClientIdOrganizationIdCommunityIdAware.class);
        createLog(Set.of(aware.getOrganizationId()),
                Set.of(aware.getCommunityId()),
                Set.of(aware.getClientId()),
                AuditLogAction.CCD_GENERATE_AND_VIEW,
                null,
                null
        );
    }

    @Override
    public void logFacesheetDownload(Long clientId) {
        var aware = clientService.findById(clientId, ClientIdOrganizationIdCommunityIdAware.class);
        createLog(Set.of(aware.getOrganizationId()),
                Set.of(aware.getCommunityId()),
                Set.of(aware.getClientId()),
                AuditLogAction.FACESHEET_GENERATE_AND_DOWNLOAD,
                null,
                null
        );
    }

    @Override
    public void logFacesheetView(Long clientId) {
        var aware = clientService.findById(clientId, ClientIdOrganizationIdCommunityIdAware.class);
        createLog(Set.of(aware.getOrganizationId()),
                Set.of(aware.getCommunityId()),
                Set.of(aware.getClientId()),
                AuditLogAction.FACESHEET_GENERATE_AND_VIEW,
                null,
                null
        );
    }

    @Override
    public void logDownloadDocument(Long documentId) {
        var aware = clientDocumentService.findById(documentId, ClientIdOrganizationIdCommunityIdAware.class);
        createLog(
                Set.of(aware.getOrganizationId()),
                Set.of(aware.getCommunityId()),
                Set.of(aware.getClientId()),
                AuditLogAction.DOCUMENT_DOWNLOAD,
                null,
                null
        );
    }

    @Override
    public void logViewCareTeamMember(Long careTeamMemberId) {
        var careTeamMember = clientCareTeamMemberService.findById(careTeamMemberId, ClientCareTeamMember.class);
        var auditLogRelation = new AuditLogClientCareTeamMemberRelation();
        auditLogRelation.setCareTeamMember(careTeamMember);

        var clientId = careTeamMember.getClientId();
        var aware = clientService.findById(clientId, OrganizationIdAndCommunityIdAware.class);
        createLog(
                Set.of(aware.getOrganizationId()),
                Set.of(aware.getCommunityId()),
                Set.of(clientId),
                AuditLogAction.CLIENT_CARE_TEAM_VIEW,
                auditLogRelation,
                null
        );
    }

    @Override
    public void logViewMarketplace(Long communityId) {
        createLog(
                null,
                null,
                null,
                AuditLogAction.MARKETPLACE_VIEW,
                null,
                null
        );
    }

    @Override
    public void logViewChats(List<String> conversationSids) {
        var auditLogSearchFilter = createAuditLogSearchFilter(null, conversationSids);
        createLog(
                null,
                null,
                null,
                AuditLogAction.CHAT_VIEW_LISTING,
                null,
                auditLogSearchFilter
        );
    }

    @Override
    public void logCreateChat(String conversationSid) {
        var auditLogChatRelation = new AuditLogChatRelation();
        auditLogChatRelation.setConversationSid(conversationSid);
        createLog(
                null,
                null,
                null,
                AuditLogAction.CHAT_CREATE,
                auditLogChatRelation,
                null
        );
    }

    @Override
    public void logUpdateChatParticipants(EditConversationDto editConversationDto) {
        var auditLogChatRelation = new AuditLogChatRelation();
        auditLogChatRelation.setConversationSid(editConversationDto.getConversationSid());
        createLog(
                null,
                null,
                null,
                AuditLogAction.CHAT_PARTICIPANT_UPDATE,
                auditLogChatRelation,
                null
        );
    }

    @Override
    public void logStartCall(InitiateCallDto initiateCallDto) {
        var auditLogChatRelation = new AuditLogChatRelation();
        auditLogChatRelation.setConversationSid(initiateCallDto.getConversationSid());
        createLog(
                null,
                null,
                null,
                AuditLogAction.CALL_START,
                auditLogChatRelation,
                null
        );
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
    public void logSignDocument(Long requestId) {
        logSignatureRequest(requestId, AuditLogAction.DOCUMENT_SIGN);
    }

    private void logSignatureRequest(Long requestId, AuditLogAction action) {
        var requestAware = documentSignatureRequestService.findById(requestId, SignatureRequestTemplateNameAware.class);
        var auditLogRelation = new AuditLogSignatureRequestRelation();
        auditLogRelation.setSignatureRequestIds(List.of(requestId));
        createLog(
                Set.of(requestAware.getClientOrganizationId()),
                Set.of(requestAware.getClientCommunityId()),
                Set.of(requestAware.getClientId()),
                action,
                auditLogRelation,
                null
        );
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

    private void createLog(Set<Long> organizationIds, Set<Long> communityIds, Set<Long> clientIds, AuditLogAction action, AuditLogRelation auditLogRelation, AuditLogSearchFilter auditLogSearchFilter) {
        var employeeId = loggedUserService.getCurrentEmployeeId();
        var auditLog = auditLogFactory.createClientLog(
                organizationIds,
                communityIds,
                employeeId,
                clientIds,
                AuditLogUtils.getRemoteAddress(),
                action,
                auditLogRelation,
                auditLogSearchFilter,
                true
        );
        auditLogService.save(auditLog);
    }

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION, fallbackExecution = true)
    public <T> void processPublishedEvent(AuditLogPublishedEvent<T> event) {
        event.getConsumer().accept(event.getParam());
    }
}
