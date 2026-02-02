IF (OBJECT_ID('UQ_PNR_regid_service', 'UQ') IS NOT NULL)
BEGIN
  ALTER TABLE [dbo].[PushNotificationRegistration]
    DROP CONSTRAINT [UQ_PNR_regid_service];
END
GO