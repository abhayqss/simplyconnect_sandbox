UPDATE [dbo].[Document]
SET [description] = SUBSTRING([description], 0, 3951)
WHERE [description] IS NOT NULL
GO

ALTER TABLE [dbo].[Document]
    ALTER COLUMN [description] varchar(3950)
GO