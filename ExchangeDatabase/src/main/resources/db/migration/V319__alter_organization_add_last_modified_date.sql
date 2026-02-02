SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Organization] 
	ADD [last_modified] [datetime] NULL;
GO
