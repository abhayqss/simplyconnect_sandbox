SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Resident] ADD [opt_out] [bit] NULL DEFAULT 0;
GO