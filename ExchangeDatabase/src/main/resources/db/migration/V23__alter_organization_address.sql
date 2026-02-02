SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[OrganizationAddress] ADD [legacy_id] [varchar](255) NULL;
GO

ALTER TABLE [dbo].[OrganizationAddress] ADD [legacy_table] [varchar](255) NULL;
GO

UPDATE [dbo].[OrganizationAddress] SET
  [legacy_id]=[Organization].[legacy_id],
  [legacy_table]=[Organization].[legacy_table]
FROM [dbo].[OrganizationAddress] LEFT JOIN [dbo].[Organization]
ON [dbo].[OrganizationAddress].[org_id]=[dbo].[Organization].[id];
GO

ALTER TABLE [dbo].[OrganizationAddress] ALTER COLUMN [legacy_id] [varchar](255) NOT NULL;
GO

ALTER TABLE [dbo].[OrganizationAddress] ALTER COLUMN [legacy_table] [varchar](255) NOT NULL;
GO

ALTER TABLE [dbo].[OrganizationAddress]
ADD CONSTRAINT UQ_OrganizationAddress_legacy UNIQUE ([database_id], [legacy_table], [legacy_id]);
GO