SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[AssessmentScaleObservation] DROP COLUMN [code]
GO
ALTER TABLE [dbo].[AssessmentScaleObservation] ADD [code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[AssessmentScaleObservation] ADD FOREIGN KEY([code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[AssessmentScaleObservation] DROP COLUMN [code_system]
GO
CREATE TABLE [dbo].[AssessmentScaleObservation_InterpretationCode]
(
  [observation_id] [bigint] NOT NULL,
  [interpretation_code_id] [bigint] NOT NULL,
  FOREIGN KEY ([observation_id]) REFERENCES [dbo].[AssessmentScaleObservation] ([id]),
  FOREIGN KEY ([interpretation_code_id]) REFERENCES [dbo].[CcdCode] ([id])
)
GO

ALTER TABLE [dbo].[AssessmentScaleSupportingObservation] DROP COLUMN [code]
GO
ALTER TABLE [dbo].[AssessmentScaleSupportingObservation] ADD [code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[AssessmentScaleSupportingObservation] ADD FOREIGN KEY([code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[AssessmentScaleSupportingObservation] DROP COLUMN [code_system]
GO
ALTER TABLE [dbo].[AssessmentScaleSupportingObservation] DROP COLUMN [value_code]
GO
ALTER TABLE [dbo].[AssessmentScaleSupportingObservation] ADD [value_code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[AssessmentScaleSupportingObservation] ADD FOREIGN KEY([value_code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO

ALTER TABLE [dbo].[CaregiverCharacteristic] DROP COLUMN [code]
GO
ALTER TABLE [dbo].[CaregiverCharacteristic] ADD [code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[CaregiverCharacteristic] ADD FOREIGN KEY([code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[CaregiverCharacteristic] DROP COLUMN [value]
GO
ALTER TABLE [dbo].[CaregiverCharacteristic] ADD [value_id] [bigint] NULL
GO
ALTER TABLE [dbo].[CaregiverCharacteristic] ADD FOREIGN KEY([value_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[CaregiverCharacteristic] DROP COLUMN [value_code_system]
GO
ALTER TABLE [dbo].[CaregiverCharacteristic] DROP COLUMN [participant_role_code]
GO
ALTER TABLE [dbo].[CaregiverCharacteristic] ADD [participant_role_code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[CaregiverCharacteristic] ADD FOREIGN KEY([participant_role_code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO

ALTER TABLE [dbo].[HighestPressureUlcerStage] DROP COLUMN [value]
GO
ALTER TABLE [dbo].[HighestPressureUlcerStage] ADD [value_id] [bigint] NULL
GO
ALTER TABLE [dbo].[HighestPressureUlcerStage] ADD FOREIGN KEY([value_id]) REFERENCES [dbo].[CcdCode] ([id])
GO

ALTER TABLE [dbo].[NumberOfPressureUlcersObservation] DROP COLUMN [observation_value]
GO
ALTER TABLE [dbo].[NumberOfPressureUlcersObservation] ADD [observation_value_id] [bigint] NULL
GO
ALTER TABLE [dbo].[NumberOfPressureUlcersObservation] ADD FOREIGN KEY([observation_value_id]) REFERENCES [dbo].[CcdCode] ([id])
GO

ALTER TABLE [dbo].[PressureUlcerObservation] DROP COLUMN [value]
GO
ALTER TABLE [dbo].[PressureUlcerObservation] ADD [value_id] [bigint] NULL
GO
ALTER TABLE [dbo].[PressureUlcerObservation] ADD FOREIGN KEY([value_id]) REFERENCES [dbo].[CcdCode] ([id])
GO

ALTER TABLE [dbo].[StatusProblemObservation] DROP COLUMN [value]
GO
ALTER TABLE [dbo].[StatusProblemObservation] ADD [value_id] [bigint] NULL
GO
ALTER TABLE [dbo].[StatusProblemObservation] ADD FOREIGN KEY([value_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[StatusProblemObservation] DROP COLUMN [method_code]
GO
ALTER TABLE [dbo].[StatusProblemObservation] ADD [method_code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[StatusProblemObservation] ADD FOREIGN KEY([method_code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO

ALTER TABLE [dbo].[StatusResultObservation] DROP COLUMN [code]
GO
ALTER TABLE [dbo].[StatusResultObservation] ADD [code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[StatusResultObservation] ADD FOREIGN KEY([code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[StatusResultObservation] ADD [value_code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[StatusResultObservation] ADD FOREIGN KEY([value_code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
CREATE TABLE [dbo].[StatusResultObservation_InterpretationCode]
(
  [result_observation_id] [bigint] NOT NULL,
  [interpretation_code_id] [bigint] NOT NULL,
  FOREIGN KEY ([result_observation_id]) REFERENCES [dbo].[StatusResultObservation] ([id]),
  FOREIGN KEY ([interpretation_code_id]) REFERENCES [dbo].[CcdCode] ([id])
)
GO
ALTER TABLE [dbo].[StatusResultObservation] DROP COLUMN [method_code]
GO
ALTER TABLE [dbo].[StatusResultObservation] ADD [method_code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[StatusResultObservation] ADD FOREIGN KEY([method_code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[StatusResultObservation] DROP COLUMN [target_site_code]
GO
ALTER TABLE [dbo].[StatusResultObservation] ADD [target_site_code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[StatusResultObservation] ADD FOREIGN KEY([target_site_code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO

ALTER TABLE [dbo].[StatusResultOrganizer] DROP COLUMN [code]
GO
ALTER TABLE [dbo].[StatusResultOrganizer] ADD [code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[StatusResultOrganizer] ADD FOREIGN KEY([code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[StatusResultOrganizer] DROP COLUMN [code_system]
GO

ALTER TABLE [dbo].[TargetSiteCode] DROP COLUMN [code]
GO
ALTER TABLE [dbo].[TargetSiteCode] ADD [code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[TargetSiteCode] ADD FOREIGN KEY([code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO

ALTER TABLE [dbo].[TargetSiteCode] DROP COLUMN [value]
GO
ALTER TABLE [dbo].[TargetSiteCode] ADD [value_id] [bigint] NULL
GO
ALTER TABLE [dbo].[TargetSiteCode] ADD FOREIGN KEY([value_id]) REFERENCES [dbo].[CcdCode] ([id])
GO