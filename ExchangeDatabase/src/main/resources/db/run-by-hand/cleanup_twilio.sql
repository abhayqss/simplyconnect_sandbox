OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1
update employee set twilio_service_conversation_sid = null, twilio_user_sid = null where 1 = 1
delete from TwilioConversation where 1 = 1
delete from ConversationNotification where 1 = 1
delete from PersonalChat where 1 = 1
delete from GroupChatParticipantHistory where 1 = 1
delete from TwilioParticipantReadMessageStatus where 1 = 1
delete from VideoCallParticipantHistory where 1 = 1
delete from VideoCallHistory where 1 = 1
update IncidentReport set twilio_conversation_sid = null where 1 = 1
