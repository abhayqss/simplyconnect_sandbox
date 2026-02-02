SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ServiceStatusCheckNote](
	[id] [bigint] NOT NULL,
	[service_plan_id] [bigint] NOT NULL,
	[resource_name] [varchar](256) NOT NULL,
	[provider_name] [varchar](256) NULL,
	[audit_person] [varchar](256) NOT NULL,
	[check_date] [datetime2](7) NOT NULL,
	[next_check_date] [datetime2](7) NULL,
	[service_provided] [tinyint] NOT NULL,
 CONSTRAINT [PK_ServiceStatusCheckNote] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ServiceStatusCheckNote]  WITH CHECK ADD  CONSTRAINT [FK_ServiceStatusCheckNote_ServicePlan] FOREIGN KEY([service_plan_id])
REFERENCES [dbo].[ServicePlan] ([id])
GO

ALTER TABLE [dbo].[ServiceStatusCheckNote] CHECK CONSTRAINT [FK_ServiceStatusCheckNote_ServicePlan]
GO

INSERT INTO [dbo].[NoteSubType]
           ([description]
           ,[follow_up_code]
           ,[position]
           ,[encounter_code]
           ,[hidden_phr]
           ,[code]
           ,[is_manual]
           ,[allowed_for_group_note])
     VALUES
           ('Service Status Check'
           ,null
           ,20
           ,null
           ,0
           ,'SERVICE_STATUS_CHECK'
           ,1
           ,0)
GO