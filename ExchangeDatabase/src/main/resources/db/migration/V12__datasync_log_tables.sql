SET XACT_ABORT ON
GO

EXEC sp_rename 'DataSyncLog', 'DataSyncDataLog';
EXEC sp_rename 'DataSyncProblem', 'DataSyncLog';
EXEC sp_rename 'DataSyncLog.failed_portion_ids', 'description', 'COLUMN'
EXEC sp_rename 'DataSyncLog.source_entity_name', 'table_name', 'COLUMN'
GO

