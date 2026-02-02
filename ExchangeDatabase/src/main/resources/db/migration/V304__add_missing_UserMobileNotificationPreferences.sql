-- --- Changes Description
--
-- Bugfix: Set default notification settings for patients
--
-- (1) init notification preferences with default values for registered users that are missing them

CREATE PROCEDURE #insert_default_UserMobileNotificationPreferences2  -- temp procedure
    @userId BIGINT,
    @numInserts BIGINT
AS
  BEGIN
    DECLARE @InsertedNotificationPreferences TABLE([id] BIGINT, [processed] BIT);
    DECLARE @counter BIGINT;
    DECLARE @prefId BIGINT;
    SET @counter = @numInserts;

    WHILE @counter > 0
      BEGIN
        INSERT INTO [dbo].[NotificationPreferences] ([event_type_id], [notification_type], [responsibility])
        OUTPUT INSERTED.id, 0 INTO @InsertedNotificationPreferences
        VALUES
          (@counter, 'EMAIL', 'I'),
          (@counter, 'PUSH_NOTIFICATION', 'I');
        SET @counter = @counter - 1;
      END;

    WHILE EXISTS(SELECT * FROM @InsertedNotificationPreferences WHERE [processed] = 0)
      BEGIN
        SELECT TOP (1) @prefId = [id]
        FROM @InsertedNotificationPreferences
        WHERE [processed] = 0;

        INSERT INTO [dbo].[UserMobileNotificationPreferences] ([id], [user_id]) VALUES (@prefId, @userId);

        UPDATE @InsertedNotificationPreferences
        SET [processed] = 1
        WHERE [id] = @prefId;
      END;
  END;

SET XACT_ABORT ON
GO

SET ANSI_PADDING ON
GO

-- (1) init notification preferences with default values for registered users that are missing them
-- (1.1) count event types
DECLARE @event_type_count BIGINT;
SET @event_type_count = (SELECT count(id) FROM [dbo].[EventType]);

-- (1.2) create iterator through existing activated users
DECLARE cur CURSOR FOR SELECT um.[id] FROM [UserMobile] um WHERE um.[active] = 1 AND um.id NOT IN (SELECT user_id FROM [UserMobileNotificationPreferences]);
OPEN cur;

-- (1.3) insert default preferences
DECLARE @user_id BIGINT;
FETCH NEXT FROM cur INTO @user_id;
WHILE @@FETCH_STATUS = 0 BEGIN
  EXEC #insert_default_UserMobileNotificationPreferences2 @user_id, @event_type_count;
  FETCH NEXT FROM cur INTO @user_id;
END;

-- (1.4) dispose iterator
CLOSE cur;
DEALLOCATE cur;
GO
