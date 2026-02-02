package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.conversation.ConversationParticipatingAccessibilityFilter;
import com.scnsoft.eldermark.beans.twilio.user.IdentityListItemDto;
import com.scnsoft.eldermark.dto.conversation.ConversationParticipantAccessibilityDto;
import com.scnsoft.eldermark.dto.conversation.ConversationTwilioDbSyncCheckResult;
import com.scnsoft.eldermark.dto.conversation.CreateConversationDto;
import com.scnsoft.eldermark.dto.conversation.EditConversationDto;
import com.scnsoft.eldermark.service.ClientCareTeamMemberService;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.CommunityCareTeamMemberService;
import com.scnsoft.eldermark.service.ContactService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.service.twilio.TwilioUserService;
import com.scnsoft.eldermark.service.twilio.VideoCallHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ConversationFacadeImpl implements ConversationFacade {
    private static final Logger logger = LoggerFactory.getLogger(ConversationFacadeImpl.class);

    @Autowired
    private ChatService twilioChatService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private VideoCallHistoryService videoCallHistoryService;

    @Autowired
    private TwilioUserService twilioUserService;

    @Autowired
    private ClientCareTeamMemberService clientCareTeamMemberService;

    @Autowired
    private CommunityCareTeamMemberService communityCareTeamMemberService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Override
    public String generateToken() {
        return twilioChatService.generateToken(loggedUserService.getCurrentEmployeeId());
    }

    @Override
    @PreAuthorize("@chatSecurityService.canStart(#dto.employeeIds) and " +
            "(#dto.incidentReportId == null ? true : @incidentReportSecurityService.canView(#dto.incidentReportId))")
    public String create(@P("dto") CreateConversationDto dto) {
        return twilioChatService.createChat(dto.getEmployeeIds(), loggedUserService.getCurrentEmployeeId(),
                dto.getFriendlyName(), dto.getParticipatingClientId(), dto.getIncidentReportId());
    }

    @Override
    @PreAuthorize("@chatSecurityService.canAddMembers(#dto.addedEmployeeIds)")
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
    public ConversationParticipantAccessibilityDto getParticipantAccessibility(ConversationParticipatingAccessibilityFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        filter.setExcludedEmployeeId(loggedUserService.getCurrentEmployeeId());

        var dto = new ConversationParticipantAccessibilityDto();

        var start = System.currentTimeMillis();
        logger.info("clientService.existsChatAccessible start, filter = {}", filter);
        dto.setAreClientsAccessible(clientService.existsChatAccessible(permissionFilter, filter));
        logger.info("clientService.existsChatAccessible end in {}ms, filter = {}", System.currentTimeMillis() - start, filter);

        start = System.currentTimeMillis();
        logger.info("clientCareTeamMemberService.existsChatAccessible start, filter = {}", filter);
        dto.setAreClientCareTeamMembersAccessible(clientCareTeamMemberService.existsChatAccessible(permissionFilter, filter));
        logger.info("clientCareTeamMemberService.existsChatAccessible end in {}ms, filter = {}", System.currentTimeMillis() - start, filter);

        start = System.currentTimeMillis();
        logger.info("communityCareTeamMemberService.existsChatAccessible start, filter = {}", filter);
        dto.setAreCommunityCareTeamMembersAccessible(communityCareTeamMemberService.existsChatAccessible(permissionFilter, filter));
        logger.info("communityCareTeamMemberService.existsChatAccessible end in {}ms, filter = {}", System.currentTimeMillis() - start, filter);

        start = System.currentTimeMillis();
        logger.info("contactService.existsChatAccessible start, filter = {}", filter);
        dto.setAreContactsAccessible(contactService.existsChatAccessible(permissionFilter, filter));
        logger.info("contactService.existsChatAccessible end in {}ms, filter = {}", System.currentTimeMillis() - start, filter);

        return dto;
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

    /**
     * Intended to synchronize twilio users with DB employees after employees from DB was deleted.
     * Checks if and lists twilio users which doesn't have corresponding employee in DB
     *
     * @return
     */
    @Override
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public List<String> findDeletedUsers() {
        return twilioUserService.findDeletedUsers();
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public List<String> findBrokenUsers() {
        return twilioUserService.findBrokenUsers();
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public List<ConversationTwilioDbSyncCheckResult> findBrokenChats() {
        return twilioChatService.findBrokenChats();
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public void fixBrokenChats(List<String> conversationSids, boolean logActionsOnly) {
        twilioChatService.fixBrokenChats(conversationSids, logActionsOnly);
    }

    /**
     * Intended to synchronize twilio users with DB employees after employees from DB was deleted.
     * Provided users are removed as if they never existed:
     * 1. Call history where users participated is deleted
     * 2. Personal chats with users are deleted
     * 3. Messages of users are deleted in group chats
     * 4. Users are removed from group chats
     * 5. Users are deleted from Twilio
     *
     * @return
     */
    @Override
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public void deleteTwilioUsers(Collection<String> identities) {
        videoCallHistoryService.deleteCallHistoryForIdentities(identities);
        twilioChatService.deleteChatHistoryForIdentities(identities);
        twilioUserService.deleteIdentitiesFromTwilio(identities);
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
