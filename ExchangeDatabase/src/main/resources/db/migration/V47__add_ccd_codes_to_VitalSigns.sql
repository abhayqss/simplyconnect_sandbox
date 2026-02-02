SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[VitalSignObservation] DROP COLUMN [result_type_code]
ALTER TABLE [dbo].[VitalSignObservation] ADD [result_type_code_id] [bigint] NULL
ALTER TABLE [dbo].[VitalSignObservation] ADD FOREIGN KEY([result_type_code_id]) REFERENCES [dbo].[CcdCode] ([id])

ALTER TABLE [dbo].[VitalSignObservation] DROP COLUMN [interpretation_code]
ALTER TABLE [dbo].[VitalSignObservation] ADD [interpretation_code_id] [bigint] NULL
ALTER TABLE [dbo].[VitalSignObservation] ADD FOREIGN KEY([interpretation_code_id]) REFERENCES [dbo].[CcdCode] ([id])

ALTER TABLE [dbo].[VitalSignObservation] DROP COLUMN [method_code]
ALTER TABLE [dbo].[VitalSignObservation] ADD [method_code_id] [bigint] NULL
ALTER TABLE [dbo].[VitalSignObservation] ADD FOREIGN KEY([method_code_id]) REFERENCES [dbo].[CcdCode] ([id])

ALTER TABLE [dbo].[VitalSignObservation] DROP COLUMN [target_site_code]
ALTER TABLE [dbo].[VitalSignObservation] ADD [target_site_code_id] [bigint] NULL
ALTER TABLE [dbo].[VitalSignObservation] ADD FOREIGN KEY([target_site_code_id]) REFERENCES [dbo].[CcdCode] ([id])

ALTER TABLE [dbo].[VitalSignObservation] ALTER COLUMN [unit] [varchar](50) NULL