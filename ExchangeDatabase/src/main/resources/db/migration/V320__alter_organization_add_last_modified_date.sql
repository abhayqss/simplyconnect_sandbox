SET XACT_ABORT ON
GO

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
    CONVERT(VARCHAR(255), DecryptByKey([description]))  description,
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
