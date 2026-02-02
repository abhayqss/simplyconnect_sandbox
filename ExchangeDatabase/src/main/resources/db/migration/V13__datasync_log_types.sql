SET XACT_ABORT ON
GO

-- Data sync log
CREATE TABLE [dbo].[DataSyncLogType] (
  [id] [bigint] PRIMARY KEY,
  [name] [varchar](255) NOT NULL
);

INSERT INTO [dbo].[DataSyncLogType] ([id], [name]) VALUES (1, 'ERROR'), (2, 'INFO');

DELETE FROM [dbo].[DataSyncLog];

ALTER TABLE [dbo].[DataSyncLog] ADD [type_id] [bigint] NOT NULL,
FOREIGN KEY ([type_id]) REFERENCES [dbo].[DataSyncLogType] ([id]);

-- Data sync data log
CREATE TABLE [dbo].[DataSyncObjectStatus] (
  [id] [bigint] PRIMARY KEY,
  [name] [varchar](255) NOT NULL
);

INSERT INTO [dbo].[DataSyncObjectStatus] ([id], [name]) VALUES (1, 'NEW'), (2, 'EXISTING');

DELETE FROM [dbo].[DataSyncDataLog];

ALTER TABLE [dbo].[DataSyncDataLog] DROP COLUMN [source_object_status];

ALTER TABLE [dbo].[DataSyncDataLog] ADD [source_object_status_id] [bigint] NOT NULL,
FOREIGN KEY ([source_object_status_id]) REFERENCES [dbo].[DataSyncObjectStatus] ([id]);

GO

