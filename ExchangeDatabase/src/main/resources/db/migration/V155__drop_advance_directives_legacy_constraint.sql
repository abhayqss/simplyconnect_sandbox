SET XACT_ABORT ON
GO

/****** Object:  Index [UQ_AdvanceDirective_legacy]    Script Date: 06/01/2015 17:40:45 ******/
IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[AdvanceDirective]') AND name = N'UQ_AdvanceDirective_legacy')
ALTER TABLE [dbo].[AdvanceDirective] DROP CONSTRAINT [UQ_AdvanceDirective_legacy]
GO
