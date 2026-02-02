ALTER TABLE [dbo].[resident_enc]
    ALTER COLUMN [comment] VARCHAR(5000) NULL;
GO

ALTER TABLE [dbo].[resident_enc_History]
    ALTER COLUMN [comment] VARCHAR(5000) NULL;
GO

EXEC update_resident_view
GO

EXEC update_resident_history_view
GO
