SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[AllergyObservation] ADD [legacy_id] [bigint] NULL;
GO

UPDATE [dbo].[AllergyObservation] SET
  [legacy_id]=[Allergy].[legacy_id]
FROM [dbo].[AllergyObservation] LEFT JOIN [dbo].[Allergy]
ON [dbo].[AllergyObservation].[allergy_id]=[dbo].[Allergy].[id];
GO

ALTER TABLE [dbo].[AllergyObservation] ALTER COLUMN [legacy_id] [bigint] NOT NULL;
GO

ALTER TABLE [dbo].[AllergyObservation]
ADD CONSTRAINT UQ_AllergyObservation_legacy UNIQUE ([database_id], [legacy_id]);
GO