SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[Language] DROP COLUMN [name]
GO
ALTER TABLE [dbo].[Language] ADD [code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[Language] ADD FOREIGN KEY([code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[Language] DROP COLUMN [ability_mode]
GO
ALTER TABLE [dbo].[Language] ADD [ability_mode_id] [bigint] NULL
GO
ALTER TABLE [dbo].[Language] ADD FOREIGN KEY([ability_mode_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[Language] DROP COLUMN [ability_proficiency]
GO
ALTER TABLE [dbo].[Language] ADD [ability_proficiency_id] [bigint] NULL
GO
ALTER TABLE [dbo].[Language] ADD FOREIGN KEY([ability_proficiency_id]) REFERENCES [dbo].[CcdCode] ([id])
GO

/*ALTER TABLE [dbo].[Organization] DROP COLUMN [sales_region]
GO*/

ALTER TABLE [dbo].[Resident] DROP COLUMN [gender]
GO
ALTER TABLE [dbo].[Resident] ADD [gender_id] [bigint] NULL
GO
ALTER TABLE [dbo].[Resident] ADD FOREIGN KEY([gender_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[Resident] DROP COLUMN [marital_status]
GO
ALTER TABLE [dbo].[Resident] ADD [marital_status_id] [bigint] NULL
GO
ALTER TABLE [dbo].[Resident] ADD FOREIGN KEY([marital_status_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[Resident] DROP COLUMN [ethnic_group]
GO
ALTER TABLE [dbo].[Resident] ADD [ethnic_group_id] [bigint] NULL
GO
ALTER TABLE [dbo].[Resident] ADD FOREIGN KEY([ethnic_group_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[Resident] DROP COLUMN [religion]
GO
ALTER TABLE [dbo].[Resident] ADD [religion_id] [bigint] NULL
GO
ALTER TABLE [dbo].[Resident] ADD FOREIGN KEY([religion_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[Resident] DROP COLUMN [race]
GO
ALTER TABLE [dbo].[Resident] ADD [race_id] [bigint] NULL
GO
ALTER TABLE [dbo].[Resident] ADD FOREIGN KEY([race_id]) REFERENCES [dbo].[CcdCode] ([id])

GO
ALTER TABLE [dbo].[Guardian] DROP COLUMN [relationship_code]
GO
ALTER TABLE [dbo].[Guardian] ADD [relationship_code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[Guardian] ADD FOREIGN KEY([relationship_code_id]) REFERENCES [dbo].[CcdCode] ([id])
