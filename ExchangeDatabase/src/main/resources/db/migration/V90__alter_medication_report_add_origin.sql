SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[MedicationReport] ADD [origin] [varchar](20) NULL;
GO

