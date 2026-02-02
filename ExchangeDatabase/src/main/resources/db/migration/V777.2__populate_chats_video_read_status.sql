merge into TwilioConversation target
using (select twilio_conversation_sid, 'PERSONAL' conversation_type
       from PersonalChat
       union
       select twilio_conversation_sid, 'GROUP' conversation_type
       from GroupChatParticipantHistory) source
on target.twilio_conversation_sid = source.twilio_conversation_sid
WHEN NOT MATCHED THEN
    insert (twilio_conversation_sid, last_message_index, conversation_type)
    values (twilio_conversation_sid, -1, conversation_type);
GO

merge into EmployeeMissedCallReadStatus target
using (select distinct ph.twilio_identity,
                       isnull(ch.updated_conversation_sid, ch.initial_conversation_sid) twilio_conversation_sid
       from VideoCallHistory ch
                join VideoCallParticipantHistory ph on ch.id = ph.video_call_history_id
) source
on target.twilio_identity = source.twilio_identity and target.twilio_conversation_sid = source.twilio_conversation_sid
    when not matched then
insert (employee_id, twilio_conversation_sid, twilio_identity, last_video_history_read)
values (
        convert(bigint, REPLACE(twilio_identity, 'e', '')),
        twilio_conversation_sid,
        twilio_identity,
        getdate()
       );
GO
