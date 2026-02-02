/****** Object:  Table [dbo].[AssessmentScoringValue]    Script Date: 5/15/2018 3:16:43 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[AssessmentScoringValue](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[assessment_id] [bigint] NULL,
	[question_name] [varchar](max) NULL,
	[answer_name] [varchar](max) NOT NULL,
	[result_group_name] [varchar](100) NULL,
	[value] [bigint] NOT NULL,
 CONSTRAINT [PK_AssessmentScoringValues] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
SET IDENTITY_INSERT [dbo].[AssessmentScoringValue] ON 

GO
INSERT [dbo].[AssessmentScoringValue] ([id], [assessment_id], [question_name], [answer_name], [result_group_name], [value]) VALUES (1, 1, NULL, N'item1', NULL, 0)
GO
INSERT [dbo].[AssessmentScoringValue] ([id], [assessment_id], [question_name], [answer_name], [result_group_name], [value]) VALUES (2, 1, NULL, N'item2', NULL, 1)
GO
INSERT [dbo].[AssessmentScoringValue] ([id], [assessment_id], [question_name], [answer_name], [result_group_name], [value]) VALUES (3, 1, NULL, N'item3', NULL, 2)
GO
INSERT [dbo].[AssessmentScoringValue] ([id], [assessment_id], [question_name], [answer_name], [result_group_name], [value]) VALUES (4, 1, NULL, N'item4', NULL, 3)
GO
INSERT [dbo].[AssessmentScoringValue] ([id], [assessment_id], [question_name], [answer_name], [result_group_name], [value]) VALUES (5, 2, NULL, N'item1', NULL, 0)
GO
INSERT [dbo].[AssessmentScoringValue] ([id], [assessment_id], [question_name], [answer_name], [result_group_name], [value]) VALUES (6, 2, NULL, N'item2', NULL, 1)
GO
INSERT [dbo].[AssessmentScoringValue] ([id], [assessment_id], [question_name], [answer_name], [result_group_name], [value]) VALUES (7, 2, NULL, N'item3', NULL, 2)
GO
INSERT [dbo].[AssessmentScoringValue] ([id], [assessment_id], [question_name], [answer_name], [result_group_name], [value]) VALUES (8, 2, NULL, N'item4', NULL, 3)
GO
SET IDENTITY_INSERT [dbo].[AssessmentScoringValue] OFF
GO

SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[AssessmentScoringGroup](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[assessment_id] [bigint] NOT NULL,
	[score_low] [bigint] NULL,
	[score_high] [bigint] NULL,
	[severity] [varchar](5000) NULL,
	[severity_short] [varchar](100) NULL,
	[highlighting] [varchar](10) NULL,
	[comments] [varchar](5000) NULL,
 CONSTRAINT [PK_AssessmentScoringGroup] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
SET IDENTITY_INSERT [dbo].[AssessmentScoringGroup] ON 

GO
INSERT [dbo].[AssessmentScoringGroup] ([id], [assessment_id], [score_low], [score_high], [severity], [severity_short], [highlighting], [comments]) VALUES (1, 1, 0, 4, N'No anxiety disorder.', N'No', N'Low', N'No actions required')
GO
INSERT [dbo].[AssessmentScoringGroup] ([id], [assessment_id], [score_low], [score_high], [severity], [severity_short], [highlighting], [comments]) VALUES (2, 1, 5, 9, N'Mild anxiety disorder.', N'Mild', N'Medium', N'Monitor')
GO
INSERT [dbo].[AssessmentScoringGroup] ([id], [assessment_id], [score_low], [score_high], [severity], [severity_short], [highlighting], [comments]) VALUES (3, 1, 10, 14, N'Moderate anxiety disorder.', N'Moderate', N'High', N'Possible clinically significant condition')
GO
INSERT [dbo].[AssessmentScoringGroup] ([id], [assessment_id], [score_low], [score_high], [severity], [severity_short], [highlighting], [comments]) VALUES (4, 1, 15, 999, N'Severe anxiety disorder.', N'Severe', N'High', N'Active treatment probably warranted')
GO
INSERT [dbo].[AssessmentScoringGroup] ([id], [assessment_id], [score_low], [score_high], [severity], [severity_short], [highlighting], [comments]) VALUES (5, 2, 0, 4, N'Scores <=4 suggest minimal depression which may not require treatment. Functionally, the patient does not report limitations due to their symptoms.', N'Minimal or none', N'Low', N'Monitor; may not require treatment')
GO
INSERT [dbo].[AssessmentScoringGroup] ([id], [assessment_id], [score_low], [score_high], [severity], [severity_short], [highlighting], [comments]) VALUES (6, 2, 5, 9, N'Scores 5-9 suggest mild depression which may require only watchful waiting and repeated PHQ-9 at followup. Functionally, the patient does not report limitations due to their symptoms. WARNING: This patient is having thoughts concerning for suicidal ideation or self-harm, and should be probed further, referred, or transferred for emergency psychiatric evaluation as clinically appropriate and depending on clinician overall risk assessment.', N'Mild', N'Medium', N'Use clinical judgment (symptom duration, functional impairment) to determine necessity of treatment')
GO
INSERT [dbo].[AssessmentScoringGroup] ([id], [assessment_id], [score_low], [score_high], [severity], [severity_short], [highlighting], [comments]) VALUES (7, 2, 10, 14, N'Scores 10-14 suggest moderate depression severity; patients should have a treatment plan ranging form counseling, followup, and/or pharmacotherapy. Functionally, the patient does not report limitations due to their symptoms. WARNING: This patient is having thoughts concerning for suicidal ideation or self-harm, and should be probed further, referred, or transferred for emergency psychiatric evaluation as clinically appropriate and depending on clinician overall risk assessment.', N'Moderate', N'Medium', N'Use clinical judgment (symptom duration, functional impairment) to determine necessity of treatment')
GO
INSERT [dbo].[AssessmentScoringGroup] ([id], [assessment_id], [score_low], [score_high], [severity], [severity_short], [highlighting], [comments]) VALUES (8, 2, 15, 19, N'Scores 15-19 suggest moderately severe depression; patients typically should have immediate initiation of pharmacotherapy and/or psychotherapy. Functionally, the patient finds it is “extremely difficult” to perform life tasks due to their symptoms. WARNING: This patient is having thoughts concerning for suicidal ideation or self-harm, and should be probed further, referred, or transferred for emergency psychiatric evaluation as clinically appropriate and depending on clinician overall risk assessment.', N'Moderately severe', N'High', N'Warrants active treatment with psychotherapy, medications, or combination')
GO
INSERT [dbo].[AssessmentScoringGroup] ([id], [assessment_id], [score_low], [score_high], [severity], [severity_short], [highlighting], [comments]) VALUES (9, 2, 20, 999, N'Scores 20 and greater suggest severe depression; patients typically should have immediate initiation of pharmacotherapy and expedited referral to mental health specialist. Functionally, the patient finds it is “extremely difficult” to perform life tasks due to their symptoms. WARNING: This patient is having thoughts concerning for suicidal ideation or self-harm, and should be probed further, referred, or transferred for emergency psychiatric evaluation as clinically appropriate and depending on clinician overall risk assessment.', N'Severe', N'High', N'Warrants active treatment with psychotherapy, medications, or combination')
GO
SET IDENTITY_INSERT [dbo].[AssessmentScoringGroup] OFF
GO

SET ANSI_PADDING ON
GO

ALTER TABLE [dbo].[Assessment]
  ADD [scoring_enabled] [bit] NULL,
	  [severity_column_name] [varchar](100) NULL
GO

UPDATE [dbo].[Assessment]
   SET [scoring_enabled] = 1
      ,[severity_column_name] = 'Symptom Severity'
 WHERE id=1
GO

UPDATE [dbo].[Assessment]
   SET [scoring_enabled] = 1
      ,[severity_column_name] = 'Depression severity'
 WHERE id=2
GO