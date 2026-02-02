
ALTER TABLE [dbo].[EventGroup]
  ADD [code] VARCHAR(100) NULL;
GO

UPDATE [dbo].[EventGroup] SET code = 'EMERGENCY' WHERE name='Emergency';
UPDATE [dbo].[EventGroup] SET code = 'CHANGING_HEALTH_CONDITIONS' WHERE name='Changing Health Conditions';
UPDATE [dbo].[EventGroup] SET code = 'MEDICATIONS_ALERTS_REACTIONS' WHERE name='Medications Alerts & Reactions';
UPDATE [dbo].[EventGroup] SET code = 'BEHAVIOR_MENTAL_HEALTH' WHERE name='Behavior / Mental Health';
UPDATE [dbo].[EventGroup] SET code = 'GENERAL_LIFE_ASSESSMENT' WHERE name='General / Life / Assessment';
UPDATE [dbo].[EventGroup] SET code = 'ABUSE_SAFETY' WHERE name='Abuse / Safety';
UPDATE [dbo].[EventGroup] SET code = 'NOTES' WHERE name='Notes';
GO

ALTER TABLE [dbo].[EventGroup] ALTER COLUMN [code] VARCHAR(100) NOT NULL;
GO

