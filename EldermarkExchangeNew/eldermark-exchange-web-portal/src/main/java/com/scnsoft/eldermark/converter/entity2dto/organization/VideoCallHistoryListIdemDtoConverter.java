package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.conversation.CallHistoryListItemDto;
import com.scnsoft.eldermark.dto.conversation.CallHistoryType;
import com.scnsoft.eldermark.entity.IdNamesAware;
import com.scnsoft.eldermark.entity.video.VideoCallHistory;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.twilio.ConversationUtils;
import com.scnsoft.eldermark.service.twilio.TwilioUserService;
import com.scnsoft.eldermark.service.twilio.VideoCallUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Component
@Transactional(propagation = Propagation.SUPPORTS)
public class VideoCallHistoryListIdemDtoConverter implements BiFunction<VideoCallHistory, Long, CallHistoryListItemDto> {

    @Autowired
    private TwilioUserService userService;

    @Override
    public CallHistoryListItemDto apply(VideoCallHistory callHistory, Long requestedEmployeeId) {
        var result = new CallHistoryListItemDto();

        var requestedUserIdentity = ConversationUtils.employeeIdToIdentity(requestedEmployeeId);
        var allIdentities = VideoCallUtils.getIdentities(callHistory.getParticipantsHistory());
        var otherIdentities = allIdentities.stream().filter(i -> !i.equals(requestedUserIdentity)).collect(Collectors.toList());

        result.setId(callHistory.getId());
        if (StringUtils.isNotEmpty(callHistory.getFriendlyConversationName())) {
            result.setName(callHistory.getFriendlyConversationName());
        } else {
            var isPersonalCall = allIdentities.size() == 2;
            if (isPersonalCall) {
                var otherIdentity = otherIdentities.get(0);

                var otherLatestEntry = VideoCallUtils.filterOfIdentity(callHistory.getParticipantsHistory(), otherIdentity)
                        .reduce((entry1, entry2) -> entry1.getStateDatetime().isAfter(entry2.getStateDatetime()) ? entry1 : entry2)
                        .orElseThrow();


                result.setName(userService.namesFromIdentities(otherIdentities).get(0).getFullName());

                var role = otherLatestEntry.getEmployeeRole();
                if (role != null) {
                    result.setRoleName(role.getCode().name());
                    result.setRoleTitle(role.getDisplayName());
                }
            } else {
                var name = userService.namesFromIdentities(otherIdentities).stream()
                        .map(IdNamesAware::getFullName)
                        .filter(StringUtils::isNotEmpty)
                        .sorted()
                        .collect(Collectors.joining(", "));

                result.setName(name);

            }
        }

        CallHistoryType type = resolveType(callHistory, requestedUserIdentity);
        result.setTypeName(type.name());
        result.setTypeTitle(type.getDisplayName());


        if (type != CallHistoryType.MISSED && callHistory.getEndDatetime() != null && callHistory.getStartDatetime() != null) {
            //todo - or until the moment current user left the call?
            result.setDuration(callHistory.getEndDatetime().toEpochMilli() - callHistory.getStartDatetime().toEpochMilli());
        }

        result.setDate(DateTimeUtils.toEpochMilli(callHistory.getRecordDatetime()));
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
