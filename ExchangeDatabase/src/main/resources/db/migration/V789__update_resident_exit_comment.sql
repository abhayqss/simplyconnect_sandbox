IF COL_LENGTH('resident_enc', 'exit_comment') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc
            DROP COLUMN exit_comment;
    END
GO

-- ////

IF COL_LENGTH('resident_enc_History', 'exit_comment') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc_History
            DROP COLUMN exit_comment;
    END
GO

ALTER TABLE [dbo].[resident_enc]
    ADD [exit_comment] VARCHAR(5000) NULL;
GO

ALTER TABLE [dbo].[resident_enc_History]
    ADD [exit_comment] VARCHAR(5000) NULL;
GO

exec resident_table_modified
go
