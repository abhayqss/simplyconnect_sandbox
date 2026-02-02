IF COL_LENGTH('resident_enc', 'exit_date') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc
            DROP COLUMN exit_date;
    END
GO

IF COL_LENGTH('resident_enc', 'deactivation_reason') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc
            DROP COLUMN deactivation_reason;
    END
GO

IF COL_LENGTH('resident_enc', 'activation_date') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc
            DROP COLUMN activation_date;
    END
GO

IF COL_LENGTH('resident_enc', 'deactivation_date') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc
            DROP COLUMN deactivation_date;
    END
GO

IF COL_LENGTH('resident_enc', 'comment') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc
            DROP COLUMN comment;
    END
GO

IF COL_LENGTH('resident_enc', 'program_type') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc
            DROP COLUMN program_type;
    END
GO
-- ////

IF COL_LENGTH('resident_enc_History', 'exit_date') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc_History
            DROP COLUMN exit_date;
    END
GO

IF COL_LENGTH('resident_enc_History', 'deactivation_reason') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc_History
            DROP COLUMN deactivation_reason;
    END
GO

IF COL_LENGTH('resident_enc_History', 'activation_date') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc_History
            DROP COLUMN activation_date;
    END
GO

IF COL_LENGTH('resident_enc_History', 'deactivation_date') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc_History
            DROP COLUMN deactivation_date;
    END
GO

IF COL_LENGTH('resident_enc_History', 'comment') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc_History
            DROP COLUMN comment;
    END
GO

IF COL_LENGTH('resident_enc_History', 'program_type') IS NOT NULL
    BEGIN
        ALTER TABLE resident_enc_History
            DROP COLUMN program_type;
    END
GO

ALTER TABLE [dbo].[resident_enc]
    ADD [comment] VARCHAR(256) NULL,
        [exit_date] DATETIME2(7) NULL,
        [deactivation_reason] VARCHAR(256) NULL,
        [program_type] VARCHAR(256) NULL,
        [activation_date] DATETIME2(7) NULL,
        [deactivation_date] DATETIME2(7) NULL;
GO

ALTER TABLE [dbo].[resident_enc_History]
    ADD [comment] VARCHAR(256) NULL,
        [exit_date] DATETIME2(7) NULL,
        [deactivation_reason] VARCHAR(256) NULL,
        [program_type] VARCHAR(256) NULL,
        [activation_date] DATETIME2(7) NULL,
        [deactivation_date] DATETIME2(7) NULL;
GO

EXEC update_resident_view
GO

EXEC update_resident_history_view
GO
