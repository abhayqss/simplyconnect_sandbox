IF OBJECT_ID('GroupChatParticipantHistory') IS NOT NULL
  DROP TABLE [dbo].[GroupChatParticipantHistory]
GO

CREATE TABLE GroupChatParticipantHistory (
  [id]                         bigint       not null  identity,
  constraint PK_GroupChatParticipantHistory primary key ([id]),

  [twilio_conversation_sid]    varchar(40)  not null,
  [twilio_participant_sid]     varchar(40)  not null,
  [twilio_identity]            varchar(20)  not null,
  [added_datetime]             datetime2(7) not null,
  [added_by_twilio_identity]   varchar(20)  not null,
  [deleted_datetime]           datetime2(7),
  [deleted_reason]             varchar(20),
  [removed_by_twilio_identity] varchar(20),
)
GO

CREATE INDEX IX_GroupChatParticipantHistory_twilio_participant_sid
  ON GroupChatParticipantHistory (twilio_participant_sid) INCLUDE ([deleted_datetime]);
GO
