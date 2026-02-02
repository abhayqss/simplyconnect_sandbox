IF (OBJECT_ID('[dbo].[ReferralService]') IS NOT NULL)
  DROP TABLE [dbo].[ReferralService];
GO

ALTER TABLE [dbo].[ServicePlanGoal]
ADD [provider_name] [varchar](256) NULL,
    [email] [varchar](256) NULL,
    [phone] [varchar](256) NULL,
    [is_ongoing] [BIT] NOT NULL DEFAULT(0),
    [contact_name] [varchar](256) NULL,
    [request_status] [varchar](15) NULL,
    [status] [varchar](15) NULL;
GO