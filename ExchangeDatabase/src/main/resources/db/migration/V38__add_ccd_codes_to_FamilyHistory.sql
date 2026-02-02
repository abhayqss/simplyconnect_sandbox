SET XACT_ABORT ON
GO
ALTER TABLE [dbo].[FamilyHistory] DROP COLUMN [related_subject_code]
GO
ALTER TABLE [dbo].[FamilyHistory] ADD [related_subject_code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[FamilyHistory] ADD FOREIGN KEY([related_subject_code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[FamilyHistory] DROP COLUMN [administrative_gender_code]
GO
ALTER TABLE [dbo].[FamilyHistory] ADD [administrative_gender_code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[FamilyHistory] ADD FOREIGN KEY([administrative_gender_code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[FamilyHistoryObservation] DROP COLUMN [problem_value]
GO
ALTER TABLE [dbo].[FamilyHistoryObservation] ADD [problem_value_id] [bigint] NULL
GO
ALTER TABLE [dbo].[FamilyHistoryObservation] ADD FOREIGN KEY([problem_value_id]) REFERENCES [dbo].[CcdCode] ([id])
GO
ALTER TABLE [dbo].[FamilyHistoryObservation] DROP COLUMN [problem_type_code]
GO
ALTER TABLE [dbo].[FamilyHistoryObservation] ADD [problem_type_code_id] [bigint] NULL
GO
ALTER TABLE [dbo].[FamilyHistoryObservation] ADD FOREIGN KEY([problem_type_code_id]) REFERENCES [dbo].[CcdCode] ([id])
GO