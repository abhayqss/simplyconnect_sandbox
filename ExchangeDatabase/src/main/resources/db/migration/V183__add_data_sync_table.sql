CREATE TABLE dbo.DataSyncStats (
    [id] [bigint] IDENTITY(1,1) NOT NULL PRIMARY KEY,
    [iteration_number] [bigint] NULL,
    [database_id] [bigint] NULL,
    [sync_service_name] [varchar](50) NULL,
    [started] [datetime2](7) NULL,
    [completed] [datetime2](7) NULL,
    CONSTRAINT fk_dataSyncStats_db_id FOREIGN KEY ([database_id]) REFERENCES [dbo].[SourceDatabase] ([id]),
    CONSTRAINT fk_parent_iteration_number FOREIGN KEY ([iteration_number]) REFERENCES dbo.DataSyncStats,
);
GO
-- migrate stats from DataSyncIteration
SET IDENTITY_INSERT dbo.DataSyncStats ON

INSERT INTO dbo.DataSyncStats (id, iteration_number, started, completed)
SELECT i.id, NULL, i.started, i.completed FROM dbo.DataSyncIteration i

SET IDENTITY_INSERT dbo.DataSyncStats OFF


DECLARE @NewIterationNumberSeed BIGINT
SELECT @NewIterationNumberSeed = CASE WHEN MAX(id) IS NULL THEN 0 ELSE max(id) END
FROM dbo.DataSyncStats;

DBCC CHECKIDENT (DataSyncStats, RESEED, @NewIterationNumberSeed)


-- drop DataSyncIteration
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[load_datasync_log_report]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[load_datasync_log_report];
GO

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_DataSyncIteration_DataSyncLog]') AND parent_object_id = OBJECT_ID(N'[dbo].[DataSyncLog]'))
ALTER TABLE [dbo].[DataSyncLog] DROP CONSTRAINT [FK_DataSyncIteration_DataSyncLog];
GO

DROP TABLE dbo.DataSyncIteration;
GO

ALTER TABLE [dbo].[DataSyncLog] ADD CONSTRAINT FK_DataSyncLog_IterationNumber
FOREIGN KEY ([iteration_number]) REFERENCES dbo.DataSyncStats([id]);
GO