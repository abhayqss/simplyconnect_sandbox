package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.conversation.EmployeeSearchWithFavouriteFilter;
import com.scnsoft.eldermark.entity.audit.event.AuditLogPublishedEvent;
import com.scnsoft.eldermark.mobile.dto.conversation.EditConversationDto;
import com.scnsoft.eldermark.mobile.dto.conversation.call.InitiateCallDto;
import com.scnsoft.eldermark.mobile.dto.employee.EmployeeDto;
import com.scnsoft.eldermark.mobile.filter.MobileClientFilter;
import com.scnsoft.eldermark.mobile.filter.MobileDocumentFilter;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

import java.util.List;

public interface AuditLogFacade {

    void logLogin();

    void logResetPassword(String token);

    void logViewContacts(Pair<EmployeeSearchWithFavouriteFilter, List<EmployeeDto>> pair);

    void logViewContact(Long contactId);

    void logViewClients(MobileClientFilter clientFilter);

    void logViewClient(Long clientId);

    void logViewMedication(Long medicationId);

    void logCreateMedication(Long medicationId);

    void logEditMedication(Long medicationId);

    void logViewDocuments(MobileDocumentFilter filter);

    void logViewDocument(Long documentId);

    void logDownloadCcd(Long clientId);

    void logViewCcd(Long clientId);

    void logFacesheetDownload(Long clientId);

    void logFacesheetView(Long clientId);

    void logDownloadDocument(Long documentId);

    void logViewCareTeamMember(Long careTeamMemberId);

    void logViewMarketplace(Long communityId);

    void logViewChats(List<String> conversationSids);

    void logCreateChat(String conversationSid);

    void logUpdateChatParticipants(EditConversationDto editConversationDto);

    void logStartCall(InitiateCallDto initiateCallDto);

    void logCancelSignatureRequest(Long requestId);

    void logRenewSignatureRequest(Long requestId);

    void logResendPIN(Long requestId);

    void logSignDocument(Long requestId);

    <T> void processPublishedEvent(AuditLogPublishedEvent<T> event);
}
