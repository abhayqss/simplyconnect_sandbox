SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[ReactionObservation] ADD
	[legacy_id] [bigint] NOT NULL,
	[legacy_table] [varchar](255) NOT NULL;
	
ALTER TABLE [dbo].[SeverityObservation] ADD
	[legacy_id] [bigint] NOT NULL,
	[legacy_table] [varchar](255) NOT NULL;
	
GO
