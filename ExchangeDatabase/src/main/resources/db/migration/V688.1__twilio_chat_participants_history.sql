IF OBJECT_ID('GroupChatParticipantHistory') IS NOT NULL
  DROP TABLE [dbo].[GroupChatParticipantHistory]
GO

CREATE TABLE GroupChatParticipantHistory (
  [id]                         bigint       not null  identity,
  constraint PK_GroupChatParticipantHistory primary key ([id]),

  [record_datetime]            datetime2(7) not null,
  [twilio_conversation_sid]    varchar(40)  not null,
  [twilio_participant_sid]     varchar(40)  not null,
  [twilio_identity]            varchar(20)  not null,
  [operation]                  varchar(20)  not null,
  [added_by_twilio_identity]   varchar(20),
  [deleted_by_twilio_identity] varchar(20),
)
GO
