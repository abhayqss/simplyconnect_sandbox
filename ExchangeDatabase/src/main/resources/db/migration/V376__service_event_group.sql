ALTER TABLE [dbo].[EventGroup] ADD [is_service] [bit] NOT NULL DEFAULT 0
GO

update [dbo].[EventGroup] set is_service = 1 where name = 'Notes'
GO