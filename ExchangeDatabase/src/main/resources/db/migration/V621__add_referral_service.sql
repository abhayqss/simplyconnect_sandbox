SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[ReferralService](
  [id] [bigint] IDENTITY(1,1) NOT NULL,
  [service_plan_goal_id] [bigint] NOT NULL,
  [provider_name] [varchar](256) NULL,
  [email] [varchar](256) NULL,
  [phone] [varchar](256) NULL,
  [is_ongoing] [BIT] NOT NULL,
  [contact_name] [varchar](256) NULL,
  [request_status] [varchar](15) NULL,
  [status] [varchar](15) NULL,
  CONSTRAINT [PK_ReferralService] PRIMARY KEY CLUSTERED
(
  [id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
  ) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

ALTER TABLE [dbo].[ReferralService]  WITH CHECK ADD CONSTRAINT [FK_ReferralService_ServicePlanGoal] FOREIGN KEY([service_plan_goal_id])
REFERENCES [dbo].[ServicePlanGoal] ([id])
GO

ALTER TABLE [dbo].[ReferralService] CHECK CONSTRAINT [FK_ReferralService_ServicePlanGoal]
GO