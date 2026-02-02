SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Inquiry] ADD
	[phones] [varchar](max) NULL;
GO