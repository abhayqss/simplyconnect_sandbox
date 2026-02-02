package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.beans.twilio.user.CommunityAndRoleAwareIdentityListItemDto;
import com.scnsoft.eldermark.beans.twilio.user.IdentityListItemDto;
import com.scnsoft.eldermark.mobile.dto.conversation.CreateConversationDto;
import com.scnsoft.eldermark.mobile.dto.conversation.EditConversationDto;
import com.scnsoft.eldermark.mobile.dto.docutrack.SendToDocutrackDto;
import com.scnsoft.eldermark.mobile.facade.ConversationFacade;
import com.scnsoft.eldermark.mobile.facade.DocutrackFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversations")
public class ConversationController {

    @Autowired
    private ConversationFacade conversationFacade;

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

    @GetMapping
    public Response<List<String>> findConversations(
        @RequestParam(required = false) List<Long> employeeIds,
        @RequestParam(required = false) String friendlyName
    ) {
        return Response.successResponse(conversationFacade.find(employeeIds, friendlyName));
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

    @GetMapping("/users")
    public Response<List<IdentityListItemDto>> getUsers(@RequestParam(name = "conversationSids") List<String> conversationSids) {
        return Response.successResponse(conversationFacade.getUsersByConversationSids(conversationSids));
    }

    @PostMapping("/leave")
    public Response<Void> leave(@RequestParam(name = "conversationSid") String conversationSid) {
        conversationFacade.leaveConversation(conversationSid);
        return Response.successResponse();
    }

    @GetMapping("/detailed-users")
    public Response<List<CommunityAndRoleAwareIdentityListItemDto>> getDetailedUsers(@RequestParam(name = "conversationSid") String conversationSid) {
        return Response.successResponse(conversationFacade.getUserDetailsByConversationSid(conversationSid));
    }

    @PutMapping(value = "/send-to-docutrack", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> sendToDocutrack(@RequestBody SendToDocutrackDto sendToDocutrackDto) {
        docutrackFacade.sendToDocutrack(sendToDocutrackDto);
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
    @PostMapping(value = "/{conversationSid}/connect",  produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> connectConversation(@PathVariable("conversationSid") String conversationSid) {
        conversationFacade.connectConversation(conversationSid);
        return Response.successResponse();
    }

    @PostMapping(value = "/{conversationSid}/disconnect",  produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> disconnectConversation(@PathVariable("conversationSid") String conversationSid) {
        conversationFacade.disconnectConversation(conversationSid);
        return Response.successResponse();
    }
}
