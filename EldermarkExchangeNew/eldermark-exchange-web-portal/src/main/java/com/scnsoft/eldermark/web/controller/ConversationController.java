package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.conversation.*;
import com.scnsoft.eldermark.beans.twilio.user.IdentityListItemDto;
import com.scnsoft.eldermark.dto.CareTeamMemberRoleAvatarAwareDto;
import com.scnsoft.eldermark.dto.ClientNameCommunityOrganizationDto;
import com.scnsoft.eldermark.dto.conversation.*;
import com.scnsoft.eldermark.dto.docutrack.AttachFromDocutrackDto;
import com.scnsoft.eldermark.dto.docutrack.SendToDocutrackDto;
import com.scnsoft.eldermark.facade.*;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/conversations")
public class ConversationController {

    @Autowired
    private ConversationFacade conversationFacade;

    @Autowired
    private ContactFacade contactFacade;

    @Autowired
    private ClientFacade clientFacade;

    @Autowired
    private CommunityFacade communityFacade;

    @Autowired
    private OrganizationFacade organizationFacade;

    @Autowired
    private CareTeamMemberFacade careTeamMemberFacade;

    @Autowired
    private DocutrackFacade docutrackFacade;

    @PostMapping(path = "/access-token")
    public Response<String> generateToken() {
        return Response.successResponse(conversationFacade.generateToken());
    }

    @PostMapping
    public Response<String> create(@RequestBody CreateConversationDto dto) {
        return Response.successResponse(conversationFacade.create(dto));
    }

    @PostMapping("/participants")
    public Response<String> addParticipants(@RequestBody EditConversationDto dto) {
        return Response.successResponse(conversationFacade.addParticipants(dto));
    }

    @DeleteMapping("/participants")
    public Response<Void> deleteParticipants(@RequestBody EditConversationDto dto) {
        conversationFacade.deleteParticipants(dto);
        return Response.successResponse();
    }

    @PostMapping("/users")
    public Response<List<IdentityListItemDto>> getUsers(@RequestParam(name = "conversationSids") List<String> conversationSids) {
        return Response.successResponse(conversationFacade.getUsersByConversationSids(conversationSids));
    }

    @PostMapping("/leave")
    public Response<Void> leave(@RequestParam(name = "conversationSid") String conversationSid) {
        conversationFacade.leaveConversation(conversationSid);
        return Response.successResponse();
    }

    @GetMapping(value = "/participating/organizations")
    public Response<List<IdentifiedTitledEntityDto>> getOrganizationNames(
            @Valid @ModelAttribute ConversationParticipantAccessibilityFilter filter) {
        return Response.successResponse(organizationFacade.findChatAccessible(filter));
    }

    @GetMapping(value = "/participating/communities")
    public Response<List<IdentifiedTitledEntityDto>> getCommunityNames(
            @Valid @ModelAttribute ConversationParticipantAccessibilityCommunityFilter filter) {
        return Response.successResponse(communityFacade.findChatAccessible(filter));
    }

    @GetMapping("/participating/accessibility")
    public Response<ConversationParticipantAccessibilityDto> getParticipantAccessibility(
            @ModelAttribute ConversationParticipatingAccessibilityFilter filter) {
        return Response.successResponse(conversationFacade.getParticipantAccessibility(filter));
    }

    @GetMapping(value = "/participating/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ConversationClientListItemDto>> findClients(@Valid @ModelAttribute AccessibleChatClientFilter filter) {
        return Response.successResponse(clientFacade.findChatAccessibleClients(filter));
    }

    @GetMapping("/participating/clients/{clientId}")
    public Response<ClientNameCommunityOrganizationDto> findClientById(@PathVariable(name = "clientId") Long clientId) {
        return Response.successResponse(clientFacade.findChatClient(clientId));
    }

