SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[ResidentAdmittanceHistory] 
	ALTER COLUMN [discharge_date] [datetime2](7);
GO