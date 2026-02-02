SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[PersonTelecom] ADD [legacy_id] [bigint] NULL;
GO

ALTER TABLE [dbo].[PersonTelecom] ADD [legacy_table] [varchar](255) NULL;
GO

UPDATE [dbo].[PersonTelecom] SET
  [legacy_id]=[Person].[legacy_id],
  [legacy_table]=[Person].[legacy_table]
FROM [dbo].[PersonTelecom] LEFT JOIN [dbo].[Person]
ON [dbo].[PersonTelecom].[person_id]=[dbo].[Person].[id];
GO

ALTER TABLE [dbo].[PersonTelecom] ALTER COLUMN [legacy_id] [bigint] NOT NULL;
GO

ALTER TABLE [dbo].[PersonTelecom] ALTER COLUMN [legacy_table] [varchar](255) NOT NULL;
GO

ALTER TABLE [dbo].[PersonTelecom]
ADD CONSTRAINT UQ_PersonTelecom_legacy UNIQUE ([database_id], [legacy_table], [legacy_id], [sync_qualifier]);
GO