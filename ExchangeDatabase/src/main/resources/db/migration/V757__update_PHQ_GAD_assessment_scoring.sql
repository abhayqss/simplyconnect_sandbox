declare @gad_id bigint;
SELECT @gad_id=[id] from [dbo].[Assessment] where code='GAD7';

declare @phq_id bigint;
SELECT @phq_id=[id] from [dbo].[Assessment] where code='PHQ9';

UPDATE [dbo].[AssessmentScoringGroup]
   SET [score_high] = 24
 WHERE assessment_id=@gad_id AND score_high=21

UPDATE [dbo].[AssessmentScoringGroup]
   SET [score_high] = 30
 WHERE assessment_id=@phq_id AND score_high=27
GO
