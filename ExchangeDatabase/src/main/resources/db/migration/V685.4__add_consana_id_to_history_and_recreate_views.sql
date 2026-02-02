IF COL_LENGTH('resident_enc_History', 'consana_xref_id') IS NOT NULL
    BEGIN
        alter table resident_enc_History
            drop column consana_xref_id;
    END
GO

ALTER TABLE [dbo].[resident_enc_History]
    ADD [consana_xref_id] VARCHAR(40) NULL;
GO

exec update_resident_view
go

exec update_resident_history_view
go
