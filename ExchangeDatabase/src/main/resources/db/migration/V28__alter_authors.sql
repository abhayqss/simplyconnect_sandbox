SET XACT_ABORT ON
GO

UPDATE [dbo].[Author] SET [legacy_id]=[Resident].[legacy_id]
FROM [dbo].[Author] LEFT JOIN [dbo].[Resident]
ON [dbo].[Author].[resident_id] = [dbo].[Resident].[id]
WHERE [dbo].[Author].[legacy_table]='Header_Authors';
GO

ALTER TABLE [dbo].[Author] DROP CONSTRAINT UK_20nbkdbedh90rxr9sjuqf28ya;
GO

ALTER TABLE [dbo].[Author] ALTER COLUMN [legacy_id] bigint NOT NULL;
GO

ALTER TABLE [dbo].[Author] ALTER COLUMN [legacy_table] varchar(255) NOT NULL;
GO

ALTER TABLE [dbo].[Author]
ADD CONSTRAINT UK_Author_legacy UNIQUE ([database_id], [legacy_table], [legacy_id]);
GO