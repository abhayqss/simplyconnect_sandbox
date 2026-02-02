SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Medication] ADD
	[administration_timing_value] [varchar](max) NULL;
GO
