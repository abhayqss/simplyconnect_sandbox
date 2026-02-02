package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.twilio.video.AddParticipantOutcomeItem;
import com.scnsoft.eldermark.beans.twilio.video.InitiateCallOutcome;
import com.scnsoft.eldermark.dto.conversation.CallHistoryListItemDto;
import com.scnsoft.eldermark.dto.conversation.call.AddCallParticipantsDto;
import com.scnsoft.eldermark.dto.conversation.call.CallRoomDto;
import com.scnsoft.eldermark.dto.conversation.call.InitiateCallDto;
import com.scnsoft.eldermark.dto.conversation.call.RemoveCallParticipantsDto;
import com.scnsoft.eldermark.facade.VideoCallConversationFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping(path = "/incident-reports/call")
    public Response<InitiateCallOutcome> initiateIrCall(@RequestBody InitiateCallDto initiateCallDto) {
        return Response.successResponse(videoCallConversationFacade.initiateIrCall(initiateCallDto));
    }

    @PostMapping(path = "/participants")
    public Response<List<AddParticipantOutcomeItem>> addParticipants(@RequestBody AddCallParticipantsDto addCallParticipantsDto) {
        return Response.successResponse(videoCallConversationFacade.addParticipants(addCallParticipantsDto));
    }

    @DeleteMapping(path = "/participants")
    public Response<Void> removeParticipants(@RequestBody RemoveCallParticipantsDto removeCallParticipantsDto) {
        videoCallConversationFacade.removeParticipants(removeCallParticipantsDto);
        return Response.successResponse();
    }

    @PostMapping(path = "/decline")
    public Response<Void> declineCall(@RequestBody CallRoomDto callRoomDto) {
        videoCallConversationFacade.declineCall(callRoomDto.getRoomSid());
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
}
