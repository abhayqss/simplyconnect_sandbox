update SourceDatabase set is_chat_enabled = NULL, is_video_enabled = NULL

update employee_enc set twilio_user_sid = null, twilio_service_conversation_sid = null

delete from PersonalChat
delete from GroupChatParticipantHistory
update IncidentReport set twilio_conversation_sid = null

delete from VideoCallParticipantHistory
delete from VideoCallHistory
