package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.chat.TwilioParticipantReadMessageStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TwilioParticipantReadMessageStatusDao extends AppJpaRepository<TwilioParticipantReadMessageStatus, String> {

    @Modifying
    @Query(nativeQuery = true,
            value = "merge into TwilioParticipantReadMessageStatus source  " +
                    "using  " +
                    "    (select :participantSid         as participant_sid,  " +
                    "            :messageIndex          as message_index,  " +
                    "            :twilioConversationSid as conversation_sid,  " +
                    "            :employeeId            as employee_id) target  " +
                    "on source.twilio_participant_sid = target.participant_sid  " +
                    "when matched then  " +
                    "    update  " +
                    "    set last_read_message_index = IIF(target.message_index > last_read_message_index, target.message_index,  " +
                    "                                      last_read_message_index)  " +
                    "when not matched then  " +
                    "    insert (twilio_participant_sid, twilio_conversation_sid, last_read_message_index, employee_id)  " +
                    "    values (participant_sid, conversation_sid, message_index, employee_id);")
    void upsertLastReadMessage(@Param("participantSid") String participantSid,
                               @Param("messageIndex") Integer messageIndex,
                               @Param("twilioConversationSid") String conversationSid,
                               @Param("employeeId") Long employeeId);
}
