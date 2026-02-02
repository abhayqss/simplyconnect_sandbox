package com.scnsoft.eldermark.dao.chat;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.chat.GroupChatParticipantHistory;

import java.util.Collection;
import java.util.List;

public interface GroupChatParticipantHistoryDao extends AppJpaRepository<GroupChatParticipantHistory, Long> {

    GroupChatParticipantHistory findFirstByTwilioParticipantSidAndDeletedDatetimeIsNull(String participantSid);

    List<GroupChatParticipantHistory> findByTwilioConversationSidIn(Collection<String> twilioConversationSids);

    List<GroupChatParticipantHistory> findByTwilioConversationSidInAndDeletedDatetimeIsNull(Collection<String> twilioConversationSids);
}
