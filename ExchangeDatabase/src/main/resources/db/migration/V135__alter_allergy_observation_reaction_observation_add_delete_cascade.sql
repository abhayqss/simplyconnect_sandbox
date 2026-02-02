SET XACT_ABORT ON
GO

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_8vssyk7gx48xqwbiyn1y2nh71]') AND parent_object_id = OBJECT_ID(N'[dbo].[AllergyObservation_ReactionObservation]'))
ALTER TABLE [dbo].[AllergyObservation_ReactionObservation] DROP CONSTRAINT [FK_8vssyk7gx48xqwbiyn1y2nh71]

ALTER TABLE [dbo].[AllergyObservation_ReactionObservation]  WITH CHECK ADD  CONSTRAINT [FK_8vssyk7gx48xqwbiyn1y2nh71] FOREIGN KEY([reaction_observation_id])
REFERENCES [dbo].[ReactionObservation] ([id])
ON DELETE CASCADE
GO

ALTER TABLE [dbo].[AllergyObservation_ReactionObservation] CHECK CONSTRAINT [FK_8vssyk7gx48xqwbiyn1y2nh71]
GO

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_jb18xqnh8jtpq1x3ems0pm60v]') AND parent_object_id = OBJECT_ID(N'[dbo].[AllergyObservation_ReactionObservation]'))
ALTER TABLE [dbo].[AllergyObservation_ReactionObservation] DROP CONSTRAINT [FK_jb18xqnh8jtpq1x3ems0pm60v]

ALTER TABLE [dbo].[AllergyObservation_ReactionObservation]  WITH CHECK ADD  CONSTRAINT [FK_jb18xqnh8jtpq1x3ems0pm60v] FOREIGN KEY([allergy_observation_id])
REFERENCES [dbo].[AllergyObservation] ([id])
ON DELETE CASCADE
GO

ALTER TABLE [dbo].[AllergyObservation_ReactionObservation] CHECK CONSTRAINT [FK_jb18xqnh8jtpq1x3ems0pm60v]
GO
	
