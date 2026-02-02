package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.twilio.video.AddParticipantOutcomeItem;
import com.scnsoft.eldermark.beans.twilio.video.InitiateCallOutcome;
import com.scnsoft.eldermark.dto.conversation.CallHistoryListItemDto;
import com.scnsoft.eldermark.dto.conversation.call.AddCallParticipantsDto;
import com.scnsoft.eldermark.dto.conversation.call.InitiateCallDto;
import com.scnsoft.eldermark.dto.conversation.call.RemoveCallParticipantsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VideoCallConversationFacade {

    InitiateCallOutcome initiateCall(InitiateCallDto initiateCallDto);

    InitiateCallOutcome initiateIrCall(InitiateCallDto initiateCallDto);

    List<AddParticipantOutcomeItem> addParticipants(AddCallParticipantsDto addCallParticipantsDto);

    void declineCall(String roomSid);

    void removeParticipants(RemoveCallParticipantsDto removeCallParticipantsDto);

    Page<CallHistoryListItemDto> findHistory(Long employeeId, Pageable pageable);

    boolean canStartCall(String conversationSid);
}
