-- --- Changes Description
--
-- Feature: Send event notifications to patients
--
-- (1) create a table for storing patient notification preferences
-- (2) init notification preferences for registered users
-- (3) alter EventNotification structure
-- (4) alter UserMobile structure : add invited_by to track invitations

CREATE PROCEDURE #insert_default_UserMobileNotificationPreferences  -- temp procedure
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

-- (1) create a table for storing patient notification preferences
CREATE TABLE [dbo].[UserMobileNotificationPreferences] (
  [id]      [BIGINT] NOT NULL PRIMARY KEY
    CONSTRAINT [FK_UMNP_parent] FOREIGN KEY REFERENCES [dbo].[NotificationPreferences] ([id]),
  [user_id] [BIGINT] NOT NULL
    CONSTRAINT [FK_UMNP_UserMobile] FOREIGN KEY REFERENCES [dbo].[UserMobile] ([id])
);
GO

-- (2) init notification preferences with default values for registered users
-- (2.1) count event types
DECLARE @event_type_count BIGINT;
SET @event_type_count = (SELECT count(id) FROM [dbo].[EventType]);

-- (2.2) create iterator through existing activated users
DECLARE cur CURSOR FOR SELECT [id] FROM [UserMobile] WHERE [active] = 1;
OPEN cur;

-- (2.3) insert default preferences
DECLARE @user_id BIGINT;
FETCH NEXT FROM cur INTO @user_id;
WHILE @@FETCH_STATUS = 0 BEGIN
  EXEC #insert_default_UserMobileNotificationPreferences @user_id, @event_type_count;
  FETCH NEXT FROM cur INTO @user_id;
END;

-- (2.4) dispose iterator
CLOSE cur;
DEALLOCATE cur;
GO

-- (3) alter EventNotification structure
-- (3.1) decrypt the symmetric key
OPEN SYMMETRIC KEY SymmetricKey1 DECRYPTION BY CERTIFICATE Certificate1;
GO

-- (3.2) drop unused temp table
IF OBJECT_ID('EventNotification_temp') IS NOT NULL
  DROP TABLE [dbo].[EventNotification_temp];

-- (3.3) add new column referencing users and new column storing person name to EventNotification_enc
ALTER TABLE dbo.EventNotification_enc
  ADD [patient_user_id] BIGINT NULL
  CONSTRAINT FK_EventNotification_UserMobile FOREIGN KEY REFERENCES dbo.UserMobile (id);
ALTER TABLE dbo.EventNotification_enc
  ADD [person_name] varbinary(MAX);
ALTER TABLE dbo.EventNotification_enc
  ALTER COLUMN [employee_id] BIGINT;
ALTER TABLE dbo.EventNotification_enc
  ALTER COLUMN [care_team_role_id] BIGINT;

-- (3.4) recreate EventNotification view

IF OBJECT_ID('EventNotification') IS NOT NULL
  DROP VIEW [dbo].[EventNotification];
GO

CREATE VIEW [dbo].[EventNotification]
AS
  SELECT
    [id],
    [event_id],
    [employee_id],
    [notification_type],
    [created_datetime],
    [care_team_role_id],
    [responsibility],
    [sent_datetime],
    [patient_user_id],
    CONVERT(VARCHAR(255), DecryptByKey([person_name])) person_name,
    CONVERT(VARCHAR(50), DecryptByKey([description]))  description,
    CONVERT(VARCHAR(MAX), DecryptByKey([content]))     content,
    CONVERT(VARCHAR(255), DecryptByKey([destination])) destination
  FROM EventNotification_enc;
GO

-- (3.5) recreate triggers on EventNotification view

CREATE TRIGGER [EventNotificationInsert]
  ON [EventNotification]
INSTEAD OF INSERT
AS
  BEGIN
    INSERT INTO [EventNotification_enc]
    ([event_id], [employee_id], [notification_type], [created_datetime], [care_team_role_id], [responsibility], [sent_datetime], [patient_user_id], [person_name], [description], [content], [destination])
      SELECT
        [event_id],
        [employee_id],
        [notification_type],
        [created_datetime],
        [care_team_role_id],
        [responsibility],
        [sent_datetime],
        [patient_user_id],

        EncryptByKey(Key_GUID('SymmetricKey1'), [person_name]) person_name,
        EncryptByKey(Key_GUID('SymmetricKey1'), [description]) description,
        EncryptByKey(Key_GUID('SymmetricKey1'), [content])     content,
        EncryptByKey(Key_GUID('SymmetricKey1'), [destination]) destination
      FROM inserted;
  END;
GO

CREATE TRIGGER [EventNotificationUpdate]
  ON [EventNotification]
INSTEAD OF UPDATE
AS
  BEGIN
    UPDATE [EventNotification_enc]
    SET
      [event_id]          = i.[event_id],
      [employee_id]       = i.[employee_id],
      [notification_type] = i.[notification_type],
      [created_datetime]  = i.[created_datetime],
      [care_team_role_id] = i.[care_team_role_id],
      [responsibility]    = i.[responsibility],
      [sent_datetime]     = i.[sent_datetime],
      [patient_user_id]   = i.[patient_user_id],
      [person_name]       = EncryptByKey(Key_GUID('SymmetricKey1'), i.[person_name]),
      [description]       = EncryptByKey(Key_GUID('SymmetricKey1'), i.[description]),
      [content]           = EncryptByKey(Key_GUID('SymmetricKey1'), i.[content]),
      [destination]       = EncryptByKey(Key_GUID('SymmetricKey1'), i.[destination])
    FROM inserted i
    WHERE [EventNotification_enc].[id] = i.[id];
  END;
GO

-- (3.6) populate contact name for existing event notifications

UPDATE en
SET person_name = (
  SELECT TOP (1) [first_name] + ' ' + [last_name]
  FROM dbo.Employee e
  WHERE e.id = en.employee_id
) FROM EventNotification en
WHERE en.employee_id IS NOT NULL;
GO

-- (3.7) close the symmetric key
CLOSE SYMMETRIC KEY SymmetricKey1;
GO

-- (4) alter UserMobile structure : add invited_by to track invitations
ALTER TABLE [dbo].[UserMobile]
  ADD [invited_by] BIGINT NULL CONSTRAINT [FK_UM_UserMobile_inviter] FOREIGN KEY REFERENCES [dbo].[UserMobile] ([id]);
GO
