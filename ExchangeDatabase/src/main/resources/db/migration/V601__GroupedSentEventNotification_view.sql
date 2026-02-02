CREATE VIEW [dbo].[GroupedSentEventNotification]
AS
SELECT         min(id) as id, event_id, employee_id, care_team_role_id, responsibility
FROM            dbo.EventNotification
WHERE sent_datetime is not null
GROUP BY event_id, employee_id, care_team_role_id, responsibility

GO