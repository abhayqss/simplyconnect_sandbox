IF OBJECT_ID('UK_8vssyk7gx48xqwbiyn1y2nh71') IS NOT NULL
	ALTER TABLE [dbo].[AllergyObservation_ReactionObservation] DROP CONSTRAINT [UK_8vssyk7gx48xqwbiyn1y2nh71]
GO

ALTER TABLE [dbo].[AllergyObservation_ReactionObservation] WITH CHECK ADD  CONSTRAINT [UK_8vssyk7gx48xqwbiyn1y2nh71] UNIQUE NONCLUSTERED 
(
	[reaction_observation_id] ASC,[allergy_observation_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO