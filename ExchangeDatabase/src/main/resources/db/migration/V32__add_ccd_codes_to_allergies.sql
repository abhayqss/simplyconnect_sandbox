SET XACT_ABORT ON
GO

/* Allergy Observation */
ALTER TABLE [dbo].[AllergyObservation] ALTER COLUMN [allergy_type_text] [varchar](255) NULL
ALTER TABLE [dbo].[AllergyObservation] ALTER COLUMN [product_text] [varchar](255) NULL

ALTER TABLE [dbo].[AllergyObservation] DROP COLUMN [allergy_type_code]
ALTER TABLE [dbo].[AllergyObservation] DROP COLUMN [product_code]
ALTER TABLE [dbo].[AllergyObservation] DROP COLUMN [observation_status_code]

ALTER TABLE [dbo].[AllergyObservation] ADD [allergy_type_code_id] [bigint] NULL
ALTER TABLE [dbo].[AllergyObservation] ADD [product_code_id] [bigint] NULL
ALTER TABLE [dbo].[AllergyObservation] ADD [observation_status_code_id] [bigint] NULL

ALTER TABLE [dbo].[AllergyObservation] ADD FOREIGN KEY([allergy_type_code_id]) REFERENCES [dbo].[CcdCode] ([id])
ALTER TABLE [dbo].[AllergyObservation] ADD FOREIGN KEY([product_code_id]) REFERENCES [dbo].[CcdCode] ([id])
ALTER TABLE [dbo].[AllergyObservation] ADD FOREIGN KEY([observation_status_code_id]) REFERENCES [dbo].[CcdCode] ([id])

/* Reaction Observation */
ALTER TABLE [dbo].[ReactionObservation] ALTER COLUMN [reaction_text] [varchar](255) NULL

ALTER TABLE [dbo].[ReactionObservation] DROP COLUMN [reaction_code]
ALTER TABLE [dbo].[ReactionObservation] ADD [reaction_code_id] [bigint] NULL
ALTER TABLE [dbo].[ReactionObservation] ADD FOREIGN KEY([reaction_code_id]) REFERENCES [dbo].[CcdCode] ([id])

/* Severity Observation */
ALTER TABLE [dbo].[SeverityObservation] ALTER COLUMN [severity_text] [varchar](255) NULL

ALTER TABLE [dbo].[SeverityObservation] DROP COLUMN [severity_code]
ALTER TABLE [dbo].[SeverityObservation] ADD [severity_code_id] [bigint] NULL
ALTER TABLE [dbo].[SeverityObservation] ADD FOREIGN KEY([severity_code_id]) REFERENCES [dbo].[CcdCode] ([id])