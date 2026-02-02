SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Language] ADD [legacy_id] [bigint] NULL;
GO

UPDATE [dbo].[Language] SET [legacy_id]=[Resident].[legacy_id]
FROM [dbo].[Language] LEFT JOIN [dbo].[Resident]
ON [dbo].[Language].[resident_id]=[dbo].[Resident].[id];
GO

ALTER TABLE [dbo].[Language] ALTER COLUMN [legacy_id] [bigint] NOT NULL;
GO

ALTER TABLE [dbo].[Language]
ADD CONSTRAINT UQ_Language_legacy UNIQUE ([database_id], [legacy_id]);
GO