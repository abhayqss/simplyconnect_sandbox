INSERT INTO [dbo].[NoteSubType]
([description], [follow_up_code], [position], [encounter_code], [hidden_phr], [code], [is_manual], [allowed_for_group_note])
VALUES ('Client Program', null, 20, null, 0, 'CLIENT_PROGRAM', 1, 0)
GO

IF OBJECT_ID('ClientProgramNote') IS NOT NULL
    DROP TABLE [dbo].[ClientProgramNote]
GO

IF OBJECT_ID('ClientProgramNoteType') IS NOT NULL
    DROP TABLE [dbo].[ClientProgramNoteType]
GO

CREATE TABLE [dbo].[ClientProgramNoteType]
(
    [id]          [bigint] IDENTITY (1,1) NOT NULL,
    [code]        [varchar](255)          NULL,
    [description] [varchar](255)          NULL,
    CONSTRAINT [PK_ClientProgramNoteType] PRIMARY KEY CLUSTERED
        ([id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

INSERT INTO [ClientProgramNoteType] ([code], [description])
VALUES ('COMMUNION', 'Communion'),
       ('CHURCH_SERVICE', 'Church Service'),
       ('BINGO', 'Bingo'),
       ('WELLNESS_CHECK', 'Wellness Check'),
       ('FALL_PREVENTION', 'Fall Prevention'),
       ('CHAIR_EXERCISES', 'Chair exercises'),
       ('WII_BOWLING', 'Wii bowling'),
       ('PODIATRIST', 'Podiatrist'),
       ('FLU_SHOT_CLINIC', 'Flu Shot Clinic'),
       ('COVID_VACCINE_CLINIC', 'Covid Vaccine Clinic'),
       ('FARMERS_MARKET', 'Farmers Market'),
       ('MOBILE_LIBRARY', 'Mobile Library'),
       ('OSU_NUTRITION_PROGRAM', 'OSU Nutrition Program')
GO

CREATE TABLE [dbo].[ClientProgramNote]
(
    [id]                          [bigint]       NOT NULL,
    [client_program_note_type_id] [bigint]       NOT NULL,
    [service_provider]            [varchar](256) NOT NULL,
    [start_date]                  [datetime2](7) NOT NULL,
    [end_date]                    [datetime2](7) NOT NULL,
    CONSTRAINT [PK_ClientProgramNote] PRIMARY KEY CLUSTERED
        ([id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[ClientProgramNote]
    WITH CHECK ADD CONSTRAINT [FK_ClientProgramNote_ClientProgramNoteType] FOREIGN KEY ([client_program_note_type_id])
        REFERENCES [dbo].[ClientProgramNoteType] ([id])
GO

ALTER TABLE [dbo].[ClientProgramNote]
    CHECK CONSTRAINT [FK_ClientProgramNote_ClientProgramNoteType]
GO

ALTER TABLE [dbo].[ClientProgramNote]
    WITH CHECK ADD CONSTRAINT [FK_Note_ClientProgramNote] FOREIGN KEY ([id])
        REFERENCES [dbo].[Note] ([id])
GO

ALTER TABLE [dbo].[ClientProgramNote]
    CHECK CONSTRAINT [FK_Note_ClientProgramNote]
GO