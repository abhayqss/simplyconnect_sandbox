IF COL_LENGTH('resident_enc', 'pharmacy_pid') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc DROP COLUMN pharmacy_pid;
    END
GO

IF COL_LENGTH('resident_enc_History', 'pharmacy_pid') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc_History
            DROP COLUMN pharmacy_pid;
    END
GO

ALTER TABLE [dbo].[resident_enc]
    ADD [pharmacy_pid] [varchar](100) NULL;
GO

ALTER TABLE [dbo].[resident_enc_History]
    ADD [pharmacy_pid] [varchar](100) NULL;
GO

exec resident_table_modified
go