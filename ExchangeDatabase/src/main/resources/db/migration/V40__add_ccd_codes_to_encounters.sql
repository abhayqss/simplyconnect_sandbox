SET XACT_ABORT ON
GO

/* Encounter */
ALTER TABLE [dbo].[Encounter] DROP COLUMN [type_code]
ALTER TABLE [dbo].[Encounter] ADD [encounter_type_code_id] [bigint] NULL
ALTER TABLE [dbo].[Encounter] ADD FOREIGN KEY([encounter_type_code_id]) REFERENCES [dbo].[CcdCode] ([id])

ALTER TABLE [dbo].[Encounter] DROP COLUMN [type_text]
ALTER TABLE [dbo].[Encounter] ADD [encounter_type_text] [varchar](255) NULL

ALTER TABLE [dbo].[Encounter] DROP COLUMN [disposition_code]
ALTER TABLE [dbo].[Encounter] ADD [disposition_code_id] [bigint] NULL
ALTER TABLE [dbo].[Encounter] ADD FOREIGN KEY([disposition_code_id]) REFERENCES [dbo].[CcdCode] ([id])

DROP TABLE EncounterProvider
CREATE TABLE [dbo].[EncounterProviderCode]
(
  [encounter_id] [bigint] NOT NULL,
	[provider_code_id] [bigint] NOT NULL,
  FOREIGN KEY ([encounter_id]) REFERENCES [dbo].[Encounter] ([id]),
  FOREIGN KEY ([provider_code_id]) REFERENCES [dbo].[CcdCode] ([id])
)