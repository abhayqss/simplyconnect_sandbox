SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[UserMobile]
  ADD [first_name] [VARCHAR](255) NULL;
ALTER TABLE [dbo].[UserMobile]
  ADD [last_name] [VARCHAR](255) NULL;
GO

UPDATE um
SET um.[first_name] = up.[first_name], um.[last_name] = up.[last_name]
FROM [dbo].[UserMobile] um INNER JOIN [dbo].[UserProfile] up ON up.[user_id] = um.[id];

ALTER TABLE [dbo].[UserProfile] DROP COLUMN [first_name];
ALTER TABLE [dbo].[UserProfile] DROP COLUMN [last_name];
GO

UPDATE [dbo].[SourceDatabase] SET [name] = 'Unaffiliated' WHERE [name] = 'Physician Repo';
UPDATE [dbo].[Organization] SET [name] = 'Unaffiliated' WHERE [name] = 'Physician Repo';
GO
