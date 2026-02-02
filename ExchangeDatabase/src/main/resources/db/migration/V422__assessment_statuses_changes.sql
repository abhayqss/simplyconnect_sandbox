SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

UPDATE [dbo].[ResidentAssessmentResult]
SET assessment_status = 'COMPLETED'
WHERE assessment_id in (1, 2)
GO