SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

ALTER TABLE [dbo].[ResidentAssessmentResult] ADD
	assessment_status varchar(50) NOT NULL default 'IN_PROCESS'
GO