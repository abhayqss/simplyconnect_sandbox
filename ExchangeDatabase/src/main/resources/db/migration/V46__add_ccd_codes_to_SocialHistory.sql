SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[SmokingStatusObservation] DROP COLUMN [value]
ALTER TABLE [dbo].[SmokingStatusObservation] ADD [value_code_id] [bigint] NULL
ALTER TABLE [dbo].[SmokingStatusObservation] ADD FOREIGN KEY([value_code_id]) REFERENCES [dbo].[CcdCode] ([id])

ALTER TABLE [dbo].[TobaccoUse] DROP COLUMN [value]
ALTER TABLE [dbo].[TobaccoUse] ADD [value_code_id] [bigint] NULL
ALTER TABLE [dbo].[TobaccoUse] ADD FOREIGN KEY([value_code_id]) REFERENCES [dbo].[CcdCode] ([id])

ALTER TABLE [dbo].[SocialHistoryObservation] DROP COLUMN [social_history_type]
ALTER TABLE [dbo].[SocialHistoryObservation] ADD [type_code_id] [bigint] NULL
ALTER TABLE [dbo].[SocialHistoryObservation] ADD FOREIGN KEY([type_code_id]) REFERENCES [dbo].[CcdCode] ([id])

ALTER TABLE [dbo].[SocialHistoryObservation] DROP COLUMN [social_history_value]
ALTER TABLE [dbo].[SocialHistoryObservation] ADD [value_code_id] [bigint] NULL
ALTER TABLE [dbo].[SocialHistoryObservation] ADD FOREIGN KEY([value_code_id]) REFERENCES [dbo].[CcdCode] ([id])

ALTER TABLE [dbo].[SocialHistoryObservation] DROP COLUMN [social_history_free_text]
ALTER TABLE [dbo].[SocialHistoryObservation] ADD [free_text] [varchar](255) NULL




