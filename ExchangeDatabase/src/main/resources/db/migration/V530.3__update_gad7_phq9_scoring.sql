DECLARE @gad7assessmentId BIGINT
SELECT @gad7assessmentId = ID FROM Assessment WHERE short_name = 'GAD-7';

DECLARE @phq9assessmentId BIGINT
SELECT @phq9assessmentId = ID FROM Assessment WHERE short_name = 'PHQ-9';

UPDATE AssessmentScoringGroup
SET score_high = 21
WHERE assessment_id = @gad7assessmentId AND score_low = 15;

UPDATE AssessmentScoringGroup
SET score_high = 27
WHERE assessment_id = @phq9assessmentId AND score_low = 20;