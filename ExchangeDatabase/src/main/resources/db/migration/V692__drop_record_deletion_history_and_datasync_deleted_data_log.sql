IF OBJECT_ID('RecordDeletionHistory') IS NOT NULL
	DROP TABLE [dbo].[RecordDeletionHistory];
GO

IF OBJECT_ID('DataSyncDeletedDataLog') IS NOT NULL
	DROP TABLE [dbo].[DataSyncDeletedDataLog];
GO
