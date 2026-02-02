SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Resident] ADD [is_archived] [bit] NOT NULL DEFAULT(0)

GO