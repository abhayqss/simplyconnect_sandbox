SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
UPDATE [dbo].[AssessmentScoringGroup]
   SET [score_high] = 24
 WHERE score_high=999 and assessment_id = 1
GO

UPDATE [dbo].[AssessmentScoringGroup]
   SET [score_high] = 30
 WHERE score_high=999 and assessment_id = 2
GO

ALTER TABLE [dbo].[Assessment]
  ADD [management_comment] [varchar](5000) NULL
GO

UPDATE [dbo].[Assessment]
   SET [management_comment] = 'Scores >=10: Further assessment (including diagnostic interview and mental status examination) and/or referral to a mental health professional recommended.'
 WHERE id=1
GO

UPDATE [dbo].[Assessment]
   SET [management_comment] = 'Final diagnosis should be made with clinical interview and mental status examination including assessment of patient''s level of distress and functional impairment.'
 WHERE id=2
GO

SET IDENTITY_INSERT [dbo].[AssessmentGroup] ON 
GO
INSERT [dbo].[AssessmentGroup] ([id], [name]) VALUES (2, N'Other')
GO
SET IDENTITY_INSERT [dbo].[AssessmentGroup] OFF
GO

SET IDENTITY_INSERT [dbo].[Assessment] ON 
GO
INSERT [dbo].[Assessment] ([id], [assessment_group_id], [name], [json_content], [short_name], [scoring_enabled], [severity_column_name], [management_comment])
	 VALUES (3, 2, N'Comprehensive Assessment', N'{"pages":[{"name":"page1","elements":[{"type":"radiogroup","name":"question1","title":"Hospitalizations in the last 2 years?","isRequired":true,"choices":[{"value":"item1","text":"Yes"},{"value":"item2","text":"No"}]},{"type":"text","name":"question2","customdatepicker":true,"visibleIf":"{question1} = \"item1\"","title":"Date"},{"type":"text","name":"question3","visibleIf":"{question1} = \"item1\"","title":"Hospital"},{"type":"text","name":"question4","visibleIf":"{question1} = \"item1\"","title":"Diagnosis"},{"type":"text","name":"question5","visibleIf":"{question1} = \"item1\"","title":"LOS"},{"type":"radiogroup","name":"question6","title":"Emergency visits not resulting in hospitalizations in the last 2 years?","isRequired":true,"choices":[{"value":"item1","text":"Yes"},{"value":"item2","text":"No"}]},{"type":"text","name":"question7","customdatepicker":true,"visibleIf":"{question6} = \"item1\"","title":"Date"},{"type":"text","name":"question8","visibleIf":"{question6} = \"item1\"","title":"Hospital"},{"type":"text","name":"question9","visibleIf":"{question6} = \"item1\"","title":"Reason for ER Visit"},{"type":"text","name":"question10","visibleIf":"{question6} = \"item1\"","title":"Diagnosis"},{"type":"radiogroup","name":"question11","title":"Outpatient Procedures","isRequired":true,"choices":[{"value":"item1","text":"Yes"},{"value":"item2","text":"No"}]},{"type":"text","name":"question12","customdatepicker":true,"visibleIf":"{question11} = \"item1\"","title":"Date"},{"type":"text","name":"question13","visibleIf":"{question11} = \"item1\"","title":"Hospital or Other Site"},{"type":"text","name":"question14","visibleIf":"{question11} = \"item1\"","title":"Diagnosis"},{"type":"text","name":"question15","visibleIf":"{question11} = \"item1\"","title":"Procedure"},{"type":"radiogroup","name":"question16","title":"PCP Visits","isRequired":true,"choices":[{"value":"item1","text":"Yes"},{"value":"item2","text":"No"}]},{"type":"text","name":"question17","customdatepicker":true,"visibleIf":"{question16} = \"item1\"","title":"Date"},{"type":"text","name":"question18","visibleIf":"{question16} = \"item1\"","title":"Diagnoses"},{"type":"radiogroup","name":"question19","title":"Ancillary Utilization","isRequired":true,"choices":[{"value":"item1","text":"Yes"},{"value":"item2","text":"No"}]},{"type":"text","name":"question20","customdatepicker":true,"visibleIf":"{question19} = \"item1\"","title":"Date"},{"type":"text","name":"question21","visibleIf":"{question19} = \"item1\"","title":"Provider"},{"type":"text","name":"question22","visibleIf":"{question19} = \"item1\"","title":"Diagnoses"},{"type":"text","name":"question23","visibleIf":"{question19} = \"item1\"","title":"DME"}],"title":"Utilization"}]}', N'Comprehensive Assessment', 0, NULL, NULL)
GO
SET IDENTITY_INSERT [dbo].[Assessment] OFF
GO

SET ANSI_PADDING OFF
GO
