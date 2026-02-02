SET XACT_ABORT ON
GO


ALTER TABLE [dbo].[Organization] ADD [module_hie] [bit] NOT NULL DEFAULT(1)
ALTER TABLE [dbo].[Organization] ALTER COLUMN [module_hie] [bit] NULL

GO