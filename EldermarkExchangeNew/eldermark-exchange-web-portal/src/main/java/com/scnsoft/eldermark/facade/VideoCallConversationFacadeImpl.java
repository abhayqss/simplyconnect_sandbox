package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.twilio.video.AddParticipantOutcomeItem;
import com.scnsoft.eldermark.beans.twilio.video.InitiateCallOutcome;
import com.scnsoft.eldermark.dto.conversation.CallHistoryListItemDto;
import com.scnsoft.eldermark.dto.conversation.call.AddCallParticipantsDto;
import com.scnsoft.eldermark.dto.conversation.call.InitiateCallDto;
import com.scnsoft.eldermark.dto.conversation.call.RemoveCallParticipantsDto;
import com.scnsoft.eldermark.entity.video.VideoCallHistory;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.service.security.VideoCallSecurityService;
import com.scnsoft.eldermark.service.twilio.VideoCallHistoryService;
import com.scnsoft.eldermark.service.twilio.VideoCallService;
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
//todo apply security
public class VideoCallConversationFacadeImpl implements VideoCallConversationFacade {

    @Autowired
    private VideoCallService videoCallService;

    @Autowired
    private VideoCallSecurityService videoCallSecurityService;

    @Autowired
    private VideoCallHistoryService videoCallHistoryService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private BiFunction<VideoCallHistory, Long, CallHistoryListItemDto> videoCallHistoryListIdemDtoConverter;

    @Autowired
    private LoggedUserService loggedUserService;

    @Override
    @PreAuthorize("@videoCallSecurityService.canStart(#initiateCallDto.conversationSid, #initiateCallDto.employeeIds)")
    public InitiateCallOutcome initiateCall(@P("initiateCallDto") InitiateCallDto initiateCallDto) {
        if (StringUtils.isNotEmpty(initiateCallDto.getConversationSid())) {
            return videoCallService.initiateCallInConversation(initiateCallDto.getConversationSid(), null,
                    initiateCallDto.getEmployeeIds(), loggedUserService.getCurrentEmployeeId());
        }
        return videoCallService.initiateCallForEmployees(
                null,
                initiateCallDto.getEmployeeIds(),
                loggedUserService.getCurrentEmployeeId(),
                null //todo support participating client on web when initiate call for employees?
        );
    }

    @Override
    @PreAuthorize("@videoCallSecurityService.canStartIrCall(#initiateCallDto.conversationSid, #initiateCallDto.incidentReportId)")
    public InitiateCallOutcome initiateIrCall(@P("initiateCallDto") InitiateCallDto initiateCallDto) {
        return videoCallService.initiateCallInIrConversation(initiateCallDto.getIncidentReportId(), loggedUserService.getCurrentEmployeeId());
    }

    @Override
    @PreAuthorize("@videoCallSecurityService.canAddMembers(#addCallParticipantsDto.employeeIds)")
    public List<AddParticipantOutcomeItem> addParticipants(@P("addCallParticipantsDto") AddCallParticipantsDto addCallParticipantsDto) {
       return videoCallService.addParticipants(addCallParticipantsDto.getRoomSid(), addCallParticipantsDto.getFriendlyName(),
               addCallParticipantsDto.getEmployeeIds(), loggedUserService.getCurrentEmployeeId(), addCallParticipantsDto.getParticipatingClientId());
    }

    @Override
    public void declineCall(String roomSid) {
        videoCallService.declineCall(roomSid, loggedUserService.getCurrentEmployeeId());
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
}
