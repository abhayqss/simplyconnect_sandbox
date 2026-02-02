
SET XACT_ABORT ON
GO

IF (OBJECT_ID('[dbo].[ProblemObservationTranslation]') IS NULL)
	CREATE TABLE [dbo].[ProblemObservationTranslation] (
		[problem_observation_id] [bigint] NOT NULL,
		[translation_code_id] [bigint] NOT NULL,
		CONSTRAINT [FK_ProblemObservationTranslation_ProblemObservation] FOREIGN KEY ([problem_observation_id]) REFERENCES [dbo].[ProblemObservation] ([id]),
		CONSTRAINT [FK_ProblemObservationTranslation_AnyCcdCode] FOREIGN KEY ([translation_code_id]) REFERENCES [dbo].[AnyCcdCode] ([id])
	);
GO