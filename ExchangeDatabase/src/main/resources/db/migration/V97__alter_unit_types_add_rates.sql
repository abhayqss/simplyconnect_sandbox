SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[UnitType] ADD
	[monthly_rate] [decimal](19,2) NULL,
	[daily_rate] [decimal](19,2) NULL;
GO
