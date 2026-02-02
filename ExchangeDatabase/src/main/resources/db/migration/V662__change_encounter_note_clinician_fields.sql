EXEC sp_RENAME 'EncounterNote.clinician_completing_encounter', 'other_clinician_completing_encounter', 'COLUMN';
GO

ALTER TABLE [dbo].[EncounterNote]
  ADD [clinician_completing_encounter_id] [bigint] NULL;
GO

ALTER TABLE [dbo].[EncounterNote]  WITH CHECK ADD  CONSTRAINT [FK_EncounterNote_Employee_enc] FOREIGN KEY([clinician_completing_encounter_id])
REFERENCES [dbo].[Employee_enc] ([id])
GO

ALTER TABLE [dbo].[EncounterNote] CHECK CONSTRAINT [FK_EncounterNote_Employee_enc]
GO