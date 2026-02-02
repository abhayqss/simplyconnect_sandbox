ALTER TABLE [dbo].[NoteSubType]
    ADD [position] BIGINT NULL
GO

UPDATE [dbo].[NoteSubType]
   SET [position] = 3
 WHERE [description] = 'Care Management - 24 hours'

UPDATE [dbo].[NoteSubType]
   SET [position] = 4
WHERE [description] = 'Care Management - 14 days'

UPDATE [dbo].[NoteSubType]
   SET [position] = 5
WHERE [description] = 'Care Management - Additional follow up'

UPDATE [dbo].[NoteSubType]
   SET [position] = 6
WHERE [description] = 'Physician Inpatient Expectation'

UPDATE [dbo].[NoteSubType]
   SET [position] = 6
WHERE [description] = 'Office Note or ED Note'

UPDATE [dbo].[NoteSubType]
   SET [position] = 6
WHERE [description] = 'H&P'

UPDATE [dbo].[NoteSubType]
   SET [position] = 6
WHERE [description] = 'Consult Note'

UPDATE [dbo].[NoteSubType]
   SET [position] = 6
WHERE [description] = 'Inpatient Progress Note'

UPDATE [dbo].[NoteSubType]
   SET [position] = 6
WHERE [description] = 'Operative Note'

UPDATE [dbo].[NoteSubType]
   SET [position] = 6
WHERE [description] = 'Nursing Progress Note'

UPDATE [dbo].[NoteSubType]
   SET [position] = 6
WHERE [description] = 'Physician Communication'

UPDATE [dbo].[NoteSubType]
   SET [position] = 6
WHERE [description] = 'Pharmacy Intervention Note'

UPDATE [dbo].[NoteSubType]
   SET [position] = 6
WHERE [description] = 'Pharmacy Monitoring Note'

UPDATE [dbo].[NoteSubType]
   SET [position] = 6
WHERE [description] = 'Rehab Notes (OT, Speech, PT)'

UPDATE [dbo].[NoteSubType]
   SET [position] = 6
WHERE [description] = 'Nutrition Therapy Note'

UPDATE [dbo].[NoteSubType]
   SET [position] = 6
WHERE [description] = 'Physician Clarification Request from Medical Records (HIM query)'

UPDATE [dbo].[NoteSubType]
   SET [position] = 100
WHERE [description] = 'Other'

GO

ALTER TABLE [dbo].[NoteSubType] ALTER COLUMN [position] BIGINT NOT NULL
GO
