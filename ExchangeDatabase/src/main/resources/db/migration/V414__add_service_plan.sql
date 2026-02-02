SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ServicePlan](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[status] [varchar](100) NULL,
	[resident_id] [bigint] NOT NULL,
	[employee_id] [bigint] NOT NULL,
	[date_created] [datetime2](7) NULL,
	[date_completed] [datetime2](7) NULL,
	[date_modified] [datetime2](7) NULL,
 CONSTRAINT [PK_ServicePlan] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ServicePlan]  WITH CHECK ADD  CONSTRAINT [FK_ServicePlan_Employee_enc] FOREIGN KEY([employee_id])
REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[ServicePlan] CHECK CONSTRAINT [FK_ServicePlan_Employee_enc]
GO

ALTER TABLE [dbo].[ServicePlan]  WITH CHECK ADD  CONSTRAINT [FK_ServicePlan_resident_enc] FOREIGN KEY([resident_id])
REFERENCES [dbo].[resident_enc] ([id])
GO

ALTER TABLE [dbo].[ServicePlan] CHECK CONSTRAINT [FK_ServicePlan_resident_enc]
GO

CREATE TABLE [dbo].[ServicePlanScoring](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[service_plan_id] [bigint] NOT NULL,
	[health_status_score] [int] NULL,
	[transportation_score] [int] NULL,
	[housing_score] [int] NULL,
	[nutrition_security_score] [int] NULL,
	[support_score] [int] NULL,
	[behavioral_score] [int] NULL,
	[other_score] [int] NULL,
 CONSTRAINT [PK_ServicePlanGraduation] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[ServicePlanScoring]  WITH CHECK ADD  CONSTRAINT [FK_ServicePlanGraduation_ServicePlan] FOREIGN KEY([service_plan_id])
REFERENCES [dbo].[ServicePlan] ([id])
GO

ALTER TABLE [dbo].[ServicePlanScoring] CHECK CONSTRAINT [FK_ServicePlanGraduation_ServicePlan]
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ServicePlanNeed](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[service_plan_id] [bigint] NOT NULL,
	[type] [varchar](100) NOT NULL,
	[priority] [varchar](10) NULL,
 CONSTRAINT [PK_ServicePlanNeed] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ServicePlanNeed]  WITH CHECK ADD  CONSTRAINT [FK_ServicePlanNeed_ServicePlan] FOREIGN KEY([service_plan_id])
REFERENCES [dbo].[ServicePlan] ([id])
GO

ALTER TABLE [dbo].[ServicePlanNeed] CHECK CONSTRAINT [FK_ServicePlanNeed_ServicePlan]
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ServicePlanEducationTaskNeed](
	[id] [bigint] NOT NULL,
	[activation_or_education_task] [varchar](max) NOT NULL,
	[target_completion_date] [datetime2](7) NOT NULL,
	[completion_date] [datetime2](7) NULL,
 CONSTRAINT [PK_ServicePlanEducationTaskNeed] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ServicePlanGoalNeed](
	[id] [bigint] NOT NULL,
	[need_opportunity] [varchar](256) NOT NULL,
	[proficiency_graduation_criteria] [varchar](max) NULL,
 CONSTRAINT [PK_ServicePlanGoalNeed] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ServicePlanGoal](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[goal] [varchar](256) NOT NULL,
	[barriers] [varchar](max) NULL,
	[intervention_action] [varchar](max) NULL,
	[resource_name] [varchar](256) NULL,
	[target_completion_date] [datetime2](7) NOT NULL,
	[completion_date] [datetime2](7) NULL,
	[service_plan_goal_need_id] [bigint] NULL,
 CONSTRAINT [PK_ServicePlanGoal] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ServicePlanGoal]  WITH CHECK ADD  CONSTRAINT [FK_ServicePlanGoal_ServicePlanGoalNeed] FOREIGN KEY([service_plan_goal_need_id])
REFERENCES [dbo].[ServicePlanGoalNeed] ([id])
GO

ALTER TABLE [dbo].[ServicePlanGoal] CHECK CONSTRAINT [FK_ServicePlanGoal_ServicePlanGoalNeed]
GO