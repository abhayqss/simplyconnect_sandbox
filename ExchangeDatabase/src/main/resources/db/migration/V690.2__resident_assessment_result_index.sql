CREATE NONCLUSTERED INDEX IX_ResidentAssessmentResult_archived_assessment_status
    ON [dbo].[ResidentAssessmentResult] ([archived],[assessment_status])
    INCLUDE ([id],[assessment_id],[resident_id],[date_assigned],[date_completed])
GO