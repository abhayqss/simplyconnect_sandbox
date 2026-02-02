SET XACT_ABORT ON
GO

CREATE TABLE [dbo].[AuthorizationActivity_Person]
(
  [authorization_activity_id] [bigint] NOT NULL,
  [person_id] [bigint] NOT NULL UNIQUE,
  FOREIGN KEY ([authorization_activity_id]) REFERENCES [dbo].[AuthorizationActivity] ([id]),
  FOREIGN KEY ([person_id]) REFERENCES [dbo].[Person] ([id])
);

ALTER TABLE [dbo].[Immunization] ADD
	[person_id] [bigint] NULL,
  FOREIGN KEY([person_id]) REFERENCES [dbo].[Person] ([id]);
GO

ALTER TABLE [dbo].[Medication] ADD
	[person_id] [bigint] NULL,
  FOREIGN KEY([person_id]) REFERENCES [dbo].[Person] ([id]);
GO