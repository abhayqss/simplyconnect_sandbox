package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.twilio.video.AddParticipantOutcomeItem;
import com.scnsoft.eldermark.beans.twilio.video.InitiateCallOutcome;
import com.scnsoft.eldermark.entity.video.VideoCallHistory;
import com.scnsoft.eldermark.mobile.dto.conversation.call.*;
import com.scnsoft.eldermark.mobile.dto.conversation.call.history.CallHistoryListItemDto;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.service.security.VideoCallSecurityService;
import com.scnsoft.eldermark.service.twilio.*;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.BiFunction;

@Service
@Transactional
public class VideoCallConversationFacadeImpl implements VideoCallConversationFacade {

    @Autowired
    private VideoCallService videoCallService;

    @Autowired
    private VideoCallSecurityService videoCallSecurityService;

    @Autowired
    private VideoCallHistoryService videoCallHistoryService;

    @Autowired
    private BiFunction<VideoCallHistory, Long, CallHistoryListItemDto> videoCallHistoryListIdemDtoConverter;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private TwilioAccessTokenService accessTokenService;

    @Override
    @PreAuthorize("@videoCallSecurityService.canStart(#initiateCallDto.conversationSid, #initiateCallDto.employeeIds)")
    public InitiateCallOutcome initiateCall(@P("initiateCallDto") InitiateCallDto initiateCallDto) {
        if (StringUtils.isNotEmpty(initiateCallDto.getConversationSid())) {
            return videoCallService.initiateCallInConversation(initiateCallDto.getConversationSid(), initiateCallDto.getFriendlyName(),
                    initiateCallDto.getEmployeeIds(), loggedUserService.getCurrentEmployeeId());
        }
        return videoCallService.initiateCallForEmployees(
                initiateCallDto.getFriendlyName(),
                initiateCallDto.getEmployeeIds(),
                loggedUserService.getCurrentEmployeeId(),
                initiateCallDto.getParticipatingClientId()
        );
    }

    @Override
    @PreAuthorize("@videoCallSecurityService.canAddMembers(#addCallParticipantsDto.employeeIds)")
    public List<AddParticipantOutcomeItem> addParticipants(
            @P("addCallParticipantsDto") AddCallParticipantsDto addCallParticipantsDto) {
        return videoCallService.addParticipants(
                addCallParticipantsDto.getRoomSid(),
                addCallParticipantsDto.getFriendlyName(),
                addCallParticipantsDto.getEmployeeIds(),
                loggedUserService.getCurrentEmployeeId(),
                addCallParticipantsDto.getParticipatingClientId()
        );
    }

    @Override
    public void declineCall(String roomSid, String devicePushNotificationToken) {
        videoCallService.declineCall(roomSid, loggedUserService.getCurrentEmployeeId(), devicePushNotificationToken);
    }

    @Override
    public void declineCallByRoomToken(DeclineByRoomTokenDto declineByRoomTokenDto) {
        var token = accessTokenService.parse(declineByRoomTokenDto.getRoomAccessToken());
        var calleeEmployeeId = ConversationUtils.employeeIdFromIdentity(token.getIdentity());
        var roomSid = VideoCallUtils.getRoomSidOrThrow(token);

        videoCallService.declineCall(roomSid, calleeEmployeeId);
    }

    @Override
    public void removeParticipants(RemoveCallParticipantsDto removeCallParticipantsDto) {
        videoCallService.removeParticipants(removeCallParticipantsDto.getRoomSid(), removeCallParticipantsDto.getIdentities(),
                loggedUserService.getCurrentEmployeeId());
    }

    @Override
    @PreAuthorize("@videoCallSecurityService.canViewHistory()")
    public Page<CallHistoryListItemDto> findHistory(Long employeeId, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        pageable = PaginationUtils.applyEntitySort(pageable, CallHistoryListItemDto.class);
        if (loggedUserService.getCurrentEmployeeId().equals(employeeId)) {
            videoCallHistoryService.historyListViewed(employeeId);
        }
        return videoCallHistoryService.findByEmployeeId(permissionFilter, employeeId, pageable)
                .map(callHistory -> videoCallHistoryListIdemDtoConverter.apply(callHistory, employeeId));
    }

    @Override
    public boolean canStartCall(String conversationSid) {
        return videoCallSecurityService.canStart(conversationSid, null);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomTokenDto getActiveCallRoomWithToken(String conversationSid) {
        var roomWithToken = videoCallService.getActiveCallRoomWithToken(loggedUserService.getCurrentEmployeeId(), conversationSid);
        return new RoomTokenDto(roomWithToken.getFirst(), roomWithToken.getSecond());
    }

    @Override
    public List<AddParticipantOutcomeItem> addNonActiveOrPendingCallConversationParticipants(
            AddAlreadyInChatCallParticipantsDto addCallParticipantsDto) {
        return videoCallService.addNonActiveOrPendingCallConversationParticipants(
                addCallParticipantsDto.getRoomSid(),
                addCallParticipantsDto.getEmployeeIds(),
                loggedUserService.getCurrentEmployeeId()
        );
    }

    @Override
    public void muteParticipant(MuteParticipantDto muteParticipantDto) {
        videoCallService.muteParticipant(
                muteParticipantDto.getRoomSid(),
                muteParticipantDto.getEmployeeId(),
                loggedUserService.getCurrentEmployeeId());
    }
}
