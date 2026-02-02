CREATE TABLE [dbo].[EncounterNoteType](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[code] [varchar](255) NULL,
	[description] [varchar](255) NULL,
	CONSTRAINT [PK_EncounterNoteType] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

INSERT INTO [EncounterNoteType]
([code],[description])
VALUES 	('ATTEMPT_TO_CONTACT', 'Attempt to contact'),
		('CARE_COORDINATION', 'Care coordination'),
		('FACE_TO_FACE_VISIT', 'Face to face visit'),
		('PHONE_CALL_EMAIL', 'Phone calls and email with patient'),
		('PRES_MGMT_RECON', 'Prescription management/medication reconciliation')

GO

CREATE TABLE [dbo].[EncounterNote](
	[id] [bigint] NOT NULL,
	[encounter_note_type_id] [bigint] NOT NULL,
	[clinician_completing_encounter] [varchar](255) NULL,
	[encounter_date] [datetime2](7) NULL,
	[facility_code] [varchar](255) NULL,
	[from_time] [datetime2](7) NULL,
	[to_time] [datetime2](7) NULL,
	CONSTRAINT [PK_EncounterNote] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[EncounterNote]  WITH CHECK ADD  CONSTRAINT [FK_EncounterNoteType_EncounterType] FOREIGN KEY([encounter_note_type_id])
REFERENCES [dbo].[EncounterNoteType] ([id])
GO

ALTER TABLE [dbo].[EncounterNote]  WITH CHECK ADD  CONSTRAINT [FK_Note_EncounterNote] FOREIGN KEY([id])
REFERENCES [dbo].[Note] ([id])
GO

ALTER TABLE [dbo].[NoteSubType] add encounter_code varchar(50)
GO

UPDATE NoteSubType SET position = 20 WHERE position = 6
GO

INSERT INTO [NoteSubType]
([description],[follow_up_code],[encounter_code],[position])
VALUES ('Face to face encounter',  NULL, 'FACE_TO_FACE_ENCOUNTER',6),
('Non face to face encounter', NULL, 'NON_FACE_TO_FACE_ENCOUNTER',6)
GO
