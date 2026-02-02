IF (OBJECT_ID('[dbo].[delete_notification_preferences_mobile]') IS NOT NULL)
  DROP PROCEDURE [dbo].[delete_notification_preferences_mobile];
GO

CREATE PROCEDURE [dbo].[delete_notification_preferences_mobile]
    @NotificationPreferencesIds [dbo].[ID_LIST_TABLE_TYPE] READONLY
AS
  SET NOCOUNT ON;
  BEGIN
    BEGIN TRANSACTION;

    DECLARE @np_id BIGINT;
    DECLARE cur CURSOR FOR SELECT [id] FROM @NotificationPreferencesIds;
    OPEN cur;

    FETCH NEXT FROM cur
    INTO @np_id;
    PRINT 'deleting notification preferences';
    WHILE @@FETCH_STATUS = 0 BEGIN
      PRINT @np_id;
      delete from [UserMobileNotificationPreferences] where [id] = @np_id;
      delete from [NotificationPreferences] where [id] = @np_id;
      FETCH NEXT FROM cur
      INTO @np_id;
    END;
    CLOSE cur;
    DEALLOCATE cur;

    IF @@TRANCOUNT > 0
      COMMIT TRANSACTION;
  END;
GO
