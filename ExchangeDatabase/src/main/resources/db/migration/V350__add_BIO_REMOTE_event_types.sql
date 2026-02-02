SET ANSI_PADDING ON
GO
SET XACT_ABORT ON
GO

ALTER TABLE [dbo].[EventType]
  ADD [for_external_use] BIT NOT NULL DEFAULT 0;

ALTER TABLE [dbo].[EventType]
  ALTER COLUMN [event_group_id] [BIGINT] NOT NULL;
GO

DECLARE @EventGroupId BIGINT = (
  SELECT [id]
  FROM [dbo].[EventGroup]
  WHERE [name] = 'Changing Health Conditions');

INSERT INTO [dbo].[EventType] ([code], [description], [event_group_id], [for_external_use])
VALUES
  ('BIO', 'Biometric alert', @EventGroupId, 1),
  ('REMOTE', 'Remote monitoring alert', @EventGroupId, 1);
GO

DECLARE @EventTypeId BIGINT = (
  SELECT [id]
  FROM [dbo].[EventType]
  WHERE [code] = 'BIO');

DECLARE @EventTypeId2 BIGINT = (
  SELECT [id]
  FROM [dbo].[EventType]
  WHERE [code] = 'REMOTE');

INSERT INTO [dbo].[EventType_CareTeamRole_Xref] ([event_type_id], [care_team_role_id], [responsibility])
VALUES
  (@EventTypeId, 1, 'I'),
  (@EventTypeId, 2, 'I'),
  (@EventTypeId, 3, 'I'),
  (@EventTypeId, 4, 'I'),
  (@EventTypeId, 5, 'I'),
  (@EventTypeId, 6, 'I'),
  (@EventTypeId, 7, 'I'),
  (@EventTypeId, 8, 'I'),
  (@EventTypeId, 9, 'I'),
  (@EventTypeId, 10, 'I'),
  (@EventTypeId, 11, 'I'),

  (@EventTypeId2, 1, 'V'),
  (@EventTypeId2, 2, 'V'),
  (@EventTypeId2, 3, 'V'),
  (@EventTypeId2, 4, 'V'),
  (@EventTypeId2, 5, 'V'),
  (@EventTypeId2, 6, 'V'),
  (@EventTypeId2, 7, 'V'),
  (@EventTypeId2, 8, 'V'),
  (@EventTypeId2, 9, 'V'),
  (@EventTypeId2, 10, 'V'),
  (@EventTypeId2, 11, 'V');
GO

SET XACT_ABORT OFF
GO
SET ANSI_PADDING OFF
GO
