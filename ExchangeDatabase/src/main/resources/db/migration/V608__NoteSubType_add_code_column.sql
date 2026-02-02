ALTER TABLE [dbo].[NoteSubType]
  ADD [code] VARCHAR(100) NULL;
GO

UPDATE [dbo].[NoteSubType] SET code = 'PHYSICIAN_INPATIENT_EXPECTATION' WHERE description='Physician Inpatient Expectation';
UPDATE [dbo].[NoteSubType] SET code = 'OFFICE_NOTE_OR_ED_NOTE' WHERE description='Office Note or ED Note';
UPDATE [dbo].[NoteSubType] SET code = 'H_&_P' WHERE description='H&P';
UPDATE [dbo].[NoteSubType] SET code = 'CONSULT_NOTE' WHERE description='Consult Note';
UPDATE [dbo].[NoteSubType] SET code = 'INPATIENT_PROGRESS_NOTE' WHERE description='Inpatient Progress Note';
UPDATE [dbo].[NoteSubType] SET code = 'OPERATIVE_NOTE' WHERE description='Operative Note';
UPDATE [dbo].[NoteSubType] SET code = 'NURSING_PROGRESS_NOTE' WHERE description='Nursing Progress Note';
UPDATE [dbo].[NoteSubType] SET code = 'PHYSICIAN_COMMUNICATION' WHERE description='Physician Communication';
UPDATE [dbo].[NoteSubType] SET code = 'PHARMACY_INTERVENTION_NOTE' WHERE description='Pharmacy Intervention Note';
UPDATE [dbo].[NoteSubType] SET code = 'PHARMACY_MONITORING_NOTE' WHERE description='Pharmacy Monitoring Note';
UPDATE [dbo].[NoteSubType] SET code = 'REHAB_NOTES' WHERE description='Rehab Notes (OT, Speech, PT)';
UPDATE [dbo].[NoteSubType] SET code = 'NUTRITION_THERAPY_NOTE' WHERE description='Nutrition Therapy Note';
UPDATE [dbo].[NoteSubType] SET code = 'PHYSICIAN_CLARIFICATION_REQUEST_FROM_MEDICAL_RECORDS' WHERE description='Physician Clarification Request from Medical Records (HIM query)';
UPDATE [dbo].[NoteSubType] SET code = 'OTHER' WHERE description='Other';
UPDATE [dbo].[NoteSubType] SET code = 'CARE_MANAGEMENT_24_HOURS' WHERE description='Care Management - 24 hours';
UPDATE [dbo].[NoteSubType] SET code = 'CARE_MANAGEMENT_14_DAYS' WHERE description='Care Management - 14 days';
UPDATE [dbo].[NoteSubType] SET code = 'CARE_MANAGEMENT_ADDITIONAL_FOLLOW_UP' WHERE description='Care Management - Additional follow up';
UPDATE [dbo].[NoteSubType] SET code = 'ASSESSMENT_NOTE' WHERE description='Assessment note';
UPDATE [dbo].[NoteSubType] SET code = 'FACE_TO_FACE_ENCOUNTER' WHERE description='Face to face encounter';
UPDATE [dbo].[NoteSubType] SET code = 'NON_FACE_TO_FACE_ENCOUNTER' WHERE description='Non face to face encounter';
UPDATE [dbo].[NoteSubType] SET code = 'ASSESSMENT' WHERE description='Assessment';
GO

ALTER TABLE [dbo].[NoteSubType] ALTER COLUMN [code] VARCHAR(100) NOT NULL;
GO
