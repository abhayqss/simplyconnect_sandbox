IF EXISTS(SELECT *
          FROM sys.indexes
          WHERE name = 'IX_Event_datetime' AND object_id = OBJECT_ID('dbo.Event_enc'))
    DROP INDEX [IX_Event_datetime] ON [dbo].[Event_enc]
GO

IF EXISTS(SELECT *
          FROM sys.indexes
          WHERE name = 'IX_Event_event_type_id' AND object_id = OBJECT_ID('dbo.Event_enc'))
    DROP INDEX [IX_Event_event_type_id] ON [dbo].[Event_enc]
GO

ALTER TABLE [dbo].[Event_enc]
  ALTER COLUMN [event_datetime] datetime2(7) NOT NULL
GO

CREATE NONCLUSTERED INDEX [IX_Event_datetime] ON [dbo].[Event_enc]
(
	[event_datetime] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO

CREATE NONCLUSTERED INDEX [IX_Event_event_type_id] ON [dbo].[Event_enc]
(
	[event_type_id] ASC
)
INCLUDE ( 	[id],
	[resident_id],
	[event_datetime],
	[is_er_visit]) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO