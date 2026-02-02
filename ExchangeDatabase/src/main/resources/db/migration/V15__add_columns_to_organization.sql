SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Organization] ADD
	[testing_training] [bit] NULL,
	[inactive] [bit] NULL;
GO