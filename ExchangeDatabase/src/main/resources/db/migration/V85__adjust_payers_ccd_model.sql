SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[PolicyActivity] ADD [result_id] varchar(50) NOT NULL DEFAULT '';
GO

ALTER TABLE [dbo].[PolicyActivity] ADD [member_id] varchar(50) NOT NULL DEFAULT '';
GO