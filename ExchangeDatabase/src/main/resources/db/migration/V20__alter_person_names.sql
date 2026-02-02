SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Name] ADD [legacy_id] [bigint] NULL;
GO

ALTER TABLE [dbo].[Name] ADD [legacy_table] [varchar](255) NULL;
GO

UPDATE [dbo].[Name] SET
  [legacy_id]=[Person].[legacy_id],
  [legacy_table]=[Person].[legacy_table]
FROM [dbo].[Name] LEFT JOIN [dbo].[Person]
ON [dbo].[Name].[person_id]=[dbo].[Person].[id];
GO

ALTER TABLE [dbo].[Name] ALTER COLUMN [legacy_id] [bigint] NOT NULL;
GO

ALTER TABLE [dbo].[Name] ALTER COLUMN [legacy_table] [varchar](255) NOT NULL;
GO

ALTER TABLE [dbo].[Name]
ADD CONSTRAINT UQ_Name_legacy UNIQUE ([database_id], [legacy_table], [legacy_id]);
GO

