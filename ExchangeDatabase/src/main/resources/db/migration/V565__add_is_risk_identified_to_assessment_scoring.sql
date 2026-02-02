ALTER TABLE [dbo].[AssessmentScoringGroup]
    ADD is_risk_identified bit null default 0 with values;

GO
UPDATE [dbo].[AssessmentScoringGroup] SET is_risk_identified = 1 WHERE id in (SELECT asg.id FROM [dbo].[AssessmentScoringGroup] asg 
																				join [dbo].[Assessment] a on a.id = asg.assessment_id
																				WHERE asg.score_low >= 5 AND a.code in ('GAD7', 'PHQ9'));
GO

