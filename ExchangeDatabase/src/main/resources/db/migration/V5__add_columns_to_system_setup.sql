SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[SystemSetup] ADD
	[moveouts_count_on_next_day] [varchar](5) NULL;
GO