SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[RecordDeletionHistory] ADD [creation_date] [datetime2](7) NOT NULL;
GO

ALTER TABLE [dbo].[DataSyncLog] ALTER COLUMN [date] [datetime2](7) NOT NULL;
GO

ALTER TABLE [dbo].[DataSyncDataLog] ALTER COLUMN [date] [datetime2](7) NOT NULL;
GO