package com.scnsoft.eldermark.mobile.converters.conversation;

import com.scnsoft.eldermark.beans.projection.IdNameAvatarIdStatusAware;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.IdNamesAware;
import com.scnsoft.eldermark.entity.video.VideoCallHistory;
import com.scnsoft.eldermark.mobile.dto.conversation.call.history.CallHistoryListItemDto;
import com.scnsoft.eldermark.mobile.dto.conversation.call.history.CallHistoryType;
import com.scnsoft.eldermark.mobile.dto.conversation.call.history.IdNameAvatarIdActiveDto;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.twilio.*;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class VideoCallHistoryListIdemDtoConverter implements BiFunction<VideoCallHistory, Long, CallHistoryListItemDto> {

    @Autowired
    private ChatService chatService;

    @Autowired
    private TwilioUserService userService;

    @Autowired
    private TwilioAccessTokenService twilioAccessTokenService;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public CallHistoryListItemDto apply(VideoCallHistory callHistory, Long requestedEmployeeId) {
        var result = new CallHistoryListItemDto();

        var requestedUserIdentity = ConversationUtils.employeeIdToIdentity(requestedEmployeeId);
        var allIdentities = VideoCallUtils.getIdentities(callHistory.getParticipantsHistory());
        var otherIdentities = allIdentities.stream().filter(i -> !i.equals(requestedUserIdentity)).collect(Collectors.toList());

        result.setId(callHistory.getId());

        String name;

        if (StringUtils.isNotEmpty(callHistory.getFriendlyConversationName())) {
            name = callHistory.getFriendlyConversationName();
        } else {
            var isPersonalCall = allIdentities.size() == 2;
            if (isPersonalCall) {
                name = userService.namesFromIdentities(otherIdentities).get(0).getFullName();
            } else {
                name = userService.namesFromIdentities(otherIdentities).stream()
                        .map(IdNamesAware::getFullName)
                        .filter(StringUtils::isNotEmpty)
                        .sorted()
                        .collect(Collectors.joining(", "));
            }
        }

        result.setName(name);

        CallHistoryType type = resolveType(callHistory, requestedUserIdentity);
        result.setTypeName(type.name());
        result.setTypeTitle(type.getDisplayName());


        if (type != CallHistoryType.MISSED && callHistory.getEndDatetime() != null && callHistory.getStartDatetime() != null) {
            //todo - or until the moment current user left the call?
            result.setDuration(callHistory.getEndDatetime().toEpochMilli() - callHistory.getStartDatetime().toEpochMilli());
        }

        result.setDate(DateTimeUtils.toEpochMilli(callHistory.getRecordDatetime()));

        result.setParticipatingEmployeeIds(ConversationUtils.employeeIdsFromIdentities(allIdentities));
        result.setParticipatingEmployees(
                employeeService.findAllById(
                                ConversationUtils.employeeIdsFromIdentities(allIdentities),
                                IdNameAvatarIdStatusAware.class
                        ).stream()
                        .map(idNameAvatarIdStatusAware -> new IdNameAvatarIdActiveDto(
                                idNameAvatarIdStatusAware.getId(),
                                idNameAvatarIdStatusAware.getAvatarId(),
                                idNameAvatarIdStatusAware.getAvatarAvatarName(),
                                idNameAvatarIdStatusAware.getStatus() == EmployeeStatus.ACTIVE
                        ))
                        .collect(Collectors.toList()));

        result.setConversationSid(VideoCallUtils.getConversation(callHistory));

        result.setIsConversationDisconnected(!chatService.isConnected(result.getConversationSid()));

        if (callHistory.getStartDatetime() != null && callHistory.getEndDatetime() == null &&
                !VideoCallUtils.participantWasRemoved(callHistory.getParticipantsHistory(), requestedUserIdentity)) {
            result.setRoomAccessToken(
                    twilioAccessTokenService.generateVideoToken(
                            requestedUserIdentity,
                            callHistory.getRoomSid()
                    )
            );
        }
        result.setRoomSid(callHistory.getRoomSid());

        return result;
    }

    private CallHistoryType resolveType(VideoCallHistory callHistory, String currentUserIdentity) {
        //todo missed for caller?
        if (callHistory.getCallerTwilioIdentity().equals(currentUserIdentity)) {
            return CallHistoryType.OUTGOING;
        }
        return VideoCallUtils.findFirstIdentityEntry(
                        VideoCallUtils.filterWithOnCall(callHistory.getParticipantsHistory()), currentUserIdentity)
                .map(x -> CallHistoryType.INCOMING)
                .orElse(CallHistoryType.MISSED);

    }
}
