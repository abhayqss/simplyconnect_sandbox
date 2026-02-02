ALTER VIEW [dbo].[EventNotification]
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
    CONVERT(VARCHAR(MAX), DecryptByKey([person_name])) person_name,
    CONVERT(VARCHAR(MAX), DecryptByKey([description]))  description,
    CONVERT(VARCHAR(MAX), DecryptByKey([content]))     content,
    CONVERT(VARCHAR(MAX), DecryptByKey([destination])) destination
  FROM EventNotification_enc;

GO