SET XACT_ABORT ON
GO
ALTER TABLE [dbo].[AdvanceDirective] DROP COLUMN [advance_directive_type]
GO
ALTER TABLE [dbo].[AdvanceDirective] ADD [advance_directive_type_id] [bigint] NULL
GO
ALTER TABLE [dbo].[AdvanceDirective] ADD FOREIGN KEY([advance_directive_type_id]) REFERENCES [dbo].[CcdCode] ([id])
GO