SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Document]
ADD [exists_in_file_store] [bit] NOT NULL DEFAULT(1)