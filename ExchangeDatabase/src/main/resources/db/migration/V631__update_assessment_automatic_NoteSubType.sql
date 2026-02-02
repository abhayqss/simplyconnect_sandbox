UPDATE [dbo].[NoteSubType]
   SET [allowed_for_event_note] = 1
 WHERE code = 'ASSESSMENT_NOTE'
GO