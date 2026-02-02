package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.twilio.video.AddParticipantOutcomeItem;
import com.scnsoft.eldermark.beans.twilio.video.InitiateCallOutcome;
import com.scnsoft.eldermark.mobile.dto.conversation.call.*;
import com.scnsoft.eldermark.mobile.dto.conversation.call.history.CallHistoryListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VideoCallConversationFacade {

    InitiateCallOutcome initiateCall(InitiateCallDto initiateCallDto);

    List<AddParticipantOutcomeItem> addParticipants(AddCallParticipantsDto addCallParticipantsDto);

    void declineCall(String roomSid, String devicePushNotificationToken);

    void declineCallByRoomToken(DeclineByRoomTokenDto declineByRoomTokenDto);

    void removeParticipants(RemoveCallParticipantsDto removeCallParticipantsDto);

    Page<CallHistoryListItemDto> findHistory(Long employeeId, Pageable pageable);

    boolean canStartCall(String conversationSid);

    RoomTokenDto getActiveCallRoomWithToken(String conversationSid);

    List<AddParticipantOutcomeItem> addNonActiveOrPendingCallConversationParticipants(AddAlreadyInChatCallParticipantsDto addCallParticipantsDto);

    void muteParticipant(MuteParticipantDto muteParticipantDto);
}
