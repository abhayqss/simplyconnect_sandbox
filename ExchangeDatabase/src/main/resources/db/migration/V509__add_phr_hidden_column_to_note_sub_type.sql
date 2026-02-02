SET XACT_ABORT ON
GO

ALTER TABLE NoteSubType ADD [hidden_phr] bit not null default(0);
GO

UPDATE NoteSubType set hidden_phr = 1 where encounter_code in('FACE_TO_FACE_ENCOUNTER', 'NON_FACE_TO_FACE_ENCOUNTER');
UPDATE NoteSubType set hidden_phr = 1 where id = 18;
GO