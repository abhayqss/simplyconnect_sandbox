IF EXISTS(SELECT *
              FROM sys.indexes
              WHERE name = 'IX_Note_encounter_time_from' AND object_id = OBJECT_ID('dbo.Note'))
        DROP INDEX [IX_Note_encounter_time_from] ON [dbo].[Note]
GO

IF EXISTS(SELECT *
              FROM sys.indexes
              WHERE name = 'IX_Note_encounter_time_to' AND object_id = OBJECT_ID('dbo.Note'))
        DROP INDEX [IX_Note_encounter_time_to] ON [dbo].[Note]
GO

IF COL_LENGTH('Note', 'encounter_date') IS NOT NULL
    BEGIN
        alter table Note
            drop column encounter_date;
    END
GO

ALTER TABLE [dbo].[Note]
    ADD [encounter_date] [datetime2](7) null;
GO

IF COL_LENGTH('Note', 'encounter_time_from') IS NOT NULL
    BEGIN
        alter table Note
            drop column encounter_time_from;
    END
GO

ALTER TABLE [dbo].[Note]
    ADD [encounter_time_from] [datetime2](7) null;
GO

IF COL_LENGTH('Note', 'encounter_time_to') IS NOT NULL
    BEGIN
        alter table Note
            drop column encounter_time_to;
    END
GO

ALTER TABLE [dbo].[Note]
    ADD [encounter_time_to] [datetime2](7) null;
GO

IF COL_LENGTH('Note', 'other_clinician_completing_encounter') IS NOT NULL
    BEGIN
        alter table Note
            drop column other_clinician_completing_encounter;
    END
GO

ALTER TABLE [dbo].[Note]
    ADD [other_clinician_completing_encounter] [varchar](256) null;
GO

IF COL_LENGTH('Note', 'clinician_completing_encounter_id') IS NOT NULL
    BEGIN
		alter table Note
            drop constraint FK_Note_completing_encounter_Employee_enc;
        alter table Note
            drop column clinician_completing_encounter_id;
    END
GO

ALTER TABLE [dbo].[Note]
    ADD [clinician_completing_encounter_id] [bigint] null;
GO

ALTER TABLE [dbo].[Note]  WITH CHECK ADD  CONSTRAINT [FK_Note_completing_encounter_Employee_enc] FOREIGN KEY([clinician_completing_encounter_id])
REFERENCES [dbo].[Employee_enc] ([id])
GO


CREATE NONCLUSTERED INDEX [IX_Note_encounter_time_from] ON [dbo].[Note]
(
	[encounter_time_from] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO

CREATE NONCLUSTERED INDEX [IX_Note_encounter_time_to] ON [dbo].[Note]
(
	[encounter_time_to] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO



UPDATE n
SET 
	 n.encounter_date=en.encounter_date
	,n.encounter_time_from=en.time_from
	,n.encounter_time_to=en.time_to
	,n.other_clinician_completing_encounter=en.other_clinician_completing_encounter
	,n.clinician_completing_encounter_id=en.clinician_completing_encounter_id
FROM Note n INNER JOIN EncounterNote en ON en.id = n.id
GO

DELETE FROM EncounterNote where [encounter_note_type_id] is null
GO

IF EXISTS(SELECT *
              FROM sys.indexes
              WHERE name = 'IX_EncounterNote_encounter_note_type_id' AND object_id = OBJECT_ID('dbo.EncounterNote'))
        DROP INDEX [IX_EncounterNote_encounter_note_type_id] ON [dbo].[EncounterNote]
GO

ALTER TABLE EncounterNote
    ALTER COLUMN encounter_note_type_id bigint NOT NULL;
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

IF EXISTS(SELECT *
              FROM sys.indexes
              WHERE name = 'IX_Encounter_note_time_to' AND object_id = OBJECT_ID('dbo.EncounterNote'))
        DROP INDEX [IX_Encounter_note_time_to] ON [dbo].[EncounterNote]
GO

IF COL_LENGTH('EncounterNote', 'encounter_date') IS NOT NULL
    BEGIN
        alter table EncounterNote
            drop column encounter_date;
    END
GO

IF COL_LENGTH('EncounterNote', 'time_from') IS NOT NULL
    BEGIN
        alter table EncounterNote
            drop column time_from;
    END
GO

IF COL_LENGTH('EncounterNote', 'time_to') IS NOT NULL
    BEGIN
        alter table EncounterNote
            drop column time_to;
    END
GO

IF COL_LENGTH('EncounterNote', 'other_clinician_completing_encounter') IS NOT NULL
    BEGIN
        alter table EncounterNote
            drop column other_clinician_completing_encounter;
    END
GO

IF COL_LENGTH('EncounterNote', 'clinician_completing_encounter_id') IS NOT NULL
    BEGIN
		alter table EncounterNote
            drop constraint FK_EncounterNote_Employee_enc;
        alter table EncounterNote
            drop column clinician_completing_encounter_id;
    END
GO
