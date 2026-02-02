SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[SourceDatabase] ADD [name_and_port] [varchar](255) NULL DEFAULT ''
GO

UPDATE [dbo].[SourceDatabase] SET [name_and_port] = [name]
GO

ALTER TABLE [dbo].[SourceDatabase] ALTER COLUMN [name_and_port] [varchar](255) NOT NULL
GO

