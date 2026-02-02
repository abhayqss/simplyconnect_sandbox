IF OBJECT_ID('PersonalChat') IS NOT NULL
  DROP TABLE [dbo].[PersonalChat]
GO

CREATE TABLE PersonalChat (
  [id]                      bigint      not null  identity,
  constraint PK_PersonalChat primary key ([id]),

  [twilio_conversation_sid] varchar(40) not null,
  [twilio_identity_1]       varchar(20) not null,
  [twilio_identity_2]       varchar(20) not null
)
GO
