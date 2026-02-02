DELETE FROM [dbo].[PushNotificationRegistration]
WHERE [id] NOT IN (
  SELECT MAX([id])
  FROM [dbo].[PushNotificationRegistration]
  GROUP BY [reg_id], [service]
);

IF (OBJECT_ID('UQ_PNR_regid_service_user', 'UQ') IS NOT NULL)
  BEGIN
    ALTER TABLE [dbo].[PushNotificationRegistration]
      DROP CONSTRAINT [UQ_PNR_regid_service_user];
  END
GO

ALTER TABLE [dbo].[PushNotificationRegistration]
  ADD CONSTRAINT [UQ_PNR_regid_service] UNIQUE ([reg_id], [service]);
GO
