package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.twilio.user.CommunityAndRoleAwareIdentityListItemDto;
import com.scnsoft.eldermark.beans.twilio.user.IdentityListItemDto;
import com.scnsoft.eldermark.mobile.dto.conversation.CreateConversationDto;
import com.scnsoft.eldermark.mobile.dto.conversation.EditConversationDto;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.twilio.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ConversationFacadeImpl implements ConversationFacade {

    @Autowired
    private ChatService twilioChatService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Override
    public String generateToken() {
        return twilioChatService.generateToken(loggedUserService.getCurrentEmployeeId());
    }

    @Override
    @PreAuthorize("@chatSecurityService.canStart(#dto.employeeIds)")
    public String create(@P("dto") CreateConversationDto dto) {
        return twilioChatService.createChat(dto.getEmployeeIds(), loggedUserService.getCurrentEmployeeId(),
                dto.getFriendlyName(), dto.getParticipatingClientId(), null);
    }

    @Override
    public List<String> find(List<Long> employeeIds, String friendlyName) {
        return twilioChatService.findConversations(
                employeeIds,
                friendlyName,
                loggedUserService.getCurrentEmployeeId()
        );
    }

    @Override
    @PreAuthorize("T(org.apache.commons.collections4.CollectionUtils).isNotEmpty(#dto.addedEmployeeIds) ? " +
            "@chatSecurityService.canAddMembers(#dto.addedEmployeeIds) : " +
            "true")
    public String addParticipants(EditConversationDto dto) {
        return twilioChatService.addParticipants(
                dto.getConversationSid(),
                dto.getFriendlyName(),
                dto.getAddedEmployeeIds(),
                loggedUserService.getCurrentEmployeeId(),
                dto.getParticipatingClientId());
    }

    @Override
    public void deleteParticipants(EditConversationDto dto) {
        twilioChatService.deleteParticipants(dto.getConversationSid(), dto.getRemovedEmployeeIds(), loggedUserService.getCurrentEmployeeId());
    }

    @Override
    public List<IdentityListItemDto> getUsersByConversationSids(List<String> conversationSids) {
        return twilioChatService.findEmployeeChatUsersByConversationSids(conversationSids, loggedUserService.getCurrentEmployeeId());
    }

    @Override
    public void leaveConversation(String conversationSid) {
        var currentEmployeeId = loggedUserService.getCurrentEmployeeId();
        twilioChatService.leaveConversation(conversationSid, currentEmployeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityAndRoleAwareIdentityListItemDto> getUserDetailsByConversationSid(String conversationSid) {
        return twilioChatService.findEmployeeChatUserDetailsByConversationSid(conversationSid, loggedUserService.getCurrentEmployeeId());
    }

    @Override
    public void addReaction(String conversationSid, String messageSid, Long reactionId) {
        var currentEmployeeId = loggedUserService.getCurrentEmployeeId();
        twilioChatService.addReaction(conversationSid, messageSid, reactionId, currentEmployeeId);

    }

    @Override
    public void removeReaction(String conversationSid, String messageSid, Long reactionId) {
        var currentEmployeeId = loggedUserService.getCurrentEmployeeId();
        twilioChatService.removeReaction(conversationSid, messageSid, reactionId, currentEmployeeId);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public void connectConversation(String conversationSid) {
        twilioChatService.updateConversationsDisconnection(Set.of(conversationSid), false, true);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public void disconnectConversation(String conversationSid) {
        twilioChatService.updateConversationsDisconnection(Set.of(conversationSid), true, false);
    }
}
