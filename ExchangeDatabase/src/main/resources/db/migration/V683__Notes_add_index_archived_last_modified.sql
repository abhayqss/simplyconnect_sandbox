IF EXISTS(SELECT *
          FROM sys.indexes
          WHERE name = 'IX_Note_archived_last_modified_date' AND object_id = OBJECT_ID('dbo.Note'))
	DROP INDEX [IX_Note_archived_last_modified_date] ON [dbo].[Note]
GO

CREATE NONCLUSTERED INDEX [IX_Note_archived_last_modified_date] ON [dbo].[Note]
(
	[archived] ASC,
	[last_modified_date] ASC
)
INCLUDE ( 	[id],
	[event_id]) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO