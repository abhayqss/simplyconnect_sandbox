package com.scnsoft.eldermark.mobile.facade.home;

import com.scnsoft.eldermark.beans.ConversationType;
import com.scnsoft.eldermark.beans.projection.AvatarIdNameAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.NamesAware;
import com.scnsoft.eldermark.entity.MissedChatsAndCalls;
import com.scnsoft.eldermark.mobile.dto.home.MissedChatsAndCallsHomeSectionDto;
import com.scnsoft.eldermark.service.MissedChatsAndCallsService;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.service.twilio.ConversationUtils;
import com.scnsoft.eldermark.service.twilio.TwilioUserService;
import com.scnsoft.eldermark.service.twilio.VideoCallHistoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MissedChatsAndCallsHomeSectionProviderImpl implements MissedChatsAndCallsHomeSectionProvider {

    @Autowired
    private MissedChatsAndCallsService missedChatsAndCallsService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private TwilioUserService twilioUserService;

    @Autowired
    private VideoCallHistoryService videoCallHistoryService;

    @Override
    public List<MissedChatsAndCallsHomeSectionDto> loadMissedChatsAndCalls(Long currentEmployeeId, int limit) {
        var missedEntityItems = missedChatsAndCallsService.loadMissedChatsAndCalls(currentEmployeeId, limit);

        var result = missedEntityItems.stream()
                .map(missedEntity -> convertMissedChatCall(currentEmployeeId, missedEntity))
                .collect(Collectors.toList());

        return result;
    }

    private MissedChatsAndCallsHomeSectionDto convertMissedChatCall(Long currentEmployeeId, MissedChatsAndCalls missedEntity) {
        var dto = new MissedChatsAndCallsHomeSectionDto();

        if (ConversationType.PERSONAL == missedEntity.getConversationType()) {
            var otherUser = getOtherPersonalChatUser(currentEmployeeId, missedEntity);

            dto.setAvatarId(otherUser.getAvatarId());
            dto.setAvatarName(otherUser.getAvatarAvatarName());
            dto.setFirstName(otherUser.getFirstName());
            dto.setLastName(otherUser.getLastName());
        } else if (ConversationType.GROUP == missedEntity.getConversationType()) {
            if (StringUtils.isNotEmpty(missedEntity.getFriendlyConversationName())) {
                dto.setGroupChatName(missedEntity.getFriendlyConversationName());
            } else {
                var selfIdentity = ConversationUtils.employeeIdToIdentity(currentEmployeeId);
                var otherIdentities = chatService.getGroupChatIdentities(
                                List.of(missedEntity.getConversationSid()),
                                selfIdentity
                        )
                        .stream()
                        .filter(identity -> !selfIdentity.equals(identity))
                        .collect(Collectors.toList());

                var names = twilioUserService.findByIdentities(otherIdentities, NamesAware.class)
                        .stream()
                        .map(NamesAware::getFullName)
                        .sorted()
                        .collect(Collectors.joining(", "));
                dto.setGroupChatName(names);
            }
        }

        if (missedEntity.getCallHistoryId() != null) {
            var allIdentities = videoCallHistoryService.getCallParticipantsIdentities(missedEntity.getCallHistoryId());
            dto.setParticipatingEmployeeIds(ConversationUtils.employeeIdsFromIdentities(allIdentities));
        }

        dto.setDateTime(missedEntity.getDateTime().toEpochMilli());
        dto.setCallHistoryId(missedEntity.getCallHistoryId());
        dto.setConversationSid(missedEntity.getConversationSid());
        dto.setIsConversationDisconnected(!chatService.isConnected(missedEntity.getConversationSid()));

        return dto;
    }

    private PersonalChatCallUserProjection getOtherPersonalChatUser(Long currentEmployeeId, MissedChatsAndCalls missedEntity) {
        var selfIdentity = ConversationUtils.employeeIdToIdentity(currentEmployeeId);
        var userIdentities = chatService.getPersonalChatIdentities(
                List.of(missedEntity.getConversationSid()),
                selfIdentity
        );

        var otherUserIdentity = userIdentities.stream().filter(
                        identity -> !selfIdentity.equals(identity)
                )
                .findFirst()
                .orElseThrow();

        return twilioUserService.findByIdentities(List.of(otherUserIdentity), PersonalChatCallUserProjection.class).get(0);
    }

    interface PersonalChatCallUserProjection extends IdAware, NamesAware, AvatarIdNameAware {
    }
}