    @GetMapping(value = "/participating/client-care-team-members", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<CareTeamMemberRoleAvatarAwareDto>> findClientCareTeamMembers(
            @Valid @ModelAttribute AccessibleChatClientCareTeamFilter filter) {
        return Response.successResponse(careTeamMemberFacade.findChatAccessibleClientCareTeamMembers(filter));
    }

    @GetMapping(value = "/participating/community-care-team-members", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<CareTeamMemberRoleAvatarAwareDto>> findCommunityCareTeamMembers(
            @Valid @ModelAttribute AccessibleChatCommunityCareTeamFilter filter) {
        return Response.successResponse(careTeamMemberFacade.findChatAccessibleCommunityCareTeamMembers(filter));
    }

    @GetMapping(value = "/participating/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<IdentifiedNamedEntityDto>> findAccessibleContactNames(
            @Valid @ModelAttribute AccessibleChatContactFilter filter) {
        return Response.successResponse(contactFacade.findChatAccessibleNamesWithChatEnabled(filter));
    }

    @GetMapping(value = "/video/participating/client-care-team-members", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<CareTeamMemberRoleAvatarAwareDto>> findVideoCallAccessibleClientCareTeamMembers(
            @Valid @ModelAttribute AccessibleChatClientCareTeamFilter filter) {
        return Response.successResponse(careTeamMemberFacade.findVideoCallAccessibleClientCareTeamMembers(filter));
    }

    @GetMapping(value = "/video/participating/community-care-team-members", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<CareTeamMemberRoleAvatarAwareDto>> findVideoCallAccessibleCommunityCareTeamMembers(
            @Valid @ModelAttribute AccessibleChatCommunityCareTeamFilter filter) {
        return Response.successResponse(careTeamMemberFacade.findVideoCallAccessibleCommunityCareTeamMembers(filter));
    }

    @PutMapping(value = "/send-to-docutrack", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> sendToDocutrack(@RequestBody SendToDocutrackDto sendToDocutrackDto) {
        docutrackFacade.sendToDocutrack(sendToDocutrackDto);
        return Response.successResponse();
    }

    @PutMapping(value = "/attach-from-docutrack", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> attachFromDocutrack(@RequestBody AttachFromDocutrackDto attachFromDocutrackDto) {
        docutrackFacade.attachFromDocutrack(attachFromDocutrackDto);
        return Response.successResponse();
    }

    @PostMapping(value = "/{conversationSid}/messages/{messageSid}/reactions/{reactionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> addMessageReaction(@PathVariable("conversationSid") String conversationSid,
                                             @PathVariable("messageSid") String messageSid,
                                             @PathVariable("reactionId") Long reactionId) {
        conversationFacade.addReaction(conversationSid, messageSid, reactionId);
        return Response.successResponse();
    }

    @DeleteMapping(value = "/{conversationSid}/messages/{messageSid}/reactions/{reactionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> removeMessageReaction(@PathVariable("conversationSid") String conversationSid,
                                                @PathVariable("messageSid") String messageSid,
                                                @PathVariable("reactionId") Long reactionId) {
        conversationFacade.removeReaction(conversationSid, messageSid, reactionId);
        return Response.successResponse();
    }

    //non-ui endpoints

    //    synchronize twilio users and employees after employees were deleted
    @GetMapping(value = "/find-deleted-users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<String>> findDeletedUsers() {
        return Response.successResponse(conversationFacade.findDeletedUsers());
    }

    @GetMapping(value = "/find-broken-users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<String>> findBrokenUsers() {
        return Response.successResponse(conversationFacade.findBrokenUsers());
    }

    @GetMapping(value = "/find-broken-chats", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ConversationTwilioDbSyncCheckResult>> findBrokenChats() {
        return Response.successResponse(conversationFacade.findBrokenChats());
    }

    @PostMapping(value = "/fix-broken-chats", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ConversationTwilioDbSyncCheckResult>> findBrokenChats(
            @RequestParam("conversationSids") List<String> conversationSids,
            @RequestParam(value = "logActionsOnly", required = false, defaultValue = "false") boolean logActionsOnly
    ) {
        conversationFacade.fixBrokenChats(conversationSids, logActionsOnly);
        return Response.successResponse();
    }

    @PostMapping(value = "/delete-twilio-users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> deleteTwilioUsers(@RequestParam("identities") Set<String> identities) {
        conversationFacade.deleteTwilioUsers(identities);
        return Response.successResponse();
    }

    @PostMapping(value = "/{conversationSid}/connect", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> connectConversation(@PathVariable("conversationSid") String conversationSid) {
        conversationFacade.connectConversation(conversationSid);
        return Response.successResponse();
    }

    @PostMapping(value = "/{conversationSid}/disconnect", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> disconnectConversation(@PathVariable("conversationSid") String conversationSid) {
        conversationFacade.disconnectConversation(conversationSid);
        return Response.successResponse();
    }
    //non-ui endpoints end
}
