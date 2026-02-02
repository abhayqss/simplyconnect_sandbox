-- A follow-up to V9 migration

SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Encounter] ADD
	[person_id] [bigint] NULL,
  FOREIGN KEY([person_id]) REFERENCES [dbo].[Person] ([id]);
GO