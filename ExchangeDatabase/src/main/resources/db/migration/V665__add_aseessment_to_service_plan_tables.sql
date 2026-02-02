IF OBJECT_ID('ResidentAssessmentResult_ServicePlanNeedExcludedQuestion') IS NOT NULL
  DROP TABLE [dbo].[ResidentAssessmentResult_ServicePlanNeedExcludedQuestion]
GO

CREATE TABLE [dbo].[ResidentAssessmentResult_ServicePlanNeedExcludedQuestion](
	[resident_assessment_result_id] [bigint] NOT NULL,
	[question_name] [varchar](1000) NOT NULL
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ResidentAssessmentResult_ServicePlanNeedExcludedQuestion]  WITH CHECK ADD  CONSTRAINT [FK_ResidentAssessmentResult_ServicePlanNeedExcludedQuestion_ResidentAssessmentResult] FOREIGN KEY([resident_assessment_result_id])
REFERENCES [dbo].[ResidentAssessmentResult] ([id])
GO

ALTER TABLE [dbo].[ResidentAssessmentResult_ServicePlanNeedExcludedQuestion] CHECK CONSTRAINT [FK_ResidentAssessmentResult_ServicePlanNeedExcludedQuestion_ResidentAssessmentResult]
GO


IF OBJECT_ID('ResidentAssessmentResult_ServicePlanNeedExcludedSection') IS NOT NULL
  DROP TABLE [dbo].[ResidentAssessmentResult_ServicePlanNeedExcludedSection]
GO

CREATE TABLE [dbo].[ResidentAssessmentResult_ServicePlanNeedExcludedSection](
	[resident_assessment_result_id] [bigint] NOT NULL,
	[section_name] [varchar](1000) NOT NULL
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ResidentAssessmentResult_ServicePlanNeedExcludedSection]  WITH CHECK ADD  CONSTRAINT [FK_ResidentAssessmentResult_ServicePlanNeedExcludedSection_ResidentAssessmentResult] FOREIGN KEY([resident_assessment_result_id])
REFERENCES [dbo].[ResidentAssessmentResult] ([id])
GO

ALTER TABLE [dbo].[ResidentAssessmentResult_ServicePlanNeedExcludedSection] CHECK CONSTRAINT [FK_ResidentAssessmentResult_ServicePlanNeedExcludedSection_ResidentAssessmentResult]
GO