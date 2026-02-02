SET XACT_ABORT ON
GO

/* Immunization */
ALTER TABLE [dbo].[Immunization] DROP COLUMN [code]
ALTER TABLE [dbo].[Immunization] ADD [code_id] [bigint] NULL
ALTER TABLE [dbo].[Immunization] ADD FOREIGN KEY([code_id]) REFERENCES [dbo].[CcdCode] ([id])

ALTER TABLE [dbo].[Immunization] DROP COLUMN [route_code]
ALTER TABLE [dbo].[Immunization] ADD [route_code_id] [bigint] NULL
ALTER TABLE [dbo].[Immunization] ADD FOREIGN KEY([route_code_id]) REFERENCES [dbo].[CcdCode] ([id])

ALTER TABLE [dbo].[Immunization] DROP COLUMN [site_code]
ALTER TABLE [dbo].[Immunization] ADD [site_code_id] [bigint] NULL
ALTER TABLE [dbo].[Immunization] ADD FOREIGN KEY([site_code_id]) REFERENCES [dbo].[CcdCode] ([id])

ALTER TABLE [dbo].[Immunization] DROP COLUMN [administration_unit_code]
ALTER TABLE [dbo].[Immunization] ADD [administration_unit_code_id] [bigint] NULL
ALTER TABLE [dbo].[Immunization] ADD FOREIGN KEY([administration_unit_code_id]) REFERENCES [dbo].[CcdCode] ([id])

/* ImmunizationRefusalReason */
ALTER TABLE [dbo].[ImmunizationRefusalReason] DROP COLUMN [code]
ALTER TABLE [dbo].[ImmunizationRefusalReason] ADD [code_id] [bigint] NULL
ALTER TABLE [dbo].[ImmunizationRefusalReason] ADD FOREIGN KEY([code_id]) REFERENCES [dbo].[CcdCode] ([id])

