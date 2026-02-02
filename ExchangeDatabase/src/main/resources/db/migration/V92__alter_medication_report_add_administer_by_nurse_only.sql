SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[MedicationReport] ADD [administer_by_nurse_only] [bit] NULL;
GO

