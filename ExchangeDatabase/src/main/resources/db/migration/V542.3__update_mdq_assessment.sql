DECLARE @assessmentId BIGINT
SELECT @assessmentId = ID FROM Assessment WHERE short_name = 'MDQ';

UPDATE AssessmentScoringGroup
SET comments = '"Yes" to 7 or more of the 13 items in Part 1 AND
     "Yes" to 7 items in Part 2 AND
     "Moderate problem" or "Serious problem" is selected in Part 3'
WHERE assessment_id = @assessmentId AND score_low = 1107;