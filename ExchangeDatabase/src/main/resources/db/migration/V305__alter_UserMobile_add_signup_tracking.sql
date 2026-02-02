ALTER TABLE [dbo].[UserMobile]
  ADD [last_successful_signup_time] [DATETIME] NULL;
ALTER TABLE [dbo].[UserMobile]
  ADD [current_signup_start_time] [DATETIME] NULL;

ALTER TABLE [dbo].[PushNotificationRegistration]
  ADD CONSTRAINT [UQ_PNR_regid_service] UNIQUE NONCLUSTERED (reg_id, service);
GO