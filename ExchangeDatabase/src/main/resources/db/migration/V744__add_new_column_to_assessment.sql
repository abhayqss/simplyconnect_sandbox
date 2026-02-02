ALTER table [dbo].[Assessment]
    ADD [events_preferences] VARCHAR(255) NULL;
GO

UPDATE [dbo].[Assessment]
SET [events_preferences] = 'NO_EVENTS'
WHERE [send_event_enabled] = 0

UPDATE [dbo].[Assessment]
SET [events_preferences] = 'EVENTS_WITH_DETAILED_ANSWERS_WITHOUT_NOTES'
WHERE [send_event_enabled] = 1
  AND [code] = 'CARE_MGMT'

UPDATE [dbo].[Assessment]
SET [events_preferences] = 'EVENTS_SHORT_WITH_NOTES'
WHERE [send_event_enabled] = 1
  AND [code] != 'CARE_MGMT'

ALTER table [dbo].[Assessment]
    ALTER COLUMN [events_preferences] VARCHAR(255) NOT NULL;
GO