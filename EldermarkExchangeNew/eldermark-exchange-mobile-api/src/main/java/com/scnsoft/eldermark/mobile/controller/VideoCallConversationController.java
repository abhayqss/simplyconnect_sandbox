package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.beans.twilio.video.AddParticipantOutcomeItem;
import com.scnsoft.eldermark.beans.twilio.video.InitiateCallOutcome;
import com.scnsoft.eldermark.mobile.dto.conversation.call.*;
import com.scnsoft.eldermark.mobile.dto.conversation.call.history.CallHistoryListItemDto;
import com.scnsoft.eldermark.mobile.facade.VideoCallConversationFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/conversations/video")
public class VideoCallConversationController {

    @Autowired
    private VideoCallConversationFacade videoCallConversationFacade;

    @PostMapping
    public Response<InitiateCallOutcome> initiateCall(@RequestBody InitiateCallDto initiateCallDto) {
        return Response.successResponse(videoCallConversationFacade.initiateCall(initiateCallDto));
    }

    @PostMapping(path = "/participants")
    public Response<List<AddParticipantOutcomeItem>> addParticipants(
            @RequestBody AddCallParticipantsDto addCallParticipantsDto) {
        return Response.successResponse(
                videoCallConversationFacade.addParticipants(addCallParticipantsDto)
        );
    }

    @DeleteMapping(path = "/participants")
    public Response<Void> removeParticipants(@RequestBody RemoveCallParticipantsDto removeCallParticipantsDto) {
        videoCallConversationFacade.removeParticipants(removeCallParticipantsDto);
        return Response.successResponse();
    }

    @PostMapping(path = "/decline")
    public Response<Void> declineCall(@RequestBody DeclineCallDto declineCallDto) {
        videoCallConversationFacade.declineCall(
                declineCallDto.getRoomSid(),
                declineCallDto.getDevicePushNotificationToken()
        );
        return Response.successResponse();
    }

    @PostMapping(path = "/decline-by-room-token")
    public Response<Void> declineCallByRoomToken(@RequestBody DeclineByRoomTokenDto declineByRoomTokenDto) {
        videoCallConversationFacade.declineCallByRoomToken(declineByRoomTokenDto);
        return Response.successResponse();
    }

    @GetMapping(path = "/history")
    public Response<List<CallHistoryListItemDto>> findHistory(@RequestParam("employeeId") Long employeeId, Pageable pageable) {
        return Response.pagedResponse(videoCallConversationFacade.findHistory(employeeId, pageable));
    }

    @GetMapping(path = "can-start")
    public Response<Boolean> canStartCall(@RequestParam("conversationSid") String conversationSid) {
        return Response.successResponse(videoCallConversationFacade.canStartCall(conversationSid));
    }

    @GetMapping(path = "/room-token")
    public Response<RoomTokenDto> getRoomAccessToken(@RequestParam("conversationSid") String conversationSid) {
        return Response.successResponse(videoCallConversationFacade.getActiveCallRoomWithToken(conversationSid));
    }

    @PostMapping(path = "/add-non-participating")
    public Response<List<AddParticipantOutcomeItem>> addNonActiveOrPendingCallConversationParticipants(
            @RequestBody AddAlreadyInChatCallParticipantsDto addCallParticipantsDto) {
        return Response.successResponse(
                videoCallConversationFacade.addNonActiveOrPendingCallConversationParticipants(addCallParticipantsDto)
        );
    }

    @PostMapping(path = "/participants/mute")
    public Response<Void> muteParticipant(@RequestBody @Valid MuteParticipantDto muteParticipantDto) {
        videoCallConversationFacade.muteParticipant(muteParticipantDto);
        return Response.successResponse();
    }
}
