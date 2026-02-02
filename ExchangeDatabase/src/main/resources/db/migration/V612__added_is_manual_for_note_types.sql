ALTER TABLE [dbo].[NoteSubType]
  ADD [is_manual] bit NULL;
GO

UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'PHYSICIAN_INPATIENT_EXPECTATION';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'OFFICE_NOTE_OR_ED_NOTE';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'H_&_P';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'CONSULT_NOTE';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'INPATIENT_PROGRESS_NOTE';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'OPERATIVE_NOTE';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'NURSING_PROGRESS_NOTE';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'PHYSICIAN_COMMUNICATION';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'PHARMACY_INTERVENTION_NOTE';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'PHARMACY_MONITORING_NOTE';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'REHAB_NOTES';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'NUTRITION_THERAPY_NOTE';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'PHYSICIAN_CLARIFICATION_REQUEST_FROM_MEDICAL_RECORDS';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'OTHER';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'CARE_MANAGEMENT_24_HOURS';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'CARE_MANAGEMENT_14_DAYS';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'CARE_MANAGEMENT_ADDITIONAL_FOLLOW_UP';
UPDATE [dbo].[NoteSubType] SET is_manual = 0 WHERE code = 'ASSESSMENT_NOTE';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'FACE_TO_FACE_ENCOUNTER';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'NON_FACE_TO_FACE_ENCOUNTER';
UPDATE [dbo].[NoteSubType] SET is_manual = 1 WHERE code = 'ASSESSMENT';
GO

ALTER TABLE [dbo].[NoteSubType] ALTER COLUMN [is_manual] bit NOT NULL;
GO