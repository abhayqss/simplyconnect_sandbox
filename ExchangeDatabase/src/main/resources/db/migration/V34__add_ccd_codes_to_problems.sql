SET XACT_ABORT ON
GO

/* Problem Observation */
ALTER TABLE [dbo].[ProblemObservation] ADD [negation_ind] [bit] NULL
ALTER TABLE [dbo].[ProblemObservation] ALTER COLUMN [age_observation_unit] [varchar](50) NULL

ALTER TABLE [dbo].[ProblemObservation] DROP COLUMN [problem_type]
ALTER TABLE [dbo].[ProblemObservation] DROP COLUMN [problem_code]
ALTER TABLE [dbo].[ProblemObservation] DROP COLUMN [problem_status_code]
ALTER TABLE [dbo].[ProblemObservation] DROP COLUMN [health_status_code]

ALTER TABLE [dbo].[ProblemObservation] ADD [problem_type_code_id] [bigint] NULL
ALTER TABLE [dbo].[ProblemObservation] ADD [problem_value_code_id] [bigint] NULL
ALTER TABLE [dbo].[ProblemObservation] ADD [problem_status_code_id] [bigint] NULL
ALTER TABLE [dbo].[ProblemObservation] ADD [health_status_code_id] [bigint] NULL

ALTER TABLE [dbo].[ProblemObservation] ADD FOREIGN KEY([problem_type_code_id]) REFERENCES [dbo].[CcdCode] ([id])
ALTER TABLE [dbo].[ProblemObservation] ADD FOREIGN KEY([problem_value_code_id]) REFERENCES [dbo].[CcdCode] ([id])
ALTER TABLE [dbo].[ProblemObservation] ADD FOREIGN KEY([problem_status_code_id]) REFERENCES [dbo].[CcdCode] ([id])
ALTER TABLE [dbo].[ProblemObservation] ADD FOREIGN KEY([health_status_code_id]) REFERENCES [dbo].[CcdCode] ([id])