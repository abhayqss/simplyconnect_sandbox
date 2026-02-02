SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[PersonAddress] ADD [legacy_id] [bigint] NULL;
GO

ALTER TABLE [dbo].[PersonAddress] ADD [legacy_table] [varchar](255) NULL;
GO

UPDATE [dbo].[PersonAddress] SET
  [legacy_id]=[Person].[legacy_id],
  [legacy_table]=[Person].[legacy_table]
FROM [dbo].[PersonAddress] LEFT JOIN [dbo].[Person]
ON [dbo].[PersonAddress].[person_id]=[dbo].[Person].[id];
GO

ALTER TABLE [dbo].[PersonAddress] ALTER COLUMN [legacy_id] [bigint] NOT NULL;
GO

ALTER TABLE [dbo].[PersonAddress] ALTER COLUMN [legacy_table] [varchar](255) NOT NULL;
GO

ALTER TABLE [dbo].[PersonAddress]
ADD CONSTRAINT UQ_PersonAddress_legacy UNIQUE ([database_id], [legacy_table], [legacy_id]);
GO