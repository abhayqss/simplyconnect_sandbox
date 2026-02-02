SET XACT_ABORT ON
GO

/* Result */
ALTER TABLE [dbo].[Result] DROP COLUMN [code]
ALTER TABLE [dbo].[Result] DROP COLUMN [code_system]

ALTER TABLE [dbo].[Result] ADD [code_id] [bigint] NULL
ALTER TABLE [dbo].[Result] ADD FOREIGN KEY([code_id]) REFERENCES [dbo].[CcdCode] ([id])

/* Result Observation */
ALTER TABLE [dbo].[ResultObservation] ALTER COLUMN [result_value_unit] [varchar](50) NULL

ALTER TABLE [dbo].[ResultObservation] DROP COLUMN [result_type_code]
ALTER TABLE [dbo].[ResultObservation] DROP COLUMN [result_type_code_system]
ALTER TABLE [dbo].[ResultObservation] DROP COLUMN [method_code]
ALTER TABLE [dbo].[ResultObservation] DROP COLUMN [site_code]

ALTER TABLE [dbo].[ResultObservation] ADD [result_type_code_id] [bigint] NULL
ALTER TABLE [dbo].[ResultObservation] ADD [method_code_id] [bigint] NULL
ALTER TABLE [dbo].[ResultObservation] ADD [site_code_id] [bigint] NULL

ALTER TABLE [dbo].[ResultObservation] ADD FOREIGN KEY([result_type_code_id]) REFERENCES [dbo].[CcdCode] ([id])
ALTER TABLE [dbo].[ResultObservation] ADD FOREIGN KEY([method_code_id]) REFERENCES [dbo].[CcdCode] ([id])
ALTER TABLE [dbo].[ResultObservation] ADD FOREIGN KEY([site_code_id]) REFERENCES [dbo].[CcdCode] ([id])

DROP TABLE ResultObservationInterpretationCode
CREATE TABLE [dbo].[ResultObservationInterpretationCode]
(
  [result_observation_id] [bigint] NOT NULL,
	[interpretation_code_id] [bigint] NOT NULL,
  FOREIGN KEY ([result_observation_id]) REFERENCES [dbo].[ResultObservation] ([id]),
  FOREIGN KEY ([interpretation_code_id]) REFERENCES [dbo].[CcdCode] ([id])
)