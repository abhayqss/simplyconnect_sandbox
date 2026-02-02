SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[AdvanceDirective] ADD [legacy_id] [bigint] NULL;
GO

UPDATE [dbo].[AdvanceDirective] SET [legacy_id]=[Resident].[legacy_id]
FROM [dbo].[AdvanceDirective] LEFT JOIN [dbo].[Resident]
ON [dbo].[AdvanceDirective].[resident_id]=[dbo].[Resident].[id];
GO

ALTER TABLE [dbo].[AdvanceDirective] ALTER COLUMN [legacy_id] [bigint] NOT NULL;
GO

ALTER TABLE [dbo].[AdvanceDirective]
ADD CONSTRAINT UQ_AdvanceDirective_legacy UNIQUE ([database_id], [legacy_id]);
GO