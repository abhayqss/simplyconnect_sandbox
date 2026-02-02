SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[SourceDatabase] ADD [max_days_to_process_appointment] [int] NULL;
GO

UPDATE [dbo].[SourceDatabase] SET [max_days_to_process_appointment] = 3;
GO