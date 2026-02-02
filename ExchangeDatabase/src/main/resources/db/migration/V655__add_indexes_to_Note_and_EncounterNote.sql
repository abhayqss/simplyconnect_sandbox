IF EXISTS(SELECT *
          FROM sys.indexes
          WHERE name = 'IX_Note_last_modified_date_id_chain_id' AND object_id = OBJECT_ID('dbo.Note'))
    DROP INDEX [IX_Note_last_modified_date_id_chain_id] ON [dbo].[Note]
GO

CREATE NONCLUSTERED INDEX [IX_Note_last_modified_date_id_chain_id] ON [dbo].[Note]
(
	[last_modified_date] ASC
)
INCLUDE ( 	[id],
	[chain_id]) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


IF EXISTS(SELECT *
          FROM sys.indexes
          WHERE name = 'IX_EncounterNote_encounter_note_type_id' AND object_id = OBJECT_ID('dbo.EncounterNote'))
    DROP INDEX [IX_EncounterNote_encounter_note_type_id] ON [dbo].[EncounterNote]
GO

CREATE NONCLUSTERED INDEX [IX_EncounterNote_encounter_note_type_id] ON [dbo].[EncounterNote]
(
	[encounter_note_type_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


IF EXISTS(SELECT *
          FROM sys.indexes
          WHERE name = 'IX_Encounter_note_time_from' AND object_id = OBJECT_ID('dbo.EncounterNote'))
    DROP INDEX [IX_Encounter_note_time_from] ON [dbo].[EncounterNote]
GO

CREATE NONCLUSTERED INDEX [IX_Encounter_note_time_from] ON [dbo].[EncounterNote]
(
	[time_from] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


IF EXISTS(SELECT *
          FROM sys.indexes
          WHERE name = 'IX_Encounter_note_time_to' AND object_id = OBJECT_ID('dbo.EncounterNote'))
    DROP INDEX [IX_Encounter_note_time_to] ON [dbo].[EncounterNote]
GO

CREATE NONCLUSTERED INDEX [IX_Encounter_note_time_to] ON [dbo].[EncounterNote]
(
	[time_to] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO