SET XACT_ABORT ON
GO

SET ANSI_PADDING ON
GO

-- 1. Get id of Patient Record Updated event type
DECLARE @pru_id BIGINT;
SET @pru_id = (SELECT id
               FROM [dbo].[EventType]
               where [code] = 'PRU');

-- 2. create iterator through existing active users
DECLARE cur CURSOR FOR SELECT user_mobile_id
                       FROM [UserMobile] um LEFT JOIN AuthToken at on um.id = at.user_mobile_id
                       where at.expiration_time IS NULL OR at.expiration_time > GETDATE()
                       group by (user_mobile_id)
                       having count(at.id) > 0

OPEN cur;

-- 3. insert default preferences
DECLARE @user_id BIGINT;
DECLARE @UserNotificationPreferencesToInsert TABLE([id] BIGINT, [user_id] BIGINT)
FETCH NEXT FROM cur
INTO @user_id;
WHILE @@FETCH_STATUS = 0 BEGIN
  INSERT INTO [dbo].[NotificationPreferences] ([event_type_id], [notification_type], [responsibility])
  OUTPUT INSERTED.id, @user_id INTO @UserNotificationPreferencesToInsert ([id], [user_id])
  VALUES
    (@pru_id, 'EMAIL', 'I'),
    (@pru_id, 'PUSH_NOTIFICATION', 'I');

  FETCH NEXT FROM cur
  INTO @user_id;
END;

-- 4. dispose iterator
CLOSE cur;
DEALLOCATE cur;

-- 5. update users preferences
INSERT INTO UserMobileNotificationPreferences ([id], [user_id]) SELECT id, user_id FROM @UserNotificationPreferencesToInsert
GO