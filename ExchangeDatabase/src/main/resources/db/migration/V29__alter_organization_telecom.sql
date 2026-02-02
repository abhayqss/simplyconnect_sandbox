SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[OrganizationTelecom] ADD [legacy_id] [varchar](255) NULL;
GO

ALTER TABLE [dbo].[OrganizationTelecom] ADD [legacy_table] [varchar](255) NULL;
GO

UPDATE [dbo].[OrganizationTelecom] SET
  [legacy_id]=[Organization].[legacy_id],
  [legacy_table]=[Organization].[legacy_table]
FROM [dbo].[OrganizationTelecom] LEFT JOIN [dbo].[Organization]
ON [dbo].[OrganizationTelecom].[organization_id]=[dbo].[Organization].[id];
GO

ALTER TABLE [dbo].[OrganizationTelecom] ALTER COLUMN [legacy_id] [varchar](255) NOT NULL;
GO

ALTER TABLE [dbo].[OrganizationTelecom] ALTER COLUMN [legacy_table] [varchar](255) NOT NULL;
GO

ALTER TABLE [dbo].[OrganizationTelecom]
ADD CONSTRAINT UQ_OrganizationTelecom_legacy UNIQUE ([database_id], [legacy_table], [legacy_id]);
GO