ALTER TABLE [dbo].[Assessment] 
	ADD [code] [varchar](100) NULL
GO

UPDATE [dbo].[Assessment]
   SET [code] = 'GAD7'
 WHERE id=1
 UPDATE [dbo].[Assessment]
   SET [code] = 'PHQ9'
 WHERE id=2
 UPDATE [dbo].[Assessment]
   SET [code] = 'COMPREHENSIVE'
 WHERE id=3
 GO