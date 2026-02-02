SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[MedScheduleCode] ALTER COLUMN [passing_times] [varchar](255) NULL;
GO
