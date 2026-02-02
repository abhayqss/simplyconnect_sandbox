SET XACT_ABORT ON
GO

-- A follow-up to V251 migration

ALTER TABLE [dbo].[ResultObservationRange] ALTER COLUMN [database_id] [bigint] NULL;
ALTER TABLE [dbo].[ResultObservationInterpretationCode] ALTER COLUMN [database_id] [bigint] NULL;
ALTER TABLE [dbo].[StatusResultObservationRange] ALTER COLUMN [database_id] [bigint] NULL;
ALTER TABLE [dbo].[EncounterProviderCode] ALTER COLUMN [database_id] [bigint] NULL;
ALTER TABLE [dbo].[AdvanceDirectivesVerifier] ALTER COLUMN [database_id] [bigint] NULL;
GO