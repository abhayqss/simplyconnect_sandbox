SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[SmokingStatusObservation] ADD [effective_time_high] datetime2(7) NULL;
GO
