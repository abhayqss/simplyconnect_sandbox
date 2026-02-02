DELETE FROM [dbo].[PushNotificationRegistration]
WHERE [id] NOT IN (
  SELECT MIN([id])
  FROM [dbo].[PushNotificationRegistration]
  GROUP BY [reg_id], [service], [user_id]
);

ALTER TABLE [dbo].[PushNotificationRegistration]
  ADD CONSTRAINT [UQ_PNR_regid_service_user] UNIQUE ([reg_id], [service], [user_id]);
GO
