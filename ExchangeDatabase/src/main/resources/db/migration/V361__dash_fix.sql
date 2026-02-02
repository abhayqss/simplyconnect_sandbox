UPDATE [dbo].[NoteSubType]
   SET [description] = 'Care Management - 24 hours'
 WHERE [description] = 'Care Management ? 24 hours'

UPDATE [dbo].[NoteSubType]
   SET [description] = 'Care Management - 14 days'
 WHERE [description] = 'Care Management ? 14 days'

 UPDATE [dbo].[NoteSubType]
   SET [description] = 'Care Management - Additional follow up'
 WHERE [description] = 'Care Management ? Additional follow up'
GO
