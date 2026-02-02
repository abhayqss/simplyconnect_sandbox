SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

EXEC sp_RENAME 'dbo.ServicePlan.status' , 'service_plan_status', 'COLUMN'

EXEC sp_RENAME 'dbo.ServicePlan.date_modified' , 'last_modified_date', 'COLUMN'

ALTER TABLE [dbo].[ServicePlan] ADD
	[chain_id] [bigint] NULL,
	[archived] [bit] NOT NULL DEFAULT 0,
	[status] [varchar](50) NOT NULL DEFAULT 'CREATED'
GO

ALTER TABLE [dbo].[ServicePlanScoring] ADD
	[total_score]  AS ((((((coalesce([health_status_score],(0))+coalesce([transportation_score],(0)))+coalesce([housing_score],(0)))+coalesce([nutrition_security_score],(0)))+coalesce([support_score],(0)))+coalesce([behavioral_score],(0)))+coalesce([other_score],(0)))
GO

SET ANSI_PADDING OFF
GO

