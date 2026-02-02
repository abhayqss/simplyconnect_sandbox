DECLARE @assessment_group_id BIGINT;
SELECT @assessment_group_id = id FROM [dbo].[AssessmentGroup] WHERE name = 'Care Management';

UPDATE [dbo].[Assessment]
   SET [assessment_group_id] = @assessment_group_id
 WHERE [code] in ('IN_HOME', 'IN_HOME_CARE')
GO