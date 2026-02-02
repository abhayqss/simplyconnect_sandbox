SET XACT_ABORT ON
GO


ALTER TABLE [dbo].[Organization] ADD [module_cloud_storage] [bit] NOT NULL DEFAULT(0)
ALTER TABLE [dbo].[Organization] ALTER COLUMN [module_cloud_storage] [bit] NULL

GO