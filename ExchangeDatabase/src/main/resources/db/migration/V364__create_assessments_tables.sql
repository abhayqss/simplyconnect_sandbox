/****** Object:  Table [dbo].[Note]    Script Date: 2/22/2018 4:09:45 PM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[AssessmentGroup](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](500) NULL,
 CONSTRAINT [PK_AssessmentGroup] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

CREATE TABLE [dbo].[Assessment](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[assessment_group_id] [bigint] NOT NULL,
	[name] [varchar](500) NULL,
	[json_content] [varchar](max) NOT NULL,
	[short_name] [varchar](500) NULL,
 CONSTRAINT [PK_Assessment] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[Assessment]  WITH CHECK ADD  CONSTRAINT [FK_Assessment_AssessmentGroup] FOREIGN KEY([assessment_group_id])
REFERENCES [dbo].[AssessmentGroup] ([id])
GO

ALTER TABLE [dbo].[Assessment] CHECK CONSTRAINT [FK_Assessment_AssessmentGroup]
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ResidentAssessmentResult](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[assessment_id] [bigint] NOT NULL,
	[resident_id] [bigint] NOT NULL,
	[json_result] [varchar](max) NULL,
	[chain_id] [bigint] NULL,
	[archived] [bit] NOT NULL,
	[employee_id] [bigint] NULL,
	[date_assigned] [datetime2](7) NULL,
	[date_completed] [datetime2](7) NULL,
	[comment] [varchar](max) NULL,
 CONSTRAINT [PK_ResidentAssessmentResult] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

INSERT INTO [dbo].[AssessmentGroup] ([name]) VALUES ('Behavioral Health')
GO

INSERT [dbo].[Assessment] ([assessment_group_id], [name], [json_content], [short_name]) VALUES (1, N'GAD-7 (General Anxiety Disorder-7)', N'{"pages":[{"name":"page1","elements":[{"type":"radiogroup","name":"question1","title":"Feeling nervous, anxious, or on edge","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question2","title":"Not being able to stop or control worrying","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question3","title":"Worrying too much about different things","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question4","title":"Trouble relaxing","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question5","title":"Being so restless that it''s hard to sit still","isRequired":true,"choices":[{"value":"item1","text":" Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question6","title":"Becoming easily annoyed or irritable","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question7","title":"Feeling afraid as if something awful might happen","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question8","title":"How difficult have these problems made it for you to do your work, take care of things at home, or get along with other people?","choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Somewhat difficult"},{"value":"item3","text":"Very difficult"},{"value":"item4","text":"Extremely difficult"}]}],"title":"Over the last 2 weeks, how often have you been bothered by any of the following problems?\n"}]}', N'GAD-7')
GO
INSERT [dbo].[Assessment] ([assessment_group_id], [name], [json_content], [short_name]) VALUES (1, N'PHQ-9 (Patient Health Questionnaire-9)', N'{"pages":[{"name":"page1","elements":[{"type":"radiogroup","name":"question1","title":"Little interest or pleasure in doing things?","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question2","title":"Feeling down, depressed, or hopeless?","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question3","title":"Trouble falling or staying asleep, or sleeping too much?","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question4","title":"Feeling tired or having little energy?","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question5","title":"Poor appetite or overeating?","isRequired":true,"choices":[{"value":"item1","text":" Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question6","title":"Feeling bad about yourself — or that you are a failure or have let yourself or your family down?","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question7","title":"Trouble concentrating on things, such as reading the newspaper or watching television?","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question8","title":"Moving or speaking so slowly that other people could have noticed? Or so fidgety or restless that you have been moving a lot more than usual?","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question9","title":"Thoughts that you would be better off dead, or thoughts of hurting yourself in some way?","isRequired":true,"choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Several days"},{"value":"item3","text":"More than half the days"},{"value":"item4","text":"Nearly every day"}]},{"type":"radiogroup","name":"question10","title":"How difficult have these problems made it for you to do your work, take care of things at home, or get along with other people??","choices":[{"value":"item1","text":"Not at all"},{"value":"item2","text":"Somewhat difficult"},{"value":"item3","text":"Very difficult"},{"value":"item4","text":"Extremely difficult"}]}],"title":"Over the last 2 weeks, how often have you been bothered by any of the following problems?\n"}]}', N'PHQ-9')
GO
