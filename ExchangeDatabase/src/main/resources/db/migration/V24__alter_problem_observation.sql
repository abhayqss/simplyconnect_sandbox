SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[ProblemObservation] ADD [legacy_id] [bigint] NULL;
GO

UPDATE [dbo].[ProblemObservation] SET
  [legacy_id]=[Problem].[legacy_id]
FROM [dbo].[ProblemObservation] LEFT JOIN [dbo].[Problem]
ON [dbo].[ProblemObservation].[problem_id]=[dbo].[Problem].[id];
GO

ALTER TABLE [dbo].[ProblemObservation] ALTER COLUMN [legacy_id] [bigint] NOT NULL;
GO

ALTER TABLE [dbo].[ProblemObservation]
ADD CONSTRAINT UQ_ProblemObservation_legacy UNIQUE ([database_id], [legacy_id]);
GO