ALTER TABLE [dbo].[ReferralRequestNotification]
  ADD [is_org_admin]  bit NOT NULL DEFAULT(0);
GO