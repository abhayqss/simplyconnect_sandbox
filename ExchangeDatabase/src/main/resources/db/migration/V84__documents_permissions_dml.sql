SET XACT_ABORT ON
GO

INSERT INTO [dbo].[Role] ([name]) VALUES
('ROLE_PARTNER_USER');

UPDATE [dbo].[Document] SET [eldermark_shared] = 1;
ALTER TABLE [dbo].[Document] ALTER COLUMN [eldermark_shared] bit NOT NULL;

UPDATE [dbo].[SourceDatabase] SET [is_eldermark] = 1;
ALTER TABLE [dbo].[SourceDatabase] ALTER COLUMN [is_eldermark] bit NOT NULL;

GO
